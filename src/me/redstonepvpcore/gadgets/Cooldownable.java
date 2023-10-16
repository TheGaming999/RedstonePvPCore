package me.redstonepvpcore.gadgets;

import java.util.UUID;

public interface Cooldownable {

	default void cooldownOrPerform() {

	}

	default boolean checkCooldown(UUID uniqueId) {
		return true;
	}

}
