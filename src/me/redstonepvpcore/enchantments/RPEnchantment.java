package me.redstonepvpcore.enchantments;

public class RPEnchantment {
	
	private String name;
	private String displayName;
	private int maxLevel;
	private int id;
	private EnchantmentDamageHandler damageHandler;
	
	public RPEnchantment(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EnchantmentDamageHandler getDamageHandler() {
		return damageHandler;
	}

	public void setDamageHandler(EnchantmentDamageHandler damageHandler) {
		this.damageHandler = damageHandler;
	}
	
}
