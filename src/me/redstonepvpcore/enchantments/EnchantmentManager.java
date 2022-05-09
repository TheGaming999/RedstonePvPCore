package me.redstonepvpcore.enchantments;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import me.redstonepvpcore.utils.ConfigCreator;
import me.redstonepvpcore.utils.NBTEditor;

public class EnchantmentManager {
	
	private Map<String, RPEnchantment> enchantments = new LinkedHashMap<>();
	private Map<Integer, String> enchantmentsIds = new HashMap<>();
	
	public EnchantmentManager() {
		setup();
	}
	
	public enum EnchantResult {
		
		FAIL_EXCEED_MAX_LEVEL(false), FAIL_NULL_ITEM(false), FAIL_NULL_ENCHANTMENT(false), SUCCESS(true);

		private boolean success;
		@Nullable private ItemStack itemStack;
		
		EnchantResult(boolean b) {
			success = b;
		}
		
		public boolean isSuccessful() {
			return success;
		}
		
		private EnchantResult setItemStack(@Nullable ItemStack itemStack) {
			this.itemStack = itemStack;
			return this;
		}
		
		@Nullable
		public ItemStack getItemStack() {
			return itemStack;
		}
		
	}
	
	public void setup() {
		enchantments.clear();
		enchantmentsIds.clear();
		FileConfiguration config = ConfigCreator.getConfig("enchantments.yml");
		AtomicInteger id = new AtomicInteger();
		config.getKeys(false).forEach(name -> {
			RPEnchantment enchantment = new RPEnchantment(name.toUpperCase());
			enchantment.setDisplayName(config.getString(name + ".display-name"));
			enchantment.setMaxLevel(config.getInt(name + ".max-level"));
			enchantment.setId(id.incrementAndGet());
			name = name.toUpperCase();
			switch(name) {
			case "POISON":
				enchantment.setDamageHandler(new PoisonDamageHandler(PotionEffectType.POISON));
				break;
			case "WITHER":
				enchantment.setDamageHandler(new WitherDamageHandler(PotionEffectType.WITHER));
				break;
			case "BLINDNESS":
				enchantment.setDamageHandler(new BlindnessDamageHandler(PotionEffectType.BLINDNESS));
				break;
			case "SLOWNESS":
				enchantment.setDamageHandler(new SlownessDamageHandler(PotionEffectType.SLOW));
				break;
			case "HUNGER":
				enchantment.setDamageHandler(new HungerDamageHandler(PotionEffectType.HUNGER));
				break;
			case "EXTRADAMAGE":
				enchantment.setDamageHandler(new ExtradamageDamageHandler(PotionEffectType.INCREASE_DAMAGE));
				break;
			case "NAUSEA":
				enchantment.setDamageHandler(new NauseaDamageHandler(PotionEffectType.CONFUSION));
				break;
			}
			enchantments.put(name, enchantment);
			enchantmentsIds.put(enchantment.getId(), name);
		});
	}
	
	public void register(String name, RPEnchantment enchantment) {
		enchantments.put(name, enchantment);
		enchantmentsIds.put(enchantments.size()+1, name);
	}
	
	public void register(String name, RPEnchantment enchantment, int id) {
		enchantments.put(name, enchantment);
		if(id <= 0)
			enchantmentsIds.put(enchantment.getId(), name);
		else
			enchantmentsIds.put(id, name);
	}
	
	public Map<String, RPEnchantment> getEnchantmentsMap() {
		return enchantments;
	}
	
	public RPEnchantment getEnchantment(String name) {
		return enchantments.get(name);
	}
	
	public String getEnchantmentName(int id) {
		return enchantmentsIds.get(id);
	}
	
	public RPEnchantment getEnchantment(int id) {
		return enchantments.get(enchantmentsIds.get(id));
	}
	
	private String[] romanNumerals = new String[] {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
	private List<String> romanList = Arrays.asList(romanNumerals);
	
	public String getRomanNumeral(int lvl) {
		if(lvl <= 0) return "0";
		if(lvl <= 10) return romanNumerals[lvl-1];
		return String.valueOf(lvl);
	}

	public int parse(String roman) {
		roman = roman.toUpperCase();
		if(romanList.contains(roman)) return romanList.indexOf(roman)+1;
		return Integer.parseInt(roman);
	}
	
	public EnchantResult enchant(ItemStack itemStack, String name, int lvl) {
		return enchant(itemStack, name, lvl, false);
	}
	
	public EnchantResult enchant(ItemStack itemStack, String name, int lvl, boolean unsafe) {
		int enchantmentsAmount = getEnchantmentsMap().size();
		RPEnchantment enchantment = getEnchantment(name.toUpperCase());
		if(enchantment == null) return EnchantResult.FAIL_NULL_ENCHANTMENT;
		if(!unsafe)
		if(lvl > enchantment.getMaxLevel()) return EnchantResult.FAIL_EXCEED_MAX_LEVEL;
		if(itemStack == null || itemStack.getType() == Material.AIR) return EnchantResult.FAIL_NULL_ITEM;
		int[] enchantmentIds = NBTEditor.getIntArray(itemStack, "rpids");
		if(enchantmentIds == null) enchantmentIds = new int[enchantmentsAmount];
		int[] enchantmentLvls = NBTEditor.getIntArray(itemStack, "rplvls");
		if(enchantmentLvls == null) enchantmentLvls = new int[enchantmentsAmount];
		int emptySection = -1;
		int foundEnchantment = -1;
		for(int i = 0; i < enchantmentIds.length; i++) {
			if(enchantmentIds[i] == 0) {
				emptySection = i;
			} else if (enchantmentIds[i] == enchantment.getId()) {
				foundEnchantment = i;
			}
		}
		if(foundEnchantment != -1) {
			if(lvl <= 0) {
				enchantmentIds[foundEnchantment] = 0;
				enchantmentLvls[foundEnchantment] = -1;
			} else {
				enchantmentLvls[foundEnchantment] = lvl-1;
			}	
		} else if (emptySection != -1) {
			if(!(lvl <= 0)) {
				enchantmentIds[emptySection] = enchantment.getId();
				enchantmentLvls[emptySection] = lvl-1;
			}
		}
		if(IntStream.of(enchantmentIds).sum() <= 0) enchantmentIds = null;
		if(IntStream.of(enchantmentLvls).sum() <= -1) enchantmentLvls = null;
		itemStack = NBTEditor.set(itemStack, enchantmentIds, "rpids");
		itemStack = NBTEditor.set(itemStack, enchantmentLvls, "rplvls");
		return EnchantResult.SUCCESS.setItemStack(itemStack);
	}
	
}
