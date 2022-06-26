package me.redstonepvpcore.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.gadgets.Gadget;
import me.redstonepvpcore.player.BypassManager;
import me.redstonepvpcore.shop.Shop;
import me.redstonepvpcore.utils.NBTEditor;
import me.redstonepvpcore.utils.XMaterial;

public class InventoryListener implements Listener {

	private RedstonePvPCore parent;
	private final ItemStack lapis = XMaterial.LAPIS_LAZULI.parseItem();
	private final ItemStack air = XMaterial.AIR.parseItem();
	private Shop shop;

	public InventoryListener(RedstonePvPCore parent) {
		this.parent = parent;
	}

	public void register() {
		shop = parent.getShop();
		parent.getServer().getPluginManager().registerEvents(this, parent);
	}

	public void unregister() {
		InventoryOpenEvent.getHandlerList().unregister(parent);
		InventoryCloseEvent.getHandlerList().unregister(parent);
		InventoryClickEvent.getHandlerList().unregister(parent);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getType() == InventoryType.ENCHANTING) {
			EnchantingInventory einv = (EnchantingInventory) inv;
			lapis.setAmount(64);
			einv.setSecondary(lapis);
		} else if (e.getView().getTitle().equals(shop.getInventoryName())) {
			Gadget.sendSound((Player) e.getPlayer(), shop.getOpenSound());
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getType() == InventoryType.ENCHANTING) {
			EnchantingInventory einv = (EnchantingInventory) inv;
			einv.setSecondary(air);
		} else if (e.getView().getTitle().equals(shop.getInventoryName())) {
			Gadget.sendSound((Player) e.getPlayer(), shop.getCloseSound());
		}
	}

	@EventHandler
	public void onInventoryItem(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		ItemStack stack = e.getCurrentItem();
		if (stack == null || stack.getType() == Material.AIR) return;
		Player p = (Player) e.getWhoClicked();
		if (inv.getType() == InventoryType.ENCHANTING) {
			if (stack.isSimilar(lapis)) {
				e.setCancelled(true);
				return;
			}
		} else {
			if (e.getView().getTitle().equals(shop.getInventoryName())) {
				boolean isBypassing = BypassManager.isBypassOn(p.getUniqueId());
				String perm = NBTEditor.getString(stack, "rp-permission");
				if (perm == null) {
					e.setCancelled(true);
					return;
				}
				if (!isBypassing && perm != null && !p.hasPermission(perm)) {
					e.setCancelled(true);
					p.closeInventory();
					Gadget.sendMessage(p, parent.getMessages().getShopNoPermission());
					Gadget.sendSound(p, shop.getNoPermissionSound());
					return;
				}
				String configName = NBTEditor.getString(stack, "rp-configname");
				int cost = NBTEditor.getInt(stack, "rp-cost");

				if (cost < 0) return;

				ItemStack costItemStack = shop.getCostItemStacks().get(configName);
				if (costItemStack == null) {
					e.setCancelled(true);
					return;
				}
				costItemStack.setAmount(cost);
				if (isBypassing || p.getInventory().containsAtLeast(costItemStack, cost)) {
					if (!isBypassing) p.getInventory().removeItem(costItemStack);
					p.getInventory().addItem(shop.getRealItemStacks().get(configName));
					p.updateInventory();
					p.closeInventory();
					Gadget.sendMessage(p,
							parent.getMessages()
									.getShopBuy()
									.replace("%name%", stack.getItemMeta().getDisplayName())
									.replace("%cost%", String.valueOf(cost))
									.replace("%cost-item%", costItemStack.getType().name()));
					Gadget.sendSound(p, shop.getBuySound());
				} else {
					p.closeInventory();
					Gadget.sendMessage(p, parent.getMessages().getShopNotEnough());
					Gadget.sendSound(p, shop.getNotEnoughSound());
				}
				e.setCancelled(true);
			}
		}
	}

}
