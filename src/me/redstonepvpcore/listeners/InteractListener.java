package me.redstonepvpcore.listeners;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.gadgets.FrameGiver;
import me.redstonepvpcore.gadgets.Gadget;
import me.redstonepvpcore.gadgets.GadgetManager;
import me.redstonepvpcore.gadgets.GadgetType;
import me.redstonepvpcore.messages.Messages;
import me.redstonepvpcore.messages.TimeFormatter;
import me.redstonepvpcore.player.GadgetSetterManager;
import me.redstonepvpcore.utils.ConfigCreator;

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
		PlayerInteractAtEntityEvent.getHandlerList().unregister(parent);
		PlayerInteractEntityEvent.getHandlerList().unregister(parent);
		HangingBreakByEntityEvent.getHandlerList().unregister(parent);
	}

	public RedstonePvPCore getParent() {
		return parent;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) return;
		Player player = e.getPlayer();
		UUID uniqueId = player.getUniqueId();
		Location location = clickedBlock.getLocation();
		// Check if player is within a map and has a gadget to set and register
		if (player.isOp() && GadgetSetterManager.isAssigned(uniqueId)) {
			// Convert location back to string format, so it can be stored in the config
			// file
			String stringLocation = GadgetManager.deparseLocation(location);
			// Get the gadget the player is trying to create / register / set
			Gadget gadget = GadgetSetterManager.getAssignedGadget(uniqueId);
			if (GadgetManager.isEntityGadget(gadget)) return;
			// Special case for cooldown gadgets
			if (gadget.getType() == GadgetType.COOLDOWN) {
				if (!GadgetManager.isGadget(location)) {
					Messages.sendMessage(player, gadget.getMessagesHolder().getMessage(2));
				} else {
					int timeLeft = GadgetSetterManager.getAssignedCooldown(uniqueId);
					GadgetManager.addCooldown(GadgetManager.deparseSectionLocation(location), timeLeft);
					Messages.sendMessage(player,
							gadget.getMessagesHolder()
									.getMessage(0)
									.replace("%location%", stringLocation)
									.replace("%time%", String.valueOf(timeLeft))
									.replace("%time_long%", TimeFormatter.formatLong(timeLeft, true))
									.replace("%time_split%", TimeFormatter.formatShortSplit(timeLeft, true))
									.replace("%time_short%", TimeFormatter.formatShort(timeLeft, true)));
				}
			} else {
				GadgetManager.addGadget(gadget, stringLocation, GadgetSetterManager.getAssignedSubType(uniqueId));
				Messages.sendMessage(player,
						gadget.getMessagesHolder().getMessage(0).replace("%location%", stringLocation));
			}

			GadgetManager.saveGadgets();
			ConfigCreator.saveConfig("data.yml");
			getParent().getMainCommand().updatePagedList();
			// Remove assigned gadget from player because he did set the gadget from the
			// code above
			GadgetSetterManager.cancel(uniqueId);
			// Cancel event to prevent opening containers such as workbenches and anvils
			e.setCancelled(true);
			return;
		}
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Gadget gadget = GadgetManager.getGadget(location);
			if (gadget == null) return; // There is no gadget on that location. Stop!
			e.setCancelled(true);
			if (gadget.testCooldown(player)) gadget.perform(player);

		} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (!GadgetManager.isGadget(location) || !player.isOp()) return;
			Gadget gadget = GadgetManager.removeGadget(location);
			Messages.sendMessage(player,
					gadget.getMessagesHolder()
							.getMessage(1)
							.replace("%location%", GadgetManager.deparseLocation(location)));
			GadgetManager.saveGadgets();
			ConfigCreator.saveConfig("data.yml");
			getParent().getMainCommand().updatePagedList();
		}
	}

	@EventHandler
	public void onEntityInteractAt(PlayerInteractAtEntityEvent e) {
		Entity entity = e.getRightClicked();
		if (entity.getType() != EntityType.ITEM_FRAME) return;

		Player player = e.getPlayer();
		UUID uniqueId = player.getUniqueId();
		Location location = entity.getLocation();
		if (GadgetSetterManager.isAssigned(uniqueId)) {
			String stringLocation = GadgetManager.deparseLocation(location);
			Gadget gadget = GadgetSetterManager.getAssignedGadget(uniqueId);
			if (gadget.getType() == GadgetType.COOLDOWN) {
				if (!GadgetManager.isGadget(location)) {
					Messages.sendMessage(player, gadget.getMessagesHolder().getMessage(2));
				} else {
					int timeLeft = GadgetSetterManager.getAssignedCooldown(uniqueId);
					GadgetManager.addCooldown(GadgetManager.deparseSectionLocation(location), timeLeft);
					Messages.sendMessage(player,
							gadget.getMessagesHolder()
									.getMessage(0)
									.replace("%location%", stringLocation)
									.replace("%time%", String.valueOf(timeLeft))
									.replace("%time_long%", TimeFormatter.formatLong(timeLeft, true))
									.replace("%time_split%", TimeFormatter.formatShortSplit(timeLeft, true))
									.replace("%time_short%", TimeFormatter.formatShort(timeLeft, true)));
				}
			} else {
				Messages.sendMessage(player,
						gadget.getMessagesHolder().getMessage(0).replace("%location%", stringLocation));
				GadgetManager.addFrameGiver(stringLocation);
			}
			GadgetManager.saveGadgets();
			ConfigCreator.saveConfig("data.yml");
			getParent().getMainCommand().updatePagedList();
			GadgetSetterManager.cancel(uniqueId);
			e.setCancelled(true);
			return;
		}
		if (player.getGameMode().equals(GameMode.CREATIVE) && player.isSneaking()) {
			e.setCancelled(false);
			return;
		}
		if (GadgetManager.isGadget(location)) {
			FrameGiver frameGiver = (FrameGiver) GadgetManager.getGadget(location);
			if (!frameGiver.testCooldown(player)) return;
			frameGiver.perform(entity);
			frameGiver.perform(player);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent e) {
		Entity entity = e.getRightClicked();
		if (entity == null) return;
		if (entity.getType() != EntityType.ITEM_FRAME) return;
		Player player = e.getPlayer();
		if (player.getGameMode().equals(GameMode.CREATIVE) && player.isSneaking()) {
			e.setCancelled(false);
		} else {
			if (GadgetManager.isGadget(entity.getLocation())) e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityBreak(HangingBreakByEntityEvent e) {
		Entity entity = e.getEntity();
		if (entity == null) return;
		if (entity.getType() != EntityType.ITEM_FRAME) return;

		Entity remover = e.getRemover();
		if (!(remover instanceof Player) || e.getCause() == RemoveCause.EXPLOSION) {
			e.setCancelled(true);
			return;
		}

		Location location = entity.getLocation();
		Player player = (Player) remover;

		if (GadgetManager.isGadget(location) && player.isOp()) {
			Gadget gadget = GadgetManager.removeGadget(location);
			Messages.sendMessage(player,
					gadget.getMessagesHolder()
							.getMessage(1)
							.replace("%location%", GadgetManager.deparseLocation(location)));
			GadgetManager.saveGadgets();
			ConfigCreator.saveConfig("data.yml");
			getParent().getMainCommand().updatePagedList();
		}
	}

}
