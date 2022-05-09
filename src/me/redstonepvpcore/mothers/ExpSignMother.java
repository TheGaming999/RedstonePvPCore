package me.redstonepvpcore.mothers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.sounds.SoundInfo;
import me.redstonepvpcore.sounds.SoundParser;
import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.ItemStackReader;

public class ExpSignMother {

	private SoundInfo useSound;
	private ItemStack takeItemStack;
	private int giveAmount;
	private boolean giveLevels;
	
	public ExpSignMother() {
		setup();	
	}

	public void setup() {
		FileConfiguration config = ConfigCreator.getConfig("exp-sign.yml");
		ConfigurationSection useSoundSection = config.getConfigurationSection("use-sound");
		useSound = SoundParser.parse(useSoundSection);
		takeItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("take-item"), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
		setGiveAmount(config.getInt("give.amount"));
		setGiveLevels(config.getString("give.type").startsWith("L"));
	}

	public void setUseSound(SoundInfo sound) {
		this.useSound = sound;
	}

	public SoundInfo getUseSound() {
		return useSound;
	}

	public ItemStack getTakeItemStack() {
		return takeItemStack;
	}

	public void setTakeItemStack(ItemStack takeItemStack) {
		this.takeItemStack = takeItemStack;
	}

	public int getGiveAmount() {
		return giveAmount;
	}

	public void setGiveAmount(int giveAmount) {
		this.giveAmount = giveAmount;
	}

	public boolean isGiveLevels() {
		return giveLevels;
	}

	public void setGiveLevels(boolean giveLevels) {
		this.giveLevels = giveLevels;
	}
	
}
