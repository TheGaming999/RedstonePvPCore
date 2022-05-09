package me.redstonepvpcore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;

import me.redstonepvpcore.RedstonePvPCore;

public class ItemMergeListener implements Listener {

	private RedstonePvPCore parent;

	public ItemMergeListener(RedstonePvPCore parent) {
		this.parent = parent;
	}

	public void register() {
		parent.getServer().getPluginManager().registerEvents(this, parent);
	}

	public void unregister() {
		ItemMergeEvent.getHandlerList().unregister(parent);
	}

	@EventHandler
	public void onItemMerge(ItemMergeEvent e) {
		if(!parent.isDropPartyRunning()) return;
		e.setCancelled(true);
	}
	
}
