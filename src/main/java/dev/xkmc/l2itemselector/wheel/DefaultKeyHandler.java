package dev.xkmc.l2itemselector.wheel;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import static dev.xkmc.l2itemselector.wheel.ArcCode.*;

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
					if (WheelHandler.wheel != null) WheelHandler.wheel.onSwitchedAway();
				}
			}
			case RIGHT -> {
				int target = WheelHandler.wheelIndex + 1;
				if (WheelAdaptor.get(player, target) != null) {
					WheelHandler.wheelIndex = target;
					WheelHandler.keyboardIndex = -1;
					if (WheelHandler.wheel != null) WheelHandler.wheel.onSwitchedAway();
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

	protected void execute(WheelAdaptor<?> wheel, Player player, ActionCode action, RegionCode code) {
		switch (action) {
			case SWITCH -> {
				WheelHandler.wheelIndex += code.switcher();
				WheelHandler.keyboardIndex = -1;
				wheel.onSwitchedAway();
			}
			case SEL_CLOSE -> {
				wheel.select(code.sel());
				WheelHandler.disableWheel(player);
			}
			case SELECT -> wheel.select(code.sel());
			case CLOSE -> WheelHandler.disableWheel(player);
		}
	}

	@Override
	public void leftClick(WheelAdaptor<?> wheel, Player player) {
		var ctx = resolveDeadZone(wheel.getContext(player, wheel.getWheelSize()));
		execute(wheel, player, getAction(ctx, ActionInput.LEFT), ctx.code());
	}

	@Override
	public void rightClick(WheelAdaptor<?> wheel, Player player) {
		var ctx = resolveDeadZone(wheel.getContext(player, wheel.getWheelSize()));
		execute(wheel, player, getAction(ctx, ActionInput.RIGHT), ctx.code());
	}

	private WheelContext resolveDeadZone(WheelContext ctx) {
		var code = ctx.code();
		if (code.sel() >= 0) return ctx;
		int target = ctx.hover() >= 0 ? ctx.hover() : ctx.sel();
		if (target >= 0) {
			code = new RegionCode(target, code.outside(), code.switcher());
			return new WheelContext(ctx.region(), ctx.sel(), ctx.hover(), code, ctx.left(), ctx.right(), ctx.keys());
		}
		return ctx;
	}

	@Override
	public ArcCode getArcColor(WheelContext ctx) {
		boolean rightHeld = GLFW.glfwGetMouseButton(
				Minecraft.getInstance().getWindow().getWindow(),
				GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
		boolean leftHeld = GLFW.glfwGetMouseButton(
				Minecraft.getInstance().getWindow().getWindow(),
				GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		var action = getAction(ctx, leftHeld ? ActionInput.LEFT : rightHeld ? ActionInput.RIGHT : ActionInput.RELEASE);
		return switch (action) {
			case SWITCH -> SWITCH;
			case SELECT, SEL_CLOSE -> leftHeld || rightHeld ? SELECT : NONE;
			case CLOSE -> CLOSE;
			default -> NONE;
		};
	}

	public abstract ActionCode getAction(WheelContext ctx, ActionInput input);

	public enum ActionInput {
		RELEASE, LEFT, RIGHT
	}

	public enum ActionCode {
		SWITCH, SELECT, CLOSE, SEL_CLOSE, NONE;

		public ActionCode closeIf(boolean close) {
			if (close) {
				if (this == SELECT) return SEL_CLOSE;
				if (this == NONE) return CLOSE;
			}
			return this;
		}
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
		public ActionCode getAction(WheelContext ctx, ActionInput input) {
			return switch (input) {
				case RELEASE -> ctx.hover() >= 0 ? ActionCode.SELECT : ActionCode.CLOSE;
				case LEFT -> ctx.code().switcher() != 0 ? ActionCode.SWITCH :
						(ctx.code().sel() >= 0 ? ActionCode.SELECT : ActionCode.NONE).closeIf(!WheelHandler.held);
				case RIGHT -> ctx.code().sel() >= 0 && !ctx.code().outside() ? ActionCode.SEL_CLOSE : ActionCode.CLOSE;
			};
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
		public ActionCode getAction(WheelContext ctx, ActionInput input) {
			return switch (input) {
				case RELEASE -> ctx.hover() >= 0 ? ActionCode.SELECT : ActionCode.CLOSE;
				case LEFT -> ctx.code().sel() >= 0 ? ActionCode.SELECT : ActionCode.NONE;
				case RIGHT -> ctx.code().switcher() != 0 ? ActionCode.SWITCH : ActionCode.CLOSE;
			};
		}

	}

}
