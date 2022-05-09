package me.redstonepvpcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.messages.Messages;
import me.redstonepvpcore.player.Permissions;
import me.redstonepvpcore.shop.Shop;

public class ShopCommand implements CommandExecutor {

	private RedstonePvPCore plugin;

	public ShopCommand(RedstonePvPCore plugin) {
		this.plugin = plugin;
	}

	public Shop getShop() {
		return plugin.getShop();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (args.length) {
		case 0:
			if(!sender.hasPermission(Permissions.SHOP)) {
				return true;
			}
			if(!(sender instanceof Player)) {
				Messages.sendMessage(sender, plugin.getMessages().getPlayerOnly());
				return true;
			}
			Player player = (Player)sender;
			Inventory newInv = Bukkit.createInventory(player, getShop().getSlots(), getShop().getInventoryName());
			newInv.setContents(getShop().getInventory().getContents());
			player.openInventory(newInv);
			break;
		case 1:
			if(!sender.hasPermission(Permissions.SHOP_OTHER)) {
				return true;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				Messages.sendMessage(sender, plugin.getMessages().getUnknownPlayer().replace("%target%", args[0]));
				return true;
			}
			Inventory targetInv = Bukkit.createInventory(target, getShop().getSlots(), getShop().getInventoryName());
			targetInv.setContents(getShop().getInventory().getContents());
			target.openInventory(targetInv);
			break;
		default:
			break;
		}
		return true;
	}

}
