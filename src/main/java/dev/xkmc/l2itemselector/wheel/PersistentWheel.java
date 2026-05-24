package dev.xkmc.l2itemselector.wheel;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface PersistentWheel<T extends WheelAdaptor.Entry> extends WheelAdaptor<T> {

	boolean isValid(Player player);

	@Nullable
	default WheelAdaptor<?> getAtIndex(Player player, int index, boolean main) {
		return index == 0 && isValid(player) ? this : null;
	}

}
