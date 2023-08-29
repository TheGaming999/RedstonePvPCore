package me.redstonepvpcore.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.enchantments.EnchantmentManager.EnchantResult;
import me.redstonepvpcore.sounds.SoundInfo;
import me.redstonepvpcore.sounds.SoundParser;
import me.redstonepvpcore.utils.Colorizer;
import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.ItemStackReader;
import me.redstonepvpcore.utils.NBTEditor;

public class Shop {

	private Inventory inventory;
	private String invName;
	private int slots;
	private SoundInfo openSound;
	private SoundInfo closeSound;
	private SoundInfo buySound;
	private SoundInfo notEnoughSound;
	private SoundInfo noPermissionSound;
	private Map<String, ItemStack> realItemStacks;
	private Map<String, ItemStack> costItemStacks;
	private FileConfiguration config;

	public Shop() {
		loadInventory();
	}

	public static boolean isPurchased(ItemStack itemStack) {
		return NBTEditor.contains(itemStack, "rp-configname");
	}

	public void loadInventory() {
		config = ConfigCreator.getConfig("shop.yml");
		invName = Colorizer.colorize(config.getString("gui.title", "Shop"));
		slots = this.config.getInt("gui.size", 54);
		inventory = Bukkit.createInventory(null, slots, Colorizer.colorize(invName));
		realItemStacks = new HashMap<>();
		costItemStacks = new HashMap<>();
		openSound = SoundParser.parse(config.getConfigurationSection("open-sound"));
		closeSound = SoundParser.parse(config.getConfigurationSection("close-sound"));
		buySound = SoundParser.parse(config.getConfigurationSection("buy-sound"));
		notEnoughSound = SoundParser.parse(config.getConfigurationSection("not-enough-sound"));
		noPermissionSound = SoundParser.parse(config.getConfigurationSection("no-permission-sound"));
		ConfigurationSection section = config.getConfigurationSection("gui.items");
		Set<String> shopMenuItems = section.getKeys(false);
		for (String key : shopMenuItems) {
			String configName = key;
			ItemStack itemStack = ItemStackReader.fromConfigurationSection(section.getConfigurationSection(key),
					"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
			ItemStack costItemStack = ItemStackReader.fromConfigurationSection(section.getConfigurationSection(key),
					"cost-material", "cost-amount", "cost-data", "cost-name", "cost-lore", "cost-enchantments",
					"cost-flags", " ");
			int slot = section.getInt(key + ".slot", 0);
			String permission = section.getString(key + ".permission", "");
			int cost = section.getInt(key + ".cost", 0);

			List<String> customEnchantmentsList = section.getStringList(key + ".custom-enchantments");
			if (customEnchantmentsList != null && !customEnchantmentsList.isEmpty()) {
				for (String enchantment : customEnchantmentsList) {
					String[] split = enchantment.split(" ");
					String name = split[0];
					int lvl = RedstonePvPCore.getInstance().getEnchantmentManager().parse(split[1]);
					EnchantResult result = RedstonePvPCore.getInstance()
							.getEnchantmentManager()
							.enchant(itemStack, name, lvl, true);
					if (result.isSuccessful()) itemStack = result.getItemStack();
				}
			}
			boolean soulBound = section.getBoolean(key + ".soulbound");
			if (soulBound) itemStack = RedstonePvPCore.getInstance().getSoulBoundManager().addSoulBound(itemStack);

			realItemStacks.put(configName, itemStack);
			costItemStacks.put(configName, costItemStack);
			ItemStack shopItemStack = ItemStackReader.fromConfigurationSection(section.getConfigurationSection(key),
					"display-material", "display-amount", "display-data", "display-name", "display-lore",
					"display-enchantments", "display-flags", " ");

			shopItemStack = NBTEditor.set(shopItemStack, configName, "rp-configname");
			shopItemStack = NBTEditor.set(shopItemStack, slot, "rp-slot");
			shopItemStack = NBTEditor.set(shopItemStack, permission, "rp-permission");
			shopItemStack = NBTEditor.set(shopItemStack, cost, "rp-cost");
			inventory.setItem(slot, shopItemStack);
		}
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public String getInventoryName() {
		return invName;
	}

	public void setInventoryName(String invName) {
		this.invName = invName;
	}

	public int getSlots() {
		return slots;
	}

	public void setSlots(int slots) {
		this.slots = slots;
	}

	public SoundInfo getOpenSound() {
		return openSound;
	}

	public void setOpenSound(SoundInfo openSound) {
		this.openSound = openSound;
	}

	public SoundInfo getCloseSound() {
		return closeSound;
	}

	public void setCloseSound(SoundInfo closeSound) {
		this.closeSound = closeSound;
	}

	public SoundInfo getBuySound() {
		return buySound;
	}

	public void setBuySound(SoundInfo buySound) {
		this.buySound = buySound;
	}

	public SoundInfo getNotEnoughSound() {
		return notEnoughSound;
	}

	public void setNotEnoughSound(SoundInfo notEnoughSound) {
		this.notEnoughSound = notEnoughSound;
	}

	public SoundInfo getNoPermissionSound() {
		return noPermissionSound;
	}

	public void setNoPermissionSound(SoundInfo noPermissionSound) {
		this.noPermissionSound = noPermissionSound;
	}

	public Map<String, ItemStack> getRealItemStacks() {
		return realItemStacks;
	}

	public void setRealItemStacks(Map<String, ItemStack> realItemStacks) {
		this.realItemStacks = realItemStacks;
	}

	public Map<String, ItemStack> getCostItemStacks() {
		return costItemStacks;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void setConfig(FileConfiguration config) {
		this.config = config;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

}
