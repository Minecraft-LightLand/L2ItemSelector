package dev.xkmc.l2itemselector.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.bus.api.Event;

import java.util.function.Predicate;

public class GenericKeyEvent extends Event {

	private final Predicate<InputConstants.Key> pred;
	private final int action;

	public GenericKeyEvent(Predicate<InputConstants.Key> pred, int action) {
		this.pred = pred;
		this.action = action;
	}

	public int getAction() {
		return action;
	}

	public boolean test(InputConstants.Key key) {
		return pred.test(key);
	}

}
