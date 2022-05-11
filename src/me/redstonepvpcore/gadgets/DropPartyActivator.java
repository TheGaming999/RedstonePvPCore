package me.redstonepvpcore.gadgets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import me.redstonepvpcore.messages.TimeFormatter;
import me.redstonepvpcore.mothers.DropPartyActivatorMother;
import me.redstonepvpcore.player.BypassManager;
import me.redstonepvpcore.player.Permissions;
import me.redstonepvpcore.utils.ActionBar;
import me.redstonepvpcore.utils.CooldownScheduler;
import me.redstonepvpcore.utils.CooldownScheduler.AsyncCooldownEntry;
import xyz.xenondevs.particle.ParticleEffect;

public class DropPartyActivator extends Gadget {

	private boolean running;
	private AtomicReference<BukkitTask> dropTask;
	private AsyncCooldownEntry<Location> cooldown = null;
	public AtomicInteger elapsedTime = new AtomicInteger();
	private boolean deactivateFlag = false;
	public boolean speedChanged;
	private Set<Integer> waterSpawnDurations = new HashSet<>();
	private Location waterSpawnLocation;
	private Location bottomLocation;
	private Location particleSpawnLocation;
	private Location[] particleSpawnLocationCorners;
	private ParticleEffect smokeLarge;
	private ParticleEffect spell;
	private List<ItemStack> itemsToDrop;

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

	public AsyncCooldownEntry<Location> getCooldown() {
		return cooldown;
	}

	public Location getRandomDropLocation(Location loc) {
		return loc.clone().add(ThreadLocalRandom.current().nextDouble(-1, 2), 1, ThreadLocalRandom.current().nextDouble(-1, 2));
	}

	public void deactivate() {
		deactivateFlag = true;
	}

	public boolean activate() {
		if(running) return false;
		running = true;
		getParent().setDropPartyRunning(true);
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
		int lastIndex = itemsToDrop.size();
		int droppingDuration = mother.getDroppingDuration();
		int betweenDropsDuration = mother.getBetweenDropsDuration();
		waterSpawnDurations.clear();
		waterSpawnDurations.addAll(mother.getWaterSpawnDurations());
		Block bottomBlock = bottomLocation.getBlock();
		Material originalType = bottomBlock.getType();
		dropTask = new AtomicReference<>();
		AtomicInteger tickCounter = new AtomicInteger(getParent().getDropPartyActivatorMother().getBetweenDropsDuration()-1);
		AtomicInteger currentTick = new AtomicInteger(0);
		boolean debugTicks = getParent().getDropPartyActivatorMother().isDebugTicks();
		elapsedTime.set(1);
		dropTask.set(getParent().doAsyncRepeating(() -> {
			if(debugTicks) 
				ActionBar.sendPlayersActionBar("Current tick: " + getParent().getDropPartyActivatorMother().getUseSound().getTicks());
			currentTick.incrementAndGet();
			if(mother.isChangeBetweenDropsSpeed()) {
				Integer newDroppingSpeed = mother.getDroppingSpeed(mother.getUseSound().getTicks());
				if(newDroppingSpeed != null)
					getParent().getDropPartyActivatorMother().setBetweenDropsDuration(newDroppingSpeed);
			}
			if(tickCounter.incrementAndGet() 
					>= getParent().getDropPartyActivatorMother().getBetweenDropsDuration()) {
				tickCounter.set(0);
				getParent().doSync(() -> drop(getRandomDropLocation(waterSpawnLocation), bottomBlock, lastIndex));
			}
			if(currentTick.get() == 20) {
				elapsedTime.incrementAndGet();
				currentTick.set(0);
			}
			if(waterSpawnDurations.contains(elapsedTime.get())) {
				getParent()
				.doSync(() -> {
					getLocation().getWorld().getBlockAt(waterSpawnLocation).setType(Material.WATER);
					for(int i = 0; i < 4; i++) drop(particleSpawnLocationCorners[i], bottomBlock, lastIndex);
				});
				sendSound(waterSpawnLocation, getParent().getDropPartyActivatorMother().getWaterSpawnSound());
				spawnParticles();	
				getParent().doSyncLater(() -> 
				getLocation().getWorld().getBlockAt(waterSpawnLocation).setType(Material.AIR),
				mother.getWaterRemoveDuration());
				waterSpawnDurations.remove(elapsedTime.get());
			}
			if(elapsedTime.get() < droppingDuration && !deactivateFlag) return;
			running = false;
			getParent().setDropPartyRunning(false);
			getParent().doSync(() -> bottomBlock.setType(originalType));
			spawnParticles();
			sendSound(getLocation(), mother.getEndSound());
			mother.setBetweenDropsDuration(betweenDropsDuration);
			mother.getUseSound().stop();
			deactivateFlag = false;
			speedChanged = false;
			dropTask.get().cancel();
		}, 0, 1));
		return true;
	}

	

