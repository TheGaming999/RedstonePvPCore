package me.redstonepvpcore;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;

import me.redstonepvpcore.commands.RedstonePvPCoreCommand;
import me.redstonepvpcore.commands.ShopCommand;
import me.redstonepvpcore.enchantments.EnchantmentManager;
import me.redstonepvpcore.gadgets.DropPartyActivator;
import me.redstonepvpcore.gadgets.GadgetManager;
import me.redstonepvpcore.gadgets.GadgetType;
import me.redstonepvpcore.listeners.DamageListener;
import me.redstonepvpcore.listeners.InteractListener;
import me.redstonepvpcore.listeners.InventoryListener;
import me.redstonepvpcore.listeners.ItemMergeListener;
import me.redstonepvpcore.messages.Messages;
import me.redstonepvpcore.mothers.ConverterMother;
import me.redstonepvpcore.mothers.DropPartyActivatorMother;
import me.redstonepvpcore.mothers.ExpSignMother;
import me.redstonepvpcore.mothers.FrameGiverMother;
import me.redstonepvpcore.mothers.RandomBoxMother;
import me.redstonepvpcore.mothers.RepairAnvilMother;
import me.redstonepvpcore.shop.Shop;
import me.redstonepvpcore.utils.ConfigCreator;

public class RedstonePvPCore extends JavaPlugin {

	private static RedstonePvPCore instance;
	private RepairAnvilMother repairAnvilMother;
	private ConverterMother converterMother;
	private RandomBoxMother randomBoxMother;
	private DropPartyActivatorMother dropPartyActivatorMother;
	private ExpSignMother expSignMother;
	private FrameGiverMother frameGiverMother;
	private EnchantmentManager enchantmentManager;
	private Messages messages;
	private BukkitScheduler scheduler;
	private InteractListener interactListener;
	private ItemMergeListener itemMergeListener;
	private DamageListener damageListener;
	private InventoryListener inventoryListener;
	private RedstonePvPCoreCommand redstonePvPCoreCommand;
	private Shop shop;
	private ShopCommand shopCommand;
	private BukkitTask dropPartyTask;
	private BukkitTask dataSavingTask;
	private NoteBlockAPI noteBlockAPI;
	private boolean dropPartyRunning;
	private final String musicPath = this.getDataFolder() + "/Music/";

	@Override
	public void onEnable() {
		saveDefaultMusicFiles();
		instance = this;
		noteBlockAPI = new NoteBlockAPI();
		noteBlockAPI.onEnable();
		ConfigCreator.copyAndSaveDefaults(false, "converters.yml", "data.yml", "drop-party.yml", "enchantments.yml",
				"exp-sign.yml", "frame-giver.yml", "item-bleed.yml", "randombox.yml", "repair-anvil.yml", "shop.yml",
				"soulbound.yml", "trash.yml", "messages.yml");
		scheduler = Bukkit.getScheduler();
		repairAnvilMother = new RepairAnvilMother();
		converterMother = new ConverterMother();
		randomBoxMother = new RandomBoxMother();
		dropPartyActivatorMother = new DropPartyActivatorMother();
		expSignMother = new ExpSignMother();
		frameGiverMother = new FrameGiverMother();
		enchantmentManager = new EnchantmentManager();
		messages = new Messages();
		shop = new Shop();
		shop.loadInventory();
		shopCommand = new ShopCommand(this);
		getCommand("shop").setExecutor(shopCommand);
		GadgetManager.loadGadgets();
		registerListeners();
		redstonePvPCoreCommand = new RedstonePvPCoreCommand(this);
		getCommand("redstonepvpcore").setExecutor(redstonePvPCoreCommand);
		startDropPartyChecker();
		startDataSavingTask();
		getLogger().info("Enabled.");
	}

	public void saveDefaultMusicFiles() {
		new File(this.getDataFolder() + "/Music/").mkdir();
		saveMusicFile("Dream Lover.nbs");
	}
	
	private void saveMusicFile(String name) {
		if(new File(musicPath + name).exists()) return;
		saveResource("Music/" + name, false);
	}
	
	@SuppressWarnings("unused")
	private void saveMusicFile(String... names) {
		for(String name : names) saveMusicFile(name);
	}
	
	/**
	 * Only for always on option.
	 */
	public void startDropPartyChecker() {
		dropPartyTask = doAsyncRepeating(() -> {
			if(dropPartyActivatorMother.isAlwaysOn()) {
				GadgetManager.getGadgets().values().stream()
				.filter(gadget -> gadget.getType() == GadgetType.DROP_PARTY_ACTIVATOR)
				.map(gadget -> (DropPartyActivator)gadget)
				.forEach(gadget -> gadget.perform(null));
			} else {
				if(dropPartyTask != null) dropPartyTask.cancel();
			}
		}, 20, 20);
	}
	
	// Every 10 minutes
	public void startDataSavingTask() {
		dataSavingTask = doAsyncRepeating(() -> {
			getLogger().info("Saving data...");
			GadgetManager.saveGadgets();
			ConfigCreator.saveConfig("data.yml");
			getLogger().info("Data saved.");
		}, 600*20, 600*20);
	}
	
	public void stopDataSavingTask() {
		if(dataSavingTask != null) dataSavingTask.cancel();
	}

	public void registerListeners() {
		interactListener = new InteractListener(this);
		itemMergeListener = new ItemMergeListener(this);
		damageListener = new DamageListener(this);
		inventoryListener = new InventoryListener(this);
		interactListener.register();
		itemMergeListener.register();
		damageListener.register();
		inventoryListener.register();
	}

	public void unregisterListeners() {
		if(interactListener != null)
			interactListener.unregister();
		if(itemMergeListener != null)
			itemMergeListener.unregister();
		if(damageListener != null)
			damageListener.unregister();
		if(inventoryListener != null)
			inventoryListener.unregister();
	}

	public void reload() {
		stopDataSavingTask();
		GadgetManager.saveGadgets();
		ConfigCreator.saveConfig("data.yml");
		ConfigCreator.reloadConfigs();
		repairAnvilMother.setup();
		randomBoxMother.setup();
		converterMother.setup();
		dropPartyActivatorMother.setup();
		expSignMother.setup();
		frameGiverMother.setup();
		enchantmentManager.setup();
		messages.setup();
		shop.loadInventory();
		GadgetManager.loadGadgets();
		redstonePvPCoreCommand = new RedstonePvPCoreCommand(this);
		getCommand("redstonepvpcore").setExecutor(redstonePvPCoreCommand);
		unregisterListeners();
		registerListeners();
		startDropPartyChecker();
		startDataSavingTask();
	}

	@Override
	public void onDisable() {
		stopDataSavingTask();
		GadgetManager.saveGadgets();
		ConfigCreator.saveConfig("data.yml");
		noteBlockAPI.onDisable();
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
	
	public ExpSignMother getExpSignMother() {
		return expSignMother;
	}
	
	public FrameGiverMother getFrameGiverMother() {
		return frameGiverMother;
	}

	public EnchantmentManager getEnchantmentManager() {
		return enchantmentManager;
	}
	
	public RedstonePvPCoreCommand getMainCommand() {
		return redstonePvPCoreCommand;
	}
	
	public Shop getShop() {
		return shop;
	}
	
	public Messages getMessages() {
		return messages;
	}

	public static RedstonePvPCore getInstance() {
		return instance;
	}

	public boolean isDropPartyRunning() {
		return dropPartyRunning;
	}

	public void setDropPartyRunning(boolean dropPartyRunning) {
		this.dropPartyRunning = dropPartyRunning;
	}

}
