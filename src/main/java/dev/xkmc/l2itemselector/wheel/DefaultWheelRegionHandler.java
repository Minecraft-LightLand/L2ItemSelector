package dev.xkmc.l2itemselector.wheel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultWheelRegionHandler implements WheelRegionHandler {

	public static final WheelRegionHandler INS = new DefaultWheelRegionHandler();

	public record WheelRegion(
			int n, float da, float a0, int x0, int y0, float r, float r0, float r1,
			float mx, float my, float distSqr, int ma
	) {

		int getHover() {
			return distSqr < r1 * r1 ? -1 : ma;
		}

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
		return new WheelRegion(n, da, a0, x0, y0, r, r0, r1, mx, my, mx * mx + my * my, ma);
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

		// render background
		WheelOverlay.fillFan(g, x0, y0, a0, (float) (Math.PI * 2), r * 1.25f, 0, 0, 0, 0x00000000, 0x60000000);
		// render wheel fan
		for (int i = 0; i < n; i++) {
			float ai = a0 + da * i;
			if (hover == i) {
				WheelOverlay.fillFan(g, x0, y0, ai, da, r0, r1, 0, 0, 0x00f4852b, 0x6ff4852b);
			} else if (sel == i) {
				WheelOverlay.fillFan(g, x0, y0, ai, da, r0, r1, 0, 0, 0x00ffffff, 0x6fffffff);
			} else {
				WheelOverlay.fillFan(g, x0, y0, ai, da, r0, r1, 0, 0, 0x00ffffff, 0x1fffffff);
			}
		}
		// render separator
		for (int i = 0; i < n; i++) {
			float a = a0 + da * i + da / 2;
			boolean active = hover >= 0 && (i == hover || i == (hover - 1 + n) % n);
			int innerColor = active ? 0xfff4852b : 0xffffffff;
			int outerColor = active ? 0x00f4852b : 0x00ffffff;
			WheelOverlay.drawSeparator(g, x0, y0, a, r1, r * 1.25f, 0.005f, 0.0025f, innerColor, outerColor);
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
		var r = region.r();

		// render wheel switch
		float sr = r * 1.25f;
		float sideWidth = x0 - sr;
		boolean outOfWheel = region.distSqr() > sr * sr;
		boolean canSwitch = false;
		if (left != null) {
			if (region.mx() < 0 && outOfWheel) {
				WheelOverlay.drawSideGradient(g, x0, y0, true, sideWidth, 0x800088ff, 0x000088ff);
				canSwitch = true;
			} else WheelOverlay.drawSideGradient(g, x0, y0, true, sideWidth, 0x60000000, 0x00000000);
			left.renderIcon(g, x0, y0, true, sideWidth);
		}
		if (right != null) {
			if (region.mx() >= 0 && outOfWheel) {
				WheelOverlay.drawSideGradient(g, x0, y0, false, sideWidth, 0x800088ff, 0x000088ff);
				canSwitch = true;
			} else WheelOverlay.drawSideGradient(g, x0, y0, false, sideWidth, 0x60000000, 0x00000000);
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

		// render inner wheel border
		WheelOverlay.fillFan(g, x0, y0, a0, (float) (Math.PI * 2), r1 + 1, r1, 0, 0, 0x80ffffff, 0x80ffffff);

		float arcAngle = WheelHandler.keyboardIndex >= 0
				? a0 + da * WheelHandler.keyboardIndex
				: (float) Math.atan2(region.my(), region.mx());

		int arcColor = switch (arc) {
			case SELECT -> 0xfff4852b;
			case SWITCH -> 0x800088ff;
			case CLOSE -> 0x80ff4444;
			default -> 0xffffffff;
		};

		// render mouse arc
		WheelOverlay.fillFan(g, x0, y0, arcAngle, da, r1 - 1.5f, r1 - 4f, 0, 0, arcColor, arcColor);

		// render sel arc
		if (sel >= 0 && (hover < 0 || hover != sel)) {
			float selAngle = a0 + da * sel;
			WheelOverlay.fillFan(g, x0, y0, selAngle, da, r1 + 2.5f, r1, 0, 0, 0xffffffff, 0xffffffff);
		}

		// render hover arc
		if (hover >= 0) {
			float sliceAngle = a0 + da * hover;
			WheelOverlay.fillFan(g, x0, y0, sliceAngle, da, r1 + 4f, r1, 0, 0, 0xfff4852b, 0xfff4852b);
		}
	}

}
