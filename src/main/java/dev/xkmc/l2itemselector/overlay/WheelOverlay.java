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
		WheelHandler.wheel.render(g, player);
	}


	public static void fillFan(GuiGraphics g, float x0, float y0, float ai, float da, float r0, float r1, float dr, int pZ, int c0, int c1) {
		Matrix4f mat = g.pose().last().pose();
		VertexConsumer vc = g.bufferSource().getBuffer(Shard.GUI_FAN);
		float x1 = x0 + Mth.cos(ai) * dr;
		float y1 = y0 + Mth.sin(ai) * dr;
		int n = (int) Math.max(3, da / (Math.PI / 24));
		for (int i = 0; i <= n; i++) {
			float a = ai + da / 2 - da / n * i;
			float x2 = x1 + Mth.cos(a) * r1;
			float y2 = y1 + Mth.sin(a) * r1;
			vc.addVertex(mat, x2, y2, pZ).setColor(c1);
			float x3 = x1 + Mth.cos(a) * r0;
			float y3 = y1 + Mth.sin(a) * r0;
			vc.addVertex(mat, x3, y3, pZ).setColor(c0);
		}

	}


	private static class Shard extends RenderType {

		public Shard(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
			super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
		}

		public static final RenderType GUI_FAN = create("gui_fan",
				DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP, 786432,
				RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_GUI_SHADER)
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setDepthTestState(LEQUAL_DEPTH_TEST)
						.createCompositeState(false));

	}

}
