package dev.xkmc.l2itemselector.select.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface CustomDisplaySelectItem {

	ItemStack getDisplay(ResourceLocation id, ItemStack stack);

}
