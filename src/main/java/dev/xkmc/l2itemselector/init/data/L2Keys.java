package dev.xkmc.l2itemselector.init.data;

import com.mojang.blaze3d.platform.InputConstants;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public enum L2Keys {
	UP("key.l2mods.up", "Up", GLFW.GLFW_KEY_UP),
	DOWN("key.l2mods.down", "Down", GLFW.GLFW_KEY_DOWN),
	LEFT("key.l2mods.left", "Left", GLFW.GLFW_KEY_LEFT),
	RIGHT("key.l2mods.right", "Right", GLFW.GLFW_KEY_RIGHT),
	SWAP("key.l2mods.swap", "Swap", GLFW.GLFW_KEY_R),
	WHEEL("key.l2mods.wheel", "Wheel", GLFW.GLFW_KEY_LEFT_ALT),
	SHIFT("key.l2mods.shift", "Hold Selection", GLFW.GLFW_KEY_LEFT_SHIFT),
	ALT("key.l2mods.alt", "Alternative Selector", GLFW.GLFW_KEY_LEFT_ALT);

	public static boolean hasShiftDown() {
		return SHIFT.map.isDown();
	}

	public static boolean hasAltDown() {
		return ALT.map.isDown();
	}

	public static boolean hasCtrlDown() {
		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 341)
				|| InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 345);
	}

	public final String id, def;
	public final int key;
	public final KeyMapping map;

	L2Keys(String id, String def, int key) {
		this.id = id;
		this.def = def;
		this.key = key;
		this.map = new KeyMapping(id, key, new KeyMapping.Category(L2ItemSelector.loc("l2mods")));
	}
}
