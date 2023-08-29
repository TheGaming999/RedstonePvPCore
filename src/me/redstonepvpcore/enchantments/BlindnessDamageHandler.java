package me.redstonepvpcore.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlindnessDamageHandler extends EnchantmentDamageHandler {

	public BlindnessDamageHandler(PotionEffectType potionEffectType) {
		super(potionEffectType);
	}

	@Override
	public void onDamage(LivingEntity target, Player damager, int level) {
		PotionEffect potionEffect = new PotionEffect(getPotionEffectType(), 190, level);
		target.addPotionEffect(potionEffect);
		target.damage(1.0, damager);
		damager.getWorld().playEffect(target.getLocation(), POTION_BREAK, 8, 5);
	}

}
