package dev.xkmc.l2itemselector.wheel;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface WheelAdaptor<T extends WheelAdaptor.Entry> extends InputHandler {

	@Nullable
	static WheelAdaptor<?> get(@Nullable Player player, int wheelIndex) {
		return get(player, wheelIndex, true);
	}

	@Nullable
	static WheelAdaptor<?> get(@Nullable Player player, int wheelIndex, boolean main) {
		if (player == null) return null;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return null;
		if (!(sel.get() instanceof Provider pvd)) return null;
		return pvd.get(player, wheelIndex, main).orElse(null);
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

	List<T> getWheelContent();

	default int getWheelSize() {
		return getWheelContent().size();
	}

	int getIndex(Player player);

	void select(int index);

	default RegionCode getMouseSelect(Player player) {
		int n = getWheelSize();
		if (n <= 1) return new RegionCode(-1, false, 0);
		return getContext(player, n).code();
	}

	default WheelContext getContext(Player player, int n) {
		int sel = getIndex(player);
		var region = getRegion();
		var left = WheelAdaptor.get(player, WheelHandler.wheelIndex - 1, false);
		var right = WheelAdaptor.get(player, WheelHandler.wheelIndex + 1, false);
		if (left != null && left.equals(this)) left = null;
		if (right != null && right.equals(this)) right = null;
		var keys = getInputHandler();
		var code = region.buildRegionCode(n, left != null, right != null);
		var hover = code.sel();
		if (hover >= 0) {
			WheelHandler.keyboardIndex = -1;
		} else if (WheelHandler.keyboardIndex >= 0) {
			hover = WheelHandler.keyboardIndex;
		}
		return new WheelContext(region, sel, hover, code, left, right, keys);
	}

	default void renderWheel(GuiGraphics g, Player player) {
		var list = getWheelContent();
		int n = list.size();
		if (n <= 1) return;

		renderImpl(g, player, list, getContext(player, n));
	}

	default void renderImpl(GuiGraphics g, Player player, List<T> list, WheelContext ctx) {
		ctx.region().render(g, player, list, ctx);
	}

	void renderIcon(GuiGraphics g, int x0, int y0, boolean left, float sideWidth);

	interface Provider {

		Optional<WheelAdaptor<?>> get(@Nullable Player player, int wheelIndex, boolean main);

	}

	interface Entry {

		void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, boolean s);

	}

}
