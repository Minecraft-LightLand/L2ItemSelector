package dev.xkmc.l2itemselector.select.item;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.TextBox;
import dev.xkmc.l2itemselector.overlay.WheelAdaptor;
import dev.xkmc.l2itemselector.overlay.WheelHandler;
import dev.xkmc.l2itemselector.select.ISelectionListener;
import dev.xkmc.l2itemselector.select.SetSelectedToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;

public class ItemSelectionListener implements ISelectionListener, WheelAdaptor.Provider {

	public static final ISelectionListener INSTANCE = new ItemSelectionListener();

	@Override
	public ResourceLocation getID() {
		return L2ItemSelector.loc("item");
	}

	@Override
	public boolean isClientActive(Player player) {
		if (Minecraft.getInstance().screen != null) return false;
		if (Screen.hasAltDown()) return false;
		var sel = IItemSelector.getSelection(player);
		return sel != null;
	}

	@Override
	public void handleServerSetSelection(SetSelectedToServer packet, Player sender) {
		var sel = IItemSelector.getSelection(sender);
		if (sel != null) {
			sel.swap(sender, packet.slot());
		}
	}

	@Override
	public boolean handleClientScroll(int diff, Player player) {
		var sel = IItemSelector.getSelection(player);
		if (sel == null) return false;
		if (WheelHandler.wheel != null) {
			int current = WheelHandler.keyboardIndex >= 0 ? WheelHandler.keyboardIndex : sel.getIndex(player);
			int total = sel.getList().size();
			WheelHandler.keyboardIndex = ((current - diff) % total + total) % total;
			return true;
		}
		toServer(sel.move(-diff, player));
		return true;
	}

	@Override
	public void handleClientKey(L2Keys key, Player player) {
		var sel = IItemSelector.getSelection(player);
		if (sel == null) return;
		int dir = switch (key) {
			case UP, LEFT -> -1;
			case DOWN, RIGHT -> 1;
			default -> 0;
		};
		if (dir == 0) return;
		if (WheelHandler.wheel != null) {
			int current = WheelHandler.keyboardIndex >= 0 ? WheelHandler.keyboardIndex : sel.getIndex(player);
			int total = sel.getList().size();
			WheelHandler.keyboardIndex = ((current + dir) % total + total) % total;
		} else {
			toServer(sel.move(dir, player));
		}
	}

	@Override
	public boolean handleClientNumericKey(int i, BooleanSupplier consumeClick) {
		return false;
	}

	@Override
	public boolean scrollBypassShift() {
		return WheelHandler.wheel != null;
	}

	@Override
	public Optional<WheelAdaptor> get(@Nullable Player player) {
		if (player == null) return Optional.empty();
		var sel = IItemSelector.getSelection(player);
		if (sel == null) return Optional.empty();
		return ClientHandler.get(sel);
	}

	static class ClientHandler {

		public static Optional<WheelAdaptor> get(IItemSelector.Holder sel) {
			return Optional.of(new ItemWheel(sel));
		}

	}

	record ItemWheel(IItemSelector.Holder sel) implements WheelAdaptor {

		@Override
		public List<Entry> getWheelContent() {
			var src = sel.getDisplayList();
			var ans = new ArrayList<Entry>();
			for (var e : src) {
				ans.add(new ItemEntry(e));
			}
			return ans;
		}

		@Override
		public int getIndex(Player player) {
			return sel.getIndex(player);
		}

		@Override
		public void render(GuiGraphics g, Player player) {
			WheelAdaptor.super.render(g, player);
			int index = getMouseSelect(player);
			if (index < 0 && WheelHandler.keyboardIndex >= 0) index = WheelHandler.keyboardIndex;
			if (index < 0) index = sel.getIndex(player);
			ItemStack stack = sel.getDisplayList().get(index);
			int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
			float r = Math.min(x0, y0) / 2f; // 轮盘半径
			float s = r * 0.02f;
			g.pose().pushPose();
			g.pose().translate(x0, y0, 0);
			g.pose().scale(s, s, s);
			g.renderItem(stack, -8, -16);
			g.pose().popPose();

			var text = stack.getHoverName();
			var font = Minecraft.getInstance().font;
			int y = (int) (y0 + s * 3);
			for (var line : font.split(text, (int) r)) {
				g.drawString(font, line, x0 - font.width(line) / 2, y, 0xffffff, false);
				y += font.lineHeight + 1;
			}

		}
	}

	record ItemEntry(ItemStack stack) implements WheelAdaptor.Entry {

		@Override
		public void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, float s) {
			s *= Math.min(r * 0.015f, da * r0 / 16f);

			float dx = x0 + Mth.cos(ai) * r0;
			float dy = y0 + Mth.sin(ai) * r0;
			g.pose().pushPose();
			g.pose().translate(dx, dy, 0);
			g.pose().scale(s, s, s);
			g.renderItem(stack, -8, -8);
			g.pose().popPose();
		}

	}
}
