package me.redstonepvpcore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.gadgets.GadgetManager;

public class JoinListener implements Listener {

	private RedstonePvPCore parent;

	public JoinListener(RedstonePvPCore parent) {
		this.parent = parent;
	}

	public void register() {
		parent.getServer().getPluginManager().registerEvents(this, parent);
	}

	public void unregister() {
		AsyncPlayerPreLoginEvent.getHandlerList().unregister(parent);
		PlayerQuitEvent.getHandlerList().unregister(parent);
	}

	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent event) {
		parent.doAsync(() -> {
			GadgetManager.getCooldownGadgets().values().forEach(cooldownGadget -> {
				cooldownGadget.loadDuration(event.getUniqueId());
			});
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		parent.doAsync(() -> {
			GadgetManager.getCooldownGadgets().values().forEach(cooldownGadget -> {
				cooldownGadget.storeDuration(event.getPlayer().getUniqueId());
				cooldownGadget.saveDuration(event.getPlayer().getUniqueId());
			});
		});
	}

}
