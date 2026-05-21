package dev.xkmc.l2itemselector.wheel;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.InputEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class WheelHandler {

	private static final long LONG_PRESS_MS = 200;

	private static long wheelPressTime = -1;
	private static boolean held = false;
	private static boolean suppress = false;

	public static int wheelIndex = 0;
	public static WheelAdaptor<?> wheel = null;

	public static int keyboardIndex = -1; //TODO move to somewhere else

	public static void handleTick(@Nullable Player player) {
		boolean holding = L2Keys.WHEEL.map.isDown();
		if (!holding) {
			wheelPressTime = -1;
			suppress = false;
		}
		else if (!held) wheelPressTime = System.currentTimeMillis();
		if (player == null || Minecraft.getInstance().screen != null) disableWheel(player);
		else handleTickImpl(player, holding);
		held = holding;
	}

	public static void handleTickImpl(Player player, boolean holding) {
		long current = System.currentTimeMillis();
		boolean longPress = current - wheelPressTime > LONG_PRESS_MS;
		if (wheel != null) { // wheel present
			var next = WheelAdaptor.get(player, wheelIndex);
			if (next == null || !next.equals(wheel)) {
				keyboardIndex = -1;
			}
			wheel = next;
			if (wheel == null) { // wheel invalid
				disableWheel(player);
			} else if (held && !holding) { // stop holding
				if (wheel.getInputHandler().onReleaseWithWheel(wheel, player, longPress))
					disableWheel(player);
			}
			return;
		}
		// wheel not present
		if (!holding) {
			if (held) {
				var sel = WheelAdaptor.get(player, wheelIndex);
				if (sel != null) {
					sel.getInputHandler().onReleaseWithoutWheel(sel, player, longPress);
				}
			}
			return;
		}
		if (suppress) return;
		// open wheel
		var sel = WheelAdaptor.get(player, wheelIndex);
		if (sel == null || sel.getWheelContent().size() <= 1 || !sel.getInputHandler().shouldOpen(longPress)) return;
		wheel = sel;
		keyboardIndex = -1;
		Minecraft.getInstance().mouseHandler.releaseMouse();
	}

	public static void disableWheel(@Nullable Player player) {
		suppress = true;
		keyboardIndex = -1;
		wheelIndex = 0;
		if (wheel == null) return;
		if (player != null && Minecraft.getInstance().screen == null) {
			Minecraft.getInstance().mouseHandler.grabMouse();
		}
		wheel = null;
	}

	public static boolean handleClick(InputEvent.MouseButton.Pre event) {
		if (wheel == null) return false;
		var player = Minecraft.getInstance().player;
		if (player == null) return false;
		if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (event.getAction() == GLFW.GLFW_RELEASE) {
				wheel.getInputHandler().leftClick(wheel, player);
			}
			event.setCanceled(true);
			return true;
		} else if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			if (event.getAction() == GLFW.GLFW_RELEASE) {
				wheel.getInputHandler().rightClick(wheel, player);
			}
			event.setCanceled(true);
			return true;
		}
		return false;
	}

}
