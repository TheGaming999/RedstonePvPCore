package me.redstonepvpcore.messages;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.redstonepvpcore.utils.Colorizer;
import me.redstonepvpcore.utils.ConfigCreator;

public class Messages {

	private FileConfiguration config;

	private String repairAnvilEmptyHand, repairAnvilAlreadyRepaired, repairAnvilNotRepairable, repairAnvilNotEnough,
	repairAnvilRepairing, repairAnvilRepaired, redstoneConverterNotEnough, goldConverterNotEnough, 
	emeraldConverterNotEnough, redstoneConverterUse, goldConverterUse, emeraldConverterUse, randomBoxNotEnough, 
	randomBoxInUse, randomBoxUse, randomBoxDone, dropPartyNotReady, dropPartyAlreadyRunning, dropPartyActivate, 
	dropPartyReady, dropPartyNotEnoughPlayers, frameGiverUse, expSignNotEnough, expSignUse, selectRepairAnvil, selectRedstoneConverter, 
	selectGoldConverter, selectEmeraldConverter, selectRandomBox, selectDropParty, selectExpSign, selectFrameGiver, 
	setRepairAnvil, setRedstoneConverter, setGoldConverter, setEmeraldConverter, setRandomBox, setDropParty, setExpSign,
	setFrameGiver, removeRepairAnvil, removeRedstoneConverter, removeGoldConverter, removeEmeraldConverter, 
	removeRandomBox, removeDropParty, removeExpSign, removeFrameGiver,
	selectCancel, unknownPlayer, playerOnly, reload, cancel, bypassOn, bypassOff, bypassOnOther, bypassOffOther,
	resetDropParty, startDropParty, noPermissionUse, enchantLevelNotNumber, enchantLevelMax, enchantItemHand, 
	enchantItemAdd, enchantItemUpdate, enchantItemRemove, soulboundAdd, soulboundRemove, shopNoPermission, 
	shopBuy, shopNotEnough, listNotNumber, listLastPage, listInvalidPage;

	private List<String> list;

	public Messages() {
		setup();
	}

	public final static boolean sendMessage(Player player, String message) {
		if(message == null || message.isEmpty()) return false;
		player.sendMessage(message);
		return true;
	}

	public final static boolean sendMessage(CommandSender sender, String message) {
		if(message == null || message.isEmpty()) return false;
		sender.sendMessage(message);
		return true;
	}

	private String get(String configField) {
		return Colorizer.colorize(config.getString(configField));
	}

	private List<String> getList(String configField) {
		return Colorizer.colorize(config.getStringList(configField));
	}

