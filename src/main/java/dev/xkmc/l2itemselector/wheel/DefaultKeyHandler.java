package dev.xkmc.l2itemselector.wheel;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import static dev.xkmc.l2itemselector.wheel.ArcCode.CLOSE;

public abstract class DefaultKeyHandler implements WheelKeyHandler {

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

	protected int getSelect(WheelAdaptor<?> wheel, Player player) {
		var code = wheel.getMouseSelect(player);
		if (code.sel() < 0) return WheelHandler.keyboardIndex;
		return code.sel();
	}

	public ActionCode getAction(WheelContext ctx, boolean canSwitch, ActionInput input) {
		return ActionCode.NONE;
	}

	public enum ActionInput {
		RELEASE, LEFT, RIGHT
	}

	public enum ActionCode {
		SWITCH, SELECT, CLOSE, SEL_CLOSE, NONE
	}

	public static class Fast extends DefaultKeyHandler {

		public static final WheelKeyHandler INS = new DefaultKeyHandler.Fast();

		@Override
		public boolean shouldOpen(boolean longPress) {
			return true;
		}

		@Override
		public boolean onReleaseWithWheel(WheelAdaptor<?> wheel, Player player, boolean longPress, boolean heldWithWheel) {
			if (longPress) {
				int index = getSelect(wheel, player);
				if (index >= 0) wheel.select(index);
				return true;
			}
			return heldWithWheel;
		}

		@Override
		public void onReleaseWithoutWheel(WheelAdaptor<?> sel, Player player, boolean longPress) {

		}

		@Override
		public void leftClick(WheelAdaptor<?> wheel, Player player) {
			var code = wheel.getMouseSelect(player);
			if (code.switcher() != 0) {
				WheelHandler.wheelIndex += code.switcher();
				WheelHandler.keyboardIndex = -1;
			} else if (code.sel() >= 0) {
				wheel.select(code.sel());
			}
		}

		@Override
		public void rightClick(WheelAdaptor<?> wheel, Player player) {
			var code = wheel.getMouseSelect(player);
			if (code.sel() >= 0 && !code.outside()) {
				wheel.select(code.sel());
			}
			WheelHandler.disableWheel(player);
		}

		@Override
		public ArcCode getArcColor(WheelContext ctx, boolean canSwitch) {
			boolean rightHeld = GLFW.glfwGetMouseButton(
					Minecraft.getInstance().getWindow().getWindow(),
					GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
			boolean leftHeld = GLFW.glfwGetMouseButton(
					Minecraft.getInstance().getWindow().getWindow(),
					GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
			if (leftHeld && canSwitch) return ArcCode.SWITCH;
			if ((rightHeld && !ctx.code().outside() || leftHeld) && ctx.code().sel() >= 0) return ArcCode.SELECT;
			if (ctx.hover() < 0 || rightHeld) return CLOSE;
			else return ArcCode.NONE;
		}

	}

	public static class Switcher extends DefaultKeyHandler {

		public static final WheelKeyHandler INS = new DefaultKeyHandler.Switcher();

		@Override
		public boolean shouldOpen(boolean longPress) {
			return longPress;
		}

		@Override
		public boolean onReleaseWithWheel(WheelAdaptor<?> wheel, Player player, boolean longPress, boolean heldWithWheel) {
			if (longPress) {
				int index = getSelect(wheel, player);
				if (index >= 0) wheel.select(index);
				return true;
			}
			return true;
		}

		@Override
		public void onReleaseWithoutWheel(WheelAdaptor<?> wheel, Player player, boolean longPress) {
			int index = wheel.getIndex(player);
			if (index >= 0) wheel.select(index);
		}

		@Override
		public void leftClick(WheelAdaptor<?> wheel, Player player) {
			int index = wheel.getMouseSelect(player).sel();
			if (index >= 0) {
				wheel.select(index);
			}
		}

		@Override
		public void rightClick(WheelAdaptor<?> wheel, Player player) {
			var code = wheel.getMouseSelect(player);
			if (code.switcher() != 0) {
				WheelHandler.wheelIndex += code.switcher();
				WheelHandler.keyboardIndex = -1;
			} else WheelHandler.disableWheel(player);
		}

		@Override
		public ArcCode getArcColor(WheelContext ctx, boolean canSwitch) {
			int hover = ctx.hover();
			boolean rightHeld = GLFW.glfwGetMouseButton(
					Minecraft.getInstance().getWindow().getWindow(),
					GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
			boolean leftHeld = GLFW.glfwGetMouseButton(
					Minecraft.getInstance().getWindow().getWindow(),
					GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
			if (leftHeld && hover >= 0) return ArcCode.SELECT;
			else if (rightHeld && canSwitch) return ArcCode.SWITCH;
			else if (hover < 0 || rightHeld) return CLOSE;
			else return ArcCode.NONE;
		}


	}

}
