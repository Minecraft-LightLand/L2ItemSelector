package dev.xkmc.l2itemselector.init.data;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

public enum L2Keys {
	UP("key.l2mods.up", "Up", GLFW.GLFW_KEY_UP),
	DOWN("key.l2mods.down", "Down", GLFW.GLFW_KEY_DOWN),
	LEFT("key.l2mods.left", "Left", GLFW.GLFW_KEY_LEFT),
	RIGHT("key.l2mods.right", "Right", GLFW.GLFW_KEY_RIGHT),
	SWAP("key.l2mods.swap", "Swap", GLFW.GLFW_KEY_R),
	SHIFT("keu.l2mods.shift", "Hold Selection", GLFW.GLFW_KEY_LEFT_SHIFT),
	ALT("keu.l2mods.alt", "Alternative Selector", GLFW.GLFW_KEY_LEFT_ALT);

	public static boolean hasShiftDown() {
		return SHIFT.map.isDown();
	}

	public static boolean hasAltDown() {
		return ALT.map.isDown();
	}

	public static boolean hasCtrlDown() {
		return Screen.hasControlDown();
	}

	public final String id, def;
	public final int key;
	public final KeyMapping map;

	L2Keys(String id, String def, int key) {
		this.id = id;
		this.def = def;
		this.key = key;
		this.map = new KeyMapping(id, key, "key.categories.l2mods");
	}
}