	public void setup() {
		config = ConfigCreator.getConfig("messages.yml");
		repairAnvilEmptyHand = get("repair-anvil-empty-hand");
		repairAnvilAlreadyRepaired = get("repair-anvil-already-repaired");
		repairAnvilNotRepairable = get("repair-anvil-not-repairable");
		repairAnvilNotEnough = get("repair-anvil-not-enough");
		repairAnvilRepairing = get("repair-anvil-repairing");
		repairAnvilRepaired = get("repair-anvil-repaired");
		redstoneConverterNotEnough = get("redstone-converter-not-enough");
		goldConverterNotEnough = get("gold-converter-not-enough");
		emeraldConverterNotEnough = get("emerald-converter-not-enough");
		redstoneConverterUse = get("redstone-converter-use");
		goldConverterUse = get("gold-converter-use");
		emeraldConverterUse = get("emerald-converter-use");
		randomBoxNotEnough = get("random-box-not-enough");
		randomBoxInUse = get("random-box-in-use");
		randomBoxUse = get("random-box-use");
		randomBoxDone = get("random-box-done");
		dropPartyNotReady = get("drop-party-not-ready");
		dropPartyAlreadyRunning = get("drop-party-already-running");
		dropPartyActivate = get("drop-party-activate");
		dropPartyReady = get("drop-party-ready");
		dropPartyNotEnoughPlayers = get("drop-party-not-enough-players");
		frameGiverUse = get("frame-giver-use");
		expSignUse = get("exp-sign-use");
		expSignNotEnough = get("exp-sign-not-enough");

		selectRepairAnvil = get("select-repair-anvil");
		selectRedstoneConverter = get("select-redstone-converter");
		selectGoldConverter = get("select-gold-converter");
		selectEmeraldConverter = get("select-emerald-converter");
		selectRandomBox = get("select-random-box");
		selectDropParty = get("select-drop-party");
		selectExpSign = get("select-exp-sign");
		selectFrameGiver = get("select-frame-giver");
		setRepairAnvil = get("set-repair-anvil");
		setRedstoneConverter = get("set-redstone-converter");
		setGoldConverter = get("set-gold-converter");
		setEmeraldConverter = get("set-emerald-converter");
		setRandomBox = get("set-random-box");
		setDropParty = get("set-drop-party");
		setExpSign = get("set-exp-sign");
		setFrameGiver = get("set-frame-giver");
		removeRepairAnvil = get("remove-repair-anvil");
		removeRedstoneConverter = get("remove-redstone-converter");
		removeGoldConverter = get("remove-gold-converter");
		removeEmeraldConverter = get("remove-emerald-converter");
		removeRandomBox = get("remove-random-box");
		removeDropParty = get("remove-drop-party");
		removeExpSign = get("remove-exp-sign");
		removeFrameGiver = get("remove-frame-giver");
		selectCancel = get("select-cancel");

		unknownPlayer = get("unknown-player");
		playerOnly = get("player-only");
		reload = get("reload");
		cancel = get("cancel");
		bypassOn = get("bypass-on");
		bypassOff = get("bypass-off");
		bypassOnOther = get("bypass-on-other");
		bypassOffOther = get("bypass-off-other");
		resetDropParty = get("reset-drop-party");
		startDropParty = get("start-drop-party");
		noPermissionUse = get("no-permission-use");
		enchantLevelNotNumber = get("enchant-level-not-number");
		enchantLevelMax = get("enchant-level-max");
		enchantItemHand = get("enchant-item-hand");
		enchantItemAdd = get("enchant-item-add");
		enchantItemUpdate = get("enchant-item-update");
		enchantItemRemove = get("enchant-item-remove");
		soulboundAdd = get("soulbound-add");
		soulboundRemove = get("soulbound-remove");
		shopNoPermission = get("shop-no-permission");
		shopBuy = get("shop-buy");
		shopNotEnough = get("shop-not-enough");
		listNotNumber = get("list-not-number");
		listLastPage = get("list-last-page");
		listInvalidPage = get("list-invalid-page");
		list = getList("list");
	}

	public String getRepairAnvilEmptyHand() {
		return repairAnvilEmptyHand;
	}

	public String getRepairAnvilAlreadyRepaired() {
		return repairAnvilAlreadyRepaired;
	}

	public String getRepairAnvilNotRepairable() {
		return repairAnvilNotRepairable;
	}

	public String getRepairAnvilNotEnough() {
		return repairAnvilNotEnough;
	}

	public String getRepairAnvilRepairing() {
		return repairAnvilRepairing;
	}

	public String getRepairAnvilRepaired() {
		return repairAnvilRepaired;
	}

	public String getSelectRepairAnvil() {
		return selectRepairAnvil;
	}

	public String getSetRepairAnvil() {
		return setRepairAnvil;
	}

	public String getSelectCancel() {
		return selectCancel;
	}

	public String getRedstoneConverterNotEnough() {
		return redstoneConverterNotEnough;
	}

	public String getGoldConverterNotEnough() {
		return goldConverterNotEnough;
	}

	public String getEmeraldConverterNotEnough() {
		return emeraldConverterNotEnough;
	}

	public String getRedstoneConverterUse() {
		return redstoneConverterUse;
	}

	public String getGoldConverterUse() {
		return goldConverterUse;
	}

	public String getEmeraldConverterUse() {
		return emeraldConverterUse;
	}

	public String getRandomBoxNotEnough() {
		return randomBoxNotEnough;
	}

	public String getRandomBoxInUse() {
		return randomBoxInUse;
	}

	public String getRandomBoxUse() {
		return randomBoxUse;
	}

	public String getRandomBoxDone() {
		return randomBoxDone;
	}

