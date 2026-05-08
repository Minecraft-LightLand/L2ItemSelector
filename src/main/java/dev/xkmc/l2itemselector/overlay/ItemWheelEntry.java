package dev.xkmc.l2itemselector.overlay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public record ItemWheelEntry(ItemStack stack) implements WheelAdaptor.Entry {

	@Override
	public void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, float s) {
		s *= Math.min(r * 0.015f, da * r0 / 16f);

		float dx = x0 + Mth.cos(ai) * r0;
		float dy = y0 + Mth.sin(ai) * r0;
		g.pose().pushPose();
		g.pose().translate(dx, dy, 0);
		g.pose().scale(s, s, s);
		g.renderItem(stack, -8, -8);
		g.pose().popPose();
	}

}