	public void drop(Location location, Block bottomBlock, int lastIndex) {
		ItemStack itemToDrop = itemsToDrop.get(ThreadLocalRandom.current().nextInt(0, lastIndex));
		Material bottomBlockType = bottomBlock.getType();
		if(bottomBlockType == Material.DIAMOND_BLOCK || bottomBlockType == Material.GOLD_BLOCK) {
			bottomBlock.setType(bottomBlockType == Material.DIAMOND_BLOCK ? Material.GOLD_BLOCK : Material.DIAMOND_BLOCK);
			getLocation().getWorld().dropItemNaturally(getRandomDropLocation(getLocation()), itemToDrop);
		}
		getLocation().getWorld().dropItemNaturally(location, itemToDrop);
		sendSound(getLocation(), getParent().getDropPartyActivatorMother().getDropSound());
	}

	public void spawnParticles() {
		smokeLarge.display(particleSpawnLocation);
		spell.display(particleSpawnLocation);
		for(int i = 0; i < 4; i++) spell.display(particleSpawnLocationCorners[i]);
	}

	@Override
	public boolean perform(@Nullable Player player) {
		boolean isBypassing = player == null ? false : BypassManager.isBypassOn(player.getUniqueId());
		int requiredPlayers = getParent().getDropPartyActivatorMother().getRequiredPlayers();
		if(!getParent().getDropPartyActivatorMother().isAlwaysOn()) {
			if(!Permissions.hasPermission(player, Permissions.DROPPARTY_USE_PERMISSION)) {
				sendMessage(player, getParent().getMessages().getNoPermissionUse());
				return false;
			}

			if(!isBypassing && Bukkit.getOnlinePlayers().size() < requiredPlayers) {
				sendMessage(player, getParent().getMessages().getDropPartyNotEnoughPlayers()
						.replace("%amount%", String.valueOf(requiredPlayers)));
				return false;
			}	
		}
		AtomicBoolean performed = new AtomicBoolean();
		cooldown = CooldownScheduler.scheduleAsync(getLocation(), getParent().getDropPartyActivatorMother().getCooldownDuration())
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
							.replace("%time%", String.valueOf(timeLeft))
							.replace("%time_long%", TimeFormatter.formatLong(timeLeft.longValue(), true))
							.replace("%time_split%", TimeFormatter.formatShortSplit(timeLeft.longValue(), true))
							.replace("%time_short%", TimeFormatter.formatShort(timeLeft.longValue(), true)));
				})
				.whenDone(() -> {
					if(getParent().getDropPartyActivatorMother().isAlwaysOn()) {
						if(Bukkit.getOnlinePlayers().size() < requiredPlayers) return;	
						activate();
						return;
					}
					@SuppressWarnings("unused")
					boolean actionDone = getParent().getDropPartyActivatorMother().isBroadcastReadyMessage() ?
							broadcastMessage(getParent().getMessages().getDropPartyReady()) :
								sendMessage(player, getParent().getMessages().getDropPartyReady()); 

					actionDone = getParent().getDropPartyActivatorMother().isBroadcastReadySound() ?
							broadcastSound(getParent().getDropPartyActivatorMother().getReadySound()) :
								sendSound(getLocation(), getParent().getDropPartyActivatorMother().getReadySound());
				});
		return performed.get();
	}

	@Override
	public boolean setup() {
		waterSpawnLocation = getLocation().clone().add(0, 1, 0);
		bottomLocation = getLocation().clone().add(0, -1, 0);
		particleSpawnLocation = getLocation().clone().add(0.50, 2, 0.50);
		particleSpawnLocationCorners = new Location[] {particleSpawnLocation.clone().add(-0.50, 0, 0.50),
				particleSpawnLocation.clone().add(0.50, 0, -0.50), particleSpawnLocation.clone().add(0.50, 0, 0.50),
				particleSpawnLocation.clone().add(-0.50, 0, -0.50)};
		smokeLarge = ParticleEffect.SMOKE_LARGE;
		spell = ParticleEffect.SPELL;
		itemsToDrop = getParent().getDropPartyActivatorMother().getItemsToDrop();
		return true;
	}

}
