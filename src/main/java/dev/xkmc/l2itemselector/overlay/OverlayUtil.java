package dev.xkmc.l2itemselector.overlay;

import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.List;

public class OverlayUtil implements ClientTooltipPositioner {

	private static int getBGColor() {
		return (int) (Math.round(L2ISConfig.CLIENT.infoAlpha.get() * 255)) << 24 | 0x100010;
	}

	public int bg = getBGColor();
	public int bs = 0x505000FF;
	public int be = 0x5028007f;
	public int tc = 0xFFFFFFFF;

	protected final GuiGraphicsExtractor g;
	protected final int x0, y0, maxW;

	public OverlayUtil(GuiGraphicsExtractor g, int x0, int y0, int maxW) {
		this.g = g;
		this.x0 = x0;
		this.y0 = y0;
		this.maxW = maxW < 0 ? getMaxWidth() : maxW;
	}

	public int getMaxWidth() {
		return g.guiWidth() / 4;
	}

	public void renderLongText(Font font, List<Component> list) {
		List<ClientTooltipComponent> ans = list.stream().flatMap(text -> font.split(text, maxW).stream())
				.map(ClientTooltipComponent::create).toList();
		g.tooltip(font, ans, x0, y0, this, null);
	}

	@Override
	public Vector2ic positionTooltip(int gw, int gh, int x, int y, int tw, int th) {
		if (x < 0) x = Math.round(gw / 8f);
		if (y < 0) y = Math.round((gh - th) / 2f);
		return new Vector2i(x, y);
	}

	/**
	 * specifies outer size
	 */
	public static void fillRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int col) {
		g.fill(x, y, x + w, y + h, col);
	}

	/**
	 * specifies inner size
	 */
	public static void drawRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int col) {
		fillRect(g, x - 1, y - 1, w + 2, 1, col);
		fillRect(g, x - 1, y - 1, 1, h + 2, col);
		fillRect(g, x - 1, y + h, w + 2, 1, col);
		fillRect(g, x + w, y - 1, 1, h + 2, col);
	}

}
