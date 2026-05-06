package dev.xkmc.l2itemselector.overlay;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class WheelOverlay implements LayeredDraw.Layer {

	@Override
	public void render(GuiGraphics g, DeltaTracker pt) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		if (WheelHandler.wheel == null) return;
		var list = WheelHandler.wheel.getDisplayList();
		int sel = WheelHandler.wheel.getIndex(player);
		int n = list.size();
		if (n <= 1) return;
		float da = (float) (Math.PI * 2 / n);
		float a0 = (float) (-Math.PI / 2 - da * sel);
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0, y0) / 2f;
		float r0 = Math.max(40, r / 2f);
		int ma = WheelHandler.getSel(x0, y0, a0, da, n, r);
		for (int i = 0; i < n; i++) {
			float ai = a0 + da * i;
			if (ma == i) {
				fillFan(g, x0, y0, ai, da, r, r / 20f, 0, 0x7fffffff);
			} else {
				fillFan(g, x0, y0, ai, da, r, r / 40f, 0, 0x3fffffff);
			}
			float dx = x0 + Mth.cos(ai) * r0;
			float dy = y0 + Mth.sin(ai) * r0;
			g.renderItem(list.get(i), (int) dx - 8, (int) dy - 8);
		}
		g.flush();
	}


	public void fillFan(GuiGraphics g, float x0, float y0, float ai, float da, float r0, float dr, int pZ, int col) {
		Matrix4f mat = g.pose().last().pose();
		VertexConsumer vc = g.bufferSource().getBuffer(Shard.GUI_FAN);
		float x1 = x0 + Mth.cos(ai) * dr;
		float y1 = y0 + Mth.sin(ai) * dr;
		int n = (int) Math.max(3, da / (Math.PI / 24));
		vc.addVertex(mat, x1, y1, pZ).setColor(col);

		for (int i = 0; i <= n; i++) {
			float a = ai + da / 2 - da / n * i;
			float x2 = x1 + Mth.cos(a) * r0;
			float y2 = y1 + Mth.sin(a) * r0;
			vc.addVertex(mat, x2, y2, pZ).setColor(col);
		}

	}


	private static class Shard extends RenderType {

		public Shard(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
			super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
		}

		public static final RenderType GUI_FAN = create("gui_fan",
				DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_FAN, 786432,
				RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_GUI_SHADER)
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setDepthTestState(LEQUAL_DEPTH_TEST)
						.createCompositeState(false));

	}

}
