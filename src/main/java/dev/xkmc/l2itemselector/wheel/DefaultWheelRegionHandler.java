package dev.xkmc.l2itemselector.wheel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultWheelRegionHandler implements WheelRegionHandler {

	public static final WheelRegionHandler INS = new DefaultWheelRegionHandler();

	public record WheelRegion(
			int n, float da, float a0, int x0, int y0, float r, float r0, float r1, float r2,

			float mx, float my, float distSqr, int ma
	) {

		int getHover() {
			return distSqr < r1 * r1 ? -1 : ma;
		}

	}

	public record ColorPalette(
			int wheelBg0, int wheelBg1,
			int fanBg, int fanSelBg, int fanHoverBg,
			int separator, int sepActive,
			int switcher, int switchHover,
			int innerBorder, int selArc, int hoverArc,
			int mouseSel, int mouseSwitch, int mouseClose, int mouseDef
	) {

		public ColorPalette(int sel, int hover, int close, int switcher) {
			this(0x00000000, 0x60000000,
					0x1fffffff & sel, 0x6fffffff & sel, 0x6fffffff & hover,
					sel, hover,
					0x60000000, switcher,
					0x80ffffff & sel, sel, hover,
					hover, switcher, close, sel);
		}

		public static ColorPalette INS = new ColorPalette(
				0xffffffff, 0x6ff4852b, 0x80ff4444, 0x800088ff
		);

	}

	public WheelRegion getRegion(int n) {
		float da = (float) (Math.PI * 2 / n);
		float a0 = (float) (-Math.PI / 2);
		var win = Minecraft.getInstance().getWindow();
		int x0 = win.getGuiScaledWidth() / 2, y0 = win.getGuiScaledHeight() / 2;
		float r = Math.min(x0, y0) / 1.5f;
		float r0 = Math.max(40, r * 0.85f);
		float r1 = r * 0.5f;
		var mh = Minecraft.getInstance().mouseHandler;
		var mx = (float) mh.xpos() * x0 * 2 / win.getScreenWidth() - x0;
		var my = (float) mh.ypos() * y0 * 2 / win.getScreenHeight() - y0;
		int ma = (int) ((Math.atan2(my, mx) - a0 + Math.PI * 2 + da / 2) / da) % n;
		return new WheelRegion(n, da, a0, x0, y0, r, r0, r1, r * 1.25f, mx, my, mx * mx + my * my, ma);
	}

	public ColorPalette getPalette() {
		return ColorPalette.INS;
	}

	@Override
	public int getHover(int n) {
		return getRegion(n).getHover();
	}

	@Override
	public void render(GuiGraphics g, Player player, List<? extends WheelAdaptor.Entry> list, WheelContext ctx) {
		int n = list.size();
		var region = getRegion(n);
		renderWheel(g, region, list, ctx.sel(), ctx.hover());
		var canSwitch = renderSwitch(g, region, ctx.left(), ctx.right());
		renderArc(g, region, ctx.sel(), ctx.hover(), ctx.keys().getArcColor(ctx.hover(), canSwitch));
	}

	protected void renderWheel(GuiGraphics g, WheelRegion region, List<? extends WheelAdaptor.Entry> list, int sel, int hover) {
		int n = region.n();
		var x0 = region.x0();
		var y0 = region.y0();
		var a0 = region.a0();
		var da = region.da();
		var r0 = region.r0();
		var r1 = region.r1();
		var r = region.r();
		var r2 = region.r2();
		var col = getPalette();

		// render background
		WheelOverlay.fillFan(g, x0, y0, a0, (float) (Math.PI * 2), r2, 0, 0, 0, col.wheelBg0(), col.wheelBg1());
		// render wheel fan
		for (int i = 0; i < n; i++) {
			float ai = a0 + da * i;
			int color;
			if (hover == i) color = col.fanHoverBg();
			else if (sel == i) color = col.fanSelBg();
			else color = col.fanBg();
			WheelOverlay.fillFan(g, x0, y0, ai, da, r0, r1, 0, 0, color & 0x00ffffff, color);
		}
		// render separator
		for (int i = 0; i < n; i++) {
			float a = a0 + da * i + da / 2;
			boolean active = hover >= 0 && (i == hover || i == (hover - 1 + n) % n);
			int innerColor = active ? col.sepActive() : col.separator();
			int outerColor = innerColor & 0x00ffffff;
			WheelOverlay.drawSeparator(g, x0, y0, a, r1, r2, 0.005f, 0.0025f, innerColor, outerColor);
		}
		// render wheel content
		for (int i = 0; i < n; i++) {
			float ai = a0 + da * i;
			list.get(i).render(g, x0, y0, ai, region.r0(), r, da, hover == i);
		}
	}

	protected boolean renderSwitch(GuiGraphics g, WheelRegion region, @Nullable WheelAdaptor<?> left, @Nullable WheelAdaptor<?> right) {

		var x0 = region.x0();
		var y0 = region.y0();
		float sr = region.r2();
		var col = getPalette();

		// render wheel switch
		float sideWidth = x0 - sr;
		boolean outOfWheel = region.distSqr() > sr * sr;
		boolean canSwitch = false;
		int side = col.switcher(), side0 = side & 0x00ffffff;
		int hover = col.switchHover(), hover0 = hover & 0x00ffffff;
		if (left != null) {
			if (region.mx() < 0 && outOfWheel) {
				WheelOverlay.drawSideGradient(g, true, sideWidth, hover, hover0);
				canSwitch = true;
			} else WheelOverlay.drawSideGradient(g, true, sideWidth, side, side0);
			left.renderIcon(g, x0, y0, true, sideWidth);
		}
		if (right != null) {
			if (region.mx() >= 0 && outOfWheel) {
				WheelOverlay.drawSideGradient(g, false, sideWidth, hover, hover0);
				canSwitch = true;
			} else WheelOverlay.drawSideGradient(g, false, sideWidth, side, side0);
			right.renderIcon(g, x0, y0, false, sideWidth);
		}
		return canSwitch;
	}

	protected void renderArc(GuiGraphics g, WheelRegion region, int sel, int hover, ArcCode arc) {
		var x0 = region.x0();
		var y0 = region.y0();
		var a0 = region.a0();
		var da = region.da();
		var r1 = region.r1();
		var col = getPalette();
		int bg = col.innerBorder();
		int selCol = col.selArc();
		int hoverCol = col.hoverArc();

		// render inner wheel border
		//TODO param
		WheelOverlay.fillFan(g, x0, y0, a0, (float) (Math.PI * 2), r1 + 1, r1, 0, 0, bg, bg);

		float arcAngle = WheelHandler.keyboardIndex >= 0
				? a0 + da * WheelHandler.keyboardIndex
				: (float) Math.atan2(region.my(), region.mx());

		int arcColor = switch (arc) {
			case SELECT -> col.mouseSel();
			case SWITCH -> col.mouseSwitch();
			case CLOSE -> col.mouseClose();
			default -> col.mouseDef();
		};

		// render mouse arc
		//TODO param
		WheelOverlay.fillFan(g, x0, y0, arcAngle, da, r1 - 1.5f, r1 - 4f, 0, 0, arcColor, arcColor);

		// render sel arc
		if (sel >= 0 && (hover < 0 || hover != sel)) {
			float selAngle = a0 + da * sel;
			//TODO param
			WheelOverlay.fillFan(g, x0, y0, selAngle, da, r1 + 2.5f, r1, 0, 0, selCol, selCol);
		}

		// render hover arc
		if (hover >= 0) {
			float sliceAngle = a0 + da * hover;
			//TODO param
			WheelOverlay.fillFan(g, x0, y0, sliceAngle, da, r1 + 4f, r1, 0, 0, hoverCol, hoverCol);
		}

	}

}
