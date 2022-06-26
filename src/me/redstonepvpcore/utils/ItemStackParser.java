package me.redstonepvpcore.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.redstonepvpcore.RedstonePvPCore;

public class ItemStackParser {

	private static String c(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	private static boolean isNumber(String number) {
		try {
			Integer.parseInt(number);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack parse(String stringValue) {
		ItemStack stringStack = new ItemStack(Material.STONE, 1);
		ItemMeta stackMeta = stringStack.getItemMeta();
		int amount = 1;
		int data = 0;
		String displayName = "";
		List<String> lore = new ArrayList<>();
		for (String stringMeta : stringValue.split(" ")) {
			if (stringMeta.startsWith("material=")) { // item stack with data support
				String itemNameWithData = stringMeta.substring(9);
				if (itemNameWithData.contains("#")) {
					String[] itemNameAndDataSplit = itemNameWithData.split("#");
					String itemName = itemNameAndDataSplit[0];
					short itemData = Short.parseShort(itemNameAndDataSplit[1]);
					stringStack = new ItemStack(Material.matchMaterial(itemName), 1, itemData);
				} else {
					String itemName = itemNameWithData;
					stringStack = XMaterial.matchXMaterial(itemName).get().parseItem();
				}
				stackMeta = stringStack.getItemMeta();
			} // item stack check
			if (stringMeta.startsWith("amount=")) {
				if (isNumber(stringMeta.substring(7))) {
					amount = Integer.parseInt(stringMeta.substring(7));
				} else {
					amount = 1;
				}
				stringStack.setAmount(amount);
			} // amount check
			if (stringMeta.startsWith("data=")) {
				if (isNumber(stringMeta.substring(5))) {
					data = Integer.parseInt(stringMeta.substring(5));
				}
				stringStack.setDurability((short) data);
			}
			if (stringMeta.startsWith("name=")) {
				displayName = c(stringMeta.substring(5));
				stackMeta.setDisplayName(displayName.replace("_", " "));
			} // name check
			if (stringMeta.startsWith("lore=")) {
				String fullLore = stringMeta.substring(5);
				if (fullLore.contains(",")) {
					for (String loreLine : fullLore.split("\\,")) {
						lore.add(c(loreLine).replace("_", " "));
					}
				} else {
					lore.add(c(fullLore).replace("_", " "));
				}
				stackMeta.setLore(lore);
			} // lore check
			if (stringMeta.startsWith("enchantments=")) {
				String fullEnchantmentWithLvl = stringMeta.substring(13);
				if (fullEnchantmentWithLvl.contains(",")) {
					for (String singleEnchantmentWithLvl : fullEnchantmentWithLvl.split("\\,")) {
						String[] enchantmentSplit = singleEnchantmentWithLvl.split("\\:");
						String enchantment = enchantmentSplit[0];
						int lvl = Integer.parseInt(enchantmentSplit[1]);
						stackMeta.addEnchant(XEnchantment.matchXEnchantment(enchantment)
								.orElse(XEnchantment.DURABILITY)
								.getEnchant(), lvl, true);
					}
				} else {
					String[] enchantmentSplit = fullEnchantmentWithLvl.split("\\:");
					String enchantment = enchantmentSplit[0];
					int lvl = Integer.parseInt(enchantmentSplit[1]);
					stackMeta.addEnchant(
							XEnchantment.matchXEnchantment(enchantment).orElse(XEnchantment.DURABILITY).getEnchant(),
							lvl, true);
				}
			} // enchantments check
			if (stringMeta.startsWith("flags=")) {
				String flagsList = stringMeta.substring(6);
				if (flagsList.contains(",")) {
					for (String singleFlag : flagsList.split("\\,")) {
						stackMeta.addItemFlags(ItemFlag.valueOf(singleFlag.toUpperCase()));
					}
				} else {
					stackMeta.addItemFlags(ItemFlag.valueOf(flagsList.toUpperCase()));
				}
			} // item flags check
			stringStack.setItemMeta(stackMeta);
		}
		return stringStack;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack parseCustom(String stringValue) {
		ItemStack stringStack = new ItemStack(Material.STONE, 1);
		ItemMeta stackMeta = stringStack.getItemMeta();
		int amount = 1;
		int data = 0;
		String displayName = "";
		List<String> lore = new ArrayList<>();
		for (String stringMeta : stringValue.split(" ")) {
			if (stringMeta.startsWith("material=")) { // item stack with data support
				String itemNameWithData = stringMeta.substring(9);
				if (itemNameWithData.contains("#")) {
					String[] itemNameAndDataSplit = itemNameWithData.split("#");
					String itemName = itemNameAndDataSplit[0];
					short itemData = Short.parseShort(itemNameAndDataSplit[1]);
					stringStack = new ItemStack(Material.matchMaterial(itemName), 1, itemData);
				} else {
					String itemName = itemNameWithData;
					stringStack = XMaterial.matchXMaterial(itemName).get().parseItem();
				}
				stackMeta = stringStack.getItemMeta();
			} // item stack check
			if (stringMeta.startsWith("amount=")) {
				if (isNumber(stringMeta.substring(7))) {
					amount = Integer.parseInt(stringMeta.substring(7));
				} else {
					amount = 1;
				}
				stringStack.setAmount(amount);
			} // amount check
			if (stringMeta.startsWith("data=")) {
				if (isNumber(stringMeta.substring(5))) {
					data = Integer.parseInt(stringMeta.substring(5));
				}
				stringStack.setDurability((short) data);
			}
			if (stringMeta.startsWith("name=")) {
				displayName = c(stringMeta.substring(5));
				stackMeta.setDisplayName(displayName.replace("_", " "));
			} // name check
			if (stringMeta.startsWith("lore=")) {
				String fullLore = stringMeta.substring(5);
				if (fullLore.contains(",")) {
					for (String loreLine : fullLore.split("\\,")) {
						lore.add(c(loreLine).replace("_", " "));
					}
				} else {
					lore.add(c(fullLore).replace("_", " "));
				}
				stackMeta.setLore(lore);
			} // lore check
			if (stringMeta.startsWith("enchantments=")) {
				String fullEnchantmentWithLvl = stringMeta.substring(13);
				if (fullEnchantmentWithLvl.contains(",")) {
					for (String singleEnchantmentWithLvl : fullEnchantmentWithLvl.split("\\,")) {
						String[] enchantmentSplit = singleEnchantmentWithLvl.split("\\:");
						String enchantment = enchantmentSplit[0];
						int lvl = Integer.parseInt(enchantmentSplit[1]);
						stackMeta.addEnchant(XEnchantment.matchXEnchantment(enchantment)
								.orElse(XEnchantment.DURABILITY)
								.getEnchant(), lvl, true);
					}
				} else {
					String[] enchantmentSplit = fullEnchantmentWithLvl.split("\\:");
					String enchantment = enchantmentSplit[0];
					int lvl = Integer.parseInt(enchantmentSplit[1]);
					stackMeta.addEnchant(
							XEnchantment.matchXEnchantment(enchantment).orElse(XEnchantment.DURABILITY).getEnchant(),
							lvl, true);
				}
			} // enchantments check
			if (stringMeta.startsWith("custom-enchantments=")) {
				String fullEnchantmentWithLvl = stringMeta.substring(20);
				if (fullEnchantmentWithLvl.contains(",")) {
					for (String singleEnchantmentWithLvl : fullEnchantmentWithLvl.split("\\,")) {
						String[] enchantmentSplit = singleEnchantmentWithLvl.split("\\:");
						String enchantment = enchantmentSplit[0];
						int lvl = Integer.parseInt(enchantmentSplit[1]);
						stringStack = RedstonePvPCore.getInstance()
								.getEnchantmentManager()
								.enchant(stringStack, enchantment, lvl, true)
								.getItemStack();
					}
				} else {
					String[] enchantmentSplit = fullEnchantmentWithLvl.split("\\:");
					String enchantment = enchantmentSplit[0];
					int lvl = Integer.parseInt(enchantmentSplit[1]);
					stringStack = RedstonePvPCore.getInstance()
							.getEnchantmentManager()
							.enchant(stringStack, enchantment, lvl, true)
							.getItemStack();
				}
			}
			if (stringMeta.startsWith("soulbound=")) {
				stringStack = RedstonePvPCore.getInstance().getSoulBoundManager().addSoulBound(stringStack);
			}
			if (stringMeta.startsWith("flags=")) {
				String flagsList = stringMeta.substring(6);
				if (flagsList.contains(",")) {
					for (String singleFlag : flagsList.split("\\,")) {
						stackMeta.addItemFlags(ItemFlag.valueOf(singleFlag.toUpperCase()));
					}
				} else {
					stackMeta.addItemFlags(ItemFlag.valueOf(flagsList.toUpperCase()));
				}
			} // item flags check
			stringStack.setItemMeta(stackMeta);
		}
		return stringStack;
	}

	public static List<ItemStack> parse(List<String> stringList) {
		List<ItemStack> itemStacks = new ArrayList<>();
		stringList.forEach(string -> itemStacks.add(parse(string)));
		return itemStacks;
	}

	public static List<ItemStack> parseCustom(List<String> stringList) {
		List<ItemStack> itemStacks = new ArrayList<>();
		stringList.forEach(string -> itemStacks.add(parseCustom(string)));
		return itemStacks;
	}

}
