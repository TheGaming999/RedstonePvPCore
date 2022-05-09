package me.redstonepvpcore.gadgets;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.player.Permissions;
import me.redstonepvpcore.sounds.SoundInfo;

public class ExpSign extends Gadget {

	private int giveAmount;
	private ItemStack takeItemStack;
	private boolean giveLevels;
	private SoundInfo useSound;
	
	public ExpSign(Location location) {
		super(GadgetType.EXP_SIGN, location);
		getMessagesHolder().setMessage(0, getParent().getMessages().getSetExpSign());
		getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveExpSign());
	}

	@Override
	public boolean perform(Player player) {
		if(!Permissions.hasPermission(player, Permissions.EXPSIGN_USE_PERMISSION)) {
			sendMessage(player, getParent().getMessages().getNoPermissionUse());
			return false;
		}
		int amount = takeItemStack.getAmount();
		if(!player.getInventory().containsAtLeast(takeItemStack, amount)) {
			sendMessage(player, getParent().getMessages().getExpSignNotEnough()
					.replace("%amount%", String.valueOf(amount)));
			return false;
		}
		player.getInventory().removeItem(takeItemStack);
		if(isGiveLevels()) 
			player.giveExpLevels(giveAmount);
		else 
			player.giveExp(giveAmount);
		sendMessage(player, getParent().getMessages().getExpSignUse());
		sendSound(player, useSound);
		return true;
	}

	@Override
	public boolean setup() {
		giveAmount = getParent().getExpSignMother().getGiveAmount();
		giveLevels = getParent().getExpSignMother().isGiveLevels();
		takeItemStack = getParent().getExpSignMother().getTakeItemStack();
		useSound = getParent().getExpSignMother().getUseSound();
		return true;
	}

	public int getGiveAmount() {
		return giveAmount;
	}

	public void setGiveAmount(int giveAmount) {
		this.giveAmount = giveAmount;
	}

	public boolean isGiveLevels() {
		return giveLevels;
	}

	public void setGiveLevels(boolean giveLevels) {
		this.giveLevels = giveLevels;
	}

	public ItemStack getTakeItemStack() {
		return takeItemStack;
	}

	public void setTakeItemStack(ItemStack takeItemStack) {
		this.takeItemStack = takeItemStack;
	}


}
