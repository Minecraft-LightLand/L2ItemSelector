package dev.xkmc.l2itemselector.select.item;

import dev.xkmc.l2core.util.ServerOnly;
import dev.xkmc.l2itemselector.init.data.L2ISTagGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class IItemSelector {

	public record Holder(ItemStack stack, IItemSelector selector) {

		public int move(int off, Player player) {
			return selector.move(off, player, stack);
		}

		public void swap(Player player, int index) {
			selector.swap(player, index, stack);
		}

		public int getIndex(Player player) {
			return selector.getIndex(player, stack);
		}

		public List<ItemStack> getDisplayList() {
			return selector.getDisplayList(stack);
		}

		public List<ItemStack> getList() {
			return selector.getList(stack);
		}

		public int getSelHash() {
			return selector.getSelHash(stack);
		}
	}

	private static final HashMap<ResourceLocation, IItemSelector> LIST = new HashMap<>();

	public static synchronized void register(IItemSelector sel) {
		LIST.put(sel.getID(), sel);
	}

	@Nullable
	public static Holder getSelection(Player player) {
		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		if (main.is(L2ISTagGen.SELECTABLE)) {
			ItemSelector ans = SimpleItemSelectConfig.get(main);
			if (ans != null) return new Holder(main, ans);
			for (var sel : LIST.values()) {
				if (sel.test(main))
					return new Holder(main, sel);
			}
		}
		if (off.is(L2ISTagGen.SELECTABLE)) {
			ItemSelector ans = SimpleItemSelectConfig.get(off);
			if (ans != null) return new Holder(off, ans);
			for (var sel : LIST.values()) {
				if (sel.test(off))
					return new Holder(off, sel);
			}
		}
		return null;
	}

	private final ResourceLocation id;

	public IItemSelector(ResourceLocation id) {
		this.id = id;
	}

	public abstract boolean test(ItemStack stack);

	@ServerOnly
	public void swap(Player sender, int index, ItemStack stack) {
		var list = getList(stack);
		index = (index + list.size()) % list.size();
		if (index < 0) return;
		if (test(sender.getMainHandItem())) {
			ItemStack e = list.get(index).copy();
			e.setCount(sender.getMainHandItem().getCount());
			sender.setItemInHand(InteractionHand.MAIN_HAND, e);
		} else if (test(sender.getOffhandItem())) {
			ItemStack e = list.get(index).copy();
			e.setCount(sender.getOffhandItem().getCount());
			sender.setItemInHand(InteractionHand.OFF_HAND, e);
		}
	}

	public abstract int getIndex(Player player, ItemStack stack);

	@OnlyIn(Dist.CLIENT)
	public int move(int i, Player player, ItemStack stack) {
		var list = getList(stack);
		int index = getIndex(player, stack);
		while (i < 0) i += list.size();
		return (index + i) % list.size();
	}

	public abstract List<ItemStack> getList(ItemStack stack);

	public List<ItemStack> getDisplayList(ItemStack stack) {
		List<ItemStack> ans = new ArrayList<>();
		for (ItemStack e : getList(stack)) {
			if (e.getItem() instanceof CustomDisplaySelectItem item) {
				ans.add(item.getDisplay(id, e));
			} else {
				ans.add(e);
			}
		}
		return ans;
	}

	public ResourceLocation getID() {
		return id;
	}

	public int getSelHash(ItemStack stack) {
		return 0;
	}

}
