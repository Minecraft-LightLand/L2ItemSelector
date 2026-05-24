package dev.xkmc.l2itemselector.wheel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ItemWheel<T extends WheelAdaptor.Entry> extends WheelAdaptor<T> {

	ItemStack getItem(List<T> list, int index);

	@Override
	default void renderIcon(GuiGraphicsExtractor g, int x0, int y0, boolean left, float sideWidth, boolean hover) {
		var list = getWheelContent();
		if (list.isEmpty()) return;
		float cx = left ? sideWidth / 2f : g.guiWidth() - sideWidth / 2f;
		float r = Math.min(x0 / 1.5f, y0) / 1.5f;
		float s = r * 0.015f;
		float r0 = Math.min(sideWidth / 2f, r * 0.75f) * (hover ? 0.3f : 0.15f);
		int n = list.size();
		float da = (float) (Math.PI * 2 / n);
		float a0 = (float) (-Math.PI / 2);
		g.pose().pushMatrix();
		g.pose().translate(cx, y0);
		for (int i = 0; i < n; i++) {
			float ai = a0 + da * i;
			float dx = Mth.cos(ai) * r0;
			float dy = Mth.sin(ai) * r0;
			g.pose().popMatrix();
			g.pose().translate(dx, dy);
			g.pose().scale(s, s);
			g.item(getItem(list, i), -8, -8);
			g.pose().popMatrix();
		}
		g.pose().popMatrix();
	}

	@Override
	default void renderImpl(GuiGraphicsExtractor g, Player player, List<T> list, WheelContext ctx) {
		WheelAdaptor.super.renderImpl(g, player, list, ctx);
		int index = ctx.hover() >= 0 ? ctx.hover() : ctx.sel();
		ItemStack stack = getItem(list, index);
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0 / 1.5f, y0) / 1.5f;
		float s = r * 0.02f;
		g.pose().pushMatrix();
		g.pose().translate(x0, y0);
		g.pose().scale(s, s);
		g.item(stack, -8, -16);
		g.itemDecorations(Minecraft.getInstance().font, stack, -8, -16);
		g.pose().popMatrix();
		var text = stack.getHoverName();
		var font = Minecraft.getInstance().font;
		int y = (int) (y0 + s * 3);
		for (var line : font.split(text, (int) r)) {
			g.text(font, line, x0 - font.width(line) / 2, y, 0xffffffff, true);
			y += font.lineHeight + 1;
		}
	}

}
