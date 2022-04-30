package me.redstonepvpcore.mothers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.ItemStackParser;
import me.redstonepvpcore.utils.XSound;
import me.redstonepvpcore.utils.XSound.Record;

public class DropPartyActivatorMother {

	private Record useRecord;
	private Record readyRecord;
	private Record endRecord;
	private Record dropRecord;
	private boolean broadcastStartSound;
	private boolean broadcastReadySound;
	private boolean broadcastStartMessage;
	private boolean broadcastReadyMessage;
	private double cooldownDuration;
	private int requiredPlayers;
	private int droppingDuration;
	private int betweenDropsDuration;
	private boolean alwaysOn;
	private List<ItemStack> itemsToDrop;
	private Set<Integer> waterSpawnDurations;
	private int waterRemoveDuration;

	public DropPartyActivatorMother() {
		setup();	
	}

	public void setup() {
		FileConfiguration config = ConfigCreator.getConfig("drop-party.yml");
		ConfigurationSection useSoundSection = config.getConfigurationSection("start-sound");
		ConfigurationSection readySoundSection = config.getConfigurationSection("ready-sound");
		ConfigurationSection endSoundSection = config.getConfigurationSection("end-sound");
		ConfigurationSection dropSoundSection = config.getConfigurationSection("drop-sound");
		Record record = new Record(XSound.matchXSound(useSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)useSoundSection.getDouble("volume"), 
				(float)useSoundSection.getDouble("pitch"), 
				useSoundSection.getBoolean("3d", true));
		if(record.sound != null) useRecord = record;
		
		record = new Record(XSound.matchXSound(readySoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)readySoundSection.getDouble("volume"), 
				(float)readySoundSection.getDouble("pitch"), 
				readySoundSection.getBoolean("3d", true));
		if(record.sound != null) readyRecord = record;
		
		record = new Record(XSound.matchXSound(endSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)endSoundSection.getDouble("volume"), 
				(float)endSoundSection.getDouble("pitch"), 
				endSoundSection.getBoolean("3d", true));
		if(record.sound != null) endRecord = record;
		
		record = new Record(XSound.matchXSound(dropSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)dropSoundSection.getDouble("volume"), 
				(float)dropSoundSection.getDouble("pitch"), 
				dropSoundSection.getBoolean("3d", true));
		if(record.sound != null) dropRecord = record;
		
		broadcastStartSound = config.getBoolean("broadcast-start-sound");
		broadcastStartMessage = config.getBoolean("broadcast-start-message");
		broadcastReadySound = config.getBoolean("broadcast-ready-sound");
		broadcastReadyMessage = config.getBoolean("broadcast-ready-message");
		cooldownDuration = config.getDouble("cooldown-duration");
		requiredPlayers = config.getInt("required-players");
		droppingDuration = config.getInt("dropping-duration");
		betweenDropsDuration = config.getInt("between-drops-duration");
		alwaysOn = config.getBoolean("always-on");
		itemsToDrop = ItemStackParser.parse(config.getStringList("items-to-drop"));
		waterSpawnDurations = new HashSet<>();
		String waterSpawnDurationsList = config.getString("water-spawn-durations");
		String[] waterSpawnDurationsSplit = waterSpawnDurationsList.split(",");
		waterRemoveDuration = config.getInt("water-remove-duration");
		for(int i = 0; i < waterSpawnDurationsSplit.length; i++) {
			waterSpawnDurations.add(Integer.parseInt(waterSpawnDurationsSplit[i]));
		}
	}

	public void setUseSound(Record record) {
		this.useRecord = record;
	}

	public Record getUseSound() {
		return useRecord;
	}

	public void setReadySound(Record record) {
		this.readyRecord = record;
	}

	public Record getReadySound() {
		return readyRecord;
	}

	public void setEndSound(Record record) {
		this.endRecord = record;
	}
	
	public Record getEndSound() {
		return endRecord;
	}
	
	public void setDropSound(Record record) {
		this.dropRecord = record;
	}
	
	public Record getDropSound() {
		return dropRecord;
	}

	public boolean isBroadcastStartSound() {
		return broadcastStartSound;
	}

	public void setBroadcastStartSound(boolean broadcastStartSound) {
		this.broadcastStartSound = broadcastStartSound;
	}

	public boolean isBroadcastReadySound() {
		return broadcastReadySound;
	}

	public void setBroadcastReadySound(boolean broadcastReadySound) {
		this.broadcastReadySound = broadcastReadySound;
	}

	public boolean isBroadcastStartMessage() {
		return broadcastStartMessage;
	}

	public void setBroadcastStartMessage(boolean broadcastStartMessage) {
		this.broadcastStartMessage = broadcastStartMessage;
	}

	public boolean isBroadcastReadyMessage() {
		return broadcastReadyMessage;
	}

	public void setBroadcastReadyMessage(boolean broadcastReadyMessage) {
		this.broadcastReadyMessage = broadcastReadyMessage;
	}

	public double getCooldownDuration() {
		return cooldownDuration;
	}

	public void setCooldownDuration(double cooldownDuration) {
		this.cooldownDuration = cooldownDuration;
	}

	public int getRequiredPlayers() {
		return requiredPlayers;
	}

	public void setRequiredPlayers(int requiredPlayers) {
		this.requiredPlayers = requiredPlayers;
	}

	public int getDroppingDuration() {
		return droppingDuration;
	}

	public void setDroppingDuration(int droppingDuration) {
		this.droppingDuration = droppingDuration;
	}

	public int getBetweenDropsDuration() {
		return betweenDropsDuration;
	}

	public void setBetweenDropsDuration(int betweenDropsDuration) {
		this.betweenDropsDuration = betweenDropsDuration;
	}

	public boolean isAlwaysOn() {
		return alwaysOn;
	}

	public void setAlwaysOn(boolean alwaysOn) {
		this.alwaysOn = alwaysOn;
	}

	public List<ItemStack> getItemsToDrop() {
		return itemsToDrop;
	}

	public void setItemsToDrop(List<ItemStack> itemsToDrop) {
		this.itemsToDrop = itemsToDrop;
	}
	
	public Set<Integer> getWaterSpawnDurations() {
		return waterSpawnDurations;
	}
	
	public int getWaterRemoveDuration() {
		return waterRemoveDuration;
	}
	
}
