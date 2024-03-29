package me.redstonepvpcore.gadgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.redstonepvpcore.utils.ConfigCreator;

public class GadgetManager {

	private static final Map<Location, Gadget> GADGETS = new HashMap<>();
	private static final Map<Location, Cooldown> COOLDOWNS = new HashMap<>();

	public static void loadGadgets() {
		FileConfiguration gadgetConfig = ConfigCreator.getConfig("data.yml");
		gadgetConfig.getStringList("random-box").forEach(GadgetManager::addRandomBox);
		gadgetConfig.getStringList("repair-anvil").forEach(GadgetManager::addRepairAnvil);
		gadgetConfig.getStringList("drop-party").forEach(GadgetManager::addDropPartyActivator);
		gadgetConfig.getStringList("exp-sign").forEach(GadgetManager::addExpSign);
		gadgetConfig.getStringList("frame-giver").forEach(GadgetManager::addFrameGiver);
		if (gadgetConfig.isList("cooldown")) gadgetConfig.set("cooldown", null);
		if (!gadgetConfig.isConfigurationSection("cooldown")) gadgetConfig.createSection("cooldown");
		gadgetConfig.getConfigurationSection("cooldown").getKeys(false).forEach(GadgetManager::addCooldown);
		gadgetConfig.getStringList("redstone-converter")
				.forEach(stringLocation -> GadgetManager.addConverter(stringLocation, ConverterType.REDSTONE));
		gadgetConfig.getStringList("gold-converter")
				.forEach(stringLocation -> GadgetManager.addConverter(stringLocation, ConverterType.GOLD));
		gadgetConfig.getStringList("emerald-converter")
				.forEach(stringLocation -> GadgetManager.addConverter(stringLocation, ConverterType.EMERALD));
	}

	public static void saveGadgets() {
		FileConfiguration gadgetConfig = ConfigCreator.getConfig("data.yml");
		List<String> repairAnvils = new ArrayList<>(), redstoneConverters = new ArrayList<>(),
				goldConverters = new ArrayList<>(), emeraldConverters = new ArrayList<>(),
				dropPartyActivators = new ArrayList<>(), expSigns = new ArrayList<>(), frameGivers = new ArrayList<>(),
				randomBoxes = new ArrayList<>(), cooldowns = new ArrayList<>(), durations = new ArrayList<>();
		GADGETS.forEach((location, holder) -> {
			switch (holder.getType()) {
				case CONVERTER:
					Converter converter = (Converter) getGadget(location);
					switch (converter.getConverterType()) {
						case EMERALD:
							emeraldConverters.add(deparseLocation(location));
							break;
						case GOLD:
							goldConverters.add(deparseLocation(location));
							break;
						case REDSTONE:
							redstoneConverters.add(deparseLocation(location));
							break;
						default:
							break;
					}
					break;
				case DROP_PARTY_ACTIVATOR:
					dropPartyActivators.add(deparseLocation(location));
					break;
				case EXP_SIGN:
					expSigns.add(deparseLocation(location));
					break;
				case FRAME_GIVER:
					frameGivers.add(deparseLocation(location));
					break;
				case RANDOM_BOX:
					randomBoxes.add(deparseLocation(location));
					break;
				case REPAIR_ANVIL:
					repairAnvils.add(deparseLocation(location));
					break;
				default:
					break;
			}
		});
		gadgetConfig.set("repair-anvil", repairAnvils);
		gadgetConfig.set("drop-party", dropPartyActivators);
		gadgetConfig.set("exp-sign", expSigns);
		gadgetConfig.set("frame-giver", frameGivers);
		gadgetConfig.set("random-box", randomBoxes);

		gadgetConfig.set("redstone-converter", redstoneConverters);
		gadgetConfig.set("gold-converter", goldConverters);
		gadgetConfig.set("emerald-converter", emeraldConverters);
		COOLDOWNS.forEach((location, cooldown) -> {
			cooldowns.add(deparseSectionLocation(location));
			durations.add(String.valueOf(cooldown.getDuration()));
			cooldown.saveDurations();
		});
		ConfigurationSection cooldownSection = gadgetConfig.getConfigurationSection("cooldown");
		for (int i = 0; i < cooldowns.size(); i++) {
			String cooldownLoc = cooldowns.get(i);
			String cooldownDuration = durations.get(i);
			ConfigurationSection cooldownLocSection = cooldownSection.getConfigurationSection(cooldownLoc);
			if (cooldownLocSection == null) cooldownLocSection = cooldownSection.createSection(cooldownLoc);
			cooldownLocSection.set("duration", Integer.parseInt(cooldownDuration));
			if (!cooldownLocSection.isConfigurationSection("players")) cooldownLocSection.createSection("players");
		}
	}

