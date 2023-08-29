package me.redstonepvpcore.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.soulbound.SoulBoundManager;

public class DropListener implements Listener {

	private RedstonePvPCore parent;
	private SoulBoundManager soulBoundManager;

	public DropListener(RedstonePvPCore parent) {
		this.parent = parent;
	}

	public void register() {
		soulBoundManager = parent.getSoulBoundManager();
		parent.getServer().getPluginManager().registerEvents(this, parent);
	}

	public void unregister() {
		ItemMergeEvent.getHandlerList().unregister(parent);
		PlayerDropItemEvent.getHandlerList().unregister(parent);
		PlayerDeathEvent.getHandlerList().unregister(parent);
	}

	@EventHandler
	public void onItemMerge(ItemMergeEvent e) {
		if (!parent.isDropPartyRunning()) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Item dropItem = e.getItemDrop();
		if (dropItem == null) return;
		ItemStack stack = dropItem.getItemStack();
		if (!soulBoundManager.isAllowDrop() && soulBoundManager.isSoulBounded(stack)) e.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (soulBoundManager.isDropOnDeath()) return;
		List<ItemStack> drops = e.getDrops();
		drops.forEach(drop -> {
			if (soulBoundManager.isSoulBounded(drop)) drop.setType(Material.AIR);
		});
	}

}
