package me.redstonepvpcore.gadgets;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.player.BypassManager;
import me.redstonepvpcore.player.Permissions;
import me.redstonepvpcore.utils.XSound.Record;

public class Converter extends Gadget {

	private ConverterType converterType;
	private ItemStack giveItemStack, takeItemStack;
	private String useMessage, notEnoughMessage;
	private Record useRecord;
	
	public Converter(Location location) {
		super(GadgetType.CONVERTER, location);
	}

	public void setConverterType(ConverterType converterType) {
		this.converterType = converterType;
	}

	public void fetchConverter() {
		switch(converterType) {
		case REDSTONE:
			giveItemStack = getParent().getConverterMother().getRedstoneGiveItemStack();
			takeItemStack = getParent().getConverterMother().getRedstoneTakeItemStack();
			useMessage = getParent().getMessages().getRedstoneConverterUse();
			notEnoughMessage = getParent().getMessages().getRedstoneConverterNotEnough();
			getMessagesHolder().setMessage(0, getParent().getMessages().getSetRedstoneConverter());
			getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveRedstoneConverter());
			useRecord = getParent().getConverterMother().getRedstoneUseRecord();
			break;
		case GOLD:
			giveItemStack = getParent().getConverterMother().getGoldGiveItemStack();
			takeItemStack = getParent().getConverterMother().getGoldTakeItemStack();
			useMessage = getParent().getMessages().getGoldConverterUse();
			notEnoughMessage = getParent().getMessages().getGoldConverterNotEnough();
			getMessagesHolder().setMessage(0, getParent().getMessages().getSetGoldConverter());
			getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveGoldConverter());
			useRecord = getParent().getConverterMother().getGoldUseRecord();
			break;
		case EMERALD:
			giveItemStack = getParent().getConverterMother().getEmeraldGiveItemStack();
			takeItemStack = getParent().getConverterMother().getEmeraldTakeItemStack();
			useMessage = getParent().getMessages().getEmeraldConverterUse();
			notEnoughMessage = getParent().getMessages().getEmeraldConverterNotEnough();
			getMessagesHolder().setMessage(0, getParent().getMessages().getSetEmeraldConverter());
			getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveEmeraldConverter());
			useRecord = getParent().getConverterMother().getEmeraldUseRecord();
			break;
		}
	}

	public ConverterType getConverterType() {
		return this.converterType;
	}

	@Override
	public boolean perform(Player player) {
		boolean isBypassing = BypassManager.isBypassOn(player.getUniqueId());
		if(!Permissions.hasPermission(player, Permissions.CONVERTERS_USE_PERMISSION)) {
			sendMessage(player, getParent().getMessages().getNoPermissionUse());
			return false;
		}
		int amount = takeItemStack.getAmount();
		if(!isBypassing && !player.getInventory().containsAtLeast(takeItemStack, amount)) {
			sendMessage(player, notEnoughMessage.replace("%amount%", String.valueOf(amount)));
			return false;
		}
		if(!isBypassing) player.getInventory().removeItem(takeItemStack);
		player.getInventory().addItem(giveItemStack);
		sendMessage(player, useMessage);
		if(useRecord != null) useRecord.forPlayer(player).play();
		return true;
	}

	@Override
	public boolean setup() {
		return false;
	}

}
