package me.redstonepvpcore.gadgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.redstonepvpcore.messages.Messages;
import me.redstonepvpcore.messages.TimeFormatter;
import me.redstonepvpcore.player.BypassManager;
import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.CooldownScheduler.SimpleCooldownEntry;
import me.redstonepvpcore.utils.CooldownScheduler.SimpleCooldownScheduler;

public class Cooldown extends Gadget {

	private int duration;
	private String locationString;
	public Map<UUID, CooldownDuration> savedDurations;
	private Set<UUID> coolingDown = new HashSet<>();
	public final SimpleCooldownScheduler<UUID> COOLDOWN = new SimpleCooldownScheduler<>();

	public Cooldown(Location location) {
		super(GadgetType.COOLDOWN, location);
		getMessagesHolder().setMessage(0, getParent().getMessages().getSetCooldown());
		getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveCooldown());
		getMessagesHolder().setMessage(2, getParent().getMessages().getSelectCooldownNotGadget());
	}

	public Cooldown withDuration(int duration) {
		this.duration = duration;
		return this;
	}

	@Override
	public boolean setup() {
		savedDurations = new HashMap<>();
		// FileConfiguration dataConfig = ConfigCreator.getConfig("data.yml");
		// ConfigurationSection cooldownSection =
		// dataConfig.getConfigurationSection("cooldown");
		locationString = GadgetManager.deparseSectionLocation(getLocation());
		/*
		 * if (cooldownSection.isConfigurationSection(locationString)) {
		 * ConfigurationSection gadgetSection =
		 * cooldownSection.getConfigurationSection(locationString);
		 * duration = gadgetSection.getInt("duration");
		 * } else {
		 * Bukkit.getLogger()
		 * .severe("Error loading config location for cooldown: " + locationString +
		 * " parsed from: "
		 * + getLocation());
		 * }
		 */
		return true;
	}

	public void storeDurations() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			storeDuration(player.getUniqueId());
		});
	}

	public void storeDuration(UUID uniqueId) {
		SimpleCooldownEntry<UUID> entry = COOLDOWN.getEntry(uniqueId);
		if (savedDurations.containsKey(uniqueId)) {
			savedDurations.get(uniqueId).set(entry.getDuration(), entry.getStartTime());
			return;
		}
		savedDurations.put(uniqueId, new CooldownDuration(entry.getDuration(), entry.getStartTime()));
	}

	public void loadDuration(UUID uniqueId) {
		FileConfiguration dataConfig = ConfigCreator.getConfig("data.yml");
		ConfigurationSection cooldownSection = dataConfig.getConfigurationSection("cooldown");
		ConfigurationSection gadgetSection = cooldownSection.getConfigurationSection(locationString);
		ConfigurationSection playerSection = gadgetSection.getConfigurationSection("players." + uniqueId.toString());
		if (playerSection != null) {
			int duration = playerSection.getInt("duration");
			long system = playerSection.getLong("system");
			if (SimpleCooldownEntry.getLiveDuration(system, duration) > 0) {
				savedDurations.put(uniqueId,
						new CooldownDuration(playerSection.getInt("duration"), playerSection.getLong("system")));
			} else {
				savedDurations.remove(uniqueId);
				gadgetSection.set("players." + uniqueId.toString(), null);
			}
		}
	}

	public void saveDuration(UUID uniqueId) {
		FileConfiguration dataConfig = ConfigCreator.getConfig("data.yml");
		ConfigurationSection cooldownSection = dataConfig.getConfigurationSection("cooldown");
		ConfigurationSection gadgetSection = cooldownSection.getConfigurationSection(locationString);
		ConfigurationSection playerSection = gadgetSection.getConfigurationSection("players." + uniqueId.toString());
		CooldownDuration cooldownDuration = savedDurations.get(uniqueId);
		if (SimpleCooldownEntry.getLiveDuration(cooldownDuration.systemTime, cooldownDuration.duration) > 0) {
			if (playerSection == null) playerSection = gadgetSection.createSection("players." + uniqueId.toString());
			playerSection.set("duration", cooldownDuration.duration);
			playerSection.set("system", cooldownDuration.systemTime);
		} else {
			// Remove section if exists, since cooldown already ended (no need to save).
			gadgetSection.set("players." + uniqueId.toString(), null);
			savedDurations.remove(uniqueId);
		}
	}

	public void saveDurations() {
		FileConfiguration dataConfig = ConfigCreator.getConfig("data.yml");
		ConfigurationSection cooldownSection = dataConfig.getConfigurationSection("cooldown");
		ConfigurationSection gadgetSection = cooldownSection.getConfigurationSection(locationString);
		savedDurations.keySet().forEach(uniqueId -> {
			ConfigurationSection playerSection = gadgetSection
					.getConfigurationSection("players." + uniqueId.toString());
			CooldownDuration cooldownDuration = savedDurations.get(uniqueId);
			if (SimpleCooldownEntry.getLiveDuration(cooldownDuration.systemTime, cooldownDuration.duration) > 0) {
				if (playerSection == null)
					playerSection = gadgetSection.createSection("players." + uniqueId.toString());
				if (cooldownDuration != null) {
					playerSection.set("duration", cooldownDuration.duration);
					playerSection.set("system", cooldownDuration.systemTime);
				}
			} else {
				// Remove section if exists, since cooldown already ended (no need to save).
				gadgetSection.set("players." + uniqueId.toString(), null);
				savedDurations.remove(uniqueId);
			}
		});
	}

	@Override
	public boolean perform(Player player) {
		UUID uniqueId = player.getUniqueId();
		if (BypassManager.isBypassOff(player)) {
			long startSystemTime = System.currentTimeMillis();
			long storedDuration = duration;
			if (savedDurations.containsKey(uniqueId)) {
				startSystemTime = savedDurations.get(uniqueId).systemTime;
				storedDuration = savedDurations.get(uniqueId).duration;
			}
			SimpleCooldownEntry<UUID> entry = COOLDOWN.schedule(uniqueId, (int) storedDuration, startSystemTime);
			entry.ifTrue(() -> {
				coolingDown.remove(uniqueId);
			}).orElse(timeLeft -> {
				coolingDown.add(uniqueId);
				sendFailMsg(player, timeLeft);
			});
		} else {
			// bypass is on, therefore cooldown is not checked
			coolingDown.remove(uniqueId);
		}
		if (savedDurations.containsKey(uniqueId)) {
			sendFailMsg(player, COOLDOWN.getEntry(uniqueId).getLiveDuration());
			savedDurations.remove(uniqueId);
			return false;
		}
		return !isCoolingDown(uniqueId);
	}

	private void sendFailMsg(Player player, Long timeLeft) {
		Messages.sendMessage(player,
				getParent().getMessages()
						.getCooldownNotDone()
						.replace("%time%", String.valueOf(timeLeft))
						.replace("%time_long%", TimeFormatter.formatLong(timeLeft, true))
						.replace("%time_split%", TimeFormatter.formatShortSplit(timeLeft, true))
						.replace("%time_short%", TimeFormatter.formatShort(timeLeft, true)));
	}

	public int getDuration() {
		return duration;
	}

	public boolean isCoolingDown(UUID uniqueId) {
		return coolingDown.contains(uniqueId);
	}

}
