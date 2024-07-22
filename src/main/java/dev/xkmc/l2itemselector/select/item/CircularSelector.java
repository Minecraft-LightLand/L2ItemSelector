package dev.xkmc.l2itemselector.select.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class CircularSelector<T> extends IItemSelector {

	public CircularSelector(ResourceLocation id) {
		super(id);
	}

	public abstract List<T> getAll(ItemStack stack);

	@Nullable
	public abstract T getSelected(ItemStack stack);

	@Override
	public int getIndex(Player player, ItemStack stack) {
		var list = getAll(stack);
		if (list.size() >= 9) return 4;
		var id = getSelected(stack);
		if (id == null) return 0;
		int index = list.indexOf(id);
		return Math.max(index, 0);
	}

	protected List<ItemStack> getListGeneric(ItemStack stack, Function<T, ItemStack> func) {
		var id = getSelected(stack);
		var ans = new ArrayList<ItemStack>();
		var list = getAll(stack);
		int index = id == null ? -1 : list.indexOf(id);
		if (list.size() < 9) {
			if (index < 0)
				ans.add(stack.copy());
			for (var e : list)
				ans.add(func.apply(e));
			return ans;
		}
		if (index < 0) {
			for (int i = 0; i < 4; i++)
				ans.add(func.apply(list.get(list.size() - 4 + i)));
			ans.add(stack.copy());
			for (int i = 0; i < 4; i++)
				ans.add(func.apply(list.get(i)));
			return ans;
		}
		for (int i = 0; i < 4; i++)
			ans.add(func.apply(list.get((index - 4 + i + list.size()) % list.size())));
		ans.add(func.apply(id));
		for (int i = 0; i < 4; i++)
			ans.add(func.apply(list.get((index + 1 + i) % list.size())));
		return ans;
	}

}
