package dev.xkmc.l2itemselector.wheel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ItemWheel<T extends WheelAdaptor.Entry> extends WheelAdaptor<T> {

	ItemStack getItem(List<T> list, int index);

	@Override
	default void renderIcon(GuiGraphics g, int x0, int y0, boolean left, float sideWidth, boolean hover) {
		var list = getWheelContent();
		if (list.isEmpty()) return;

		float cx = left ? sideWidth / 2f : g.guiWidth() - sideWidth / 2f;
		float r = Math.min(sideWidth, y0) * 0.4f;
		float r0 = Math.max(10, r * 0.85f);
		float r1 = r * 0.5f;
		int n = list.size();
		float da = (float) (Math.PI * 2 / n);
		float a0 = (float) (-Math.PI / 2);
		var col = DefaultWheelRegionHandler.ColorPalette.INS;

		g.pose().pushPose();
		g.pose().translate(cx, y0, 0);

		WheelOverlay.fillFan(g, 0, 0, a0, (float) (Math.PI * 2), r * 1.25f, 0, 0, 0, col.wheelBg0(), col.wheelBg1());

		for (int i = 0; i < n; i++) {
			float ai = a0 + da * i;
			WheelOverlay.fillFan(g, 0, 0, ai, da, r0, r1, 0, 0, col.fanBg() & 0x00ffffff, col.fanBg());
		}

		for (int i = 0; i < n; i++) {
			float a = a0 + da * i + da / 2;
			WheelOverlay.drawSeparator(g, 0, 0, a, r1, r * 1.25f, 0.005f, 0.0025f, col.separator(), col.separator() & 0x00ffffff);
		}

		for (int i = 0; i < n; i++) {
			float ai = a0 + da * i;
			list.get(i).render(g, 0, 0, ai, r0, r, da, false);
		}

		WheelOverlay.fillFan(g, 0, 0, a0, (float) (Math.PI * 2), r1 * 1.017f, r1, 0, 0, col.innerBorder(), col.innerBorder());

		g.pose().popPose();
	}

	@Override
	default void renderImpl(GuiGraphics g, Player player, List<T> list, WheelContext ctx) {
		WheelAdaptor.super.renderImpl(g, player, list, ctx);
		int index = ctx.hover() >= 0 ? ctx.hover() : ctx.sel();
		ItemStack stack = getItem(list, index);
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0, y0) / 2f;
		float s = r * 0.02f;
		g.pose().pushPose();
		g.pose().translate(x0, y0, 0);
		g.pose().scale(s, s, s);
		g.renderItem(stack, -8, -16);
		g.renderItemDecorations(Minecraft.getInstance().font, stack, -8, -16);
		g.pose().popPose();
		var text = stack.getHoverName();
		var font = Minecraft.getInstance().font;
		int y = (int) (y0 + s * 3);
		for (var line : font.split(text, (int) r)) {
			g.drawString(font, line, x0 - font.width(line) / 2, y, 0xffffff, false);
			y += font.lineHeight + 1;
		}
	}

}
