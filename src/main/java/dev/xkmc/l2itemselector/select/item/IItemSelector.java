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

	private static final HashMap<ResourceLocation, IItemSelector> LIST = new HashMap<>();

	public static synchronized void register(IItemSelector sel) {
		LIST.put(sel.getID(), sel);
	}

	@Nullable
	public static IItemSelector getSelection(Player player) {
		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		if (main.is(L2ISTagGen.SELECTABLE)) {
			ItemSelector ans = SimpleItemSelectConfig.get(main);
			if (ans != null) return ans;
			for (var sel : LIST.values()) {
				if (sel.test(main))
					return sel;
			}
		}
		if (off.is(L2ISTagGen.SELECTABLE)) {
			ItemSelector ans = SimpleItemSelectConfig.get(off);
			if (ans != null) return ans;
			for (var sel : LIST.values()) {
				if (sel.test(off))
					return sel;
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
	public void swap(Player sender, int index) {
		index = (index + getList().size()) % getList().size();
		if (index < 0) return;
		if (test(sender.getMainHandItem())) {
			ItemStack stack = getList().get(index).copy();
			stack.setCount(sender.getMainHandItem().getCount());
			sender.setItemInHand(InteractionHand.MAIN_HAND, stack);
		} else if (test(sender.getOffhandItem())) {
			ItemStack stack = getList().get(index).copy();
			stack.setCount(sender.getOffhandItem().getCount());
			sender.setItemInHand(InteractionHand.OFF_HAND, stack);
		}
	}

	public abstract int getIndex(Player player);

	@OnlyIn(Dist.CLIENT)
	public int move(int i, Player player) {
		int index = getIndex(player);
		while (i < 0) i += getList().size();
		return (index + i) % getList().size();
	}

	public abstract List<ItemStack> getList();

	public List<ItemStack> getDisplayList() {
		List<ItemStack> ans = new ArrayList<>();
		for (ItemStack stack : getList()) {
			if (stack.getItem() instanceof CustomDisplaySelectItem item) {
				ans.add(item.getDisplay(id, stack));
			} else {
				ans.add(stack);
			}
		}
		return ans;
	}

	public ResourceLocation getID() {
		return id;
	}

}
