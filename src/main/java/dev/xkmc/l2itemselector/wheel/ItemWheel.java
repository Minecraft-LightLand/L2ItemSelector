package dev.xkmc.l2itemselector.wheel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ItemWheel<T extends WheelAdaptor.Entry> extends WheelAdaptor<T> {

	ItemStack getItem(List<T> list, int index);

	@Override
	default void renderImpl(GuiGraphics g, Player player, List<T> list, int sel, int hover, boolean hasLeft, boolean hasRight) {
		WheelAdaptor.super.renderImpl(g, player, list, sel, hover, hasLeft, hasRight);
		int index = hover >= 0 ? hover : sel;
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
		//TODO review
		var text = stack.getHoverName();
		var font = Minecraft.getInstance().font;
		int y = (int) (y0 + s * 3);
		for (var line : font.split(text, (int) r)) {
			g.drawString(font, line, x0 - font.width(line) / 2, y, 0xffffff, false);
			y += font.lineHeight + 1;
		}
	}

}
