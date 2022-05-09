package me.redstonepvpcore.sounds;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface SoundInfo {
	
	boolean play(Location location);
	
	boolean play(Player player);
	
	boolean broadcast();
	
	boolean addPlayer(Player player);
	
	boolean stop();
	
	void setVolume(byte volume);
	
	void setDistance(int distance);
	
	int getTicks();
	
	void setTicks(int ticks);
	
	Object get();
	
}
