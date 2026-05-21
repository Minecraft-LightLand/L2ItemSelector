package dev.xkmc.l2itemselector.wheel;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface InputHandler {

	static Optional<InputHandler> getHandler(Player player) {
		if (WheelHandler.wheel != null) {
			return Optional.of(WheelHandler.wheel);
		}
		return SelectionRegistry.getClientActiveListener(player).map(e -> e);
	}

	void handleClientKey(L2Keys k, Player player);

	boolean handleClientScroll(int diff, Player player);

	default boolean handleClientScroll(int diff, double delta, Player player) {
		if (diff == 0) return true;
		return handleClientScroll((int) Math.signum(diff), player);
	}

	boolean scrollBypassShift();

	default boolean isHoldKeyDown(Player player) {
		return player.isShiftKeyDown();
	}

}
