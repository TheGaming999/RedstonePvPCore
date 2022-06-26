package me.redstonepvpcore.mothers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.redstonepvpcore.sounds.SoundInfo;
import me.redstonepvpcore.sounds.SoundParser;
import me.redstonepvpcore.utils.ConfigCreator;

public class FrameGiverMother {

	private SoundInfo useSound;
	private boolean giveStack;

	public FrameGiverMother() {
		setup();
	}

	public void setup() {
		FileConfiguration config = ConfigCreator.getConfig("frame-giver.yml");
		ConfigurationSection useSoundSection = config.getConfigurationSection("use-sound");
		useSound = SoundParser.parse(useSoundSection);
		giveStack = config.getBoolean("give-stack");
	}

	public void setUseSound(SoundInfo sound) {
		this.useSound = sound;
	}

	public SoundInfo getUseSound() {
		return useSound;
	}

	public boolean isGiveStack() {
		return giveStack;
	}

	public void setGiveStack(boolean giveStack) {
		this.giveStack = giveStack;
	}

}
