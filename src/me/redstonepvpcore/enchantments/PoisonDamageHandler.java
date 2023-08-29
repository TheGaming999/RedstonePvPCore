package me.redstonepvpcore.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonDamageHandler extends EnchantmentDamageHandler {

	public PoisonDamageHandler(PotionEffectType potionEffectType) {
		super(potionEffectType);
	}

	@Override
	public void onDamage(LivingEntity target, Player damager, int level) {
		PotionEffect potionEffect = new PotionEffect(getPotionEffectType(), 110, level);
		target.addPotionEffect(potionEffect);
		target.damage(0.69, damager);
		damager.getWorld().playEffect(target.getLocation(), POTION_BREAK, 4, 5);
	}

}
