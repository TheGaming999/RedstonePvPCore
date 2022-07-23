package me.redstonepvpcore.player;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import me.redstonepvpcore.utils.ConfigCreator;

public class Permissions {

	public static final String CONVERTERS_USE_PERMISSION = "redstonepvpcore.use.converters";
	public static final String DROPPARTY_USE_PERMISSION = "redstonepvpcore.use.dropparty";
	public static final String EXPSIGN_USE_PERMISSION = "redstonepvpcore.use.expsign";
	public static final String FRAMEGIVER_USE_PERMISSION = "redstonepvpcore.use.framegiver";
	public static final String RANDOMBOX_USE_PERMISSION = "redstonepvpcore.use.randombox";
	public static final String REPAIRANVIL_USE_PERMISSION = "redstonepvpcore.use.repairanvil";

	public static final String REDSTONEPVP_CORE_ADMIN = "redstonepvpcore.admin";
	public static final String SHOP = "redstonepvpcore.shop";
	public static final String SHOP_OTHER = "redstonepvpcore.shop.other";
	public static final String TRASH = "redstonepvpcore.trash";
	public static final String TRASH_OTHER = "redstonepvpcore.trash.other";

	private static final Set<String> PERMISSIONS = new HashSet<>();

	private static boolean registerPermission(String permission, String configName) {
		boolean bool = ConfigCreator.getConfig(configName).getBoolean("requires-permission");
		if (bool) PERMISSIONS.add(permission);
		return bool;
	}

	static {
		reload();
	}

	public static void reload() {
		PERMISSIONS.clear();
		registerPermission(CONVERTERS_USE_PERMISSION, "converters.yml");
		registerPermission(DROPPARTY_USE_PERMISSION, "drop-party.yml");
		registerPermission(EXPSIGN_USE_PERMISSION, "exp-sign.yml");
		registerPermission(FRAMEGIVER_USE_PERMISSION, "frame-giver.yml");
		registerPermission(RANDOMBOX_USE_PERMISSION, "randombox.yml");
		registerPermission(REPAIRANVIL_USE_PERMISSION, "repair-anvil.yml");
	}

	/**
	 * 
	 * @param player     player to check permissions for
	 * @param permission permission to check
	 * @return player has permission and permission is required
	 */
	public static boolean hasPermission(Player player, String permission) {
		return player.hasPermission(permission) != PERMISSIONS.contains(permission);
	}

	public static boolean requiresPermission(String permission) {
		return PERMISSIONS.contains(permission);
	}

}
