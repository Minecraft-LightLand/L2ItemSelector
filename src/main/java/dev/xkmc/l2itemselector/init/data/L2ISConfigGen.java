package dev.xkmc.l2itemselector.init.data;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.select.item.SimpleItemSelectConfig;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;

public class L2ISConfigGen extends ConfigDataProvider {

	public L2ISConfigGen(DataGenerator generator, String name) {
		super(generator, name);
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
