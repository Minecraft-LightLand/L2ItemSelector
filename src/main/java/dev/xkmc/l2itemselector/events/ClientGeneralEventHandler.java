package dev.xkmc.l2itemselector.events;

import com.mojang.blaze3d.platform.InputConstants;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2library.init.events.FineScrollEvent;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2ItemSelector.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientGeneralEventHandler {

	@SubscribeEvent
	public static void keyEvent(InputEvent.Key event) {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return;
		for (L2Keys k : L2Keys.values()) {
			if (k.map.getKey().getValue() == event.getKey() && event.getAction() == InputConstants.PRESS) {
				sel.get().handleClientKey(k, player);
				return;
			}
		}
		for (int i = 0; i < 9; i++) {
			if (sel.get().handleClientNumericKey(i, Minecraft.getInstance().options.keyHotbarSlots[i]::consumeClick)) {
				return;
			}
		}

	}

	@SubscribeEvent
	public static void fineScroll(FineScrollEvent event) {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return;
		if (!sel.get().scrollBypassShift() &&
				L2LibraryConfig.CLIENT.selectionScrollRequireShift.get() &&
				!player.isShiftKeyDown()) return;
		if (sel.get().handleClientScroll(event.diff, player)) {
			event.setCanceled(true);
		}
	}

}
