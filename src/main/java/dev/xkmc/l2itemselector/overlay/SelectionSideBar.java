package dev.xkmc.l2itemselector.overlay;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class SelectionSideBar<T, S extends SideBar.Signature<S>> extends SideBar<S> implements LayeredDraw.Layer {

	public SelectionSideBar(float duration, float ease) {
		super(duration, ease);
	}

	public abstract Pair<List<T>, Integer> getItems();

	public abstract boolean isAvailable(T t);

	public abstract boolean onCenter();

	public void initRender() {

	}

	@Override
	public void render(GuiGraphics g, DeltaTracker delta) {
		int width = g.guiWidth(), height = g.guiHeight();
		var level = Minecraft.getInstance().level;
		if (level == null) return;
		float pt = level.getGameTime() + delta.getGameTimeDeltaTicks();
		if (!ease(pt)) return;
		initRender();
		int x0 = this.getXOffset(width);
		int y0 = this.getYOffset(height);
		Context ctx = new Context(g, pt, Minecraft.getInstance().font, x0, y0);
		renderContent(ctx);
	}

	public void renderContent(Context ctx) {
		Pair<List<T>, Integer> content = getItems();
		var list = content.getFirst();
		for (int i = 0; i < list.size(); i++) {
			renderEntry(ctx, list.get(i), i, content.getSecond());
		}
	}

	protected abstract void renderEntry(Context ctx, T t, int index, int select);

	public record Context(GuiGraphics g, float pTick, Font font, int x0, int y0) {

		public void renderItem(ItemStack stack, int x, int y) {
			if (!stack.isEmpty()) {
				g.renderItem(stack, x, y);
				g.renderItemDecorations(font, stack, x, y);
			}
		}
	}

}
