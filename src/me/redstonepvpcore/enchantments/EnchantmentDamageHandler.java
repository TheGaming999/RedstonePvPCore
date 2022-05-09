package me.redstonepvpcore.enchantments;

import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public abstract class EnchantmentDamageHandler {

	private PotionEffectType potionEffectType;
	public final static Effect POTION_BREAK = Effect.POTION_BREAK;
	
	public EnchantmentDamageHandler(PotionEffectType potionEffectType) {
		this.potionEffectType = potionEffectType;
	}
	
	/**
	 * 
	 * @return registered potion effect type
	 */
	public PotionEffectType getPotionEffectType() {
		return potionEffectType;
	}
	
	/**
	 * When damaging the target, applies the method.
	 * @param target target being attacked
	 * @param damager who is damaging the target
	 * @param level level of the enchantment
	 */
	public abstract void onDamage(LivingEntity target, Player damager, int level);

	
}
