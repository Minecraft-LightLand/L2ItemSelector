package dev.xkmc.l2itemselector.overlay;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.l2itemselector.init.L2ItemSelectorClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.GuiLayer;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;

public class WheelOverlay implements GuiLayer {

	@Override
	public void render(GuiGraphicsExtractor g, DeltaTracker pt) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		if (WheelHandler.wheel == null) return;
		WheelHandler.wheel.render(g, player);
	}


	public static void fillFan(
			GuiGraphicsExtractor g,
			float x0, float y0,
			float ai, float da,
			float r0, float r1, float dr,
			int c0, int c1
	) {
		g.submitGuiElementRenderState(Fan.of(g, x0, y0, ai, da, r0, r1, dr, c0, c1));
	}

	public record Fan(
			RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fStack pose,
			@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds,
			float x0, float y0, float ai, float da, float r0, float r1, float dr, int c0, int c1
	) implements GuiElementRenderState {

		public static Fan of(GuiGraphicsExtractor g, float x0, float y0, float ai, float da, float r0, float r1, float dr, int c0, int c1) {
			var pose = g.pose();
			var scissor = g.peekScissorStack();

			float x1 = x0 + Mth.cos(ai) * dr;
			float y1 = y0 + Mth.sin(ai) * dr;
			float x2 = x1 + Mth.cos(ai + da / 2) * r1;
			float y2 = y1 + Mth.sin(ai + da / 2) * r1;
			float x3 = x1 + Mth.cos(ai + da / 2) * r0;
			float y3 = y1 + Mth.sin(ai + da / 2) * r0;
			float x4 = x1 + Mth.cos(ai - da / 2) * r1;
			float y4 = y1 + Mth.sin(ai - da / 2) * r1;
			float x5 = x1 + Mth.cos(ai - da / 2) * r0;
			float y5 = y1 + Mth.sin(ai - da / 2) * r0;
			int mx0 = Mth.floor(Math.min(Math.min(x2, x3), Math.min(x4, x5)));
			int mx1 = Mth.ceil(Math.max(Math.max(x2, x3), Math.max(x4, x5)));
			int my0 = Mth.floor(Math.min(Math.min(y2, y3), Math.min(y4, y5)));
			int my1 = Mth.ceil(Math.max(Math.max(y2, y3), Math.max(y4, y5)));

			var bounds = new ScreenRectangle(mx0, my0, mx1 - mx0, my1 - my0).transformMaxBounds(pose);
			return new Fan(L2ItemSelectorClient.GUI_STRIP, TextureSetup.noTexture(), pose,
					scissor, bounds,
					x0, y0, ai, da, r0, r1, dr, c0, c1);
		}

		@Override
		public void buildVertices(VertexConsumer vc) {
			float x1 = x0 + Mth.cos(ai) * dr;
			float y1 = y0 + Mth.sin(ai) * dr;
			int n = (int) Math.max(3, da / (Math.PI / 24));
			for (int i = 0; i <= n; i++) {
				float a = ai + da / 2 - da / n * i;
				float x2 = x1 + Mth.cos(a) * r1;
				float y2 = y1 + Mth.sin(a) * r1;
				vc.addVertexWith2DPose(pose, x2, y2).setColor(c1);
				float x3 = x1 + Mth.cos(a) * r0;
				float y3 = y1 + Mth.sin(a) * r0;
				vc.addVertexWith2DPose(pose, x3, y3).setColor(c0);
			}
		}

	}


}