	public static Gadget addGadget(Gadget gadget, String locationString) {
		Location loc = parseLocation(locationString);
		gadget.setLocation(loc);
		gadget.setup();
		return GADGETS.put(loc, gadget);
	}

	public static Gadget addGadget(Gadget gadget, String locationString, @Nullable ConverterType converterType) {
		if (converterType == null) return addGadget(gadget, locationString);
		Location loc = parseLocation(locationString);
		Converter converter = (Converter) gadget;
		converter.setLocation(loc);
		converter.setConverterType(converterType);
		converter.setup();
		return GADGETS.put(loc, converter);
	}

	public static Gadget addRandomBox(String locationString) {
		RandomBox randomBox = new RandomBox(parseLocation(locationString));
		randomBox.setup();
		return GADGETS.put(randomBox.getLocation(), randomBox);
	}

	public static Gadget addConverter(String locationString, ConverterType converterType) {
		Converter converter = new Converter(parseLocation(locationString));
		converter.setConverterType(converterType);
		converter.fetchConverter();
		converter.setup();
		return GADGETS.put(converter.getLocation(), converter);
	}

	public static Gadget addDropPartyActivator(String locationString) {
		DropPartyActivator dropPartyActivator = new DropPartyActivator(parseLocation(locationString));
		dropPartyActivator.setup();
		return GADGETS.put(dropPartyActivator.getLocation(), dropPartyActivator);
	}

	public static Gadget addExpSign(String locationString) {
		ExpSign expSign = new ExpSign(parseLocation(locationString));
		expSign.setup();
		return GADGETS.put(expSign.getLocation(), expSign);
	}

	public static Gadget addRepairAnvil(String locationString) {
		RepairAnvil repairAnvil = new RepairAnvil(parseLocation(locationString));
		repairAnvil.setup();
		return GADGETS.put(repairAnvil.getLocation(), repairAnvil);
	}

	public static Gadget addFrameGiver(String locationString) {
		Location loc = parseLocation(locationString);
		FrameGiver frameGiver = new FrameGiver(loc);
		frameGiver.setup();
		if (frameGiver.getLocation() == null) frameGiver.setLocation(loc);
		return GADGETS.put(frameGiver.getLocation(), frameGiver);
	}

	public static Gadget addCooldown(String locationString) {
		ConfigurationSection cooldownSection = ConfigCreator.getConfig("data.yml").getConfigurationSection("cooldown");
		ConfigurationSection gadgetSection = cooldownSection.getConfigurationSection(locationString);
		Location loc = parseSectionLocation(locationString);
		int duration = gadgetSection.getInt("duration");
		Cooldown cooldown = new Cooldown(loc);
		cooldown.withDuration(duration);
		cooldown.setup();
		if (cooldown.getLocation() == null) cooldown.setLocation(loc);
		return COOLDOWNS.put(cooldown.getLocation(), cooldown);
	}

	public static Gadget addCooldown(String locationString, int duration) {
		Location loc = parseSectionLocation(locationString);
		Cooldown cooldown = new Cooldown(loc);
		cooldown.withDuration(duration);
		cooldown.setup();
		if (cooldown.getLocation() == null) cooldown.setLocation(loc);
		return COOLDOWNS.put(cooldown.getLocation(), cooldown);
	}

