package me.redstonepvpcore.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.messages.Messages;
import me.redstonepvpcore.player.BypassManager;
import me.redstonepvpcore.player.Permissions;
import me.redstonepvpcore.shop.Shop;
import me.redstonepvpcore.utils.CollectionUtils;
import me.redstonepvpcore.utils.ConfigCreator;

public class ShopCommand implements CommandExecutor {

	private RedstonePvPCore plugin;
	private Set<String> disabledWorlds = new HashSet<>();
	private Inventory shopInv;

	public ShopCommand(RedstonePvPCore plugin) {
		this.plugin = plugin;
		List<String> disabledWorldsList = ConfigCreator.getConfig("shop.yml").getStringList("disabled-worlds");
		disabledWorlds.clear();
		if (disabledWorldsList != null && !disabledWorldsList.isEmpty()) {
			Bukkit.getWorlds().forEach(world -> {
				String worldName = world.getName();
				if (CollectionUtils.hasIgnoreCase(disabledWorldsList, worldName)) disabledWorlds.add(worldName);
			});
		}
		shopInv = Bukkit.createInventory(null, getShop().getSlots(), getShop().getInventoryName());
		shopInv.setContents(getShop().getInventory().getContents());
	}

	public Shop getShop() {
		return plugin.getShop();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (args.length) {
			case 0:
				if (!sender.hasPermission(Permissions.SHOP)) {
					return true;
				}
				if (!(sender instanceof Player)) {
					Messages.sendMessage(sender, plugin.getMessages().getPlayerOnly());
					return true;
				}
				Player player = (Player) sender;
				if (!BypassManager.isBypassOn(player.getUniqueId())
						&& disabledWorlds.contains(player.getWorld().getName()))
					return true;
				player.openInventory(shopInv);
				break;
			case 1:
				if (!sender.hasPermission(Permissions.SHOP_OTHER)) {
					return true;
				}
				Player target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					Messages.sendMessage(sender, plugin.getMessages().getUnknownPlayer().replace("%target%", args[0]));
					return true;
				}
				target.openInventory(shopInv);
				break;
			default:
				break;
		}
		return true;
	}

}
