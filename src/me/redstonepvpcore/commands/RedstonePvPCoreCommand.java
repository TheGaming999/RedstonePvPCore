package me.redstonepvpcore.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.gadgets.ConverterType;
import me.redstonepvpcore.gadgets.DropPartyActivator;
import me.redstonepvpcore.gadgets.GadgetManager;
import me.redstonepvpcore.gadgets.GadgetType;
import me.redstonepvpcore.messages.Messages;
import me.redstonepvpcore.player.BypassManager;
import me.redstonepvpcore.player.GadgetSetterManager;
import me.redstonepvpcore.utils.Colorizer;
import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.CooldownScheduler;

public class RedstonePvPCoreCommand implements CommandExecutor {

	private RedstonePvPCore parent;
	private final List<String> helpMessage = Colorizer.colorize("&3[&cRedstonePvPCore&3] &aHelp Page", 
			"&2/&6{label} &9set &7<&6goldconverter&7⎟&aemeraldconverter&7⎟&4redstoneconverter&7⎟&bdropparty&7⎟&frepairanvil&7⎟&2expsign&7⎟&cframe&7⎟&3randombox&7>",
			"&2/&6{label} &9reload &8⎟ &7reloads config files",
			"&2/&6{label} &9bypass [player] &8⎟ &7bypass every limit and break the rules",
			"&2/&6{label} &9cancel &8⎟ &7cancels &f/rputils set &8block click request",
			"&2/&6{label} &9resetdp &8⎟ &7resets drop party timer, so it can be used again",
			"&2/&6{label} &9startdp &8⎟ &7force activate all drop party activators",
			"&2/&6{label} &9list &1[page] &8⎟ &7shows all block locations that correspond to a gadget",
			"&2/&6{label} &9help 2 &8⎟ &7goto the next help page",
			"&3[&c&m                                  &3]");
	private final List<String> helpMessage2 = Colorizer.colorize("&3[&cRedstonePvPCore&3] &aHelp Page", 
			"&2/&6{label} &9enchantments &8⎟ &7lists all available enchantments",
			"&2/&6{label} &9enchant <enchant> [level] &8⎟ &7enchants the item you are holding",
			"&2/&6trash &8⎟ &7opens the trash menu",
			"&2/&6shop &9[player] &8⎟ &7opens the shop menu for you or for someone else",
			"&2/&6{label} &9sounds &8⎟ &7lists all available sounds you can use in one of the config files",
			"&2/&6{label} &9playsound &1<name> [volume] [pitch] &8⎟ &7plays a sound to you for testing",
			"&2/&6{label} &9set &1[gadget] &b-cancel &8⎟ &7allows setting gadgets till /rp cancel gets called",
			"&2/&6{label} &9help 1 &8⎟ &7goto the previous help page",
			"&3[&c&m                                  &3]");
	private final String setExampleMessage = Colorizer.colorize("&cExample: &7/{label} set randombox"); 
	
