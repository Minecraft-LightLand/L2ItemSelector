package dev.xkmc.l2itemselector.wheel;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import net.minecraft.world.entity.player.Player;

public interface WheelKeyHandler {

	static WheelKeyHandler getDefault() {
		return DefaultKeyHandler.Switcher.INS;//TODO config
	}

	void handleClientKey(L2Keys k, Player player);

	boolean handleClientScroll(int diff, Player player);

	void leftClick(WheelAdaptor<?> wheel, Player player);

	void rightClick(WheelAdaptor<?> wheel, Player player);

	boolean onReleaseWithWheel(WheelAdaptor<?> wheel, Player player, boolean longPress);

	boolean shouldOpen(boolean longPress);

	void onReleaseWithoutWheel(WheelAdaptor<?> sel, Player player, boolean longPress);

	ArcCode getArcColor(int hover, boolean canSwitch);

}
