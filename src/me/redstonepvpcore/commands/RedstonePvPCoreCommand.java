package me.redstonepvpcore.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.enchantments.RPEnchantment;
import me.redstonepvpcore.gadgets.ConverterType;
import me.redstonepvpcore.gadgets.DropPartyActivator;
import me.redstonepvpcore.gadgets.Gadget;
import me.redstonepvpcore.gadgets.GadgetManager;
import me.redstonepvpcore.gadgets.GadgetType;
import me.redstonepvpcore.messages.Messages;
import me.redstonepvpcore.messages.TimeFormatter;
import me.redstonepvpcore.player.BypassManager;
import me.redstonepvpcore.player.GadgetSetterManager;
import me.redstonepvpcore.player.Permissions;
import me.redstonepvpcore.utils.CollectionUtils;
import me.redstonepvpcore.utils.Colorizer;
import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.CooldownScheduler;
import me.redstonepvpcore.utils.CooldownScheduler.SimpleCooldownEntry;
import me.redstonepvpcore.utils.NBTEditor;
import me.redstonepvpcore.utils.PagedArrayList;
import me.redstonepvpcore.utils.XSound;

public class RedstonePvPCoreCommand implements CommandExecutor {

	private RedstonePvPCore parent;
	private final List<String> helpMessage = Colorizer.colorize("&3[&cRedstonePvPCore&3] &aHelp Page",
			"&2/&6{label} &9set &7<&6goldconverter&7⎟&aemeraldconverter&7⎟&4redstoneconverter&7⎟&bdropparty&7⎟&frepairanvil&7⎟&2expsign&7⎟&cframe&7⎟&3randombox&7>",
			"&2/&6{label} &9setcooldown <time> &8⎟ &7requests setting a cooldown for a gadget",
			"&2/&6{label} &9reload &8⎟ &7reloads config files",
			"&2/&6{label} &9bypass [player] &8⎟ &7bypass every limit and break the rules",
			"&2/&6{label} &9cancel &8⎟ &7cancels &f/rputils set &7block click request",
			"&2/&6{label} &9resetdp &8⎟ &7resets drop party timer, so it can be used again",
			"&2/&6{label} &9startdp &8⎟ &7force activate all drop party activators",
			"&2/&6{label} &9stopdp &8⎟ &7shutdowns/stops all drop party activators",
			"&2/&6{label} &9list &1[page] &8⎟ &7shows all block locations that correspond to a gadget",
			"&2/&6{label} &9help 2 &8⎟ &7goto the next help page", "&3[&c&m                                  &3]");
	private final List<String> helpMessage2 = Colorizer.colorize("&3[&cRedstonePvPCore&3] &aHelp Page",
			"&2/&6{label} &9enchantments &8⎟ &7lists all available enchantments",
			"&2/&6{label} &9enchant <enchant> [level] &8⎟ &7enchants the item you are holding",
			"&2/&6{label} &9soulbound &8⎟ &7adds soulbound to the item you are holding",
			"&2/&6trash &8⎟ &7opens the trash menu",
			"&2/&6shop &9[player] &8⎟ &7opens the shop menu for you or for someone else",
			"&2/&6{label} &9sounds &8⎟ &7lists all available sounds you can use in one of the config files",
			"&2/&6{label} &9playsound &1<name> [volume] [pitch] &8⎟ &7plays a sound to you for testing",
			"&2/&6{label} &9help 1 &8⎟ &7goto the previous help page", "&3[&c&m                                  &3]");
	private final String setExampleMessage = Colorizer.colorize("&cExample: &7/{label} set randombox");
	private final String setCooldownExampleMessage = Colorizer.colorize(
			"&cExample: &7/{label} setcooldown 60m\nDuration Examples:\n5d - 5 days\n34s - 34 seconds\n11m - 11 minutes");
	private final String enchantmentsHeaderMessage = Colorizer.colorize("&3[&cRedstonePvPCore&3] &aEnchantments");
	private final String enchantmentsFooterMessage = Colorizer.colorize("&3[&c&m                                  &3]");

	private List<String> listHeader = new ArrayList<>();
	private List<String> listFooter = new ArrayList<>();
	private List<String> listContent;
	public PagedArrayList<Gadget> pagedList;
	private String[] romanNumerals = new String[] { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };
	private List<String> romanList = Arrays.asList(romanNumerals);

