package dev.xkmc.l2itemselector.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.init.data.L2ISLangData;
import dev.xkmc.l2itemselector.init.data.L2ISTagGen;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import dev.xkmc.l2itemselector.select.SetSelectedToServer;
import dev.xkmc.l2itemselector.select.item.ItemSelectionListener;
import dev.xkmc.l2itemselector.select.item.SimpleItemSelectConfig;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.l2serial.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static dev.xkmc.l2serial.network.PacketHandler.NetDir.PLAY_TO_SERVER;

@Mod(L2ItemSelector.MODID)
@EventBusSubscriber(modid = L2ItemSelector.MODID, bus = EventBusSubscriber.Bus.MOD)
public class L2ItemSelector {

	public static final String MODID = "l2itemselector";

	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandler PACKET_HANDLER = new PacketHandler(MODID, 1,
			e -> e.create(SetSelectedToServer.class, PLAY_TO_SERVER));

	public static final ConfigTypeEntry<SimpleItemSelectConfig> ITEM_SELECTOR =
			new ConfigTypeEntry<>(PACKET_HANDLER, "item_selector", SimpleItemSelectConfig.class);

	public L2ItemSelector() {
		L2ISConfig.init();
		SelectionRegistry.register(-2000, ItemSelectionListener.INSTANCE);
		REGISTRATE.addDataGenerator(ProviderType.LANG, L2ISLangData::genLang);
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, L2ISTagGen::genItemTags);
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
	}

	public static ResourceLocation loc(String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}

}
