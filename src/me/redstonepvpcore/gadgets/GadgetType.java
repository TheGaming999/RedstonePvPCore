package me.redstonepvpcore.gadgets;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

public enum GadgetType {

	RANDOM_BOX,
	DROP_PARTY_ACTIVATOR,
	EXP_SIGN,
	FRAME_GIVER,
	REPAIR_ANVIL,
	CONVERTER,
	COOLDOWN;

	public final static Set<String> GADGET_TYPES = ImmutableSet
			.copyOf(Arrays.stream(values()).map(Enum::name).collect(Collectors.toSet()));

	@Nullable
	public static GadgetType parseType(String dataHolderTypeName) {
		return GADGET_TYPES.contains(dataHolderTypeName.toUpperCase())
				? GadgetType.valueOf(dataHolderTypeName.toUpperCase()) : null;
	}

	@Nullable
	public static GadgetType matchType(@Nullable String dataHolderTypeName) {
		String name = dataHolderTypeName;
		if (name == null) return null;
		name = name.toUpperCase();
		for (GadgetType gadgetType : GadgetType.values()) {
			String gadgetName = gadgetType.name();
			if (name.equals(gadgetName)) return gadgetType;
			if (name.equals(gadgetName.replace("_", ""))) return gadgetType;
			if (name.equals(gadgetName.split("_")[0])) return gadgetType;
			if (name.contains(gadgetName)) return gadgetType;
			if (name.contains(gadgetName.split("_")[0])) return gadgetType;
			if (gadgetName.contains(name)) return gadgetType;
			if (gadgetName.replace("_", "").contains(name)) return gadgetType;
			if (gadgetName.startsWith(name)) return gadgetType;
		}
		return null;
	}

}
