package me.redstonepvpcore.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopItem {

	private String configName;
	private ItemStack itemStack;
	private int slot;
	private String permission;
	private Material costItem;
	private int cost;

	public ShopItem() {}

	public ShopItem(String configName) {
		this.configName = configName;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public Material getCostItem() {
		return costItem;
	}

	public void setCostItem(Material costItem) {
		this.costItem = costItem;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

}
