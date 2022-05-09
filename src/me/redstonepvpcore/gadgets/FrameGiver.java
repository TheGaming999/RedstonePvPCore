package me.redstonepvpcore.gadgets;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.player.Permissions;
import me.redstonepvpcore.sounds.SoundInfo;

public class FrameGiver extends Gadget implements EntityGadget {

	private ItemStack itemStack;
	private boolean giveStack;
	private SoundInfo sound;
	
	public FrameGiver(Location location) {
		super(GadgetType.FRAME_GIVER, location);
		getMessagesHolder().setMessage(0, getParent().getMessages().getSetFrameGiver());
		getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveFrameGiver());
	}

	@Override
	public boolean perform(Player player) {
		if(!Permissions.hasPermission(player, Permissions.FRAMEGIVER_USE_PERMISSION)) {
			sendMessage(player, getParent().getMessages().getNoPermissionUse());
			return false;
		}
		if(itemStack == null) return false;
		player.getInventory().addItem(itemStack);
		sendSound(player, sound);
		sendMessage(player, getParent().getMessages().getFrameGiverUse());
		return true;
	}

	@Override
	public boolean setup() {
		giveStack = getParent().getFrameGiverMother().isGiveStack();
		sound = getParent().getFrameGiverMother().getUseSound();
		return true;
	}

	@Override
	public void perform(Entity entity) {
		itemStack = null;
		ItemFrame itemFrame = (ItemFrame)entity;
		ItemStack frameItem = itemFrame.getItem();
		if(frameItem == null || frameItem.getType() == null) return;
		if(giveStack) frameItem.setAmount(frameItem.getMaxStackSize());
		itemStack = frameItem;
	}


}
