package dev.xkmc.l2itemselector.init.data;

import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.overlay.InfoSideBar;
import net.neoforged.neoforge.common.ModConfigSpec;

public class L2ISConfig {

	public static class Client extends ConfigInit {

		public final ModConfigSpec.EnumValue<InfoSideBar.Anchor> infoAnchor;
		public final ModConfigSpec.DoubleValue infoMaxWidth;

		public final ModConfigSpec.BooleanValue selectionDisplayRequireShift;
		public final ModConfigSpec.BooleanValue selectionScrollRequireShift;

		public final ModConfigSpec.BooleanValue useFastSwitchWheel;


		Client(Builder builder) {
			markL2();
			infoAnchor = builder.text("Info box vertical alignment")
					.defineEnum("infoAnchor", InfoSideBar.Anchor.CENTER);
			infoMaxWidth = builder.text("Info box max width")
					.comment("Max width for info box. 0.5 means half screen. default: 0.3")
					.defineInRange("infoMaxWidth", 0.3, 0, 0.5);
			selectionDisplayRequireShift = builder
					.text("Render selector only when pressing [Hold Selection]")
					.define("selectionDisplayRequireShift", true);
			selectionScrollRequireShift = builder
					.text("Scroll for selection only when pressing [Hold Selection]")
					.define("selectionScrollRequireShift", true);
			useFastSwitchWheel = builder
					.text("Use alternative wheel that click wheel key switches item")
					.define("useFastSwitchWheel", false);
		}

	}

	public static final Client CLIENT = L2ItemSelector.REGISTRATE.registerClient(Client::new);

	public static void init() {
	}

}
