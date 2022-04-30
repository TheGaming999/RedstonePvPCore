package me.redstonepvpcore.mothers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.ItemStackReader;
import me.redstonepvpcore.utils.ParticleReader;
import me.redstonepvpcore.utils.XMaterial;
import me.redstonepvpcore.utils.XSound;
import me.redstonepvpcore.utils.XSound.Record;
import xyz.xenondevs.particle.ParticleBuilder;

public class RepairAnvilMother {

	private Record useRecord;
	private Record animationRecord;
	private Set<Material> repairableItems;
	private ItemStack takeItemStack;
	private List<ParticleBuilder> particles;
	private boolean playOnce;

	public RepairAnvilMother() {
		setup();
	}

	public void setup() {
		FileConfiguration config = ConfigCreator.getConfig("repair-anvil.yml");
		ConfigurationSection useSoundSection = config.getConfigurationSection("use-sound");
		ConfigurationSection animationSoundSection = config.getConfigurationSection("animation-sound");
		Record record = new Record(XSound.matchXSound(useSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)useSoundSection.getDouble("volume"), 
				(float)useSoundSection.getDouble("pitch"), 
				useSoundSection.getBoolean("3d", true));
		if(record.sound != null) useRecord = record;
		record = new Record(XSound.matchXSound(animationSoundSection.getString("name")).orElse(null),
				null, 
				null, 
				(float)animationSoundSection.getDouble("volume"), 
				(float)animationSoundSection.getDouble("pitch"), 
				animationSoundSection.getBoolean("3d", true));
		if(record.sound != null) animationRecord = record;
		repairableItems = new HashSet<>();
		config.getStringList("repairable-items").stream()
		.map(XMaterial::matchXMaterial)
		.map(Optional<XMaterial>::get)
		.map(XMaterial::parseMaterial)
		.forEach(repairableItems::add);
		particles = ParticleReader.parseAll("BLOCK_CRACK texture=ANVIL");
		takeItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("take-item"), 
				"material", "amount", "data", "name", "lore", "enchantments", "flags", " ");
		playOnce = animationSoundSection.getBoolean("play-once");
	}

	public void setUseSound(Record record) {
		this.useRecord = record;
	}

	public Record getUseSound() {
		return this.useRecord;
	}

	public void setAnimationSound(Record record) {
		this.animationRecord = record;
	}

	public Record getAnimationSound() {
		return this.animationRecord;
	}

	public boolean setPlayOnce(boolean playOnce) {
		return this.playOnce = playOnce;
	}

	public boolean isPlayOnce() {
		return this.playOnce;
	}

	public void setRepairableItems(Set<Material> repairableItems) {
		this.repairableItems = repairableItems;
	}

	public void addRepairableItem(Material material) {
		this.repairableItems.add(material);
	}

	public void addRepairableItem(String materialName) {
		this.repairableItems.add(Material.valueOf(materialName));
	}

	public void removeRepairableItem(Material material) {
		this.repairableItems.remove(material);
	}

	public Set<Material> getRepairableItems() {
		return this.repairableItems;
	}

	public boolean isRepairable(Material material) {
		return this.repairableItems.contains(material);
	}

	public void setTakeItem(ItemStack itemStack) {
		this.takeItemStack = itemStack;
	}

	public ItemStack getTakeItem() {
		return this.takeItemStack;
	}

	public void setParticles(List<ParticleBuilder> particles) {
		this.particles = particles;
	}
	
	public List<ParticleBuilder> getParticles() {
		return this.particles;
	}

}
