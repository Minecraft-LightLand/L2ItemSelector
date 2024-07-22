package dev.xkmc.l2itemselector.init.data;

import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.overlay.InfoSideBar;
import net.neoforged.neoforge.common.ModConfigSpec;

public class L2ISConfig {

	public static class Client extends ConfigInit {

		public final ModConfigSpec.DoubleValue infoAlpha;
		public final ModConfigSpec.EnumValue<InfoSideBar.Anchor> infoAnchor;
		public final ModConfigSpec.DoubleValue infoMaxWidth;

		public final ModConfigSpec.BooleanValue selectionDisplayRequireShift;
		public final ModConfigSpec.BooleanValue selectionScrollRequireShift;


		Client(Builder builder) {
			markL2();
			infoAlpha = builder.text("Info box background transparency")
					.comment("Background transparency for info text box overlay. 1 means opaque.")
					.defineInRange("infoAlpha", 0.5, 0, 1);
			infoAnchor = builder.text("Info box vertical alignment")
					.defineEnum("infoAnchor", InfoSideBar.Anchor.CENTER);
			infoMaxWidth = builder.text("Info box max width")
					.comment("Max width for info box. 0.5 means half screen. default: 0.3")
					.defineInRange("infoMaxWidth", 0.3, 0, 0.5);
			selectionDisplayRequireShift = builder
					.text("Render selector only when pressing [Hold Selection]")
					.define("selectionDisplayRequireShift", false);
			selectionScrollRequireShift = builder
					.text("Scroll for selection only when pressing [Hold Selection]")
					.define("selectionScrollRequireShift", true);
		}

	}

	public static final Client CLIENT = L2ItemSelector.REGISTRATE.registerClient(Client::new);

	public static void init() {
	}

}
