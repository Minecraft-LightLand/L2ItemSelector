package dev.xkmc.l2itemselector.select;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

@SerialClass
public class SetSelectedToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public int slot;

	@SerialClass.SerialField
	public ResourceLocation name;

	@SerialClass.SerialField
	public boolean isCtrlDown, isAltDown, isShiftDown;

	/**
	 * @deprecated
	 */
	@Deprecated
	public SetSelectedToServer() {
	}

	public SetSelectedToServer(ISelectionListener sel, int slot) {
		this.name = sel.getID();
		this.slot = slot;
		this.isCtrlDown = L2Keys.hasCtrlDown();
		this.isAltDown = L2Keys.hasAltDown();
		this.isShiftDown = L2Keys.hasShiftDown();
	}

	public void handle(Context ctx) {
		Player sender = ctx.getSender();
		if (sender == null) return;
		var sel = SelectionRegistry.getEntry(name);
		if (sel == null) return;
		sel.handleServerSetSelection(this, sender);
	}

}
