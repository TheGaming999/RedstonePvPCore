package me.redstonepvpcore.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ExtradamageDamageHandler extends EnchantmentDamageHandler {

	public ExtradamageDamageHandler(PotionEffectType potionEffectType) {
		super(potionEffectType);
	}

	@Override
	public void onDamage(LivingEntity target, Player damager, int level) {
		PotionEffect potionEffect = new PotionEffect(getPotionEffectType(), 50, level);
		damager.addPotionEffect(potionEffect);
		target.damage(1.0, damager);
		damager.getWorld().playEffect(target.getLocation(), POTION_BREAK, 12, 7);
	}

}
