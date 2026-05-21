package dev.xkmc.l2itemselector.wheel;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface WheelRegionHandler {

	static WheelRegionHandler getDefault() {
		return DefaultWheelRegionHandler.INS;
	}

	int getHover(int n);

	void render(GuiGraphics g, Player player, List<? extends WheelAdaptor.Entry> list, WheelContext ctx);

}
