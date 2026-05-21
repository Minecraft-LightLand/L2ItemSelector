package dev.xkmc.l2itemselector.overlay;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import dev.xkmc.l2itemselector.wheel.InputHandler;
import dev.xkmc.l2itemselector.wheel.WheelKeyHandler;
import dev.xkmc.l2itemselector.wheel.WheelRegionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface WheelAdaptor extends InputHandler {

	@Nullable
	static WheelAdaptor get(@Nullable Player player, int wheelIndex) {
		if (player == null) return null;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return null;
		if (!(sel.get() instanceof Provider pvd)) return null;
		return pvd.get(player, wheelIndex).orElse(null);
	}

	default WheelKeyHandler getInputHandler() {
		return WheelKeyHandler.getDefault();
	}

	default WheelRegionHandler getRegion() {
		return WheelRegionHandler.getDefault();
	}

	@Override
	default boolean scrollBypassShift() {
		return true;
	}

	@Override
	default void handleClientKey(L2Keys k, Player player) {
		getInputHandler().handleClientKey(k, player);
	}

	@Override
	default boolean handleClientScroll(int diff, Player player) {
		return getInputHandler().handleClientScroll(diff, player);
	}

	List<Entry> getWheelContent();

	default int getWheelSize() {
		return getWheelContent().size();
	}

	int getIndex(Player player);

	void select(int index);

	default int getMouseSelect(Player player) {
		int n = getWheelSize();
		if (n <= 1) return -1;
		return getRegion().getHover(n);
	}

	default void renderWheel(GuiGraphics g, Player player) {
		var list = getWheelContent();
		int n = list.size();
		if (n <= 1) return;
		int sel = getIndex(player);
		int hover = getRegion().getHover(n);
		if (hover >= 0) {
			WheelHandler.keyboardIndex = -1;
		} else if (WheelHandler.keyboardIndex >= 0) {
			hover = WheelHandler.keyboardIndex;
		}
		var left = WheelAdaptor.get(player, WheelHandler.wheelIndex - 1);
		var right = WheelAdaptor.get(player, WheelHandler.wheelIndex + 1);
		if (left != null && left.equals(this)) left = null;
		if (right != null && right.equals(this)) right = null;
		renderImpl(g, player, list, sel, hover, left != null, right != null);
	}

	default void renderImpl(GuiGraphics g, Player player, List<Entry> list, int sel, int hover, boolean hasLeft, boolean hasRight) {
		getRegion().render(g, player, list, sel, hover, hasLeft, hasRight);
	}

	interface Provider {

		Optional<WheelAdaptor> get(@Nullable Player player, int wheelIndex);

	}

	interface Entry {

		void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, float s);

	}

	interface ItemWheel extends WheelAdaptor {

		ItemStack getItem(int index);

		@Override
		default void renderImpl(GuiGraphics g, Player player, List<Entry> list, int sel, int hover, boolean hasLeft, boolean hasRight) {
			WheelAdaptor.super.renderImpl(g, player, list, sel, hover, hasLeft, hasRight);
			int index = hover >= 0 ? hover : sel;
			ItemStack stack = getItem(index);
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

}
