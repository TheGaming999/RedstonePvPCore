package me.redstonepvpcore.gadgets;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.redstonepvpcore.RedstonePvPCore;
import me.redstonepvpcore.messages.MessagesHolder;
import me.redstonepvpcore.utils.XSound.Record;

public abstract class Gadget {

	private GadgetType type;
	private Location location;
	private MessagesHolder messagesHolder;
	private final static RedstonePvPCore PARENT = (RedstonePvPCore)JavaPlugin.getProvidingPlugin(Gadget.class);
	
	public Gadget(GadgetType type, Location location) {
		this.type = type;
		this.location = location;
		this.messagesHolder = new MessagesHolder();
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public GadgetType getType() {
		return this.type;
	}
	
	public MessagesHolder getMessagesHolder() {
		return this.messagesHolder;
	}
	
	public final RedstonePvPCore getParent() {
		return PARENT;
	}
	
	public final static boolean sendMessage(Player player, @Nullable String message) {
		if(message == null || message.isEmpty()) return false;
		player.sendMessage(message);
		return true;
	}
	
	public final static boolean broadcastMessage(@Nullable String message) {
		if(message == null || message.isEmpty()) return false;
		Bukkit.broadcastMessage(message);
		return true;
	}
	
	public final static boolean sendSound(Player player, @Nullable Record record) {
		if(record == null || record.sound == null) return false;
		record.forPlayer(player).play();
		return true;
	}
	
	public final static boolean sendSound(Location location, @Nullable Record record) {
		if(record == null || record.sound == null) return false;
		record.atLocation(location).play();
		return true;
	}
	
	public final static boolean broadcastSound(@Nullable Record record) {
		if(record == null || record.sound == null) return false;
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.playSound(player.getLocation(), record.sound.parseSound(), record.volume, record.pitch);
		});
		return true;
	}
	
	/**
	 * 
	 * @param player player to perform gadget usage for
	 * @return true if gadget was used, false if conditions were not met
	 */
	public abstract boolean perform(Player player);
	
}
