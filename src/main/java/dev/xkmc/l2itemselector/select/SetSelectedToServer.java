package dev.xkmc.l2itemselector.select;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record SetSelectedToServer(
		int slot,
		ResourceLocation name,
		boolean isCtrlDown, boolean isAltDown, boolean isShiftDown
) implements SerialPacketBase<SetSelectedToServer> {

	public static SetSelectedToServer of(int slot, ResourceLocation name) {
		return new SetSelectedToServer(slot, name, L2Keys.hasCtrlDown(), L2Keys.hasAltDown(), L2Keys.hasShiftDown());
	}

	@Override
	public void handle(Player player) {
		var sel = SelectionRegistry.getEntry(name);
		if (sel == null) return;
		sel.handleServerSetSelection(this, player);
	}

}
