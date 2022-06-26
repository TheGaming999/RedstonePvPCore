package me.redstonepvpcore.sounds;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;

public class NBSSound implements SoundInfo {

	private Song song;
	private PositionSongPlayer psp;
	private RadioSongPlayer rsp;
	private boolean broadcasted;

	public NBSSound(String fileLocation) {
		song = NBSDecoder.parse(new File(fileLocation));
		psp = new PositionSongPlayer(song);
		rsp = new RadioSongPlayer(song);
		psp.setDistance(5);
	}

	@Override
	public boolean play(Location location) {
		if (location == null) return false;
		psp.setTargetLocation(location);
		psp.setPlaying(true);
		broadcasted = false;
		return true;
	}

	@Override
	public boolean play(Player player) {
		if (player == null) return false;
		psp.addPlayer(player);
		psp.setPlaying(true);
		broadcasted = false;
		return true;
	}

	@Override
	public boolean broadcast() {
		Bukkit.getOnlinePlayers().forEach(player -> addPlayer(player));
		rsp.setPlaying(true, false);
		broadcasted = true;
		return true;
	}

	@Override
	public boolean addPlayer(Player player) {
		if (player == null) return false;
		psp.addPlayer(player);
		rsp.addPlayer(player);
		return true;
	}

	@Override
	public boolean stop() {
		psp.setPlaying(false, false);
		rsp.setPlaying(false, false);
		psp.setTick((short) 0);
		rsp.setTick((short) 0);
		if (broadcasted) {
			psp.getPlayerUUIDs().clear();
			rsp.getPlayerUUIDs().clear();
		}
		return true;
	}

	@Override
	public void setVolume(byte volume) {
		psp.setVolume(volume);
		rsp.setVolume(volume);
	}

	@Override
	public void setDistance(int distance) {
		psp.setDistance(distance);
	}

	@Override
	public int getTicks() {
		if (broadcasted) return rsp.getTick();
		return psp.getTick();
	}

	@Override
	public void setTicks(int ticks) {
		psp.setTick((short) ticks);
		rsp.setTick((short) ticks);
	}

	public boolean isBroadcasted() {
		return broadcasted;
	}

	@Override
	public Object get() {
		return song;
	}

}
