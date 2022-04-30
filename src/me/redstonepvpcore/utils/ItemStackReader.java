package me.redstonepvpcore.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")
public class ItemStackReader {

	private static final char SPACE = ' ';

	private static String c(String textToTranslate) {
		return ChatColor.translateAlternateColorCodes('&', textToTranslate);
	}

	private static String c(final String textToTranslate, final Player player) {
		return ChatColor.translateAlternateColorCodes('&', textToTranslate.replace("%name%", player.getName()).replace("%display_name%", player.getDisplayName()));
	}

	private static String[] c(final String[] textToTranslate) {
		int counter = 0;
		for(String string : textToTranslate) {
			textToTranslate[counter] = c(string);
			counter++;
		}
		return textToTranslate;
	}

	private static String[] c(final String[] textToTranslate, final Player player) {
		int counter = 0;
		for(String string : textToTranslate) {
			textToTranslate[counter] = c(string, player);
			counter++;
		}
		return textToTranslate;
	}

	private static List<String> c(final List<String> textToTranslate) {
		int counter = 0;
		for(String string : textToTranslate) {
			textToTranslate.set(counter, (c(string)));
			counter++;
		}
		return textToTranslate;
	}

	/**
	 * 
	 * @param string the string where the itemstack gets parsed from
	 * @return a new ItemStack from the parsed String,
	 * <p>for example:
	 * <p><pre>item=DIAMOND | Diamond with the amount 1 and data set to 0</p></pre>
	 * <p><pre>item=WOOL{@code<metaSeparator>}data=15 | 1 Black Wool</p></pre>
	 * <p><pre>item=GRASS{@code<metaSeparator>}enchantments=LOOTING:1,DURABILITY:2 | Enchanted GRASS</p></pre>
	 * <p><i>available metas:
	 * <p>item={@code<itemName>}
	 * <p>amount={@code<amount>}
	 * <p>data={@code<durability/damage/data>}
	 * <p>name={@code<displayName>} | & colors are supported
	 * <p>lore={@code<loreLine>,<loreLine2>} | & colors are supported
	 * <p>enchantments={@code<enchant>:<lvl>,<enchant2>:<lvl>}
	 * <p>flags={@code<itemFlag>,<itemFlag2>}</p>
	 */
	public static ItemStack fromString(final String string, final String metaSeparator) {
		ItemStack stack = new ItemStack(Material.STONE, 1);
		ItemMeta meta = stack.getItemMeta();
		for(String key : string.split(metaSeparator)) {
			if(key.startsWith("item=")) {
				stack.setType(Material.matchMaterial(key.substring(5)));
			} else if (key.startsWith("amount=")) {
				stack.setAmount(Integer.valueOf(key.substring(7)));
			} else if (key.startsWith("data=")) {
				stack.setDurability(Short.valueOf(key.substring(5)));
			} else if (key.startsWith("name=")) {
				meta.setDisplayName(c(key.substring(5)));
			} else if (key.startsWith("lore=")) {
				String lore = key.substring(5);
				meta.setLore(lore.contains(",") ? Arrays.asList(c(lore.split(","))) : Arrays.asList(c(lore)));
			} else if (key.startsWith("enchantments=")) {
				String enchantments = key.substring(13);
				if(enchantments.contains(",")) {
					for(String enchantment : enchantments.split(",")) {
						String[] splitter = enchantment.split("\\:");
						String enchant = splitter[0];
						int level = Integer.valueOf(splitter[1]);
						meta.addEnchant(Enchantment.getByName(enchant), level, true);
					}
				} else {
					String[] splitter = enchantments.split("\\:");
					String enchant = splitter[0];
					int level = Integer.valueOf(splitter[1]);
					meta.addEnchant(Enchantment.getByName(enchant), level, true);
				}
			} else if (key.startsWith("flags=")) {
				String flags = key.substring(6);
				if(flags.contains(",")) {
					for(String flag : flags.split(",")) {
						meta.addItemFlags(ItemFlag.valueOf(flag));
					}
				} else {
					meta.addItemFlags(ItemFlag.valueOf(flags));
				}
			}
		}
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * 
	 * @param string the string where the itemstack gets parsed from
	 * @return a new ItemStack from the parsed String,
	 * <p>for example:
	 * <p><pre>item=DIAMOND | Diamond with the amount 1 and data set to 0</p></pre>
	 * <p><pre>item=WOOL data=15 | 1 Black Wool</p></pre>
	 * <p><pre>item=GRASS enchantments=LOOTING:1,DURABILITY:2 | Enchanted GRASS</p></pre>
	 * <p>available metas:
	 * <p>item={@code<itemName>}
	 * <p>amount={@code<amount>}
	 * <p>data={@code<durability/damage>}
	 * <p>name={@code<displayName>} | & colors are supported
	 * <p>lore={@code<loreLine>,<loreLine2>} | & colors are supported
	 * <p>enchantments={@code<enchant>:<lvl>,<enchant2>:<lvl>}
	 * <p>flags={@code<itemFlag>,<itemFlag2>}</p>
	 */
	public static ItemStack fromString(final String string) {
		ItemStack stack = new ItemStack(Material.STONE, 1);
		ItemMeta meta = stack.getItemMeta();
		for(String key : string.split(" ")) {
			if(key.startsWith("item=")) {
				stack.setType(Material.matchMaterial(key.substring(5)));
			} else if (key.startsWith("amount=")) {
				stack.setAmount(Integer.valueOf(key.substring(7)));
			} else if (key.startsWith("data=")) {
				stack.setDurability(Short.valueOf(key.substring(5)));
			} else if (key.startsWith("name=")) {
				meta.setDisplayName(c(key.substring(5)));
			} else if (key.startsWith("lore=")) {
				String lore = key.substring(5);
				meta.setLore(lore.contains(",") ? Arrays.asList(c(lore.split(","))) : Arrays.asList(c(lore)));
			} else if (key.startsWith("enchantments=")) {
				String enchantments = key.substring(13);
				if(enchantments.contains(",")) {
					for(String enchantment : enchantments.split(",")) {
						String[] splitter = enchantment.split("\\:");
						String enchant = splitter[0];
						int level = Integer.valueOf(splitter[1]);
						meta.addEnchant(Enchantment.getByName(enchant), level, true);
					}
				} else {
					String[] splitter = enchantments.split("\\:");
					String enchant = splitter[0];
					int level = Integer.valueOf(splitter[1]);
					meta.addEnchant(Enchantment.getByName(enchant), level, true);
				}
			} else if (key.startsWith("flags=")) {
				String flags = key.substring(6);
				if(flags.contains(",")) {
					for(String flag : flags.split(",")) {
						meta.addItemFlags(ItemFlag.valueOf(flag));
					}
				} else {
					meta.addItemFlags(ItemFlag.valueOf(flags));
				}
			}
		}
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * 
	 * @param string the string where the itemstack gets parsed from
	 * @return a new ItemStack from the parsed String with placeholders: %name%, %display_name%
	 * <p>for example:
	 * <p><pre>item=DIAMOND | Diamond with the amount 1 and data set to 0</p></pre>
	 * <p><pre>item=WOOL data=15 | 1 Black Wool</p></pre>
	 * <p><pre>item=GRASS enchantments=LOOTING:1,DURABILITY:2 | Enchanted GRASS</p></pre>
	 * <p>available metas:
	 * <p>item={@code<itemName>}
	 * <p>amount={@code<amount>}
	 * <p>data={@code<durability/damage>}
	 * <p>name={@code<displayName>} | & colors are supported
	 * <p>lore={@code<loreLine>,<loreLine2>} | & colors are supported
	 * <p>enchantments={@code<enchant>:<lvl>,<enchant2>:<lvl>}
	 * <p>flags={@code<itemFlag>,<itemFlag2>}</p>
	 */
	public static ItemStack fromString(final String string, final Player player) {
		ItemStack stack = new ItemStack(Material.STONE, 1);
		ItemMeta meta = stack.getItemMeta();
		for(String key : string.split(" ")) {
			if(key.startsWith("item=")) {
				stack.setType(Material.matchMaterial(key.substring(5)));
			} else if (key.startsWith("amount=")) {
				stack.setAmount(Integer.valueOf(key.substring(7)));
			} else if (key.startsWith("data=")) {
				stack.setDurability(Short.valueOf(key.substring(5)));
			} else if (key.startsWith("name=")) {
				meta.setDisplayName(c(key.substring(5), player));
			} else if (key.startsWith("lore=")) {
				String lore = key.substring(5);
				meta.setLore(lore.contains(",") ? Arrays.asList(c(lore.split(","), player)) : Arrays.asList(c(lore, player)));
			} else if (key.startsWith("enchantments=")) {
				String enchantments = key.substring(13);
				if(enchantments.contains(",")) {
					for(String enchantment : enchantments.split(",")) {
						String[] splitter = enchantment.split("\\:");
						String enchant = splitter[0];
						int level = Integer.valueOf(splitter[1]);
						meta.addEnchant(Enchantment.getByName(enchant), level, true);
					}
				} else {
					String[] splitter = enchantments.split("\\:");
					String enchant = splitter[0];
					int level = Integer.valueOf(splitter[1]);
					meta.addEnchant(Enchantment.getByName(enchant), level, true);
				}
			} else if (key.startsWith("flags=")) {
				String flags = key.substring(6);
				if(flags.contains(",")) {
					for(String flag : flags.split(",")) {
						meta.addItemFlags(ItemFlag.valueOf(flag));
					}
				} else {
					meta.addItemFlags(ItemFlag.valueOf(flags));
				}
			}
		}
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * 
	 * @param configurationSection the section where the itemstack gets parsed from
	 * @return a new ItemStack from section keys
	 * <p>for example:
	 * <p>(configurationSection):
	 * <p><pre> item: DIAMOND
	 * amount: 1
	 * data: 0
	 * name: "&6Custom Sword"
	 * lore:
	 * - "&eLore line"
	 * - "&32nd line"
	 * enchantments:
	 * - "DAMAGE_ALL:5"
	 * - "DURABILITY:20"
	 * flags:
	 * - "HIDE_ENCHANTS"
	 * </pre>
	 * Optional keys: everything except <b>item:</b>
	 */
	public static ItemStack fromConfigurationSection(ConfigurationSection configurationSection) {
		ConfigurationSection section = configurationSection;
		ItemStack stack = new ItemStack(Material.STONE, 1);
		ItemMeta meta = stack.getItemMeta();
		for(String key : section.getKeys(false)) {
			if(key.startsWith("item")) {
				stack.setType(Material.matchMaterial(section.getString(key)));
			} else if (key.startsWith("amount")) {
				stack.setAmount(section.getInt(key));
			} else if (key.startsWith("data")) {
				stack.setDurability((short)section.getInt(key));
			} else if (key.startsWith("name")) {
				meta.setDisplayName(c(section.getString(key)));
			} else if (key.startsWith("lore")) {
				List<String> lore = section.getStringList(key);
				meta.setLore(c(lore));
			} else if (key.startsWith("enchantments")) {
				List<String> enchantments = section.getStringList(key);
				enchantments.forEach(line -> {
					String[] enchantment = line.split("\\:");
					String enchant = enchantment[0].toUpperCase();
					int lvl = Integer.valueOf(enchantment[1]);
					meta.addEnchant(XEnchantment.matchXEnchantment(enchant).orElse(XEnchantment.DURABILITY).getEnchant(), lvl, true);
				});
			} else if (key.startsWith("flags")) {
				List<String> flags = section.getStringList(key);
				flags.forEach(line -> {
					meta.addItemFlags(ItemFlag.valueOf(line.toUpperCase()));
				});
			}
		}
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * 
	 * @param configurationSection the section where the itemstack gets parsed from
	 * @return a new ItemStack from section keys
	 * <p>for example:
	 * <p>(configurationSection):
	 * <p><pre> {@code<itemKey>: DIAMOND
	 * <amountKey>: 1
	 * <dataKey>: 0
	 * <nameKey>: "&6Custom Sword"
	 * <loreKey>:
	 * - "&eLore line"
	 * - "&32nd line"
	 * <enchantmentsKey>:
	 * - "DAMAGE_ALL:5"
	 * <flagsKey>:
	 * - "HIDE_ENCHANTS"}
	 * </pre>
	 * Optional keys: everything except <b>item:</b>
	 * <p>Setting a key to null will use the default key
	 * for example if amountKey is null we will just use the key "amount"
	 */
	public static ItemStack fromConfigurationSection(ConfigurationSection configurationSection, @Nullable String itemKey, @Nullable String amountKey, @Nullable String dataKey, @Nullable String nameKey, @Nullable String loreKey, @Nullable String enchantmentsKey, @Nullable String flagsKey) {
		ConfigurationSection section = configurationSection;
		itemKey = itemKey == null ? "item" : itemKey;
		amountKey = amountKey == null ? "amount" : amountKey;
		dataKey = dataKey == null ? "data" : dataKey;
		nameKey = nameKey == null ? "name" : nameKey;
		loreKey = loreKey == null ? "lore" : loreKey;
		enchantmentsKey = enchantmentsKey == null ? "enchantments" : enchantmentsKey;
		flagsKey = flagsKey == null ? "flags" : flagsKey;
		ItemStack stack = new ItemStack(Material.STONE, 1);
		ItemMeta meta = stack.getItemMeta();
		for(String key : section.getKeys(false)) {
			if(key.startsWith(itemKey)) {
				stack.setType(Material.matchMaterial(section.getString(key)));
			} else if (key.startsWith(amountKey)) {
				stack.setAmount(section.getInt(key));
			} else if (key.startsWith(dataKey)) {
				stack.setDurability((short)section.getInt(key));
			} else if (key.startsWith(nameKey)) {
				meta.setDisplayName(c(section.getString(key)));
			} else if (key.startsWith(loreKey)) {
				List<String> lore = section.getStringList(key);
				meta.setLore(c(lore));
			} else if (key.startsWith(enchantmentsKey)) {
				List<String> enchantments = section.getStringList(key);
				enchantments.forEach(line -> {
					String[] enchantment = line.split("\\:");
					String enchant = enchantment[0].toUpperCase();
					int lvl = Integer.valueOf(enchantment[1]);
					meta.addEnchant(XEnchantment.matchXEnchantment(enchant).orElse(XEnchantment.DURABILITY).getEnchant(), lvl, true);
				});
			} else if (key.startsWith(flagsKey)) {
				List<String> flags = section.getStringList(key);
				flags.forEach(line -> {
					meta.addItemFlags(ItemFlag.valueOf(line.toUpperCase()));
				});
			}
		}
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * 
	 * @param configurationSection the section where the itemstack gets parsed from
	 * @param enchantmentSplitter what's between the enchantment name and the level
	 * @return a new ItemStack from section keys
	 * <p>for example:
	 * <p>(configurationSection):
	 * <p><pre> {@code<itemKey>: DIAMOND
	 * <amountKey>: 1
	 * <dataKey>: 0
	 * <nameKey>: "&6Custom Sword"
	 * <loreKey>:
	 * - "&eLore line"
	 * - "&32nd line"
	 * <enchantmentsKey>:
	 * - "DAMAGE_ALL<enchantmentSplitter>5"
	 * <flagsKey>:
	 * - "HIDE_ENCHANTS"}
	 * </pre>
	 * Optional keys: everything except <b>item:</b>
	 * <p>Setting a key to null will use the default key
	 * for example if amountKey is null we will just use the key "amount"
	 */
	public static ItemStack fromConfigurationSection(ConfigurationSection configurationSection, @Nullable String itemKey, @Nullable String amountKey, @Nullable String dataKey, @Nullable String nameKey, @Nullable String loreKey, @Nullable String enchantmentsKey, @Nullable String flagsKey, String enchantmentSplitter) {
		ConfigurationSection section = configurationSection;
		itemKey = itemKey == null ? "item" : itemKey;
		amountKey = amountKey == null ? "amount" : amountKey;
		dataKey = dataKey == null ? "data" : dataKey;
		nameKey = nameKey == null ? "name" : nameKey;
		loreKey = loreKey == null ? "lore" : loreKey;
		enchantmentsKey = enchantmentsKey == null ? "enchantments" : enchantmentsKey;
		flagsKey = flagsKey == null ? "flags" : flagsKey;
		ItemStack stack = new ItemStack(Material.STONE, 1);
		ItemMeta meta = stack.getItemMeta();
		for(String key : section.getKeys(false)) {
			if(key.startsWith(itemKey)) {
				stack.setType(Material.matchMaterial(section.getString(key)));
			} else if (key.startsWith(amountKey)) {
				stack.setAmount(section.getInt(key));
			} else if (key.startsWith(dataKey)) {
				stack.setDurability((short)section.getInt(key));
			} else if (key.startsWith(nameKey)) {
				meta.setDisplayName(c(section.getString(key)));
			} else if (key.startsWith(loreKey)) {
				List<String> lore = section.getStringList(key);
				meta.setLore(c(lore));
			} else if (key.startsWith(enchantmentsKey)) {
				List<String> enchantments = section.getStringList(key);
				enchantments.forEach(line -> {
					String[] enchantment = line.split(enchantmentSplitter);
					String enchant = enchantment[0].toUpperCase();
					int lvl = Integer.valueOf(enchantment[1]);
					meta.addEnchant(XEnchantment.matchXEnchantment(enchant).orElse(XEnchantment.DURABILITY).getEnchant(), lvl, true);
				});
			} else if (key.startsWith(flagsKey)) {
				List<String> flags = section.getStringList(key);
				flags.forEach(line -> {
					meta.addItemFlags(ItemFlag.valueOf(line.toUpperCase()));
				});
			}
		}
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * 
	 * @param map Map where the itemstack gets parsed from
	 * @return a new ItemStack from map entries
	 * <p>for example:
	 * <p> (key), (value)
	 * <p><pre> item, DIAMOND
	 * amount, 1
	 * data, 0
	 * name, "&6Custom Sword"
	 * lore,
	 * - "&eLore line"
	 * - "&32nd line"
	 * enchantments,
	 * - "DAMAGE_ALL:5"
	 * - "DURABILITY:20"
	 * flags,
	 * - "HIDE_ENCHANTS"
	 * </pre>
	 * Optional keys: everything except <b>item:</b>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ItemStack fromMap(Map<String, Object> map) {
		ItemStack stack = new ItemStack(Material.STONE, 1);
		ItemMeta meta = stack.getItemMeta();
		for(String key : map.keySet()) {
			if(key.startsWith("item")) {
				stack.setType(Material.matchMaterial((String)map.get(key)));
			} else if (key.startsWith("amount")) {
				stack.setAmount((int)map.get(key));
			} else if (key.startsWith("data")) {
				stack.setDurability((short)map.get(key));
			} else if (key.startsWith("name")) {
				meta.setDisplayName(c((String)map.get(key)));
			} else if (key.startsWith("lore")) {
				List<String> lore = (List)map.get(key);
				meta.setLore(c(lore));
			} else if (key.startsWith("enchantments")) {
				List<String> enchantments = (List)map.get(key);
				enchantments.forEach(line -> {
					String[] enchantment = line.split("\\:");
					String enchant = enchantment[0].toUpperCase();
					int lvl = Integer.valueOf(enchantment[1]);
					meta.addEnchant(Enchantment.getByName(enchant), lvl, true);
				});
			} else if (key.startsWith("flags")) {
				List<String> flags = (List)map.get(key);
				flags.forEach(line -> {
					meta.addItemFlags(ItemFlag.valueOf(line.toUpperCase()));
				});
			}
		}
		stack.setItemMeta(meta);
		return stack;
	}

	public static List<ItemStack> fromMapList(List<Map<String, Object>> mapList) {
		List<ItemStack> itemStackList = new LinkedList<>();
		for(Map<String, Object> maps : mapList) {
			itemStackList.add(fromMap(maps));
		}
		return itemStackList;
	}

	public static ItemStack[] fromMapListAsArray(List<Map<String, Object>> mapList) {
		ItemStack[] itemStackArray = new ItemStack[] {};
		int counter = 0;
		for(Map<String, Object> maps : mapList) {
			itemStackArray[counter] = fromMap(maps);
			counter++;
		}
		return itemStackArray;
	}

	/**
	 * 
	 * @param itemStack
	 * @return Builds a string reader ItemStack. { item=....} -> fromString(..)
	 * <p>Returns "null" String when itemStack is null
	 */
	@Nonnull
	public static String toString(final ItemStack itemStack) {
		if(itemStack == null) return "null";
		StringBuilder builder = new StringBuilder();
		String item = "item=" + itemStack.getType().name();
		builder.append(item).append(SPACE);
		String amount = "amount=" + itemStack.getAmount();
		builder.append(amount).append(SPACE);
		String data = "data=" + itemStack.getDurability();
		builder.append(data);
		if(!itemStack.hasItemMeta()) return builder.toString();
		builder.append(SPACE);
		ItemMeta meta = itemStack.getItemMeta();
		String name = null;
		if(meta.hasDisplayName()) {
			name = "name=" + ChatColor.stripColor(meta.getDisplayName());
			builder.append(name).append(SPACE);
		}
		List<String> loreList = new ArrayList<>();
		int loreCounter = 0;
		if(meta.hasLore()) {
			List<String> metaLore = meta.getLore();
			int sizeIndex = metaLore.size()-1;
			for(String line : metaLore) {
				if(loreCounter != sizeIndex) {
					loreList.add(ChatColor.stripColor(line) + ',');
				} else {
					loreList.add(ChatColor.stripColor(line));
				}
				loreCounter++;
			}
		}
		builder.append("lore=");
		loreList.forEach(line -> builder.append(line));
		builder.append(SPACE);
		List<String> enchantList = new ArrayList<>();
		int enchantCounter = 0;
		if(meta.hasEnchants()) {
			Map<Enchantment, Integer> metaEnchant = meta.getEnchants();
			int enchantSizeIndex = metaEnchant.size()-1;
			for(Entry<Enchantment, Integer> entry : metaEnchant.entrySet()) {
				if(enchantCounter != enchantSizeIndex) {
					enchantList.add(entry.getKey() + ":" + entry.getValue() + ",");
				} else {
					enchantList.add(entry.getKey() + ":" + entry.getValue());
				}
				enchantCounter++;
			}
		}
		builder.append("enchantments=");
		enchantList.forEach(line -> builder.append(line));
		builder.append(SPACE);
		List<String> flagList = new ArrayList<>();
		int flagCounter = 0;
		if(meta.getItemFlags() != null && !meta.getItemFlags().isEmpty()) {
			Set<ItemFlag> metaFlags = meta.getItemFlags();
			int flagSizeIndex = metaFlags.size()-1;
			for(ItemFlag flag : metaFlags) {
				if(flagCounter != flagSizeIndex) {
					flagList.add(flag.name() + ",");
				} else {
					flagList.add(flag.name());
				}
				flagCounter++;
			}
		}
		builder.append("flags=");
		flagList.forEach(line -> builder.append(line));
		return builder.toString();
	}

	public static String[] toArray(ItemStack[] itemStackArray) {
		String[] arrayOfStrings = new String[] {};
		for(int i = 0 ; i < itemStackArray.length ; i++) {
			arrayOfStrings[i] = toString(itemStackArray[i]);
		}
		return arrayOfStrings;
	}

	public static String[] toArray(ItemStack itemStack) {
		String[] arrayOfStrings = new String[] {};
		arrayOfStrings[0] = toString(itemStack);
		return arrayOfStrings;
	}

	public static String[] toArrayFromList(List<ItemStack> itemStackList) {
		String[] arrayOfStrings = new String[] {};
		for(int i = 0 ; i < itemStackList.size() ; i++) {
			arrayOfStrings[i] = toString(itemStackList.get(i));
		}
		return arrayOfStrings;
	}

	public static List<String> toList(List<ItemStack> itemStackList) {
		List<String> stringList = new ArrayList<>();
		for(int i = 0 ; i < itemStackList.size() ; i++) {
			stringList.add(toString(itemStackList.get(i)));
		}
		return stringList;
	}

	public static List<String> toListFromArray(ItemStack[] itemStackArray) {
		List<String> stringList = new ArrayList<>();
		for(int i = 0 ; i < itemStackArray.length ; i++) {
			stringList.add(toString(itemStackArray[i]));
		}
		return stringList;
	}

	@Nullable
	public static Map<String, Object> toMap(ItemStack itemStack) {
		ItemStack stack = itemStack;
		if(stack == null) return null;
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("item", stack.getType().name());
		map.put("amount", stack.getAmount());
		map.put("data", stack.getDurability());
		if(stack.hasItemMeta()) {
			ItemMeta meta = stack.getItemMeta();
			String displayName = meta.getDisplayName();
			if(displayName != null) {
				map.put("name", ChatColor.stripColor(meta.getDisplayName()));
			}
			List<String> lore = meta.getLore();
			if(lore != null && !lore.isEmpty()) {
				List<String> newLore = new ArrayList<>();
				lore.forEach(line -> {
					newLore.add(ChatColor.stripColor(line));
				});
				map.put("lore", newLore);
			}
			Map<Enchantment, Integer> enchantments = meta.getEnchants();
			if(enchantments != null && !enchantments.isEmpty()) {
				List<String> newEnchantments = new ArrayList<>();
				enchantments.entrySet().forEach(entry -> {
					newEnchantments.add(entry.getKey() + ":" + entry.getValue());
				});
				map.put("enchantments", newEnchantments);
			}
			Set<ItemFlag> itemFlags = meta.getItemFlags();
			if(itemFlags != null && !itemFlags.isEmpty()) {
				List<String> newFlags = new ArrayList<>();
				itemFlags.forEach(flag -> {
					newFlags.add(flag.name());
				});
				map.put("flags", newFlags);
			}
		}
		return map;
	}

	@Nullable
	public static Map<String, Object> toMap(ItemStack itemStack, String itemKey, String amountKey, String dataKey, String nameKey, String loreKey, String enchantmentsKey, String flagsKey) {
		itemKey = itemKey == null ? "item" : itemKey;
		amountKey = amountKey == null ? "amount" : amountKey;
		dataKey = dataKey == null ? "data" : dataKey;
		nameKey = nameKey == null ? "name" : nameKey;
		loreKey = loreKey == null ? "lore" : loreKey;
		enchantmentsKey = enchantmentsKey == null ? "enchantments" : enchantmentsKey;
		flagsKey = flagsKey == null ? "flags" : flagsKey;
		ItemStack stack = itemStack;
		if(stack == null) return null;
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(itemKey, stack.getType().name());
		map.put(amountKey, stack.getAmount());
		map.put(dataKey, stack.getDurability());
		if(stack.hasItemMeta()) {
			ItemMeta meta = stack.getItemMeta();
			String displayName = meta.getDisplayName();
			if(displayName != null) {
				map.put(nameKey, ChatColor.stripColor(meta.getDisplayName()));
			}
			List<String> lore = meta.getLore();
			if(lore != null && !lore.isEmpty()) {
				List<String> newLore = new ArrayList<>();
				lore.forEach(line -> {
					newLore.add(ChatColor.stripColor(line));
				});
				map.put(loreKey, newLore);
			}
			Map<Enchantment, Integer> enchantments = meta.getEnchants();
			if(enchantments != null && !enchantments.isEmpty()) {
				List<String> newEnchantments = new ArrayList<>();
				enchantments.entrySet().forEach(entry -> {
					newEnchantments.add(entry.getKey() + ":" + entry.getValue());
				});
				map.put("enchantments", newEnchantments);
			}
			Set<ItemFlag> itemFlags = meta.getItemFlags();
			if(itemFlags != null && !itemFlags.isEmpty()) {
				List<String> newFlags = new ArrayList<>();
				itemFlags.forEach(flag -> {
					newFlags.add(flag.name());
				});
				map.put(flagsKey, newFlags);
			}
		}
		return map;
	}

	@Nullable
	public static Map<String, Object> toMap(ItemStack itemStack, String itemKey, String amountKey, String dataKey, String nameKey, String loreKey, String enchantmentsKey, String flagsKey, String enchantmentSplitter) {
		itemKey = itemKey == null ? "item" : itemKey;
		amountKey = amountKey == null ? "amount" : amountKey;
		dataKey = dataKey == null ? "data" : dataKey;
		nameKey = nameKey == null ? "name" : nameKey;
		loreKey = loreKey == null ? "lore" : loreKey;
		enchantmentsKey = enchantmentsKey == null ? "enchantments" : enchantmentsKey;
		flagsKey = flagsKey == null ? "flags" : flagsKey;
		ItemStack stack = itemStack;
		if(stack == null) return null;
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(itemKey, stack.getType().name());
		map.put(amountKey, stack.getAmount());
		map.put(dataKey, stack.getDurability());
		if(stack.hasItemMeta()) {
			ItemMeta meta = stack.getItemMeta();
			String displayName = meta.getDisplayName();
			if(displayName != null) {
				map.put(nameKey, ChatColor.stripColor(meta.getDisplayName()));
			}
			List<String> lore = meta.getLore();
			if(lore != null && !lore.isEmpty()) {
				List<String> newLore = new ArrayList<>();
				lore.forEach(line -> {
					newLore.add(ChatColor.stripColor(line));
				});
				map.put(loreKey, newLore);
			}
			Map<Enchantment, Integer> enchantments = meta.getEnchants();
			if(enchantments != null && !enchantments.isEmpty()) {
				List<String> newEnchantments = new ArrayList<>();
				enchantments.entrySet().forEach(entry -> {
					newEnchantments.add(entry.getKey() + enchantmentSplitter + entry.getValue());
				});
				map.put("enchantments", newEnchantments);
			}
			Set<ItemFlag> itemFlags = meta.getItemFlags();
			if(itemFlags != null && !itemFlags.isEmpty()) {
				List<String> newFlags = new ArrayList<>();
				itemFlags.forEach(flag -> {
					newFlags.add(flag.name());
				});
				map.put(flagsKey, newFlags);
			}
		}
		return map;
	}

	@Nullable
	public static List<Map<String, Object>> toMapList(List<ItemStack> itemStackList) {
		if(itemStackList == null) return null;
		List<Map<String, Object>> mapList = new LinkedList<>();
		for(ItemStack itemStack : itemStackList) {
			Map<String, Object> map = toMap(itemStack);
			mapList.add(map);
		}
		return mapList;
	}

	@Nullable
	public static List<Map<String, Object>> toMapListFromArray(ItemStack[] itemStackArray) {
		if(itemStackArray == null) return null;
		List<Map<String, Object>> mapList = new LinkedList<>();
		for(ItemStack itemStack : itemStackArray) {
			Map<String, Object> map = toMap(itemStack);
			mapList.add(map);
		}
		return mapList;
	}

	@Nullable
	public static List<ItemStack> fromList(final List<String> stringList) {
		if(stringList == null) return null; 
		List<ItemStack> itemStackList = new ArrayList<>();
		stringList.forEach(line -> {
			itemStackList.add(fromString(line));
		});
		return itemStackList;
	}

	@Nullable
	public static ItemStack[] fromListAsArray(final List<String> stringList) {
		if(stringList == null) return null; 
		ItemStack[] itemStackArray = new ItemStack[] {};
		for(int i = 0; i < stringList.size(); i++) {
			itemStackArray[i] = fromString(stringList.get(i));
		}
		return itemStackArray;
	}

	@Nullable
	public static List<ItemStack> fromArrayAsList(final String[] arrayOfStrings) {
		if(arrayOfStrings == null) return null; 
		List<ItemStack> itemStackList = new ArrayList<>();
		for(String line : arrayOfStrings) {
			itemStackList.add(fromString(line));
		}
		return itemStackList;
	}

	@Nullable
	public static ItemStack[] fromArray(final String[] arrayOfStrings) {
		if(arrayOfStrings == null) return null; 
		ItemStack[] itemStackArray = new ItemStack[] {};
		for(int i = 0; i < arrayOfStrings.length; i++) {
			itemStackArray[i] = fromString(arrayOfStrings[i]);
		}
		return itemStackArray;
	}

}