	public static Gadget removeGadget(Location location) {
		Gadget gadget = GADGETS.remove(location);
		COOLDOWNS.remove(location);
		ConfigCreator.getConfig("data.yml")
				.getConfigurationSection("cooldown")
				.set(deparseSectionLocation(location), null);
		return gadget;
	}

	public static int parseDuration(String location) {
		return Integer.parseInt(location.substring(location.lastIndexOf(" ") + 1, location.length()));
	}

	public static Location parseLocation(String location) {
		String[] arguments = location.split(" ");
		return new Location(Bukkit.getWorld(arguments[0]), Double.parseDouble(arguments[1]),
				Double.parseDouble(arguments[2]), Double.parseDouble(arguments[3]), Float.parseFloat(arguments[4]),
				Float.parseFloat(arguments[5]));
	}

	public static Location parseSectionLocation(String location) {
		location = location.replace("_-_", ".");
		String[] arguments = location.split(" ");
		return new Location(Bukkit.getWorld(arguments[0]), Double.parseDouble(arguments[1]),
				Double.parseDouble(arguments[2]), Double.parseDouble(arguments[3]), Float.parseFloat(arguments[4]),
				Float.parseFloat(arguments[5]));
	}

	public static String deparseLocation(Location location) {
		return location.getWorld().getName() + " " + String.valueOf(location.getX()) + " "
				+ String.valueOf(location.getY()) + " " + String.valueOf(location.getZ()) + " "
				+ String.valueOf(location.getYaw()) + " " + String.valueOf(location.getPitch());
	}

	public static String deparseSectionLocation(Location location) {
		return (location.getWorld().getName() + " " + String.valueOf(location.getX()) + " "
				+ String.valueOf(location.getY()) + " " + String.valueOf(location.getZ()) + " "
				+ String.valueOf(location.getYaw()) + " " + String.valueOf(location.getPitch())).replace(".", "_-_");
	}

	public static Gadget fromGadgetType(GadgetType gadgetType, @Nullable ConverterType converterType) {
		switch (gadgetType) {
			case CONVERTER:
				Converter converter = new Converter(null);
				converter.setConverterType(converterType);
				converter.fetchConverter();
				return converter;
			case DROP_PARTY_ACTIVATOR:
				return new DropPartyActivator(null);
			case EXP_SIGN:
				return new ExpSign(null);
			case FRAME_GIVER:
				return new FrameGiver(null);
			case RANDOM_BOX:
				return new RandomBox(null);
			case REPAIR_ANVIL:
				return new RepairAnvil(null);
			case COOLDOWN:
				return new Cooldown(null);
			default:
				return null;
		}
	}

	public static Map<Location, Gadget> getGadgets() {
		return GADGETS;
	}

	public static Map<Location, Cooldown> getCooldownGadgets() {
		return COOLDOWNS;
	}

	public static Set<Location> getGadgetsLocations() {
		return GADGETS.keySet();
	}

	public static Collection<Gadget> getGadgetsCollection() {
		return GADGETS.values();
	}

	public static boolean isGadget(Location location) {
		return getGadget(location) != null;
	}

	public static boolean isCooldownGadget(Location location) {
		return COOLDOWNS.get(location) != null;
	}

	public static Cooldown getCooldownGadget(Location location) {
		return COOLDOWNS.get(location);
	}

	public static boolean isEntityGadget(Location location) {
		return GADGETS.get(location) instanceof EntityGadget;
	}

	public static boolean isEntityGadget(Gadget gadget) {
		return gadget instanceof EntityGadget;
	}

	public static Gadget getGadget(Location location) {
		return GADGETS.get(location);
	}

	public static Gadget getCooldownGadget(Gadget gadget) {
		return getCooldownGadget(gadget.getLocation());
	}

	public static boolean testCooldown(Gadget gadget, Player player) {
		Cooldown cooldownGadget = getCooldownGadget(gadget.getLocation());
		if (cooldownGadget == null) return true;
		return cooldownGadget.perform(player);
	}

}
