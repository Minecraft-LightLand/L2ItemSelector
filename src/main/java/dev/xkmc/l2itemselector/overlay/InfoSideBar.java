package dev.xkmc.l2itemselector.overlay;

import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class InfoSideBar<S extends SideBar.Signature<S>> extends SideBar<S> implements LayeredDraw.Layer {

	public InfoSideBar(float duration, float ease) {
		super(duration, ease);
	}

	@Override
	public void render(GuiGraphics g, DeltaTracker delta) {
		int width = g.guiWidth(), height = g.guiHeight();
		var level = Minecraft.getInstance().level;
		if (level == null) return;
		float pt = level.getGameTime() + delta.getGameTimeDeltaTicks();
		if (!ease(pt)) return;
		var text = getText();
		if (text.isEmpty()) return;
		int anchor = L2ISConfig.CLIENT.infoAnchor.get();
		int y = height * anchor / 2;
		int w = (int) (width * L2ISConfig.CLIENT.infoMaxWidth.get());
		new TextBox(g, 0, anchor, getXOffset(width), y, w)
				.renderLongText(Minecraft.getInstance().font, text);
	}

	protected abstract List<Component> getText();

	@Override
	protected int getXOffset(int width) {
		float progress = (max_ease - ease_time) / max_ease;
		return Math.round(-progress * width / 2 + 8);
	}

}

