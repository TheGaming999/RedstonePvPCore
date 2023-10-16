package me.redstonepvpcore.gadgets;

public class CooldownDuration {

	public long duration;
	public long systemTime;

	public CooldownDuration(long duration, long systemTime) {
		this.duration = duration;
		this.systemTime = systemTime;
	}

	public CooldownDuration set(long l, long systemTime) {
		this.duration = l;
		this.systemTime = systemTime;
		return this;
	}

}
