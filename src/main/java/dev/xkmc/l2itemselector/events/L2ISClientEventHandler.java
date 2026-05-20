package dev.xkmc.l2itemselector.events;

import com.mojang.blaze3d.platform.InputConstants;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import dev.xkmc.l2itemselector.overlay.WheelHandler;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2ItemSelector.MODID, bus = EventBusSubscriber.Bus.GAME)
public class L2ISClientEventHandler {

	@SubscribeEvent
	public static void clientTick(ClientTickEvent.Pre event) {
		if (Minecraft.getInstance().level == null) {
			WheelHandler.handleTick(null);
			return;
		}
		Player player = Minecraft.getInstance().player;
		WheelHandler.handleTick(player);
	}

	@SubscribeEvent
	public static void inputEvent(GenericKeyEvent event) {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return;

		for (L2Keys k : L2Keys.values()) {
			if (event.test(k.map.getKey()) && event.getAction() == InputConstants.PRESS) {
				if (WheelHandler.wheel != null) {
					switch (k) {
						case UP -> {
							int idx = WheelHandler.wheel.getIndex(player);
							int current = WheelHandler.keyboardIndex >= 0 ? WheelHandler.keyboardIndex : Math.max(0, idx);
							int total = WheelHandler.wheel.getWheelSize();
							WheelHandler.keyboardIndex = ((current - 1) % total + total) % total;
						}
						case DOWN -> {
							int idx = WheelHandler.wheel.getIndex(player);
							int current = WheelHandler.keyboardIndex >= 0 ? WheelHandler.keyboardIndex : Math.max(0, idx);
							int total = WheelHandler.wheel.getWheelSize();
							WheelHandler.keyboardIndex = ((current + 1) % total + total) % total;
						}
						case LEFT -> {
							int target = WheelHandler.wheelIndex - 1;
							if (WheelAdaptor.get(player, target) != null) {
								WheelHandler.wheelIndex = target;
								WheelHandler.keyboardIndex = -1;
							}
						}
						case RIGHT -> {
							int target = WheelHandler.wheelIndex + 1;
							if (WheelAdaptor.get(player, target) != null) {
								WheelHandler.wheelIndex = target;
								WheelHandler.keyboardIndex = -1;
							}
						}
					}
					return;
				}

				sel.get().handleClientKey(k, player);
				return;
			}
		}
	}

	@SubscribeEvent
	public static void mouseEvent(InputEvent.MouseButton.Pre event) {
		if (WheelHandler.handleClick(event)) return;
		NeoForge.EVENT_BUS.post(new GenericKeyEvent(
				e -> e.getType() == InputConstants.Type.MOUSE && e.getValue() == event.getButton(),
				event.getAction()));
	}

	@SubscribeEvent
	public static void keyEvent(InputEvent.Key event) {
		NeoForge.EVENT_BUS.post(new GenericKeyEvent(
				e -> e.getType() != InputConstants.Type.MOUSE && e.getValue() == event.getKey(),
				event.getAction()));
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
		double d0 = event.getScrollDeltaY();
		if (d0 == 0) return;
		scroll += d0;
		int i = (int) scroll;
		scroll -= i;
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return;

		boolean bypassShift = WheelHandler.wheel != null;
		if (!bypassShift &&
				L2ISConfig.CLIENT.selectionScrollRequireShift.get() &&
				!sel.get().isHoldKeyDown(player)) {
			return;
		}

		if (WheelHandler.wheel != null && i != 0) {
			int idx = WheelHandler.wheel.getIndex(player);
			int current = WheelHandler.keyboardIndex >= 0 ? WheelHandler.keyboardIndex : Math.max(0, idx);
			int total = WheelHandler.wheel.getWheelSize();
			WheelHandler.keyboardIndex = ((current - i) % total + total) % total;
			event.setCanceled(true);
			return;
		}

		if (sel.get().handleClientScroll(i, d0, player)) {
			event.setCanceled(true);
		}
	}
}