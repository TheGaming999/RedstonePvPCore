package me.redstonepvpcore.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.enchantments.EnchantmentManager;
import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.ItemStackReader;
import me.redstonepvpcore.utils.NBTEditor;

public class DamageListener implements Listener {

	private RedstonePvPCore parent;
	private ItemStack bleedItemStack;
	private int minimumAmount;
	private int maximumAmount;
	private int dropChance;
	private EnchantmentManager enchantmentManager;
	private Set<Player> effectDamage = new HashSet<>();

	public DamageListener(RedstonePvPCore parent) {
		this.parent = parent;
	}

	public void register() {
		FileConfiguration config = ConfigCreator.getConfig("item-bleed.yml");
		bleedItemStack = ItemStackReader.fromConfigurationSection(config.getConfigurationSection("item"), "material",
				"amount", "data", "name", "lore", "enchantments", "flags", " ");
		String[] amountRange = config.getString("item.amount-range").split("->");
		minimumAmount = Integer.parseInt(amountRange[0]);
		maximumAmount = Integer.parseInt(amountRange[1]);
		dropChance = config.getInt("item.drop-chance");
		enchantmentManager = parent.getEnchantmentManager();
		effectDamage.clear();
		parent.getServer().getPluginManager().registerEvents(this, parent);
	}

	public void unregister() {
		EntityDamageByEntityEvent.getHandlerList().unregister(parent);
	}

	private int getRandom(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	private int[] getCustomEnchantments(ItemStack itemStack) {
		return NBTEditor.getIntArray(itemStack, "rpids");
	}

	private int[] getLevels(ItemStack itemStack) {
		return NBTEditor.getIntArray(itemStack, "rplvls");
	}

	public boolean isEffectDamage(Player player) {
		return effectDamage.contains(player);
	}

	public boolean setEffectDamage(Player player) {
		return effectDamage.add(player);
	}

	public boolean removeEffectDamage(Player player) {
		return effectDamage.remove(player);
	}

	public void activateEnchantments(LivingEntity entity, Player damager, int[] enchantments, int[] levels) {
		for (int i = 0; i < enchantments.length; i++) {
			int id = enchantments[i];
			if (id != 0) enchantmentManager.getEnchantment(id).getDamageHandler().onDamage(entity, damager, levels[i]);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.isCancelled()) return;
		Entity entityDamager = e.getDamager();
		Entity targetEntity = e.getEntity();
		if (!(entityDamager instanceof Player)) return;
		Player damager = (Player) entityDamager;
		if (isEffectDamage(damager)) return;
		if (!(targetEntity instanceof LivingEntity)) return;
		LivingEntity entity = (LivingEntity) targetEntity;
		ItemStack itemInHand = damager.getItemInHand();
		int[] customEnchantments = itemInHand.getType() == Material.AIR ? null : getCustomEnchantments(itemInHand);
		if (customEnchantments != null) {
			int[] levels = getLevels(itemInHand);
			if (e.getDamage() <= 0.99) return;
			setEffectDamage(damager);
			activateEnchantments(entity, damager, customEnchantments, levels);
			removeEffectDamage(damager);
		}
		if (!(targetEntity instanceof Player)) return;
		Player target = (Player) targetEntity;
		if (e.getDamage() >= 0.01 && getRandom(1, 100) <= dropChance) {
			damager.getWorld()
					.dropItemNaturally(target.getLocation().add(getRandom(-1, 1), 0, getRandom(-1, 1)), bleedItemStack)
					.getItemStack()
					.setAmount(getRandom(minimumAmount, maximumAmount));
		}
	}

}
