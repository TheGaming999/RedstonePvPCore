package me.redstonepvpcore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import me.redstonepvpcore.commands.RedstonePvPCoreCommand;
import me.redstonepvpcore.gadgets.GadgetManager;
import me.redstonepvpcore.listeners.InteractListener;
import me.redstonepvpcore.messages.Messages;
import me.redstonepvpcore.mothers.ConverterMother;
import me.redstonepvpcore.mothers.DropPartyActivatorMother;
import me.redstonepvpcore.mothers.RandomBoxMother;
import me.redstonepvpcore.mothers.RepairAnvilMother;
import me.redstonepvpcore.utils.ConfigCreator;

public class RedstonePvPCore extends JavaPlugin {

	private RepairAnvilMother repairAnvilMother;
	private ConverterMother converterMother;
	private RandomBoxMother randomBoxMother;
	private DropPartyActivatorMother dropPartyActivatorMother;
	private Messages messages;
	private BukkitScheduler scheduler;
	private InteractListener interactListener;
	private RedstonePvPCoreCommand redstonePvPCoreCommand;
	
	@Override
	public void onEnable() {
		ConfigCreator.copyAndSaveDefaults("converters.yml", "data.yml", "drop-party.yml", "enchantments.yml",
				"exp-sign.yml", "frame-giver.yml", "item-bleed.yml", "randombox.yml", "repair-anvil.yml", "shop.yml",
				"soulbound.yml", "trash.yml", "messages.yml");
		scheduler = Bukkit.getScheduler();
		repairAnvilMother = new RepairAnvilMother();
		converterMother = new ConverterMother();
		randomBoxMother = new RandomBoxMother();
		dropPartyActivatorMother = new DropPartyActivatorMother();
		messages = new Messages();
		registerListeners();
		redstonePvPCoreCommand = new RedstonePvPCoreCommand(this);
		getCommand("redstonepvpcore").setExecutor(redstonePvPCoreCommand);
		GadgetManager.loadGadgets();
	}
	
	public void registerListeners() {
		interactListener = new InteractListener(this);
		interactListener.register();
	}
	
	public void unregisterListeners() {
		if(interactListener != null)
			interactListener.unregister();
	}

	public void reload() {
		GadgetManager.saveGadgets();
		ConfigCreator.saveConfig("data.yml");
		ConfigCreator.reloadConfigs();
		repairAnvilMother.setup();
		randomBoxMother.setup();
		converterMother.setup();
		dropPartyActivatorMother.setup();
		messages.setup();	
		GadgetManager.loadGadgets();
		unregisterListeners();
		registerListeners();
	}
	
	@Override
	public void onDisable() {
		GadgetManager.saveGadgets();
		ConfigCreator.saveConfigs();
	}
	
	public BukkitTask doAsync(Runnable runnable) {
		return scheduler.runTaskAsynchronously(this, runnable);
	}
	
	public BukkitTask doAsyncLater(Runnable runnable, int delay) {
		return scheduler.runTaskLaterAsynchronously(this, runnable, delay);
	}
	
	public BukkitTask doAsyncRepeating(Runnable runnable, int delay, int speed) {
		return scheduler.runTaskTimerAsynchronously(this, runnable, delay, speed);
	}
	
	public BukkitTask doSync(Runnable runnable) {
		return scheduler.runTask(this, runnable);
	}
	
	public BukkitTask doSyncLater(Runnable runnable, int delay) {
		return scheduler.runTaskLater(this, runnable, delay);
	}
	
	public BukkitTask doSyncRepeating(Runnable runnable, int delay, int speed) {
		return scheduler.runTaskTimer(this, runnable, delay, speed);
	}
	
	public RepairAnvilMother getRepairAnvilMother() {
		return repairAnvilMother;
	}
	
	public ConverterMother getConverterMother() {
		return converterMother;
	}
	
	public RandomBoxMother getRandomBoxMother() {
		return randomBoxMother;
	}
	
	public DropPartyActivatorMother getDropPartyActivatorMother() {
		return dropPartyActivatorMother;
	}
	
	public Messages getMessages() {
		return messages;
	}
	
}
