package dev.xkmc.l2itemselector.overlay;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.InputEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class WheelHandler {

	public static WheelAdaptor wheel = null;
	public static int keyboardIndex = -1;
	public static int wheelIndex = 0;
	private static boolean suppress = false;
	public static long wheelPressTime = -1;
	private static final long LONG_PRESS_MS = 200;

	public static void handleTick(@Nullable Player player) {
		if (player == null || Minecraft.getInstance().screen != null) {
			wheelPressTime = -1;
			disableWheel(player);
			return;
		}
		boolean held = L2Keys.WHEEL.map.isDown();
		if (wheel != null) {
			wheel = WheelAdaptor.get(player, wheelIndex);
			if (wheel == null) {
				disableWheel(player);
				return;
			}
			if (!held) {
				int index = getEffectiveSelect();
				if (index >= 0) {
					wheel.onRelease(index);
				}
				disableWheel(player);
				return;
			}
			return;
		}
		if (held) {
			if (suppress) return;
			if (wheelPressTime < 0) {
				wheelPressTime = System.currentTimeMillis();
			}
			if (System.currentTimeMillis() - wheelPressTime > LONG_PRESS_MS) {
				var sel = WheelAdaptor.get(player, wheelIndex);
				if (sel != null && sel.getWheelContent().size() > 1) {
					wheel = sel;
					keyboardIndex = -1;
					Minecraft.getInstance().mouseHandler.releaseMouse();
				}
			}
		} else {
			if (wheelPressTime >= 0) {
				var sel = WheelAdaptor.get(player, wheelIndex);
				if (sel != null) {
					sel.shortPress(player);
				}
			}
			wheelPressTime = -1;
			disableWheel(player);
		}
	}

	public static void closeWheel(@Nullable Player player) {
		suppress = true;
		wheelPressTime = -1;
		keyboardIndex = -1;
		wheelIndex = 0;
		if (wheel == null) return;
		if (player != null && Minecraft.getInstance().screen == null) {
			Minecraft.getInstance().mouseHandler.grabMouse();
		}
		wheel = null;
	}

	private static void disableWheel(@Nullable Player player) {
		suppress = false;
		wheelPressTime = -1;
		keyboardIndex = -1;
		wheelIndex = 0;
		if (wheel == null) return;
		if (player != null && Minecraft.getInstance().screen == null) {
			Minecraft.getInstance().mouseHandler.grabMouse();
		}
		wheel = null;
	}

	public static int getSel() {
		var player = Minecraft.getInstance().player;
		if (player == null) return -1;
		if (WheelHandler.wheel == null) return -1;
		return WheelHandler.wheel.getMouseSelect(player);
	}

	public static int getEffectiveSelect() {
		int mouse = getSel();
		if (mouse >= 0) return mouse;
		if (keyboardIndex >= 0) return keyboardIndex;
		return -1;
	}

	public static boolean handleClick(InputEvent.MouseButton.Pre event) {
		if (wheel == null) return false;
		var player = Minecraft.getInstance().player;
		if (player != null) {
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if (event.getAction() == GLFW.GLFW_RELEASE) {
					int index = getEffectiveSelect();
					if (index >= 0) {
						wheel.select(index);
					}
				}
				event.setCanceled(true);
				return true;
			} else if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				if (event.getAction() == GLFW.GLFW_RELEASE) {
					var win = Minecraft.getInstance().getWindow();
					var mh = Minecraft.getInstance().mouseHandler;
					float mx = (float) mh.xpos() * win.getGuiScaledWidth() / win.getScreenWidth() - win.getGuiScaledWidth() / 2f;
					float my = (float) mh.ypos() * win.getGuiScaledHeight() / win.getScreenHeight() - win.getGuiScaledHeight() / 2f;
					float r = Math.min(win.getGuiScaledWidth() / 2f, win.getGuiScaledHeight() / 2f) / 1.5f * 1.25f;
					if (mx * mx + my * my <= r * r) {
						closeWheel(player);
					} else {
						int target = mx < 0 ? wheelIndex - 1 : wheelIndex + 1;
						if (WheelAdaptor.get(player, target) != null) {
							wheelIndex = target;
							keyboardIndex = -1;
						} else {
							closeWheel(player);
						}
					}
				}
				event.setCanceled(true);
				return true;
			}
		}
		return false;
	}

}
