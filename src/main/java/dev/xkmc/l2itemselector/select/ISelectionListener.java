package dev.xkmc.l2itemselector.select;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.BooleanSupplier;

public interface ISelectionListener {

	ResourceLocation getID();

	boolean isClientActive(Player player);

	void handleServerSetSelection(SetSelectedToServer setSelectedToServer, Player sender);

	boolean handleClientScroll(int diff, Player player);

	void handleClientKey(L2Keys k, Player player);

	boolean handleClientNumericKey(int i, BooleanSupplier consumeClick);

	default boolean scrollBypassShift() {
		return false;
	}

	default void toServer(int slot) {
		L2ItemSelector.PACKET_HANDLER.toServer(new SetSelectedToServer(this, slot));
	}

}
