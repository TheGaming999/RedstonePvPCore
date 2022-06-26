package me.redstonepvpcore.player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

public class BypassManager {

	private static final Set<UUID> PLAYERS = new HashSet<>();

	public static boolean isBypassOn(UUID uniqueId) {
		return PLAYERS.contains(uniqueId);
	}

	public static boolean isBypassOff(Player player) {
		return !isBypassOn(player.getUniqueId());
	}

	public static boolean setBypass(UUID uniqueId, boolean enable) {
		return enable ? PLAYERS.add(uniqueId) : PLAYERS.remove(uniqueId);
	}

	public static boolean toggleBypass(UUID uniqueId) {
		return PLAYERS.contains(uniqueId) ? PLAYERS.remove(uniqueId) : PLAYERS.add(uniqueId);
	}

}
