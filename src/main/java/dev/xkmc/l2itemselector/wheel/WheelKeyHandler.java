package dev.xkmc.l2itemselector.wheel;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import net.minecraft.world.entity.player.Player;

public interface WheelKeyHandler {

	void handleClientKey(L2Keys k, Player player);

	boolean handleClientScroll(int diff, Player player);

	void leftClick(WheelAdaptor wheel, Player player);

	void rightClick(WheelAdaptor wheel, Player player);

	boolean onReleaseWithWheel(WheelAdaptor wheel, boolean longPress);

	boolean shouldOpen(boolean longPress);

	void onReleaseWithoutWheel(boolean longPress);

}
