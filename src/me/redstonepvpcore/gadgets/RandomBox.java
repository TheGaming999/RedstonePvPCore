package me.redstonepvpcore.gadgets;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.redstonepvpcore.mothers.Actions;
import me.redstonepvpcore.mothers.RandomBoxMother;
import me.redstonepvpcore.player.BypassManager;
import me.redstonepvpcore.player.Permissions;
import me.redstonepvpcore.sounds.SoundInfo;
import me.redstonepvpcore.utils.RandomUnique;
import xyz.xenondevs.particle.ParticleEffect;

public class RandomBox extends Gadget implements Cooldownable {

	private boolean inUse;
	private ItemStack takeItemStack;

	public RandomBox(Location location) {
		super(GadgetType.RANDOM_BOX, location);
		getMessagesHolder().setMessage(0, getParent().getMessages().getSetRandomBox());
		getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveRandomBox());
	}

	public boolean isInUse() {
		return this.inUse;
	}

	public boolean setInUse(boolean inUse) {
		return this.inUse = inUse;
	}

	private int getCost(ItemStack takeItemStack, Player player) {
		int cost = takeItemStack.getAmount();
		Map<String, Integer> usePermissions = getParent().getRandomBoxMother().getUsePermissions();
		for (String permission : usePermissions.keySet())
			if (player.isPermissionSet(permission)) cost = usePermissions.get(permission);
		return cost;
	}

	private String capitalize(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
	}

	private String readableName(Material material) {
		String name = material.name().toLowerCase();
		String finalName = "";
		if (name.contains("_")) {
			name = name.replace("_", " ");
			String[] args = name.split(" ");
			for (String arg : args) finalName += " " + capitalize(arg);
			finalName = finalName.substring(1);
		} else {
			finalName = capitalize(name);

		}
		return finalName;
	}

	public boolean isInventoryFull(Player player) {
		return player.getInventory().firstEmpty() == -1;
	}

	public int getRandom(int from, int to) {
		return ThreadLocalRandom.current().nextInt(from, to + 1);
	}

	public double getRandom(double from, double to) {
		return ThreadLocalRandom.current().nextDouble(from, to + 1);
	}

	private CompletableFuture<Void> useRandomBox(Player player) {
		CompletableFuture<Void> taskFinishFuture = new CompletableFuture<>();
		AtomicInteger pos = new AtomicInteger();
		RandomBoxMother mother = getParent().getRandomBoxMother();
		int lastIndex = mother.getDisplayItems().size() - 1;
		RandomUnique.global().setRandomLimit(lastIndex);
		double shuffleDuration = mother.getShuffleDuration();
		Location centerLocation = getLocation().clone().add(0.50, 1.0, 0.50);
		AtomicInteger shuffles = new AtomicInteger();
		Item droppedItem = centerLocation.getWorld().dropItem(centerLocation, mother.getDisplayItems().get(0));
		SoundInfo animationSound = mother.getAnimationSound();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (animationSound != null) animationSound.play(centerLocation);
				int randomIndex = pos.get();
				ItemStack displayItemStack = mother.getDisplayItems().get(randomIndex);
				droppedItem.setItemStack(displayItemStack);
				droppedItem.setVelocity(new Vector(0, 0, 0));
				droppedItem.setPickupDelay(1000);
				if (shuffles.get() >= shuffleDuration) {
					getParent().doSyncLater(() -> {
						ItemStack reward = mother.getItems().get(randomIndex);
						ParticleEffect.FLAME.display(centerLocation.clone().add(0.0, 0.5, 0.0));
						Actions actions = mother.getActions(randomIndex);
						if (actions != null) actions.execute(player);
						if (!isInventoryFull(player)) {
							player.getInventory().addItem(reward);
						} else {
							player.getWorld().dropItemNaturally(player.getLocation(), reward);
						}
						droppedItem.remove();
						taskFinishFuture.complete(null);
					}, 1);
					this.cancel();
					return;
				}
				shuffles.incrementAndGet();
				pos.set(RandomUnique.global().generate());
			}
		}.runTaskTimer(getParent(), 0, 5);
		return taskFinishFuture;
	}

	@Override
	public boolean perform(Player player) {
		boolean isBypassing = BypassManager.isBypassOn(player.getUniqueId());
		if (!Permissions.hasPermission(player, Permissions.RANDOMBOX_USE_PERMISSION)) {
			sendMessage(player, getParent().getMessages().getNoPermissionUse());
			return false;
		}
		int cost = getCost(takeItemStack, player);
		if (!isBypassing && !player.getInventory().containsAtLeast(takeItemStack, cost)) {
			sendMessage(player,
					getParent().getMessages()
							.getRandomBoxNotEnough()
							.replace("%amount%", String.valueOf(cost))
							.replace("%item%", takeItemStack.getType().name())
							.replace("%item_readable%", readableName(takeItemStack.getType())));
			return false;
		}
		if (!isBypassing && inUse) {
			sendMessage(player, getParent().getMessages().getRandomBoxInUse());
			return false;
		}
		inUse = true;
		takeItemStack.setAmount(cost);
		if (!isBypassing) player.getInventory().removeItem(takeItemStack);
		sendMessage(player, getParent().getMessages().getRandomBoxUse());
		sendSound(getLocation(), getParent().getRandomBoxMother().getUseSound());
		useRandomBox(player).thenRun(() -> {
			inUse = false;
			sendMessage(player, getParent().getMessages().getRandomBoxDone());
			sendSound(getLocation(), getParent().getRandomBoxMother().getEndSound());
		});
		return true;
	}

	@Override
	public boolean setup() {
		takeItemStack = getParent().getRandomBoxMother().getTakeItemStack();
		return true;
	}

}
