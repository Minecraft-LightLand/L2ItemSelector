package dev.xkmc.l2itemselector.overlay;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.SetSelectedToServer;
import dev.xkmc.l2itemselector.select.item.IItemSelector;
import dev.xkmc.l2itemselector.select.item.ItemSelectionListener;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class WheelHandler {

	public static IItemSelector.Holder wheel = null;

	public static void handleTick(@Nullable Player player) {
		if (player == null || Minecraft.getInstance().screen != null) {
			disableWheel(player);
			return;
		}
		if (wheel != null) {
			var sel = IItemSelector.getSelection(player);
			if (sel == null) {
				disableWheel(player);
				return;
			}
			if (!L2Keys.WHEEL.map.isDown()) {
				int index = getSel();
				if (index >= 0) {
					L2ItemSelector.PACKET_HANDLER.toServer(SetSelectedToServer.of(index,
							ItemSelectionListener.INSTANCE.getID()));
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
		var sel = IItemSelector.getSelection(player);
		if (sel == null || sel.getList().size() <= 1) return;
		wheel = sel;
		Minecraft.getInstance().mouseHandler.releaseMouse();
	}

	private static void disableWheel(@Nullable Player player) {
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
		var list = WheelHandler.wheel.getDisplayList();
		int sel = WheelHandler.wheel.getIndex(player);
		int n = list.size();
		if (n <= 1) return -1;
		float da = (float) (Math.PI * 2 / n);
		float a0 = (float) (-Math.PI / 2 - da * sel);
		var win = Minecraft.getInstance().getWindow();
		int x0 = win.getGuiScaledWidth() / 2, y0 = win.getGuiScaledHeight() / 2;
		float r = Math.min(x0, y0) / 2f;
		return getSel(x0, y0, a0, da, n, r);
	}

	public static int getSel(float x0, float y0, float a0, float da, int n, float r) {
		var mh = Minecraft.getInstance().mouseHandler;
		var win = Minecraft.getInstance().getWindow();
		var mx = (float) mh.xpos() * win.getGuiScaledWidth() / win.getScreenWidth() - x0;
		var my = (float) mh.ypos() * win.getGuiScaledHeight() / win.getScreenHeight() - y0;
		int ma = (int) ((Math.atan2(my, mx) - a0 + Math.PI * 2 + da / 2) / da) % n;
		if (mx * mx + my * my > r * r) ma = -1;
		if (mx * mx + my * my < r * r / 40 / 40) ma = -1;
		return ma;
	}

}
