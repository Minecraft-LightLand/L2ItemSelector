package dev.xkmc.l2itemselector.overlay;

import dev.xkmc.l2itemselector.select.SelectionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface WheelAdaptor {

	@Nullable
	static WheelAdaptor get(@Nullable Player player, int wheelIndex) {
		if (player == null) return null;
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return null;
		if (!(sel.get() instanceof Provider pvd)) return null;
		return pvd.get(player, wheelIndex).orElse(null);
	}

	List<Entry> getWheelContent();

	int getIndex(Player player);

	default int getMouseSelect(Player player) {
		var list = getWheelContent();
		int n = list.size();
		if (n <= 1) return -1;
		float da = (float) (Math.PI * 2 / n);
		float a0 = (float) (-Math.PI / 2);
		var win = Minecraft.getInstance().getWindow();
		int x0 = win.getGuiScaledWidth() / 2, y0 = win.getGuiScaledHeight() / 2;

		float r = Math.min(x0, y0) / 2f; // 轮盘半径
		float r0 = Math.max(40, r * 0.5f); // 物品渲染位置
		float r1 = r * 0.25f; //空心部分半径

		return ClientHandler.getMouseSelect(x0, y0, a0, da, n, r, r1);
	}

	default void render(GuiGraphics g, Player player) {
		var list = WheelHandler.wheel.getWheelContent();
		int n = list.size();
		if (n <= 1) return;
		float da = (float) (Math.PI * 2 / n);
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0, y0) / 1.5f; // 轮盘半径
		float r0 = Math.max(40, r * 0.775f); // 物品渲染位置
		float r1 = r * 0.5f; //空心部分半径

		float dr0 = r * 0.025f; // 未选中偏移
		float dr1 = r * 0.05f; // 选中偏移
		float s = 1.5f; // 选中放大

		int ma = WheelHandler.wheel.getMouseSelect(player);
		float a0 = (float) (-Math.PI / 2);
		for (int i = 0; i < n; i++) {
			float ai = a0 + da * i;
			if (ma == i) {
				WheelOverlay.fillFan(g, x0, y0, ai, da, r, r1, dr1, 0, 0x7fffffff, 0x00ffffff);
			} else {
				WheelOverlay.fillFan(g, x0, y0, ai, da, r, r1, dr0, 0, 0x3fffffff, 0x00ffffff);
			}
			list.get(i).render(g, x0, y0, ai, r0, r, da, ma == i ? s : 1);
		}
		g.flush();
	}

	void select(int index);

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
		default void render(GuiGraphics g, Player player) {
			WheelAdaptor.super.render(g, player);
			int index = getMouseSelect(player);
			if (index < 0) index = getIndex(player);
			ItemStack stack = getItem(index);
			int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
			float r = Math.min(x0, y0) / 2f;
			float s = r * 0.02f;
			g.pose().pushPose();
			g.pose().translate(x0, y0, 0);
			g.pose().scale(s, s, s);
			g.renderItem(stack, -8, -16);
			g.pose().popPose();

			var text = stack.getHoverName();
			var font = Minecraft.getInstance().font;
			g.renderTooltip(font, stack.getHoverName(), 0, 0);
			TextBox box = new TextBox(g, 1, 0, x0, (int) (y0 + s * 3), (int) r);
			box.renderLongText(font, List.of(text));
		}

	}

}
