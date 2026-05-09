package dev.xkmc.l2itemselector.init;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.overlay.WheelOverlay;
import dev.xkmc.l2itemselector.select.item.ItemSelectionOverlay;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2ItemSelector.MODID)
public class L2ItemSelectorClient {

	public static final RenderPipeline GUI_STRIP = RenderPipeline
			.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
			.withVertexShader("core/gui")
			.withFragmentShader("core/gui")
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
			.withLocation(L2ItemSelector.loc("pipeline/wheel")).build();

	@SubscribeEvent
	public static void registerPipeline(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(GUI_STRIP);
	}

	@SubscribeEvent
	public static void registerOverlays(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.CROSSHAIR, L2ItemSelector.loc("tool_select"), ItemSelectionOverlay.INSTANCE);
		event.registerAbove(VanillaGuiLayers.CROSSHAIR, L2ItemSelector.loc("wheel"), new WheelOverlay());
	}

	@SubscribeEvent
	public static void registerKeyMaps(RegisterKeyMappingsEvent event) {
		for (var e : L2Keys.values()) {
			event.register(e.map);
		}
	}

}
