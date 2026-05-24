package dev.xkmc.l2itemselector.wheel;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.l2itemselector.init.L2ItemSelectorClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.GuiLayer;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

public class WheelOverlay implements GuiLayer {

	@Override
	public void render(GuiGraphicsExtractor g, DeltaTracker pt) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		if (WheelHandler.wheel == null) return;
		var next = WheelAdaptor.get(player, WheelHandler.wheelIndex);
		if (next == null) return;
		next.renderWheel(g, player);
	}

	public static void drawSideGradient(GuiGraphicsExtractor g, boolean left, float width, int outerColor, int innerColor) {
		if (left) fillGradient(g, 0, 0, width, g.guiHeight(), outerColor, innerColor);
		else fillGradient(g, g.guiWidth() - width, 0, g.guiWidth(), g.guiHeight(), innerColor, outerColor);
	}

	private static void fillGradient(GuiGraphicsExtractor g, float x0, float y0, float x1, float y1, int c0, int c1) {
		g.submitGuiElementRenderState(new Gradient(
				RenderPipelines.GUI, TextureSetup.noTexture(), g.pose(),
				x0, y0, x1, y1, c0, c1, g.peekScissorStack()
		));
	}

	public static void drawSeparator(GuiGraphicsExtractor g, float x0, float y0, float a, float r0, float r1, float w0, float w1, int c0, int c1) {
		g.submitGuiElementRenderState(Separator.of(g, x0, y0, a, w0, w1, r0, r1, c0, c1));
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
			float r = Math.max(r0, r1) + dr;
			int mx0 = Mth.floor(x0 - r);
			int mx1 = Mth.ceil(x0 + r);
			int my0 = Mth.floor(y0 - r);
			int my1 = Mth.ceil(y0 + r);

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

	public record Separator(
			RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fStack pose,
			@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds,
			float x0, float y0, float a, float w0, float w1, float r0, float r1, int c0, int c1
	) implements GuiElementRenderState {

		public static Separator of(GuiGraphicsExtractor g, float x0, float y0, float a, float w0, float w1, float r0, float r1, int c0, int c1) {
			var pose = g.pose();
			var scissor = g.peekScissorStack();

			float r = Math.max(r0, r1);
			int mx0 = Mth.floor(x0 - r);
			int mx1 = Mth.ceil(x0 + r);
			int my0 = Mth.floor(y0 - r);
			int my1 = Mth.ceil(y0 + r);

			var bounds = new ScreenRectangle(mx0, my0, mx1 - mx0, my1 - my0).transformMaxBounds(pose);
			return new Separator(L2ItemSelectorClient.GUI_STRIP, TextureSetup.noTexture(), pose,
					scissor, bounds,
					x0, y0, a, w0, w1, r0, r1, c0, c1);
		}

		@Override
		public void buildVertices(VertexConsumer vc) {
			int seg = 8;
			for (int i = 0; i <= seg; i++) {
				float t = (float) i / seg;
				float et = (float) Math.sqrt(t);
				float r = r0 + (r1 - r0) * t;
				float halfW = w0 + (w1 - w0) * t;

				int aCol = (int) (((c0 >> 24) & 0xFF) * (1 - et) + ((c1 >> 24) & 0xFF) * et);
				int rCol = (int) (((c0 >> 16) & 0xFF) * (1 - et) + ((c1 >> 16) & 0xFF) * et);
				int gCol = (int) (((c0 >> 8) & 0xFF) * (1 - et) + ((c1 >> 8) & 0xFF) * et);
				int bCol = (int) ((c0 & 0xFF) * (1 - et) + (c1 & 0xFF) * et);
				int color = (aCol << 24) | (rCol << 16) | (gCol << 8) | bCol;

				float lx = x0 + Mth.cos(a - halfW) * r;
				float ly = y0 + Mth.sin(a - halfW) * r;
				float rx = x0 + Mth.cos(a + halfW) * r;
				float ry = y0 + Mth.sin(a + halfW) * r;

				vc.addVertexWith2DPose(pose, lx, ly).setColor(color);
				vc.addVertexWith2DPose(pose, rx, ry).setColor(color);
			}
		}

	}

	public record Gradient(
			RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose,
			float x0, float y0, float x1, float y1, int col1, int col2,
			@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds
	) implements GuiElementRenderState {

		public Gradient(
				RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose,
				float x0, float y0, float x1, float y1, int col1, int col2,
				@Nullable ScreenRectangle scissorArea
		) {
			this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, getBounds((int) x0, (int) y0, (int) Math.ceil(x1), (int) Math.ceil(y1), pose, scissorArea));
		}

		@Override
		public void buildVertices(VertexConsumer vertexConsumer) {
			vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setColor(this.col1());
			vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1()).setColor(this.col1());
			vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(this.col2());
			vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0()).setColor(this.col2());
		}

		private static @Nullable ScreenRectangle getBounds(int x0, int y0, int x1, int y1, Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea) {
			ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
			return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
		}
	}

}
