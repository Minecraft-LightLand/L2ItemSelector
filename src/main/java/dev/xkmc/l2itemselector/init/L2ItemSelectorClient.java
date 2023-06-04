package dev.xkmc.l2itemselector.init;

import dev.xkmc.l2itemselector.select.item.ItemSelectionOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2ItemSelector.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2ItemSelectorClient {

	@SubscribeEvent
	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "tool_select", ItemSelectionOverlay.INSTANCE);
	}

}
