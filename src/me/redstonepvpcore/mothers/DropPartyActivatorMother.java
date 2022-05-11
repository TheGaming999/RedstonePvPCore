package me.redstonepvpcore.mothers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.sounds.SoundInfo;
import me.redstonepvpcore.sounds.SoundParser;
import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.ItemStackParser;

public class DropPartyActivatorMother {

	private SoundInfo useRecord;
	private SoundInfo readyRecord;
	private SoundInfo endRecord;
	private SoundInfo dropRecord;
	private SoundInfo waterSpawnRecord;
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
	private boolean changeBetweenDropsSpeed;
	private Map<Integer, Integer> changeBetweenDropsSpeedAtTick;
	private boolean debugTicks;

	public DropPartyActivatorMother() {
		setup();	
	}

	public void setup() {
		FileConfiguration config = ConfigCreator.getConfig("drop-party.yml");
		ConfigurationSection useSoundSection = config.getConfigurationSection("start-sound");
		ConfigurationSection readySoundSection = config.getConfigurationSection("ready-sound");
		ConfigurationSection endSoundSection = config.getConfigurationSection("end-sound");
		ConfigurationSection dropSoundSection = config.getConfigurationSection("drop-sound");
		ConfigurationSection waterSpawnSoundSection = config.getConfigurationSection("water-spawn-sound");
		
		changeBetweenDropsSpeedAtTick = new HashMap<>();
		
		useRecord = SoundParser.parse(useSoundSection);
		
		if(SoundParser.isNBSParse(useSoundSection)) {
			String unformattedChangeBetweenDrops = useSoundSection.getString("change-between-drops-speed-at-tick", "");
			if(!unformattedChangeBetweenDrops.equals("")) changeBetweenDropsSpeed = true;
			String[] split = unformattedChangeBetweenDrops.split(",");
			for(String tickAndSpeed : split) {
				String[] splitTickAndSpeed = tickAndSpeed.split("->");
				String tick = splitTickAndSpeed[0];
				String speed = splitTickAndSpeed[1];
				changeBetweenDropsSpeedAtTick.put(Integer.parseInt(tick), Integer.valueOf(speed));
			}
			debugTicks = config.getBoolean("debug-ticks");
		}

		readyRecord = SoundParser.parse(readySoundSection);
		endRecord = SoundParser.parse(endSoundSection);
		dropRecord = SoundParser.parse(dropSoundSection);
		waterSpawnRecord = SoundParser.parse(waterSpawnSoundSection);

		broadcastStartSound = config.getBoolean("broadcast-start-sound");
		broadcastStartMessage = config.getBoolean("broadcast-start-message");
		broadcastReadySound = config.getBoolean("broadcast-ready-sound");
		broadcastReadyMessage = config.getBoolean("broadcast-ready-message");
		cooldownDuration = config.getDouble("cooldown-duration");
		requiredPlayers = config.getInt("required-players");
		droppingDuration = config.getInt("dropping-duration");
		betweenDropsDuration = config.getInt("between-drops-duration");
		alwaysOn = config.getBoolean("always-on");
		itemsToDrop = ItemStackParser.parseCustom(config.getStringList("items-to-drop"));
		waterSpawnDurations = new HashSet<>();
		String waterSpawnDurationsList = config.getString("water-spawn-durations");
		String[] waterSpawnDurationsSplit = waterSpawnDurationsList.split(",");
		waterRemoveDuration = config.getInt("water-remove-duration");
		for(int i = 0; i < waterSpawnDurationsSplit.length; i++) {
			waterSpawnDurations.add(Integer.parseInt(waterSpawnDurationsSplit[i]));
		}
	}

	public void setUseSound(SoundInfo record) {
		this.useRecord = record;
	}

	public SoundInfo getUseSound() {
		return useRecord;
	}

	public void setReadySound(SoundInfo record) {
		this.readyRecord = record;
	}

	public SoundInfo getReadySound() {
		return readyRecord;
	}

	public void setEndSound(SoundInfo record) {
		this.endRecord = record;
	}

	public SoundInfo getEndSound() {
		return endRecord;
	}

	public void setDropSound(SoundInfo record) {
		this.dropRecord = record;
	}

	public SoundInfo getDropSound() {
		return dropRecord;
	}

	public void setWaterSpawnSound(SoundInfo record) {
		this.waterSpawnRecord = record;
	}

	public SoundInfo getWaterSpawnSound() {
		return waterSpawnRecord;
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

	public boolean isChangeBetweenDropsSpeed() {
		return changeBetweenDropsSpeed;
	}

	public void setChangeBetweenDropsSpeed(boolean changeBetweenDropsSpeed) {
		this.changeBetweenDropsSpeed = changeBetweenDropsSpeed;
	}

	public Map<Integer, Integer> getChangeBetweenDropsSpeedAtTick() {
		return changeBetweenDropsSpeedAtTick;
	}

	public void setChangeBetweenDropsSpeedAtTick(Map<Integer, Integer> changeBetweenDropsSpeedAtTick) {
		this.changeBetweenDropsSpeedAtTick = changeBetweenDropsSpeedAtTick;
	}

	public Integer getDroppingSpeed(int tick) {
		return changeBetweenDropsSpeedAtTick.get(tick);
	}

	public void setDroppingSpeed(int tick, int newDroppingSpeed) {
		changeBetweenDropsSpeedAtTick.put(tick, newDroppingSpeed);
	}

	public boolean isDebugTicks() {
		return debugTicks;
	}

	public void setDebugTicks(boolean debugTicks) {
		this.debugTicks = debugTicks;
	}

}
