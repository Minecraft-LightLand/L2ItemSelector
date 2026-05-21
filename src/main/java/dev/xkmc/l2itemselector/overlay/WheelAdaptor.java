package dev.xkmc.l2itemselector.overlay;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import dev.xkmc.l2itemselector.wheel.DefaultKeyHandler;
import dev.xkmc.l2itemselector.wheel.InputHandler;
import dev.xkmc.l2itemselector.wheel.WheelKeyHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

public interface WheelAdaptor extends InputHandler {

	@Nullable
	static WheelAdaptor get(@Nullable Player player, int wheelIndex) {
		if (player == null) return null;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return null;
		if (!(sel.get() instanceof Provider pvd)) return null;
		return pvd.get(player, wheelIndex).orElse(null);
	}

	default WheelKeyHandler getInputHandler() {
		return DefaultKeyHandler.SWITCH;
	}

	@Override
	default boolean scrollBypassShift() {
		return true;
	}

	@Override
	default void handleClientKey(L2Keys k, Player player) {
		getInputHandler().handleClientKey(k, player);
	}

	@Override
	default boolean handleClientScroll(int diff, Player player) {
		return getInputHandler().handleClientScroll(diff, player);
	}

	List<Entry> getWheelContent();

	default int getWheelSize() {
		return getWheelContent().size();
	}

	int getIndex(Player player);

	void select(int index);

	default int getMouseSelect(Player player) {
		int n = getWheelSize();
		if (n <= 1) return -1;
		float da = (float) (Math.PI * 2 / n);
		float a0 = (float) (-Math.PI / 2);
		var win = Minecraft.getInstance().getWindow();
		int x0 = win.getGuiScaledWidth() / 2, y0 = win.getGuiScaledHeight() / 2;
		float r = Math.min(x0, y0) / 2f;
		float r1 = r * 0.66f;
		return ClientHandler.getMouseSelect(x0, y0, a0, da, n, r, r1);
	}

	default int render(GuiGraphics g, Player player) {
		//TODO rendering
		var list = WheelHandler.wheel.getWheelContent();
		int n = list.size();
		if (n <= 1) return -1;
		float da = (float) (Math.PI * 2 / n);
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0, y0) / 1.5f;
		float r0 = Math.max(40, r * 0.85f);
		float r1 = r * 0.5f;

		float dr0 = 0;
		float dr1 = 0;
		float s = 1.1f;

