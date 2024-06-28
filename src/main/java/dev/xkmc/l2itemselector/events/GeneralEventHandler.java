package dev.xkmc.l2itemselector.events;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.select.item.ItemConvertor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@EventBusSubscriber(modid = L2ItemSelector.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GeneralEventHandler {

	@SubscribeEvent
	public static void addItemToInventory(ItemEntityPickupEvent.Pre event) {
		ItemStack prev = event.getItemEntity().getItem();
		ItemStack next = ItemConvertor.convert(prev, event.getPlayer());
		if (next != prev) {
			event.getItemEntity().setItem(next);
			event.setCanPickup(TriState.FALSE);
		}
	}

}
