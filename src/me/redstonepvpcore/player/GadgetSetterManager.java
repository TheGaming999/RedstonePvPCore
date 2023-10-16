package me.redstonepvpcore.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import me.redstonepvpcore.gadgets.ConverterType;
import me.redstonepvpcore.gadgets.Cooldown;
import me.redstonepvpcore.gadgets.Gadget;
import me.redstonepvpcore.gadgets.GadgetManager;
import me.redstonepvpcore.gadgets.GadgetType;

public class GadgetSetterManager {

	private static final Map<UUID, Gadget> SETTERS = new HashMap<>();
	private static final Map<UUID, ConverterType> SUB = new HashMap<>();
	private static final Map<UUID, Integer> COOLDOWNS = new HashMap<>();

	public static void assign(UUID uniqueId, GadgetType type) {
		SETTERS.put(uniqueId, GadgetManager.fromGadgetType(type, null));
	}

	public static void assign(UUID uniqueId, GadgetType type, ConverterType subType) {
		SETTERS.put(uniqueId, GadgetManager.fromGadgetType(type, subType));
		SUB.put(uniqueId, subType);
	}

	public static void assign(UUID uniqueId, int cooldown) {
		SETTERS.put(uniqueId, new Cooldown(null).withDuration(cooldown));
		COOLDOWNS.put(uniqueId, cooldown);
	}

	public static Gadget cancel(UUID uniqueId) {
		SUB.remove(uniqueId);
		COOLDOWNS.remove(uniqueId);
		return SETTERS.remove(uniqueId);
	}

	public static boolean isAssigned(UUID uniqueId) {
		return SETTERS.containsKey(uniqueId);
	}

	public static Gadget getAssignedGadget(UUID uniqueId) {
		return SETTERS.get(uniqueId);
	}

	public static Set<UUID> getSetters() {
		return SETTERS.keySet();
	}

	public void clear() {
		SETTERS.clear();
	}

	public static void assignSub(UUID uniqueId, ConverterType type) {
		SUB.put(uniqueId, type);
	}

	public static ConverterType cancelSub(UUID uniqueId) {
		return SUB.remove(uniqueId);
	}

	@Nullable
	public static ConverterType getAssignedSubType(UUID uniqueId) {
		return SUB.get(uniqueId);
	}

	public static Integer getAssignedCooldown(UUID uniqueId) {
		return COOLDOWNS.get(uniqueId);
	}

}
