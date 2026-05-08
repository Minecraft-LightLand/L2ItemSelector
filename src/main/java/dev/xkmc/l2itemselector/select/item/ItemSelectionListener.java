package dev.xkmc.l2itemselector.select.item;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.ItemWheelEntry;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import dev.xkmc.l2itemselector.select.ISelectionListener;
import dev.xkmc.l2itemselector.select.SetSelectedToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;

public class ItemSelectionListener implements ISelectionListener, WheelAdaptor.Provider {

	public static final ISelectionListener INSTANCE = new ItemSelectionListener();

	@Override
	public ResourceLocation getID() {
		return L2ItemSelector.loc("item");
	}

	@Override
	public boolean isClientActive(Player player) {
		if (Minecraft.getInstance().screen != null) return false;
		if (Screen.hasAltDown()) return false;
		var sel = IItemSelector.getSelection(player);
		return sel != null;
	}

	@Override
	public void handleServerSetSelection(SetSelectedToServer packet, Player sender) {
		var sel = IItemSelector.getSelection(sender);
		if (sel != null) {
			sel.swap(sender, packet.slot());
		}
	}

	@Override
	public boolean handleClientScroll(int diff, Player player) {
		var sel = IItemSelector.getSelection(player);
		if (sel == null) return false;
		toServer(sel.move(-diff, player));
		return true;
	}

	@Override
	public void handleClientKey(L2Keys key, Player player) {
		var sel = IItemSelector.getSelection(player);
		if (sel == null) return;
		if (key == L2Keys.UP) {
			toServer(sel.move(-1, player));
		} else if (key == L2Keys.DOWN) {
			toServer(sel.move(1, player));
		}
	}

	@Override
	public boolean handleClientNumericKey(int i, BooleanSupplier consumeClick) {
		return false;
	}

	@Override
	public Optional<WheelAdaptor> get(@Nullable Player player) {
		if (player == null) return Optional.empty();
		var sel = IItemSelector.getSelection(player);
		if (sel == null) return Optional.empty();
		if (sel.selector() instanceof WheelAdaptor.Provider pvd)
			return pvd.get(player);
		return ClientHandler.get(sel);
	}

	static class ClientHandler {

		public static Optional<WheelAdaptor> get(IItemSelector.Holder sel) {
			if (sel.selector() instanceof ItemSelector)
				return Optional.of(new Wheel(sel));
			return Optional.empty();
		}

	}

	public record Wheel(IItemSelector.Holder sel) implements WheelAdaptor.ItemWheel {

		@Override
		public void select(int index) {
			L2ItemSelector.PACKET_HANDLER.toServer(SetSelectedToServer.of(index,
					ItemSelectionListener.INSTANCE.getID()));
		}

		@Override
		public List<Entry> getWheelContent() {
			var src = sel.getDisplayList();
			var ans = new ArrayList<Entry>();
			for (var e : src) {
				ans.add(new ItemWheelEntry(e));
			}
			return ans;
		}

		@Override
		public ItemStack getItem(int index) {
			return sel.getDisplayList().get(index);
		}

		@Override
		public int getIndex(Player player) {
			return sel.getIndex(player);
		}

	}

}
