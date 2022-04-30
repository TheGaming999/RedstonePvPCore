package me.redstonepvpcore.mothers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.ItemStackReader;
import me.redstonepvpcore.utils.XSound;
import me.redstonepvpcore.utils.XSound.Record;

public class RandomBoxMother {

	private Record useRecord;
	private Record animationRecord;
	private Record endRecord;
	private ItemStack takeItemStack;
	private Map<String, Integer> usePermissions = new HashMap<>();
	private double shuffleDuration;
	private List<ItemStack> items = new ArrayList<>();
	private List<ItemStack> displayItems = new ArrayList<>();

	public RandomBoxMother() {
		setup();	
	}

	public void setup() {
		usePermissions.clear();
		items.clear();
		displayItems.clear();
		FileConfiguration config = ConfigCreator.getConfig("randombox.yml");
		ConfigurationSection useSoundSection = config.getConfigurationSection("use-sound");
		ConfigurationSection animationSoundSection = config.getConfigurationSection("animation-sound");
		ConfigurationSection endSoundSection = config.getConfigurationSection("end-sound");
		Record record = new Record(XSound.matchXSound(useSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)useSoundSection.getDouble("volume"), 
				(float)useSoundSection.getDouble("pitch"), 
				useSoundSection.getBoolean("3d", true));
		if(record.sound != null) useRecord = record;
		record = new Record(XSound.matchXSound(animationSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)animationSoundSection.getDouble("volume"), 
				(float)animationSoundSection.getDouble("pitch"), 
				animationSoundSection.getBoolean("3d", true));
		if(record.sound != null) animationRecord = record;
		record = new Record(XSound.matchXSound(endSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)endSoundSection.getDouble("volume"), 
				(float)endSoundSection.getDouble("pitch"), 
				endSoundSection.getBoolean("3d", true));
		if(record.sound != null) endRecord = record;
		takeItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("take-item"), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
		ConfigurationSection usePermissionsSection = config.getConfigurationSection("use-permissions");
		usePermissionsSection.getKeys(false).forEach(cost -> {
			usePermissions.put(usePermissionsSection.getString(cost), Integer.parseInt(cost));
		});
		shuffleDuration = config.getDouble("shuffle-duration");
		ConfigurationSection itemsSection = config.getConfigurationSection("items");
		itemsSection.getKeys(false).forEach(position -> {
			items.add(ItemStackReader.fromConfigurationSection(itemsSection.getConfigurationSection(position), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " "));
			displayItems.add(ItemStackReader.fromConfigurationSection(itemsSection.getConfigurationSection(position), 
					"display-material", "display-amount", "display-data", "display-name", "display-lore", 
					"display-enchantments", "display-flags", " "));
		});
	}

	public void setUseSound(Record record) {
		this.useRecord = record;
	}

	public Record getUseSound() {
		return useRecord;
	}

	public void setAnimationSound(Record record) {
		this.animationRecord = record;
	}

	public Record getAnimationSound() {
		return animationRecord;
	}

	public Record getEndSound() {
		return endRecord;
	}

	public void setEndSound(Record record) {
		this.endRecord = record;
	}

	public ItemStack getTakeItemStack() {
		return takeItemStack;
	}

	public void setTakeItemStack(ItemStack takeItemStack) {
		this.takeItemStack = takeItemStack;
	}

	public double getShuffleDuration() {
		return shuffleDuration;
	}

	public void setShuffleDuration(double shuffleDuration) {
		this.shuffleDuration = shuffleDuration;
	}

	public List<ItemStack> getItems() {
		return items;
	}
	
	public List<ItemStack> getDisplayItems() {
		return displayItems;
	}

	public Map<String, Integer> getUsePermissions() {
		return usePermissions;
	}
	
}