	private final List<String> sounds = Arrays.stream(XSound.VALUES)
			.filter(s -> s.isSupported())
			.map(s -> Colorizer.colorize("&c" + s.parseSound().name().toLowerCase()))
			.collect(Collectors.toList());
	private final String separator = Colorizer.colorize("&3, ");
	private final String soundsString = String.join(separator, sounds);

	public RedstonePvPCoreCommand(RedstonePvPCore parent) {
		this.parent = parent;
		this.listContent = parent.getMessages()
				.getList()
				.stream()
				.filter(s -> s.contains("%pos."))
				.collect(Collectors.toList());
		listHeader.clear();
		listFooter.clear();
		for (int i = 0; i < parent.getMessages().getList().indexOf(listContent.get(0)); i++) {
			listHeader.add(parent.getMessages().getList().get(i));
		}
		for (int i = listContent.size() + 1; i < parent.getMessages().getList().size(); i++) {
			listFooter.add(parent.getMessages().getList().get(i));
		}
		updatePagedList();
	}

	public void updatePagedList() {
		pagedList = new PagedArrayList<>(listContent.size(), GadgetManager.getGadgetsCollection());
	}

	private String getRomanNumeral(int lvl) {
		if (lvl <= 0) return "0";
		if (lvl <= 10) return romanNumerals[lvl - 1];
		return String.valueOf(lvl);
	}