	public RedstonePvPCoreCommand(RedstonePvPCore parent) {
		this.parent = parent;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getLabel().equalsIgnoreCase("redstonepvpcore")) {
			switch (args.length) {
			case 0:
				helpMessage.forEach(messageLine -> sender.sendMessage(messageLine.replace("{label}", label)));
				break;
			case 1:
				
				switch (args[0].toLowerCase()) {
				case "help": case "?":
					helpMessage.forEach(messageLine -> sender.sendMessage(messageLine.replace("{label}", label)));
					break;
				case "set":
					sender.sendMessage(helpMessage.get(1).replace("{label}", label));
					sender.sendMessage(setExampleMessage.replace("{label}", label));
					break;
				case "cancel":
					if(!(sender instanceof Player)) {
						Messages.sendMessage(sender, parent.getMessages().getPlayerOnly());
						return true;
					}
					Player player = (Player)sender;
					GadgetSetterManager.cancel(player.getUniqueId());
					Messages.sendMessage(player, parent.getMessages().getCancel());
					break;
				case "reload": case "rl":
					// do reload
					parent.doAsync(() -> parent.reload());
					Messages.sendMessage(sender, parent.getMessages().getReload());
					break;
				case "bypass":
					// do bypass
					if(!(sender instanceof Player)) {
						Messages.sendMessage(sender, parent.getMessages().getPlayerOnly());
						return true;
					}
					Player player1 = (Player)sender;
					UUID uniqueId = player1.getUniqueId();
					if(BypassManager.isBypassOn(uniqueId)) {
						BypassManager.setBypass(uniqueId, false);
						Messages.sendMessage(player1, parent.getMessages().getBypassOff());
					} else {
						BypassManager.setBypass(uniqueId, true);
						Messages.sendMessage(player1, parent.getMessages().getBypassOn());
					}
					break;
				case "resetdp": case "resetdropparty": case "reset":
					CooldownScheduler.getAsyncScheduler().getCoolingDownKeys().forEach(key -> {
						CooldownScheduler.getAsyncScheduler().removeCooldown(key);
					});
					Messages.sendMessage(sender, parent.getMessages().getResetDropParty());
					break;
				case "startdp": case "startdropparty": case "start": case "forcestart":
					GadgetManager.getGadgets().values().stream()
					.filter(gadget -> gadget.getType() == GadgetType.DROP_PARTY_ACTIVATOR)
					.forEach(gadget -> ((DropPartyActivator)gadget).activate());
					Messages.sendMessage(sender, parent.getMessages().getStartDropParty());
					break;
				case "list":
					
					break;
				case "save":
					GadgetManager.saveGadgets();
					ConfigCreator.saveConfigs();
					sender.sendMessage(GadgetManager.getGadgetsLocations().toString());
					break;
				case "load":
					GadgetManager.loadGadgets();
					sender.sendMessage(GadgetManager.getGadgetsLocations().toString());
					break;
				default:
					// unknown command
					break;
				}
				break;
			case 2:
				switch(args[0].toLowerCase()) {
				case "set":
					if(!(sender instanceof Player)) {
						return true;
					}
					Player player = (Player)sender;
					String subCommand = args[1];
					switch(subCommand.toLowerCase()) {
					case "repairanvil":
						GadgetSetterManager.assign(player.getUniqueId(), GadgetType.REPAIR_ANVIL);
						Messages.sendMessage(player, parent.getMessages().getSelectRepairAnvil());
						Messages.sendMessage(player, parent.getMessages().getSelectCancel());
						break;
					case "goldconverter":
						GadgetSetterManager.assign(player.getUniqueId(), GadgetType.CONVERTER, ConverterType.GOLD);
						Messages.sendMessage(player, parent.getMessages().getSelectGoldConverter());
						Messages.sendMessage(player, parent.getMessages().getSelectCancel());
						break;
					case "emeraldconverter":
						GadgetSetterManager.assign(player.getUniqueId(), GadgetType.CONVERTER, ConverterType.EMERALD);
						Messages.sendMessage(player, parent.getMessages().getSelectEmeraldConverter());
						Messages.sendMessage(player, parent.getMessages().getSelectCancel());
						break;
					case "redstoneconverter":
						GadgetSetterManager.assign(player.getUniqueId(), GadgetType.CONVERTER, ConverterType.REDSTONE);
						Messages.sendMessage(player, parent.getMessages().getSelectRedstoneConverter());
						Messages.sendMessage(player, parent.getMessages().getSelectCancel());
						break;
					case "randombox":
						GadgetSetterManager.assign(player.getUniqueId(), GadgetType.RANDOM_BOX);
						Messages.sendMessage(player, parent.getMessages().getSelectRandomBox());
						Messages.sendMessage(player, parent.getMessages().getSelectCancel());
						break;
					case "framegiver":
						GadgetSetterManager.assign(player.getUniqueId(), GadgetType.FRAME_GIVER);
						Messages.sendMessage(player, parent.getMessages().getSelectFrameGiver());
						Messages.sendMessage(player, parent.getMessages().getSelectCancel());
						break;
					case "expsign":
						GadgetSetterManager.assign(player.getUniqueId(), GadgetType.EXP_SIGN);
						Messages.sendMessage(player, parent.getMessages().getSelectExpSign());
						Messages.sendMessage(player, parent.getMessages().getSelectCancel());
						break;
					case "dropparty": case "dp": case "droppartyactivator":
						GadgetSetterManager.assign(player.getUniqueId(), GadgetType.DROP_PARTY_ACTIVATOR);
						Messages.sendMessage(player, parent.getMessages().getSelectDropParty());
						Messages.sendMessage(player, parent.getMessages().getSelectCancel());
						break;
					}
					break;
				}
				break;
			}
		}
		return true;
	}

}
