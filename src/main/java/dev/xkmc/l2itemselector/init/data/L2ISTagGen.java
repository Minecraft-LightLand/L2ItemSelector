package dev.xkmc.l2itemselector.init.data;

import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class L2ISTagGen {

	public static final TagKey<Item> SELECTABLE = ItemTags.create(new ResourceLocation(L2ItemSelector.MODID, "selectable"));

	public static void genItemTags(RegistrateItemTagsProvider pvd) {
	}

}
