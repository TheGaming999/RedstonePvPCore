package me.redstonepvpcore.listeners;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.gadgets.GadgetManager;
import me.redstonepvpcore.messages.Messages;
import me.redstonepvpcore.gadgets.Gadget;
import me.redstonepvpcore.player.GadgetSetterManager;

public class InteractListener implements Listener {

	private RedstonePvPCore parent;

	public InteractListener(RedstonePvPCore parent) {
		this.parent = parent;
	}

	public void register() {
		parent.getServer().getPluginManager().registerEvents(this, parent);
	}

	public void unregister() {
		PlayerInteractEvent.getHandlerList().unregister(parent);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Block clickedBlock = e.getClickedBlock();
		if(clickedBlock == null) return;
		Player player = e.getPlayer();
		UUID uniqueId = player.getUniqueId();
		Location location = clickedBlock.getLocation();
		// Check if player is within a map and has a gadget to set and register
		if(GadgetSetterManager.isAssigned(uniqueId)) {
			// Convert location back to string format, so it can be stored in the config file
			String stringLocation = GadgetManager.deparseLocation(location);
			// Get the gadget the player is trying to create / register / set
			Gadget gadget = GadgetSetterManager.getAssignedGadget(uniqueId);
			GadgetManager.addGadget(gadget, stringLocation, GadgetSetterManager.getAssignedSubType(uniqueId));
			Messages.sendMessage(player, gadget.getMessagesHolder().getMessage(0)
					.replace("%location%", stringLocation));
			// Remove assigned gadget from player because he did set the gadget from the code above
			GadgetSetterManager.cancel(uniqueId);
			// Cancel event to prevent opening containers such as workbenches and anvils
			e.setCancelled(true);
			return;
		}
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Gadget gadget = GadgetManager.getGadget(location);
			if(gadget == null) return; // There is no gadget on that location. Stop!
			gadget.perform(player);
			// Cancel event to prevent opening containers such as workbenches and anvils
			e.setCancelled(true);
		} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if(!GadgetManager.isGadget(location)) return;
			Gadget gadget = GadgetManager.removeGadget(location);
			Messages.sendMessage(player, gadget.getMessagesHolder().getMessage(1)
					.replace("%location%", GadgetManager.deparseLocation(location)));
		}
	}

}
