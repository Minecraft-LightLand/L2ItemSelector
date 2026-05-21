package dev.xkmc.l2itemselector.wheel;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import dev.xkmc.l2itemselector.overlay.WheelHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public abstract class DefaultKeyHandler implements WheelKeyHandler {

	public static final DefaultKeyHandler DEF = new Fast();
	public static final DefaultKeyHandler SWITCH = new Switcher();

	@Override
	public void handleClientKey(L2Keys k, Player player) {
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
	}

	@Override
	public boolean handleClientScroll(int diff, Player player) {
		int idx = WheelHandler.wheel.getIndex(player);
		int current = WheelHandler.keyboardIndex >= 0 ? WheelHandler.keyboardIndex : Math.max(0, idx);
		int total = WheelHandler.wheel.getWheelSize();
		WheelHandler.keyboardIndex = ((current - diff) % total + total) % total;
		return true;
	}

	@Override
	public void leftClick(WheelAdaptor wheel, Player player) {
		int index = getEffectiveSelect();
		if (index >= 0) {
			wheel.select(index);
		}
	}

	@Override
	public void rightClick(WheelAdaptor wheel, Player player) {
		var win = Minecraft.getInstance().getWindow();
		var mh = Minecraft.getInstance().mouseHandler;
		float mx = (float) mh.xpos() * win.getGuiScaledWidth() / win.getScreenWidth() - win.getGuiScaledWidth() / 2f;
		float my = (float) mh.ypos() * win.getGuiScaledHeight() / win.getScreenHeight() - win.getGuiScaledHeight() / 2f;
		float r = Math.min(win.getGuiScaledWidth() / 2f, win.getGuiScaledHeight() / 2f) / 1.5f * 1.25f;
		if (mx * mx + my * my <= r * r) {
			WheelHandler.disableWheel(player);
		} else {
			int target = mx < 0 ? WheelHandler.wheelIndex - 1 : WheelHandler.wheelIndex + 1;
			if (WheelAdaptor.get(player, target) != null) {
				WheelHandler.wheelIndex = target;
				WheelHandler.keyboardIndex = -1;
			} else {
				WheelHandler.disableWheel(player);
			}
		}
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
		if (WheelHandler.keyboardIndex >= 0) return WheelHandler.keyboardIndex;
		return -1;
	}

	public static class Fast extends DefaultKeyHandler {

		@Override
		public boolean shouldOpen(boolean longPress) {
			return true;
		}

		@Override
		public boolean onReleaseWithWheel(WheelAdaptor wheel, boolean longPress) {
			if (longPress) {
				int index = getEffectiveSelect();
				if (index >= 0) wheel.select(index);
				return true;
			}
			return false;
		}

		@Override
		public void onReleaseWithoutWheel(boolean longPress) {

		}

	}

	public static class Switcher extends DefaultKeyHandler {

		@Override
		public boolean shouldOpen(boolean longPress) {
			return longPress;
		}

		@Override
		public boolean onReleaseWithWheel(WheelAdaptor wheel, boolean longPress) {
			if (longPress) {
				int index = getEffectiveSelect();
				if (index >= 0) wheel.select(index);
				return true;
			}
			return true;
		}

		@Override
		public void onReleaseWithoutWheel(boolean longPress) {
			// TODO
		}

	}

}
