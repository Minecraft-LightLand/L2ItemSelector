package dev.xkmc.l2itemselector.wheel;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface WheelRegionHandler {

	static WheelRegionHandler getDefault() {
		return DefaultWheelRegionHandler.INS;
	}

	RegionCode buildRegionCode(int n, boolean hasLeft, boolean hasRight);

	void render(GuiGraphicsExtractor g, Player player, List<? extends WheelAdaptor.Entry> list, WheelContext ctx);

}
