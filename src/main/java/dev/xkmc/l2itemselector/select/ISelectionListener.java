package dev.xkmc.l2itemselector.select;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.wheel.InputHandler;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.function.BooleanSupplier;

public interface ISelectionListener extends InputHandler {

	Identifier getID();

	boolean isClientActive(Player player);

	void handleServerSetSelection(SetSelectedToServer setSelectedToServer, Player sender);

	boolean handleClientNumericKey(int i, BooleanSupplier consumeClick);

	default boolean scrollBypassShift() {
		return false;
	}

	default void toServer(int slot) {
		L2ItemSelector.PACKET_HANDLER.toServer(SetSelectedToServer.of(slot, getID()));
	}

	default boolean isHoldKeyDown(Player player) {
		return player.isShiftKeyDown();
	}

}