		int ma = ClientHandler.getMouseSelect(x0, y0, (float) (-Math.PI / 2), da, n, r, r1);
		if (ma >= 0) {
			WheelHandler.keyboardIndex = -1;
		} else if (WheelHandler.keyboardIndex >= 0) {
			ma = WheelHandler.keyboardIndex;
		}
		int selectedIndex = WheelHandler.wheel.getIndex(player);
		float a0 = (float) (-Math.PI / 2);
		WheelOverlay.fillFan(g, x0, y0, a0, (float) (Math.PI * 2), r * 1.25f, 0, 0, 0, 0x00000000, 0x60000000);
		float switchR = r * 1.25f;
		float sideWidth = x0 - switchR;
		boolean hasLeft = WheelAdaptor.get(player, WheelHandler.wheelIndex - 1) != null;
		boolean hasRight = WheelAdaptor.get(player, WheelHandler.wheelIndex + 1) != null;
		if (hasLeft) {
			WheelOverlay.drawSideGradient(g, x0, y0, true, sideWidth, 0x60000000, 0x00000000);
			g.flush();
		}
		if (hasRight) {
			WheelOverlay.drawSideGradient(g, x0, y0, false, sideWidth, 0x60000000, 0x00000000);
			g.flush();
		}
		for (int i = 0; i < n; i++) {
			float ai = a0 + da * i;
			if (ma == i) {
				WheelOverlay.fillFan(g, x0, y0, ai, da, r0, r1, dr1, 0, 0x00f4852b, 0x6ff4852b);
			} else if (selectedIndex == i) {
				WheelOverlay.fillFan(g, x0, y0, ai, da, r0, r1, dr0, 0, 0x00ffffff, 0x6fffffff);
			} else {
				WheelOverlay.fillFan(g, x0, y0, ai, da, r0, r1, dr0, 0, 0x00ffffff, 0x1fffffff);
			}
		}
		var mh = Minecraft.getInstance().mouseHandler;
		var win = Minecraft.getInstance().getWindow();
		float mx = (float) mh.xpos() * win.getGuiScaledWidth() / win.getScreenWidth() - x0;
		float my = (float) mh.ypos() * win.getGuiScaledHeight() / win.getScreenHeight() - y0;
		float distSq = mx * mx + my * my;
		g.flush();
		for (int i = 0; i < n; i++) {
			float ai = a0 + da * i;
			list.get(i).render(g, x0, y0, ai, r0, r, da, ma == i ? s : 1);
		}
		g.flush();
		boolean canSwitch = distSq > switchR * switchR && (
				mx < 0 && hasLeft || mx >= 0 && hasRight);
		if (distSq > switchR * switchR) {
			if (mx < 0 && hasLeft) {
				WheelOverlay.drawSideGradient(g, x0, y0, true, sideWidth, 0x800088ff, 0x000088ff);
			} else if (mx >= 0 && hasRight) {
				WheelOverlay.drawSideGradient(g, x0, y0, false, sideWidth, 0x800088ff, 0x000088ff);
			}
		}
		g.flush();
		WheelOverlay.fillFan(g, x0, y0, a0, (float) (Math.PI * 2), r1 + 1f, r1, 0, 0, 0x80ffffff, 0x80ffffff);
		float arcAngle = WheelHandler.keyboardIndex >= 0
				? a0 + da * WheelHandler.keyboardIndex
				: (float) Math.atan2(my, mx);
		int arcColor;
		boolean rightHeld = GLFW.glfwGetMouseButton(
				Minecraft.getInstance().getWindow().getWindow(),
				GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
		boolean leftHeld = GLFW.glfwGetMouseButton(
				Minecraft.getInstance().getWindow().getWindow(),
				GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		if (leftHeld && ma >= 0) {
			arcColor = 0xfff4852b;
		} else if (rightHeld && canSwitch) {
			arcColor = 0x800088ff;
		} else if (ma < 0 || rightHeld) {
			arcColor = 0x80ff4444;
		} else {
			arcColor = 0xffffffff;
		}
		WheelOverlay.fillFan(g, x0, y0, arcAngle, da, r1 - 1.5f, r1 - 4f, 0, 0, arcColor, arcColor);
		if (selectedIndex >= 0 && (ma < 0 || ma != selectedIndex)) {
			float selAngle = a0 + da * selectedIndex;
			WheelOverlay.fillFan(g, x0, y0, selAngle, da, r1 + 2.5f, r1, 0, 0, 0xffffffff, 0xffffffff);
		}
		if (ma >= 0) {
			float sliceAngle = a0 + da * ma;
			WheelOverlay.fillFan(g, x0, y0, sliceAngle, da, r1 + 4f, r1, 0, 0, 0xfff4852b, 0xfff4852b);
		}
		for (int i = 0; i < n; i++) {
			float a = a0 + da * i + da / 2;
			boolean active = ma >= 0 && (i == ma || i == (ma - 1 + n) % n);
			int innerColor = active ? 0xfff4852b : 0xffffffff;
			int outerColor = active ? 0x00f4852b : 0x00ffffff;
			WheelOverlay.drawSeparator(g, x0, y0, a, r1, r * 1.25f, 0.005f, 0.0025f, innerColor, outerColor);
		}
		g.flush();
		return ma;
	}

	class ClientHandler {

		static int getMouseSelect(float x0, float y0, float a0, float da, int n, float r, float r1) {
			var mh = Minecraft.getInstance().mouseHandler;
			var win = Minecraft.getInstance().getWindow();
			var mx = (float) mh.xpos() * win.getGuiScaledWidth() / win.getScreenWidth() - x0;
			var my = (float) mh.ypos() * win.getGuiScaledHeight() / win.getScreenHeight() - y0;
			int ma = (int) ((Math.atan2(my, mx) - a0 + Math.PI * 2 + da / 2) / da) % n;
			if (mx * mx + my * my < r1 * r1) ma = -1;
			return ma;
		}

	}

	interface Provider {

		Optional<WheelAdaptor> get(@Nullable Player player, int wheelIndex);

	}

	interface Entry {

		void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, float s);

	}

	interface ItemWheel extends WheelAdaptor {

		ItemStack getItem(int index);

		@Override
		default int render(GuiGraphics g, Player player) {
			int ma = WheelAdaptor.super.render(g, player);
			int index = ma >= 0 ? ma : getIndex(player);
			ItemStack stack = getItem(index);
			int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
			float r = Math.min(x0, y0) / 2f;
			float s = r * 0.02f;
			g.pose().pushPose();
			g.pose().translate(x0, y0, 0);
			g.pose().scale(s, s, s);
			g.renderItem(stack, -8, -16);
			g.renderItemDecorations(Minecraft.getInstance().font, stack, -8, -16);
			g.pose().popPose();

			var text = stack.getHoverName();
			var font = Minecraft.getInstance().font;
			int y = (int) (y0 + s * 3);
			for (var line : font.split(text, (int) r)) {
				g.drawString(font, line, x0 - font.width(line) / 2, y, 0xffffff, false);
				y += font.lineHeight + 1;
			}
			return ma;
		}

	}

}