	private int fromRomanNumeral(String roman) {
		roman = roman.toUpperCase();
		if (romanList.contains(roman)) return romanList.indexOf(roman) + 1;
		return Integer.parseInt(roman);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission(Permissions.REDSTONEPVP_CORE_ADMIN)) {
			return true;
		}
		if (cmd.getLabel().equalsIgnoreCase("redstonepvpcore")) {
			switch (args.length) {
				case 0:
					helpMessage.forEach(messageLine -> sender.sendMessage(messageLine.replace("{label}", label)));
					break;
				case 1:

					switch (args[0].toLowerCase()) {
						case "help":
						case "?":
							helpMessage
									.forEach(messageLine -> sender.sendMessage(messageLine.replace("{label}", label)));
							break;
						case "set":
							sender.sendMessage(helpMessage.get(1).replace("{label}", label));
							sender.sendMessage(setExampleMessage.replace("{label}", label));
							break;
						case "setcooldown":
							sender.sendMessage(helpMessage.get(2).replace("{label}", label));
							sender.sendMessage(setCooldownExampleMessage.replace("{label}", label));
							break;
						case "cancel":
							if (!(sender instanceof Player)) {
								Messages.sendMessage(sender, parent.getMessages().getPlayerOnly());
								return true;
							}
							Player player = (Player) sender;
							GadgetSetterManager.cancel(player.getUniqueId());
							Messages.sendMessage(player, parent.getMessages().getCancel());
							break;
						case "reload":
						case "rl":
							parent.doAsync(() -> parent.reload());
							Messages.sendMessage(sender, parent.getMessages().getReload());
							break;
						case "bypass":
							if (!(sender instanceof Player)) {
								Messages.sendMessage(sender, parent.getMessages().getPlayerOnly());
								return true;
							}
							Player player1 = (Player) sender;
							UUID uniqueId = player1.getUniqueId();
							if (BypassManager.isBypassOn(uniqueId)) {
								BypassManager.setBypass(uniqueId, false);
								Messages.sendMessage(player1, parent.getMessages().getBypassOff());
							} else {
								BypassManager.setBypass(uniqueId, true);
								Messages.sendMessage(player1, parent.getMessages().getBypassOn());
							}
							break;
						case "resetdp":
						case "resetdropparty":
						case "reset":
							CooldownScheduler.getAsyncScheduler()
									.getCooldownEntries()
									.values()
									.forEach(entry -> entry.getWhenDone().run());
							CooldownScheduler.getAsyncScheduler().clearCooldowns();
							Messages.sendMessage(sender, parent.getMessages().getResetDropParty());
							break;
						case "startdp":
						case "startdropparty":
						case "start":
						case "forcestart":
							GadgetManager.getGadgets()
									.values()
									.stream()
									.filter(gadget -> gadget.getType() == GadgetType.DROP_PARTY_ACTIVATOR)
									.forEach(gadget -> ((DropPartyActivator) gadget).activate());
							Messages.sendMessage(sender, parent.getMessages().getStartDropParty());
							break;
						case "enchant":
							sender.sendMessage(helpMessage2.get(2).replace("{label}", label));
							break;
						case "soulbound":
							if (!(sender instanceof Player)) {
								Messages.sendMessage(sender, parent.getMessages().getPlayerOnly());
								return true;
							}
							Player p = (Player) sender;
							ItemStack itemInHand = p.getItemInHand();
							if (itemInHand == null || itemInHand.getType() == Material.AIR) return true;
							if (parent.getSoulBoundManager().isSoulBounded(itemInHand)) {
								itemInHand = parent.getSoulBoundManager().deleteSoulBound(itemInHand);
								Messages.sendMessage(p, parent.getMessages().getSoulboundRemove());
							} else {
								itemInHand = parent.getSoulBoundManager().addSoulBound(itemInHand);
								Messages.sendMessage(p, parent.getMessages().getSoulboundAdd());
							}
							p.setItemInHand(itemInHand);
							break;
						case "list":
							int i = 0;
							listHeader.forEach(sender::sendMessage);
							for (Gadget gadget : pagedList.navigate(1).getElements()) {
								String pos = "%pos." + (i + 1) + "%";
								String gad = "%gadget." + (i + 1) + "%";
								String format = listContent.get(i)
										.replace(pos, GadgetManager.deparseLocation(gadget.getLocation()))
										.replace(gad, gadget.getType().name());
								sender.sendMessage(format);
								i++;
							}
							listFooter.forEach(line -> {
								sender.sendMessage(line.replace("%page%", String.valueOf(pagedList.getCurrentPage()))
										.replace("%lastpage%", String.valueOf(pagedList.getLastPage())));
							});
							break;
						case "stopdp":
							GadgetManager.getGadgets()
									.values()
									.stream()
									.filter(gadget -> gadget.getType() == GadgetType.DROP_PARTY_ACTIVATOR)
									.forEach(gadget -> ((DropPartyActivator) gadget).deactivate());
							break;
						case "save":
							GadgetManager.saveGadgets();
							ConfigCreator.saveConfigs();
							break;
						case "load":
							GadgetManager.loadGadgets();
							break;
						case "enchantments":
							sender.sendMessage(enchantmentsHeaderMessage);
							String s = Colorizer.colorize(" &8⎟ ");
							parent.getEnchantmentManager().getEnchantmentsMap().values().forEach(val -> {
								sender.sendMessage(Colorizer.colorize("&o" + val.getId() + s + "&f&o" + val.getName()
										+ s + val.getDisplayName() + s + "&o" + val.getMaxLevel()));
							});
							sender.sendMessage(enchantmentsFooterMessage);
							break;
						case "sounds":
							sender.sendMessage(soundsString);
							break;
						default:
							// unknown command
							break;
					}
					break;
				case 2:
					switch (args[0].toLowerCase()) {
						case "help":
							switch (args[1].toLowerCase()) {
								case "2":
									helpMessage2.forEach(line -> sender.sendMessage(line.replace("{label}", label)));
									break;
								default:
									break;
							}
							break;
						case "enchant":
							sender.sendMessage(helpMessage2.get(2).replace("{label}", label));
							break;
						case "bypass":
							Player target = Bukkit.getPlayer(args[1]);
							if (target == null) {
								Messages.sendMessage(sender,
										parent.getMessages().getUnknownPlayer().replace("%target%", args[1]));
								return true;
							}
							UUID uniqueId = target.getUniqueId();
							if (BypassManager.isBypassOn(uniqueId)) {
								BypassManager.setBypass(uniqueId, false);
								Messages.sendMessage(sender,
										parent.getMessages().getBypassOffOther().replace("%target%", target.getName()));
							} else {
								BypassManager.setBypass(uniqueId, true);
								Messages.sendMessage(sender,
										parent.getMessages().getBypassOnOther().replace("%target%", target.getName()));
							}
							break;
						case "list":
							int pageNumber = 1;
							try {
								pageNumber = Integer.parseInt(args[1]);
							} catch (NumberFormatException ex) {
								Messages.sendMessage(sender, parent.getMessages().getListNotNumber());
								return true;
							}
							if (pageNumber > pagedList.getLastPage()) {
								Messages.sendMessage(sender, parent.getMessages().getListLastPage());
								return true;
							}
							if (pageNumber < 1) {
								pageNumber = 1;
								Messages.sendMessage(sender, parent.getMessages().getListInvalidPage());
							}
							int i = 0;
							listHeader.forEach(sender::sendMessage);
							for (Gadget gadget : pagedList.navigate(pageNumber).getElements()) {
								String pos = "%pos." + (i + 1) + "%";
								String gad = "%gadget." + (i + 1) + "%";
								String format = listContent.get(i)
										.replace(pos, GadgetManager.deparseLocation(gadget.getLocation()))
										.replace(gad, gadget.getType().name());
								sender.sendMessage(format);
								i++;
							}
							listFooter.forEach(line -> {
								sender.sendMessage(line.replace("%page%", String.valueOf(pagedList.getCurrentPage()))
										.replace("%lastpage%", String.valueOf(pagedList.getLastPage())));
							});
							break;
						case "set": {
							if (!(sender instanceof Player)) {
								Messages.sendMessage(sender, parent.getMessages().getPlayerOnly());
								return true;
							}
							Player player = (Player) sender;
							String subCommand = args[1];
							switch (subCommand.toLowerCase()) {
								case "repairanvil":
								case "repair":
								case "anvil":
								case "anvilrepair":
									GadgetSetterManager.assign(player.getUniqueId(), GadgetType.REPAIR_ANVIL);
									Messages.sendMessage(player, parent.getMessages().getSelectRepairAnvil());
									Messages.sendMessage(player, parent.getMessages().getSelectCancel());
									break;
								case "goldconverter":
								case "gold":
									GadgetSetterManager.assign(player.getUniqueId(), GadgetType.CONVERTER,
											ConverterType.GOLD);
									Messages.sendMessage(player, parent.getMessages().getSelectGoldConverter());
									Messages.sendMessage(player, parent.getMessages().getSelectCancel());
									break;
								case "emeraldconverter":
								case "emerald":
									GadgetSetterManager.assign(player.getUniqueId(), GadgetType.CONVERTER,
											ConverterType.EMERALD);
									Messages.sendMessage(player, parent.getMessages().getSelectEmeraldConverter());
									Messages.sendMessage(player, parent.getMessages().getSelectCancel());
									break;
								case "redstoneconverter":
								case "redstone":
									GadgetSetterManager.assign(player.getUniqueId(), GadgetType.CONVERTER,
											ConverterType.REDSTONE);
									Messages.sendMessage(player, parent.getMessages().getSelectRedstoneConverter());
									Messages.sendMessage(player, parent.getMessages().getSelectCancel());
									break;
								case "randombox":
								case "rb":
								case "rbox":
								case "box":
									GadgetSetterManager.assign(player.getUniqueId(), GadgetType.RANDOM_BOX);
									Messages.sendMessage(player, parent.getMessages().getSelectRandomBox());
									Messages.sendMessage(player, parent.getMessages().getSelectCancel());
									break;
								case "framegiver":
								case "frame":
								case "itemframe":
								case "giveframe":
								case "giverframe":
									GadgetSetterManager.assign(player.getUniqueId(), GadgetType.FRAME_GIVER);
									Messages.sendMessage(player, parent.getMessages().getSelectFrameGiver());
									Messages.sendMessage(player, parent.getMessages().getSelectCancel());
									break;
								case "expsign":
								case "sign":
								case "exp":
									GadgetSetterManager.assign(player.getUniqueId(), GadgetType.EXP_SIGN);
									Messages.sendMessage(player, parent.getMessages().getSelectExpSign());
									Messages.sendMessage(player, parent.getMessages().getSelectCancel());
									break;
								case "dropparty":
								case "dp":
								case "droppartyactivator":
								case "beacon":
									GadgetSetterManager.assign(player.getUniqueId(), GadgetType.DROP_PARTY_ACTIVATOR);
									Messages.sendMessage(player, parent.getMessages().getSelectDropParty());
									Messages.sendMessage(player, parent.getMessages().getSelectCancel());
									break;
							}
							break;
						}
						case "setcooldown": {
							if (!(sender instanceof Player)) {
								Messages.sendMessage(sender, parent.getMessages().getPlayerOnly());
								return true;
							}
							Player player = (Player) sender;
							GadgetSetterManager.assign(player.getUniqueId(), TimeFormatter.toSeconds(args[1]));
							Messages.sendMessage(player, parent.getMessages().getSelectCooldown());
							Messages.sendMessage(player, parent.getMessages().getSelectCancel());
							break;
						}
						case "playsound":
							XSound sound = XSound.matchXSound(args[1].toUpperCase()).orElse(null);
							if (sound == null) {
								sender.sendMessage("Null sound!");
								return true;
							}
							if (!(sender instanceof Player)) {
								sender.sendMessage("Not player!");
								return true;
							}
							Player p = (Player) sender;
							p.playSound(p.getLocation(), sound.parseSound(), 1.0f, 1.0f);
							break;
					}
					break;
				case 3:
					switch (args[0].toLowerCase()) {
						case "playsound":
							XSound sound = XSound.matchXSound(args[1].toUpperCase()).orElse(null);
							if (sound == null) {
								sender.sendMessage("Null sound!");
								return true;
							}
							if (!(sender instanceof Player)) {
								sender.sendMessage("Not player!");
								return true;
							}
							float volume = 1.0f;
							try {
								volume = Float.parseFloat(args[2]);
							} catch (NumberFormatException ex) {
								volume = 1.0f;
							}
							Player p = (Player) sender;
							p.playSound(p.getLocation(), sound.parseSound(), volume, 1.0f);
							break;
						case "enchant":
							enchant(sender, args);
							break;
						case "set":
							if (args[1].equalsIgnoreCase("cooldown")) {
								if (!(sender instanceof Player)) {
									Messages.sendMessage(sender, parent.getMessages().getPlayerOnly());
									return true;
								}
								Player player = (Player) sender;
								GadgetSetterManager.assign(player.getUniqueId(), TimeFormatter.toSeconds(args[2]));
								Messages.sendMessage(player, parent.getMessages().getSelectCooldown());
								Messages.sendMessage(player, parent.getMessages().getSelectCancel());
							}
							break;
						case "setplayercooldown":
							Player target = Bukkit.getPlayer(args[1]);
							if (target == null) {
								Messages.sendMessage(sender,
										parent.getMessages().getUnknownPlayer().replace("%target%", args[1]));
								return true;
							}
							GadgetManager.getCooldownGadgets().values().forEach(cooldown -> {
								SimpleCooldownEntry<UUID> entry = cooldown.COOLDOWN.getEntry(target.getUniqueId());
								entry.setLiveDuration(Integer.parseInt(args[2]));
							});
							break;
					}
					break;
				case 4:
					switch (args[0].toLowerCase()) {
						case "playsound":
							XSound sound = XSound.matchXSound(args[1].toUpperCase()).orElse(null);
							if (sound == null) {
								sender.sendMessage("Null sound!");
								return true;
							}
							if (!(sender instanceof Player)) {
								sender.sendMessage("Not player!");
								return true;
							}
							float volume = 1.0f;
							try {
								volume = Float.parseFloat(args[2]);
							} catch (NumberFormatException ex) {
								volume = 1.0f;
							}
							float pitch = 1.0f;
							try {
								pitch = Float.parseFloat(args[3]);
							} catch (NumberFormatException ex) {
								pitch = 1.0f;
							}
							Player p = (Player) sender;
							p.playSound(p.getLocation(), sound.parseSound(), volume, pitch);
							break;
						case "enchant":
							enchant(sender, args);
							break;
					}
					break;
			}
		}
		return true;
	}

	private boolean enchant(CommandSender sender, String[] args) {
		boolean targeted = args.length == 4;
		Player target = null;
		if (!targeted) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, parent.getMessages().getPlayerOnly());
				return true;
			}
			target = (Player) sender;
		} else {
			target = Bukkit.getPlayer(args[3]);
			if (target == null) {
				Messages.sendMessage(sender, parent.getMessages().getUnknownPlayer().replace("%target%", args[3]));
				return true;
			}
		}
		Player player = target;
		String name = args[1].toUpperCase();
		int enchantmentsAmount = parent.getEnchantmentManager().getEnchantmentsMap().size();
		RPEnchantment enchantment = parent.getEnchantmentManager().getEnchantment(name);
		if (enchantment == null) {
			return true;
		}
		int lvl = 0;
		try {
			lvl = Integer.parseInt(args[2]);
		} catch (NumberFormatException ex) {
			try {
				lvl = fromRomanNumeral(args[2]);
			} catch (NumberFormatException ex2) {
				Messages.sendMessage(sender, parent.getMessages().getEnchantLevelNotNumber());
				return true;
			}
		}
		if (lvl > enchantment.getMaxLevel()) {
			Messages.sendMessage(sender,
					parent.getMessages()
							.getEnchantLevelMax()
							.replace("%enchantment_max_level%", String.valueOf(enchantment.getMaxLevel())));
			return true;
		}
		ItemStack itemInHand = player.getItemInHand();
		if (itemInHand.getType() == Material.AIR) {
			Messages.sendMessage(sender, parent.getMessages().getEnchantItemHand());
			return true;
		}
		int[] enchantmentIds = NBTEditor.getIntArray(itemInHand, "rpids");
		if (enchantmentIds == null) enchantmentIds = new int[enchantmentsAmount];
		int[] enchantmentLvls = NBTEditor.getIntArray(itemInHand, "rplvls");
		if (enchantmentLvls == null) enchantmentLvls = new int[enchantmentsAmount];
		int emptySection = -1;
		int foundEnchantment = -1;
		for (int i = 0; i < enchantmentIds.length; i++) {
			if (enchantmentIds[i] == 0) {
				emptySection = i;
			} else if (enchantmentIds[i] == enchantment.getId()) {
				foundEnchantment = i;
			}
		}
		// if enchantment was found
		if (foundEnchantment != -1) {
			// if trying to remove
			if (lvl <= 0) {
				// remove enchantment by setting it to 0 (empty enchantment)
				enchantmentIds[foundEnchantment] = 0;
				// set invalid lvl to the found enchantment indicating that the enchantment got
				// removed
				enchantmentLvls[foundEnchantment] = -1;
			} else {
				// or else set the correct lvl starting from 0
				enchantmentLvls[foundEnchantment] = lvl - 1;
			}
			// or else enchantment wasn't found
		} else if (emptySection != -1) {
			// if trying to remove
			if (lvl <= 0) {
				/*
				 * // set enchantment id of nothing to 0 ? useless
				 * enchantmentIds[emptySection] = 0;
				 * // set enchantment lvl to invalid level
				 * enchantmentLvls[emptySection] = -1;
				 */
			} else {
				enchantmentIds[emptySection] = enchantment.getId();
				enchantmentLvls[emptySection] = lvl - 1;
			}
		}
		// There are no enchantments. Therefore, signal NBT tags removal by nullizing
		// the arrays
		if (IntStream.of(enchantmentIds).sum() <= 0) enchantmentIds = null;
		if (IntStream.of(enchantmentLvls).sum() <= -1) enchantmentLvls = null;
		//
		itemInHand = NBTEditor.set(itemInHand, enchantmentIds, "rpids");
		itemInHand = NBTEditor.set(itemInHand, enchantmentLvls, "rplvls");
		ItemMeta meta = itemInHand.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null) lore = new ArrayList<>();
		String displayName = Colorizer.colorize(enchantment.getDisplayName());
		if (CollectionUtils.containsIgnoreCase(lore, displayName)) {
			if (lvl > 0) {
				Messages.sendMessage(sender,
						parent.getMessages()
								.getEnchantItemUpdate()
								.replace("%enchantment_name%", name)
								.replace("%enchantment_display_name%", displayName)
								.replace("%enchantment_level_number%", String.valueOf(lvl))
								.replace("%enchantment_level%", getRomanNumeral(lvl)));
				lore = CollectionUtils.replaceContainsIgnoreCase(lore, displayName,
						displayName + " " + getRomanNumeral(lvl));
			} else {
				Messages.sendMessage(sender,
						parent.getMessages()
								.getEnchantItemRemove()
								.replace("%enchantment_name%", name)
								.replace("%enchantment_display_name%", displayName)
								.replace("%enchantment_level_number%", String.valueOf(lvl))
								.replace("%enchantment_level%", String.valueOf(lvl)));
				lore.removeIf(line -> line.contains(displayName));
			}
		} else {
			if (lvl > 0) {
				Messages.sendMessage(sender,
						parent.getMessages()
								.getEnchantItemAdd()
								.replace("%enchantment_name%", name)
								.replace("%enchantment_display_name%", displayName)
								.replace("%enchantment_level_number%", String.valueOf(lvl))
								.replace("%enchantment_level%", getRomanNumeral(lvl)));
				lore.add(displayName + " " + getRomanNumeral(lvl));
			} else {
				Messages.sendMessage(sender,
						parent.getMessages()
								.getEnchantItemRemove()
								.replace("%enchantment_name%", name)
								.replace("%enchantment_display_name%", displayName)
								.replace("%enchantment_level_number%", String.valueOf(lvl))
								.replace("%enchantment_level%", String.valueOf(lvl)));
			}
		}
		meta.setLore(lore);
		itemInHand.setItemMeta(meta);
		player.setItemInHand(itemInHand);
		return false;
	}

}
