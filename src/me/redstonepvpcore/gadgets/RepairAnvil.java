package me.redstonepvpcore.gadgets;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.redstonepvpcore.player.BypassManager;
import me.redstonepvpcore.player.Permissions;
import me.redstonepvpcore.utils.ParticleReader;
import me.redstonepvpcore.utils.XSound.Record;
import xyz.xenondevs.particle.ParticleBuilder;

public class RepairAnvil extends Gadget {

	final static ItemStack AIR = new ItemStack(Material.AIR);

	public RepairAnvil(Location location) {
		super(GadgetType.REPAIR_ANVIL, location);
		getMessagesHolder().setMessage(0, getParent().getMessages().getSetRepairAnvil());
		getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveRepairAnvil());
	}

	private CompletableFuture<Void> playAnvilAnimation(ItemStack itemStack, int speed)
	{
		CompletableFuture<Void> taskFinishFuture = new CompletableFuture<>();
		AtomicInteger counter = new AtomicInteger(0);
		AtomicReference<BukkitTask> animationTask = new AtomicReference<>();
		AtomicReference<Item> droppedItem = new AtomicReference<>();
		Location centerLocation = getLocation().clone().add(0.50, 1, 0.50);
		Location particleLocation = getLocation().clone().add(0.75, 1, 0.25);
		Location particleLocation2 = getLocation().clone().add(0.25, 1, 0.75);
		boolean playOnce = getParent().getRepairAnvilMother().isPlayOnce();
		getParent().doSync(() -> {
			if(itemStack.getType() != Material.AIR) {
				droppedItem.set(getLocation().getWorld().dropItem(centerLocation, itemStack));
				droppedItem.get().setPickupDelay(999);
				droppedItem.get().setVelocity(new Vector(0, 0, 0));
			}
		});
		Record animationSound = getParent().getRepairAnvilMother().getAnimationSound();
		animationTask.set(getParent().doAsyncRepeating(() -> {
			List<ParticleBuilder> particles = getParent().getRepairAnvilMother().getParticles();
			ParticleReader.spawnParticles(centerLocation, particles);
			ParticleReader.spawnParticles(particleLocation, particles);
			ParticleReader.spawnParticles(particleLocation2, particles);
			if(counter.get() < 5)
			if(animationSound != null)
			if(!playOnce) 
				getParent().getRepairAnvilMother().getAnimationSound().atLocation(centerLocation).play();
			if(counter.incrementAndGet() == 6) {
				if(itemStack.getType() != Material.AIR)
				getParent().doSync(() -> droppedItem.get().remove());
				taskFinishFuture.complete(null);
				animationTask.get().cancel();
			}
		}, 0, speed));
		return taskFinishFuture;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean perform(Player player) {
		boolean isBypassing = BypassManager.isBypassOn(player.getUniqueId());
		if(!Permissions.hasPermission(player, Permissions.REPAIRANVIL_USE_PERMISSION)) {
			sendMessage(player, getParent().getMessages().getNoPermissionUse());
			return false;
		}
		ItemStack itemInHand = player.getItemInHand();
		if(!isBypassing && itemInHand == null) {
			sendMessage(player, getParent().getMessages().getRepairAnvilEmptyHand());
			return false;
		}
		if(!isBypassing && itemInHand.getDurability() == (short)0) {
			sendMessage(player, getParent().getMessages().getRepairAnvilAlreadyRepaired());
			return false;
		}
		Material material = itemInHand.getType();
		if(!isBypassing && !getParent().getRepairAnvilMother().isRepairable(material)) {
			sendMessage(player, getParent().getMessages().getRepairAnvilNotRepairable()
					.replace("%item%", material.name()));
			return false;
		}
		ItemStack takeItem = getParent().getRepairAnvilMother().getTakeItem();
		int takeItemAmount = takeItem.getAmount();
		if(!isBypassing && !player.getInventory().containsAtLeast(takeItem, takeItemAmount)) {
			sendMessage(player, getParent().getMessages().getRepairAnvilNotEnough()
					.replace("%item%", takeItem.getType().name())
					.replace("%amount%", String.valueOf(takeItemAmount)));
			return false;
		}
		sendMessage(player, getParent().getMessages().getRepairAnvilRepairing());
		sendSound(getLocation(), getParent().getRepairAnvilMother().getUseSound());
		if(isBypassing) 
			player.getInventory().removeItem(itemInHand);
		else 
			player.getInventory().removeItem(itemInHand, takeItem);
		player.setItemInHand(AIR);
		player.updateInventory();
		playAnvilAnimation(itemInHand, 10).thenRun(() -> {
			Record animationSound = getParent().getRepairAnvilMother().getAnimationSound();
			if(animationSound != null)
			if(getParent().getRepairAnvilMother().isPlayOnce()) animationSound.atLocation(getLocation()).play();
			itemInHand.setDurability((short)0);
			player.getInventory().addItem(itemInHand);
			sendMessage(player, getParent().getMessages().getRepairAnvilRepaired());
		});
		return true;
	}

}
