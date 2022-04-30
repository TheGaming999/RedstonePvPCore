package me.redstonepvpcore.player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BypassManager {

	private final static Set<UUID> players = new HashSet<>();
	
	public static boolean isBypassOn(UUID uniqueId) {
		return players.contains(uniqueId);
	}
	
	public static boolean setBypass(UUID uniqueId, boolean enable) {
		return enable ? players.add(uniqueId) : players.remove(uniqueId);
	}
	
	public static boolean toggleBypass(UUID uniqueId) {
		return players.contains(uniqueId) ? players.remove(uniqueId) : players.add(uniqueId);
	}
	
}
