package dev.xkmc.l2itemselector.wheel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public record ItemWheelEntry(ItemStack stack) implements WheelAdaptor.Entry {

	@Override
	public void render(GuiGraphicsExtractor g, float x0, float y0, float ai, float r0, float r, float da, boolean sel) {
		var s = sel ? 1.1f : 1;
		s *= Math.min(r * 0.015f, da * r0 / 16f);

		float dx = x0 + Mth.cos(ai) * r0;
		float dy = y0 + Mth.sin(ai) * r0;
		g.pose().pushMatrix();
		g.pose().translate(dx, dy);
		g.pose().scale(s, s);
		g.item(stack, -8, -8);
		g.itemDecorations(Minecraft.getInstance().font, stack, -8, -8);
		g.pose().popMatrix();
	}

}
