package me.redstonepvpcore.sounds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.redstonepvpcore.utils.XSound.Record;

public class VanillaSound implements SoundInfo {

	private Record record;

	public VanillaSound(Record record) {
		this.record = record;
	}

	@Override
	public boolean play(Location location) {
		if (record == null || location == null || record.sound == null) return false;
		record.atLocation(location).play();
		return true;
	}

	@Override
	public boolean play(Player player) {
		if (record == null || player == null || record.sound == null) return false;
		record.forPlayer(player).play();
		return true;
	}

	@Override
	public boolean addPlayer(Player player) {
		return false;
	}

	@Override
	public boolean stop() {
		return false;
	}

	@Override
	public boolean broadcast() {
		if (record == null || record.sound == null) return false;
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.playSound(player.getLocation(), record.sound.parseSound(), record.volume, record.pitch);
		});
		return true;
	}

	@Override
	public void setVolume(byte volume) {
		record.volume = volume;
	}

	@Override
	public void setDistance(int distance) {
		record.pitch = distance;
	}

	@Override
	public int getTicks() {
		return 0;
	}

	@Override
	public void setTicks(int ticks) {
		// do nothing
	}

	@Override
	public Object get() {
		return record;
	}

}
