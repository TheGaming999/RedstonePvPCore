package me.redstonepvpcore.mothers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.ItemStackReader;
import me.redstonepvpcore.utils.XSound;
import me.redstonepvpcore.utils.XSound.Record;

public class ConverterMother {
	
	private Record redstoneUseRecord, goldUseRecord, emeraldUseRecord;
	private boolean playAs3D;
	private ItemStack redstoneTakeItemStack, redstoneGiveItemStack, goldTakeItemStack, goldGiveItemStack,
	emeraldTakeItemStack, emeraldGiveItemStack;
	
	public ConverterMother() {
		setup();
	}
	
	public void setup() {
		FileConfiguration config = ConfigCreator.getConfig("converters.yml");
		ConfigurationSection redstoneUseSoundSection = config.getConfigurationSection("redstone.use-sound");
		ConfigurationSection goldUseSoundSection = config.getConfigurationSection("gold.use-sound");
		ConfigurationSection emeraldUseSoundSection = config.getConfigurationSection("emerald.use-sound");
		Record record = new Record(XSound.matchXSound(redstoneUseSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)redstoneUseSoundSection.getDouble("volume"), 
				(float)redstoneUseSoundSection.getDouble("pitch"), 
				redstoneUseSoundSection.getBoolean("3d", false));
		if(record.sound != null) redstoneUseRecord = record;
		record = new Record(XSound.matchXSound(goldUseSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)goldUseSoundSection.getDouble("volume"), 
				(float)goldUseSoundSection.getDouble("pitch"), 
				goldUseSoundSection.getBoolean("3d", false));
		if(record.sound != null) goldUseRecord = record;
		record = new Record(XSound.matchXSound(emeraldUseSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)emeraldUseSoundSection.getDouble("volume"), 
				(float)emeraldUseSoundSection.getDouble("pitch"), 
				emeraldUseSoundSection.getBoolean("3d", false));
		if(record.sound != null) emeraldUseRecord = record;
		redstoneTakeItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("redstone.take-item"), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
		goldTakeItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("gold.take-item"), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
		emeraldTakeItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("emerald.take-item"), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
		redstoneGiveItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("redstone.give-item"), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
		goldGiveItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("gold.give-item"), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
		emeraldGiveItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("emerald.give-item"), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
	}
	
	public boolean playAs3D(boolean playAs3D) {
		return this.playAs3D = playAs3D;
	}
	
	public boolean is3D() {
		return this.playAs3D;
	}

	public Record getRedstoneUseRecord() {
		return redstoneUseRecord;
	}

	public void setRedstoneUseRecord(Record redstoneUseRecord) {
		this.redstoneUseRecord = redstoneUseRecord;
	}

	public Record getGoldUseRecord() {
		return goldUseRecord;
	}

	public void setGoldUseRecord(Record goldUseRecord) {
		this.goldUseRecord = goldUseRecord;
	}

	public Record getEmeraldUseRecord() {
		return emeraldUseRecord;
	}

	public void setEmeraldUseRecord(Record emeraldUseRecord) {
		this.emeraldUseRecord = emeraldUseRecord;
	}

	public ItemStack getRedstoneTakeItemStack() {
		return redstoneTakeItemStack;
	}

	public void setRedstoneTakeItemStack(ItemStack redstoneTakeItemStack) {
		this.redstoneTakeItemStack = redstoneTakeItemStack;
	}

	public ItemStack getRedstoneGiveItemStack() {
		return redstoneGiveItemStack;
	}

	public void setRedstoneGiveItemStack(ItemStack redstoneGiveItemStack) {
		this.redstoneGiveItemStack = redstoneGiveItemStack;
	}

	public ItemStack getGoldTakeItemStack() {
		return goldTakeItemStack;
	}

	public void setGoldTakeItemStack(ItemStack goldTakeItemStack) {
		this.goldTakeItemStack = goldTakeItemStack;
	}

	public ItemStack getGoldGiveItemStack() {
		return goldGiveItemStack;
	}

	public void setGoldGiveItemStack(ItemStack goldGiveItemStack) {
		this.goldGiveItemStack = goldGiveItemStack;
	}

	public ItemStack getEmeraldTakeItemStack() {
		return emeraldTakeItemStack;
	}

	public void setEmeraldTakeItemStack(ItemStack emeraldTakeItemStack) {
		this.emeraldTakeItemStack = emeraldTakeItemStack;
	}

	public ItemStack getEmeraldGiveItemStack() {
		return emeraldGiveItemStack;
	}

	public void setEmeraldGiveItemStack(ItemStack emeraldGiveItemStack) {
		this.emeraldGiveItemStack = emeraldGiveItemStack;
	}
	
}
