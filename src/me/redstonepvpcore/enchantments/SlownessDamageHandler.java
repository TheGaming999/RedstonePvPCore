package me.redstonepvpcore.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlownessDamageHandler extends EnchantmentDamageHandler {

	public SlownessDamageHandler(PotionEffectType potionEffectType) {
		super(potionEffectType);
	}

	@Override
	public void onDamage(LivingEntity target, Player damager, int level) {
		PotionEffect potionEffect = new PotionEffect(getPotionEffectType(), 180 * level, level);
		target.addPotionEffect(potionEffect);
		target.damage(1.0, damager);
		damager.getWorld().playEffect(target.getLocation(), POTION_BREAK, 10, 2);
	}

}
