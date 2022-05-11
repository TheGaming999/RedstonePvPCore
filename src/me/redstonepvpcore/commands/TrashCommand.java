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
import me.redstonepvpcore.utils.Colorizer;
import me.redstonepvpcore.utils.ConfigCreator;

public class TrashCommand implements CommandExecutor {

	private RedstonePvPCore plugin;
	private String name;
	private int size;

	public TrashCommand(RedstonePvPCore plugin) {
		name = Colorizer.colorize(ConfigCreator.getConfig("trash.yml").getString("title"));
		size = ConfigCreator.getConfig("trash.yml").getInt("size");
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (args.length) {
		case 0:
			if(!sender.hasPermission(Permissions.TRASH)) {
				return true;
			}
			if(!(sender instanceof Player)) {
				Messages.sendMessage(sender, plugin.getMessages().getPlayerOnly());
				return true;
			}
			Player player = (Player)sender;
			Inventory newInv = Bukkit.createInventory(player, size, name);
			player.openInventory(newInv);
			break;
		case 1:
			if(!sender.hasPermission(Permissions.TRASH_OTHER)) {
				return true;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				Messages.sendMessage(sender, plugin.getMessages().getUnknownPlayer().replace("%target%", args[0]));
				return true;
			}
			Inventory targetInv = Bukkit.createInventory(target, size, name);
			target.openInventory(targetInv);
			break;
		default:
			break;
		}
		return true;
	}

}
