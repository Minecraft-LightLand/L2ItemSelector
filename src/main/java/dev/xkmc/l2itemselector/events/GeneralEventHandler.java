package dev.xkmc.l2itemselector.events;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.select.item.ItemConvertor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = L2ItemSelector.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GeneralEventHandler {

	@SubscribeEvent
	public static void addItemToInventory(EntityItemPickupEvent event) {
		ItemStack prev = event.getItem().getItem();
		ItemStack next = ItemConvertor.convert(prev, event.getEntity());
		if (next != prev) {
			event.getItem().setItem(next);
			event.setCanceled(true);
		}
	}

}
