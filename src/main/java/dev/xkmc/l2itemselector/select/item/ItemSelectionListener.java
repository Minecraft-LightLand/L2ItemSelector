package dev.xkmc.l2itemselector.select.item;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.ISelectionListener;
import dev.xkmc.l2itemselector.select.SetSelectedToServer;
import dev.xkmc.l2itemselector.wheel.ItemWheel;
import dev.xkmc.l2itemselector.wheel.ItemWheelEntry;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
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
		return new ResourceLocation(L2ItemSelector.MODID, "item");
	}

	@Override
	public boolean isClientActive(Player player) {
		if (Minecraft.getInstance().screen != null) return false;
		if (Screen.hasAltDown()) return false;
		IItemSelector sel = IItemSelector.getSelection(player);
		return sel != null;
	}

	@Override
	public void handleServerSetSelection(SetSelectedToServer packet, Player sender) {
		IItemSelector sel = IItemSelector.getSelection(sender);
		if (sel != null) {
			sel.swap(sender, packet.slot);
		}
	}

	@Override
	public boolean handleClientScroll(int diff, Player player) {
		IItemSelector sel = IItemSelector.getSelection(player);
		if (sel == null) return false;
		toServer(sel.move(-diff, player));
		return true;
	}

	@Override
	public void handleClientKey(L2Keys key, Player player) {
		IItemSelector sel = IItemSelector.getSelection(player);
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
	public Optional<WheelAdaptor<?>> get(@Nullable Player player, int wheelIndex, boolean main) {
		if (player == null) return Optional.empty();
		var sel = IItemSelector.getSelection(player);
		if (sel == null) return Optional.empty();
		if (sel instanceof WheelAdaptor.Provider pvd)
			return pvd.get(player, wheelIndex, main);
		return ClientHandler.get(sel, wheelIndex);
	}

	static class ClientHandler {
		private static final int MAX = 9;

		public static Optional<WheelAdaptor<?>> get(IItemSelector sel, int wheelIndex) {
			var list = sel.getDisplayList();
			int size = list.size();
			if (size <= 1) return Optional.empty();

			int pageCount = (size + MAX - 1) / MAX;
			if (size % MAX == 1 && pageCount > 1) pageCount--;

			if (wheelIndex < 0 || wheelIndex >= pageCount) return Optional.empty();

			int start = wheelIndex * MAX;
			int end = wheelIndex == pageCount - 1 && size % MAX == 1 && pageCount > 0
					? size : Math.min(start + MAX, size);
			return Optional.of(new Wheel(sel, start, end));
		}
	}

	public record Wheel(IItemSelector sel, int start, int end) implements ItemWheel<ItemWheelEntry> {

		@Override
		public void select(int index) {
			int globalIndex = start + index;
			L2ItemSelector.PACKET_HANDLER.toServer(new SetSelectedToServer(
					ItemSelectionListener.INSTANCE, globalIndex));
		}

		@Override
		public List<ItemWheelEntry> getWheelContent() {
			var src = sel.getDisplayList().subList(start, end);
			var ans = new ArrayList<ItemWheelEntry>();
			for (var e : src) {
				ans.add(new ItemWheelEntry(e));
			}
			return ans;
		}

		@Override
		public int getWheelSize() {
			return end - start;
		}

		@Override
		public ItemStack getItem(List<ItemWheelEntry> list, int index) {
			return index < 0 || index >= list.size() ? ItemStack.EMPTY : list.get(index).stack();
		}

		@Override
		public int getIndex(Player player) {
			int global = sel.getIndex(player);
			if (global >= start && global < end) return global - start;
			return -1;
		}

	}

}
