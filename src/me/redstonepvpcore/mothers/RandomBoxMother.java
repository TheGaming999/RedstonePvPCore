package me.redstonepvpcore.mothers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.sounds.SoundInfo;
import me.redstonepvpcore.sounds.SoundParser;
import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.ItemStackReader;

public class RandomBoxMother {

	private SoundInfo useSound;
	private SoundInfo animationSound;
	private SoundInfo endSound;
	private ItemStack takeItemStack;
	private Map<String, Integer> usePermissions = new HashMap<>();
	private double shuffleDuration;
	private List<ItemStack> items = new ArrayList<>();
	private List<ItemStack> displayItems = new ArrayList<>();
	private List<ItemStack> differentItems = new ArrayList<>();
	private Map<Integer, Actions> actions = new HashMap<>();

	public RandomBoxMother() {
		setup();	
	}

	@SuppressWarnings("deprecation")
	public void setup() {
		usePermissions.clear();
		items.clear();
		displayItems.clear();
		differentItems.clear();
		FileConfiguration config = ConfigCreator.getConfig("randombox.yml");
		ConfigurationSection useSoundSection = config.getConfigurationSection("use-sound");
		ConfigurationSection animationSoundSection = config.getConfigurationSection("animation-sound");
		ConfigurationSection endSoundSection = config.getConfigurationSection("end-sound");
		useSound = SoundParser.parse(useSoundSection);
		animationSound = SoundParser.parse(animationSoundSection);
		endSound = SoundParser.parse(endSoundSection);
		takeItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("take-item"), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
		ConfigurationSection usePermissionsSection = config.getConfigurationSection("use-permissions");
		usePermissionsSection.getKeys(false).forEach(cost -> {
			usePermissions.put(usePermissionsSection.getString(cost), Integer.parseInt(cost));
		});
		shuffleDuration = config.getDouble("shuffle-duration");
		ConfigurationSection itemsSection = config.getConfigurationSection("items");
		AtomicInteger positionNumber = new AtomicInteger();
		Set<String> materials = new HashSet<>();
		itemsSection.getKeys(false).forEach(position -> {
			ConfigurationSection positionSection = itemsSection.getConfigurationSection(position);
			ItemStack stack = ItemStackReader.fromConfigurationSection(positionSection, 
					"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
			stack = RedstonePvPCore.getInstance().getEnchantmentManager().enchant(stack, positionSection.getStringList("custom-enchantments"));
			items.add(stack);
			ItemStack displayStack = ItemStackReader.fromConfigurationSection(positionSection, 
					"display-material", "display-amount", "display-data", "display-name", "display-lore", 
					"display-enchantments", "display-flags", " ");
			displayItems.add(displayStack);
			if(!materials.contains(displayStack.getType().name() + displayStack.getDurability())) {
				materials.add(displayStack.getType().name() + displayStack.getDurability());
				differentItems.add(displayStack);
			}
			List<String> commands = positionSection.getStringList("commands");
			List<String> broadcastMessages = positionSection.getStringList("broadcast");
			List<String> messages = positionSection.getStringList("msg");
			Actions actions = new Actions(broadcastMessages, commands, messages, true);
			if(actions.hasExecutors()) this.actions.put(positionNumber.get(), actions);
			positionNumber.incrementAndGet();
		});
	}

	public void setUseSound(SoundInfo sound) {
		this.useSound = sound;
	}

	public SoundInfo getUseSound() {
		return useSound;
	}

	public void setAnimationSound(SoundInfo sound) {
		this.animationSound = sound;
	}

	public SoundInfo getAnimationSound() {
		return animationSound;
	}

	public SoundInfo getEndSound() {
		return endSound;
	}

	public void setEndSound(SoundInfo sound) {
		this.endSound = sound;
	}

	public ItemStack getTakeItemStack() {
		return takeItemStack;
	}

	public void setTakeItemStack(ItemStack takeItemStack) {
		this.takeItemStack = takeItemStack;
	}

	public double getShuffleDuration() {
		return shuffleDuration;
	}

	public void setShuffleDuration(double shuffleDuration) {
		this.shuffleDuration = shuffleDuration;
	}

	public List<ItemStack> getItems() {
		return items;
	}
	
	public List<ItemStack> getDisplayItems() {
		return displayItems;
	}
	
	public List<ItemStack> getDifferentItems() {
		return differentItems;
	}
	
	public Map<String, Integer> getUsePermissions() {
		return usePermissions;
	}
	
	public Actions getActions(int index) {
		return actions.get(index);
	}
	
}
