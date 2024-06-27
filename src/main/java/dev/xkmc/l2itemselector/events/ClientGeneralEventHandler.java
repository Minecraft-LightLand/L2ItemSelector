package dev.xkmc.l2itemselector.events;

import com.mojang.blaze3d.platform.InputConstants;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import dev.xkmc.l2library.init.L2LibraryConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2ItemSelector.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ClientGeneralEventHandler {

	@SubscribeEvent
	public static void inputEvent(GenericKeyEvent event) {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return;
		for (L2Keys k : L2Keys.values()) {
			if (event.test(k.map.getKey()) &&
					event.getAction() == InputConstants.PRESS) {
				sel.get().handleClientKey(k, player);
				return;
			}
		}
	}

	@SubscribeEvent
	public static void mouseEvent(InputEvent.MouseButton.Pre event) {
		NeoForge.EVENT_BUS.post(new GenericKeyEvent(e -> e.getType() == InputConstants.Type.MOUSE && e.getValue() == event.getButton(), event.getAction()));
	}

	@SubscribeEvent
	public static void keyEvent(InputEvent.Key event) {
		NeoForge.EVENT_BUS.post(new GenericKeyEvent(e -> e.getType() != InputConstants.Type.MOUSE && e.getValue() == event.getKey(), event.getAction()));
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return;
		for (int i = 0; i < 9; i++) {
			if (sel.get().handleClientNumericKey(i, Minecraft.getInstance().options.keyHotbarSlots[i]::consumeClick)) {
				return;
			}
		}

	}

	private static double scroll;

	@SubscribeEvent
	public static void scrollEvent(InputEvent.MouseScrollingEvent event) {
		double d0 = event.getScrollDelta();
		scroll += d0;
		int i = (int) scroll;
		if (i == 0) {
			return;
		}
		scroll -= i;
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return;
		if (!sel.get().scrollBypassShift() &&
				L2LibraryConfig.CLIENT.selectionScrollRequireShift.get() &&
				!player.isShiftKeyDown()) return;
		if (sel.get().handleClientScroll((int) Math.signum(i), player)) {
			event.setCanceled(true);
		}
	}

}
