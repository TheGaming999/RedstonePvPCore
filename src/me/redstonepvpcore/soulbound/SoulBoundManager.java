package me.redstonepvpcore.soulbound;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.redstonepvpcore.utils.CollectionUtils;
import me.redstonepvpcore.utils.Colorizer;
import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.NBTEditor;

public class SoulBoundManager {

	private boolean allowDrop;
	private boolean dropOnDeath;
	private boolean useLore;
	private boolean keepAfterDeath;
	private List<String> lore;

	public SoulBoundManager() {
		setup();
	}

	public void setup() {
		FileConfiguration config = ConfigCreator.getConfig("soulbound.yml");
		allowDrop = config.getBoolean("allow-drop");
		dropOnDeath = config.getBoolean("drop-on-death");
		useLore = config.getBoolean("use-lore");
		keepAfterDeath = config.getBoolean("keep-after-death", false);
		lore = Colorizer.colorize(config.getStringList("lore"));
	}

	public ItemStack addSoulBound(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) return null;
		ItemStack soulBounded = NBTEditor.set(itemStack, true, "rp-soulbound");
		ItemMeta meta = soulBounded.getItemMeta();
		if (useLore) {
			List<String> metaLore = meta.getLore();
			List<String> itemLore = metaLore == null ? new ArrayList<>() : metaLore;
			List<String> check = new ArrayList<>(CollectionUtils.getContainsIgnoreCaseCollection(itemLore, lore));
			itemLore.removeAll(check);
			lore.forEach(line -> itemLore.add(Colorizer.colorize(line)));
			meta.setLore(itemLore);
			soulBounded.setItemMeta(meta);
		}
		return soulBounded;
	}

	public ItemStack deleteSoulBound(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) return null;
		ItemStack soulBounded = NBTEditor.set(itemStack, null, "rp-soulbound");
		ItemMeta meta = soulBounded.getItemMeta();
		if (useLore) {
			List<String> metaLore = meta.getLore();
			List<String> itemLore = metaLore == null ? new ArrayList<>() : metaLore;
			List<String> check = new ArrayList<>(CollectionUtils.getContainsIgnoreCaseCollection(itemLore, lore));
			itemLore.removeAll(check);
			meta.setLore(itemLore);
			soulBounded.setItemMeta(meta);
		}
		return soulBounded;
	}

	public ItemStack setSoulBound(ItemStack itemStack, boolean add) {
		return add ? addSoulBound(itemStack) : deleteSoulBound(itemStack);
	}

	public boolean isSoulBounded(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) return false;
		return NBTEditor.contains(itemStack, "rp-soulbound");
	}

	public boolean isAllowDrop() {
		return allowDrop;
	}

	public void setAllowDrop(boolean allowDrop) {
		this.allowDrop = allowDrop;
	}

	public boolean isDropOnDeath() {
		return dropOnDeath;
	}

	public void setDropOnDeath(boolean dropOnDeath) {
		this.dropOnDeath = dropOnDeath;
	}

	public boolean isUseLore() {
		return useLore;
	}

	public void setUseLore(boolean useLore) {
		this.useLore = useLore;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public boolean isKeepAfterDeath() {
		return keepAfterDeath;
	}

	public void setKeepAfterDeath(boolean keepAfterDeath) {
		this.keepAfterDeath = keepAfterDeath;
	}

}
