package dev.xkmc.l2itemselector.wheel;

import org.jetbrains.annotations.Nullable;

public record WheelContext(
		WheelRegionHandler region,
		int sel, int hover, RegionCode code,
		@Nullable WheelAdaptor<?> left,
		@Nullable WheelAdaptor<?> right,
		WheelKeyHandler keys
) {
}
