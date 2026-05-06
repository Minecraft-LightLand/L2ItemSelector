package dev.xkmc.l2itemselector.init.data;

import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.select.item.SimpleItemSelectConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class L2ISConfigGen extends ConfigDataProvider {

	public L2ISConfigGen(DataGenerator generator, CompletableFuture<HolderLookup.Provider> pvd, String name) {
		super(generator, pvd, name);
	}

	@Override
	public void add(Collector collector) {
		collector.add(L2ItemSelector.ITEM_SELECTOR, L2ItemSelector.loc("op_blocks"), new SimpleItemSelectConfig()
				.add(L2ItemSelector.loc("command_blocks"),
						Items.COMMAND_BLOCK, Items.CHAIN_COMMAND_BLOCK, Items.REPEATING_COMMAND_BLOCK)
				.add(L2ItemSelector.loc("structure_blocks"),
						Items.STRUCTURE_BLOCK, Items.JIGSAW)
		);
	}

}
