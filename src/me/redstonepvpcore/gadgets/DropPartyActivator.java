package me.redstonepvpcore.gadgets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import me.redstonepvpcore.mothers.DropPartyActivatorMother;
import me.redstonepvpcore.player.BypassManager;
import me.redstonepvpcore.player.Permissions;
import me.redstonepvpcore.utils.CooldownScheduler;
import me.redstonepvpcore.utils.XSound;

public class DropPartyActivator extends Gadget {

	private boolean running;
	private AtomicReference<BukkitTask> droppingTask;
	
	public DropPartyActivator(Location location) {
		super(GadgetType.DROP_PARTY_ACTIVATOR, location);
		getMessagesHolder().setMessage(0, getParent().getMessages().getSetDropParty());
		getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveDropParty());
		
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public Location getRandomDropLocation() {
		return getLocation().clone().add(ThreadLocalRandom.current().nextDouble(-1, 1), 1, ThreadLocalRandom.current().nextDouble(-1, 1));
	}
	
	public boolean activate() {
		if(running) return false;
		running = true;
		DropPartyActivatorMother mother = getParent().getDropPartyActivatorMother();
		if(mother.isBroadcastStartMessage()) {
			broadcastMessage(getParent().getMessages().getDropPartyActivate());
		} else {
			getLocation().getWorld().getNearbyEntities(getLocation(), 5, 5, 5).stream().filter(entity -> entity.getType() == EntityType.PLAYER)
			.map(ent -> (Player)ent)
			.forEach(player -> {
				player.sendMessage(getParent().getMessages().getDropPartyActivate());
			});
		}
		if(mother.isBroadcastStartSound()) {
			broadcastSound(mother.getUseSound());
		} else {
			getLocation().getWorld().getNearbyEntities(getLocation(), 5, 5, 5).stream().filter(entity -> entity.getType() == EntityType.PLAYER)
			.map(ent -> (Player)ent)
			.forEach(player -> {
				sendSound(player, mother.getUseSound());
			});
		}
		List<ItemStack> itemsToDrop = mother.getItemsToDrop();
		int lastIndex = itemsToDrop.size();
		int droppingDuration = mother.getDroppingDuration();
		Set<Integer> waterSpawnDurations = mother.getWaterSpawnDurations();
		Location waterSpawnLocation = getLocation().clone().add(0, 1, 0);
		Block bottomBlock = getLocation().clone().add(0, -1, 0).getBlock();
		Material originalType = bottomBlock.getType();
		droppingTask = new AtomicReference<>();
		AtomicInteger dropped = new AtomicInteger();
		droppingTask.set(getParent().doSyncRepeating(() -> {
			ItemStack itemToDrop = itemsToDrop.get(ThreadLocalRandom.current().nextInt(0, lastIndex));
			Material bottomBlockType = bottomBlock.getType();
			if(bottomBlockType == Material.DIAMOND_BLOCK || bottomBlockType == Material.GOLD_BLOCK) {
				bottomBlock.setType(bottomBlockType == Material.DIAMOND_BLOCK ? Material.GOLD_BLOCK : Material.DIAMOND_BLOCK);
				getLocation().getWorld().dropItem(getRandomDropLocation(), itemToDrop);
			}
			getLocation().getWorld().dropItem(getRandomDropLocation(), itemToDrop);
			sendSound(getLocation(), mother.getDropSound());
			if(waterSpawnDurations.contains(dropped.get())) {
				getLocation().getWorld().getBlockAt(waterSpawnLocation).setType(Material.WATER);
				getLocation().getWorld().playSound(waterSpawnLocation, XSound.ENTITY_GENERIC_EXPLODE.parseSound(), (float)10.0, (float)1.5);
				getParent().doSyncLater(() -> {
					getLocation().getWorld().getBlockAt(waterSpawnLocation).setType(Material.AIR);
				}, mother.getWaterRemoveDuration());
			}
			if(dropped.incrementAndGet() >= droppingDuration) {
				running = false;
				bottomBlock.setType(originalType);
				sendSound(getLocation(), mother.getEndSound());
				droppingTask.get().cancel();
			}
		}, 0, mother.getBetweenDropsDuration()));
		return true;
	}
	
	@Override
	public boolean perform(Player player) {
		if(getParent().getDropPartyActivatorMother().isAlwaysOn()) {
			sendMessage(player, getParent().getMessages().getDropPartyAlreadyRunning());
			return false;
		}
		boolean isBypassing = BypassManager.isBypassOn(player.getUniqueId());
		if(!Permissions.hasPermission(player, Permissions.DROPPARTY_USE_PERMISSION)) {
			sendMessage(player, getParent().getMessages().getNoPermissionUse());
			return false;
		}
		int requiredPlayers = getParent().getDropPartyActivatorMother().getRequiredPlayers();
		if(!isBypassing && Bukkit.getOnlinePlayers().size() < requiredPlayers) {
			sendMessage(player, getParent().getMessages().getDropPartyNotEnoughPlayers()
					.replace("%amount%", String.valueOf(requiredPlayers)));
			return false;
		}	
		AtomicBoolean performed = new AtomicBoolean();
		CooldownScheduler.scheduleAsync(getLocation(), getParent().getDropPartyActivatorMother().getCooldownDuration())
		.ifTrue(() -> {
			performed.set(true);
			if(!activate()) sendMessage(player, getParent().getMessages().getDropPartyAlreadyRunning());
		})
		.orElse(timeLeft -> {
			if(isBypassing) {
				performed.set(true);
				activate();
				return;
			}
			if(running) {
				sendMessage(player, getParent().getMessages().getDropPartyAlreadyRunning());
				return;
			}
			sendMessage(player, getParent().getMessages().getDropPartyNotReady()
					.replace("%time%", String.valueOf(timeLeft)));
		})
		.whenDone(() -> {
			if(getParent().getDropPartyActivatorMother().isBroadcastReadyMessage()) {
				Bukkit.broadcastMessage(getParent().getMessages().getDropPartyReady());
			} else {
				sendMessage(player, getParent().getMessages().getDropPartyReady());
			}
			if(getParent().getDropPartyActivatorMother().isBroadcastReadySound()) {
				broadcastSound(getParent().getDropPartyActivatorMother().getReadySound());
			} else {
				sendSound(getLocation(), getParent().getDropPartyActivatorMother().getReadySound());
			}
		});
		return performed.get();
	}

}
