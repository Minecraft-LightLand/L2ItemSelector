package dev.xkmc.l2itemselector.select.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public interface CustomDisplaySelectItem {

	ItemStack getDisplay(Identifier id, ItemStack stack);

}
