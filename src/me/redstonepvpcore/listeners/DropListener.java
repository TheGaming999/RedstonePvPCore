package me.redstonepvpcore.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.soulbound.SoulBoundManager;

public class DropListener implements Listener {

	private RedstonePvPCore parent;
	private SoulBoundManager soulBoundManager;
	private Map<UUID, List<ItemStack>> keepSoulbound;

	public DropListener(RedstonePvPCore parent) {
		this.parent = parent;
	}

	public void register() {
		soulBoundManager = parent.getSoulBoundManager();
		keepSoulbound = new HashMap<>();
		parent.getServer().getPluginManager().registerEvents(this, parent);
	}

	public void unregister() {
		ItemMergeEvent.getHandlerList().unregister(parent);
		PlayerDropItemEvent.getHandlerList().unregister(parent);
		PlayerDeathEvent.getHandlerList().unregister(parent);
		PlayerRespawnEvent.getHandlerList().unregister(parent);
	}

	public void keepItem(UUID uniqueId, ItemStack itemStack) {
		if (!keepSoulbound.containsKey(uniqueId)) keepSoulbound.put(uniqueId, new ArrayList<>());
		keepSoulbound.get(uniqueId).add(itemStack);
	}

	public Map<UUID, List<ItemStack>> getKeptSoulboundedItems() {
		return keepSoulbound;
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
			if (soulBoundManager.isSoulBounded(drop)) {
				if (soulBoundManager.isKeepAfterDeath()) keepItem(e.getEntity().getUniqueId(), drop.clone());
				drop.setType(Material.AIR);
			}
		});
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if (!soulBoundManager.isKeepAfterDeath()) return;
		parent.doSyncLater(() -> {
			Player player = e.getPlayer();
			UUID uniqueId = player.getUniqueId();
			List<ItemStack> keptItems = keepSoulbound.get(uniqueId);
			if (keptItems == null) return;
			keptItems.forEach(player.getInventory()::addItem);
			keepSoulbound.remove(uniqueId);
		}, 2);
	}

}