	public String getDropPartyNotReady() {
		return dropPartyNotReady;
	}

	public String getDropPartyAlreadyRunning() {
		return dropPartyAlreadyRunning;
	}

	public String getDropPartyActivate() {
		return dropPartyActivate;
	}

	public String getDropPartyReady() {
		return dropPartyReady;
	}

	public String getDropPartyNotEnoughPlayers() {
		return dropPartyNotEnoughPlayers;
	}

	public String getFrameGiverUse() {
		return frameGiverUse;
	}

	public String getSelectRedstoneConverter() {
		return selectRedstoneConverter;
	}

	public String getSelectGoldConverter() {
		return selectGoldConverter;
	}

	public String getSelectEmeraldConverter() {
		return selectEmeraldConverter;
	}

	public String getSelectRandomBox() {
		return selectRandomBox;
	}

	public String getSelectDropParty() {
		return selectDropParty;
	}

	public String getSelectExpSign() {
		return selectExpSign;
	}

	public String getSelectFrameGiver() {
		return selectFrameGiver;
	}

	public String getSetRedstoneConverter() {
		return setRedstoneConverter;
	}

	public String getSetGoldConverter() {
		return setGoldConverter;
	}

	public String getSetEmeraldConverter() {
		return setEmeraldConverter;
	}

	public String getSetRandomBox() {
		return setRandomBox;
	}

	public String getSetDropParty() {
		return setDropParty;
	}

	public String getSetExpSign() {
		return setExpSign;
	}

	public String getSetFrameGiver() {
		return setFrameGiver;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public String getUnknownPlayer() {
		return unknownPlayer;
	}

	public String getPlayerOnly() {
		return playerOnly;
	}

	public String getReload() {
		return reload;
	}

	public String getCancel() {
		return cancel;
	}

	public String getBypassOn() {
		return bypassOn;
	}

	public String getBypassOff() {
		return bypassOff;
	}

	public String getBypassOnOther() {
		return bypassOnOther;
	}

	public String getBypassOffOther() {
		return bypassOffOther;
	}

	public String getResetDropParty() {
		return resetDropParty;
	}

	public String getStartDropParty() {
		return startDropParty;
	}

	public String getNoPermissionUse() {
		return noPermissionUse;
	}

	public List<String> getList() {
		return list;
	}

	public String getRemoveRepairAnvil() {
		return removeRepairAnvil;
	}

	public String getRemoveRedstoneConverter() {
		return removeRedstoneConverter;
	}

	public String getRemoveGoldConverter() {
		return removeGoldConverter;
	}

	public String getRemoveEmeraldConverter() {
		return removeEmeraldConverter;
	}

	public String getRemoveRandomBox() {
		return removeRandomBox;
	}

	public String getRemoveDropParty() {
		return removeDropParty;
	}

	public String getRemoveExpSign() {
		return removeExpSign;
	}

	public String getRemoveFrameGiver() {
		return removeFrameGiver;
	}

	public String getExpSignNotEnough() {
		return expSignNotEnough;
	}

	public String getExpSignUse() {
		return expSignUse;
	}

	public String getEnchantLevelNotNumber() {
		return enchantLevelNotNumber;
	}

	public String getEnchantLevelMax() {
		return enchantLevelMax;
	}

	public String getEnchantItemHand() {
		return enchantItemHand;
	}

	public String getEnchantItemAdd() {
		return enchantItemAdd;
	}

	public String getEnchantItemUpdate() {
		return enchantItemUpdate;
	}

	public String getEnchantItemRemove() {
		return enchantItemRemove;
	}

	public String getShopNoPermission() {
		return shopNoPermission;
	}

	public String getShopBuy() {
		return shopBuy;
	}

	public String getShopNotEnough() {
		return shopNotEnough;
	}

	public String getSoulboundAdd() {
		return soulboundAdd;
	}

	public String getSoulboundRemove() {
		return soulboundRemove;
	}

	public String getListNotNumber() {
		return listNotNumber;
	}

	public String getListLastPage() {
		return listLastPage;
	}

	public String getListInvalidPage() {
		return listInvalidPage;
	}

}
