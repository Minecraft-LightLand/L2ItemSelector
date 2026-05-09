package dev.xkmc.l2itemselector.overlay;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.SetSelectedToServer;
import dev.xkmc.l2itemselector.select.item.ItemSelectionListener;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.InputEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class WheelHandler {

	@Nullable
	public static WheelAdaptor wheel = null;
	public static int wheelIndex = 0;
	private static boolean suppress = false;

	public static void handleTick(@Nullable Player player) {
		if (player == null || Minecraft.getInstance().screen != null) {
			disableWheel(player);
			return;
		}
		if (wheel != null) {
			wheel = WheelAdaptor.get(player, wheelIndex);
			if (wheel == null) {
				disableWheel(player);
				return;
			}
			if (!L2Keys.WHEEL.map.isDown()) {
				int index = getSel();
				if (index >= 0) {
					wheel.select(index);
				}
				disableWheel(player);
				return;
			}
			return;
		}
		if (!L2Keys.WHEEL.map.isDown()) {
			disableWheel(player);
			return;
		}
		if (suppress) return;
		var sel = WheelAdaptor.get(player, wheelIndex);
		if (sel == null || sel.getWheelContent().size() <= 1) return;
		wheel = sel;
		Minecraft.getInstance().mouseHandler.releaseMouse();
	}

	private static void disableWheel(@Nullable Player player) {
		wheelIndex = 0;
		suppress = false;
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

	public static boolean handleClick(InputEvent.MouseButton.Pre event) {
		if (wheel == null) return false;
		var player = Minecraft.getInstance().player;
		if (player != null) {

			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if (event.getAction() == GLFW.GLFW_RELEASE) {
					int index = getSel();
					if (index >= 0) {
						L2ItemSelector.PACKET_HANDLER.toServer(SetSelectedToServer.of(index,
								ItemSelectionListener.INSTANCE.getID()));
					}
					disableWheel(player);
					suppress = true;
				}
				event.setCanceled(true);
				return true;
			} else if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				if (event.getAction() == GLFW.GLFW_RELEASE) {
					wheelIndex++;
				}
				event.setCanceled(true);
				return true;
			}

		}
		return false;
	}

}
