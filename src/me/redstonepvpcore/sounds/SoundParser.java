package me.redstonepvpcore.sounds;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import me.redstonepvpcore.utils.XSound;
import me.redstonepvpcore.utils.XSound.Record;

public class SoundParser {

	private static final String MUSIC_PATH = "plugins/RedstonePvPCore/Music/";
	private static final Set<String> NBS = new HashSet<>();

	public static SoundInfo parse(ConfigurationSection section) {
		SoundInfo soundInfo = null;
		if (section.getString("name").startsWith("NBS:")) {
			String startPath = section.getString("name").contains("/") ? "" : MUSIC_PATH;
			soundInfo = new NBSSound(startPath + section.getString("name").substring(4));
			soundInfo.setVolume(((Double) section.getDouble("volume", 100)).byteValue());
			soundInfo.setDistance(((Double) section.getDouble("pitch", 1)).intValue());
			soundInfo.setTicks(section.getInt("start-at-tick", 0));
			NBS.add(section.getName());
		} else {
			if (section.getString("name") == null || section.getString("name").isEmpty()) {
				// do nothing
			} else {
				Record record = new Record(XSound.matchXSound(section.getString("name")).get(), null, null,
						(float) section.getDouble("volume"), (float) section.getDouble("pitch"),
						section.getBoolean("3d", false));
				if (record.sound != null) soundInfo = new VanillaSound(record);
			}
			NBS.remove(section.getName());
		}
		return soundInfo;
	}

	/**
	 * <p>
	 * (required) [optional]
	 * </p>
	 * <p>
	 * Format Default: "(soundname) [volume] [pitch]"
	 * </p>
	 * <p>
	 * Format NBS: "NBS:(soundname) [volume] [distance] [startticks]"
	 * </p>
	 * 
	 * @param name string to parse from
	 * @return sound info to play or whatever
	 */
	public static SoundInfo parse(String name) {
		SoundInfo soundInfo = null;
		if (name.startsWith("NBS:")) {
			String startPath = name.contains("/") ? "" : MUSIC_PATH;
			String[] split = name.split(" ");
			soundInfo = new NBSSound(startPath + split[0].substring(4));
			soundInfo.setVolume(split.length > 1 ? Byte.parseByte(split[1]) : 100);
			soundInfo.setDistance(split.length > 2 ? Integer.parseInt(split[2]) : 0);
			soundInfo.setTicks(split.length > 3 ? Integer.parseInt(split[3]) : 0);
			NBS.add(name);
		} else {
			if (name == null || name.isEmpty()) {
				// do nothing
			} else {
				String[] split = name.split(" ");
				Record record = new Record(XSound.matchXSound(split[0]).get(), null, null,
						split.length > 1 ? Float.parseFloat(split[1]) : 1.0f,
						split.length > 2 ? Float.parseFloat(split[2]) : 1.0f, false);
				if (record.sound != null) soundInfo = new VanillaSound(record);
			}
			NBS.remove(name);
		}
		return soundInfo;
	}

	public static boolean isNBSParse(ConfigurationSection section) {
		return NBS.contains(section.getName());
	}

	public static boolean isNBSParse(String name) {
		return NBS.contains(name);
	}

}
