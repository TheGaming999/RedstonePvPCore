package me.redstonepvpcore.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Maps;

/**
 * 
 * @apiNote Simple utility class to manage, create, and schedule cooldowns.
 */
@SuppressWarnings("unchecked")
public class CooldownScheduler {

	private static final AsyncCooldownScheduler<Object> ASYNC_SCHEDULER = createAsync();
	private static final SimpleCooldownScheduler<Object> SIMPLE_SCHEDULER = createSimple();
	private static final long THOUSAND = 1000;

	private static <T> SimpleCooldownScheduler<T> createSimple() {
		return (SimpleCooldownScheduler<T>) SimpleCooldownScheduler.create();
	}

	private static <T> AsyncCooldownScheduler<T> createAsync() {
		return (AsyncCooldownScheduler<T>) AsyncCooldownScheduler.create();
	}

	public static class SimpleCooldownScheduler<T> {

		private Map<T, SimpleCooldownEntry<T>> entries = Maps.newHashMap();

		public SimpleCooldownScheduler() {
			this.entries = Maps.newHashMap();
		}

		public static <T> SimpleCooldownScheduler<T> create() {
			return new SimpleCooldownScheduler<T>();
		}

		public <R> SimpleCooldownEntry<T> schedule(T name, int cooldown) {
			SimpleCooldownEntry<T> entry = null;
			if (!entries.containsKey(name)) {
				entry = new SimpleCooldownEntry<>(name, cooldown);
				entry.startSystemTime = System.currentTimeMillis();
				entries.put(name, entry);
				performFirstTime(entry);
			} else {
				entry = entries.get(name);
				if (entry.duration != cooldown) entry.duration = cooldown;
			}
			return performActions(entry);
		}

		public <R> SimpleCooldownEntry<T> schedule(T name, int cooldown, boolean performFirstTime) {
			SimpleCooldownEntry<T> entry = null;
			if (!entries.containsKey(name)) {
				entry = new SimpleCooldownEntry<>(name, cooldown);
				entry.startSystemTime = System.currentTimeMillis();
				entries.put(name, entry);
				if (performFirstTime) performFirstTime(entry);
			} else {
				entry = entries.get(name);
				if (entry.duration != cooldown) entry.duration = cooldown;
			}
			return performActions(entry);
		}

		public <R> SimpleCooldownEntry<T> schedule(T name, int cooldown, long startSystemTime) {
			SimpleCooldownEntry<T> entry = null;
			if (!entries.containsKey(name)) {
				entry = new SimpleCooldownEntry<>(name, cooldown);
				entry.startSystemTime = startSystemTime;
				entries.put(name, entry);
			} else {
				entry = entries.get(name);
				if (entry.duration != cooldown) entry.duration = cooldown;
			}
			return performActions(entry);
		}

		public SimpleCooldownEntry<T> getEntry(T name) {
			return entries.get(name);
		}

		public void unschedule(T name) {
			entries.remove(name);
		}

		private void performFirstTime(SimpleCooldownEntry<T> entry) {
			Bukkit.getScheduler().runTaskLater(getAsyncScheduler().plugin, () -> performActions(entry), 1);
		}

		private SimpleCooldownEntry<T> performActions(SimpleCooldownEntry<T> entry) {
			long secondsLeft = ((entry.startSystemTime / THOUSAND) + entry.duration)
					- (System.currentTimeMillis() / THOUSAND);
			if (secondsLeft > 0) {
				if (entry.consumer != null) entry.consumer.accept(secondsLeft);
				return entry;
			}
			if (entry.runnable != null) entry.runnable.run();
			entry.startSystemTime = System.currentTimeMillis();
			return entry;
		}

	}

	public static class AsyncCooldownScheduler<T> {

		private Plugin plugin;
		private Map<T, AsyncCooldownEntry<T>> entries;
		private BukkitTask cooldownTask;
		private boolean isCancelled;

		public static <T> AsyncCooldownScheduler<T> create() {
			return new AsyncCooldownScheduler<T>(JavaPlugin.getProvidingPlugin(AsyncCooldownScheduler.class));
		}

		public static <T> AsyncCooldownScheduler<T> create(Plugin plugin) {
			return new AsyncCooldownScheduler<T>(plugin);
		}

		public AsyncCooldownEntry<T> schedule(T name, double cooldown) {
			AsyncCooldownEntry<T> entry = null;
			if (!entries.containsKey(name)) {
				entry = new AsyncCooldownEntry<>(name, cooldown);
				entry.startDuration = cooldown;
				entries.put(name, entry);
			} else {
				entry = entries.get(name);
			}
			startIfNotStarted();
			return entry;
		}

		public AsyncCooldownEntry<T> getEntry(T name) {
			return this.entries.get(name);
		}

		public Set<T> getCoolingDownKeys() {
			return this.entries.keySet();
		}

		public Map<T, AsyncCooldownEntry<T>> getCooldownEntries() {
			return this.entries;
		}

		private AsyncCooldownEntry<T> processEntry(T name, double cooldown) {
			return new AsyncCooldownEntry<T>(name, twoDecimals(cooldown - 0.05));
		}

		public AsyncCooldownEntry<T> removeCooldown(T name) {
			return this.entries.remove(name);
		}

		public AsyncCooldownEntry<T> setCooldown(T name, AsyncCooldownEntry<T> asyncCooldownEntry) {
			return this.entries.put(name, asyncCooldownEntry);
		}

		public void clearCooldowns() {
			this.entries.clear();
		}

		public BukkitTask getCooldownTask() {
			return this.cooldownTask;
		}

		public boolean isCancelled() {
			return isCancelled;
		}

		public AsyncCooldownScheduler(Plugin plugin) {
			this.plugin = plugin;
			this.entries = Maps.newHashMap();
		}

		private double twoDecimals(double value) {
			return Math.floor(value * 100) / 100;
		}

		private void startIfNotStarted() {
			if (!getCooldownEntries().isEmpty())
				if (cooldownTask == null || isCancelled) cooldownTask = new BukkitRunnable() {
					@Override
					public void run() {
						if (getCooldownEntries().isEmpty()) {
							isCancelled = true;
							cancel();
							return;
						}
						isCancelled = false;
						Iterator<Map.Entry<T, AsyncCooldownEntry<T>>> iterator = getCooldownEntries().entrySet()
								.iterator();
						while (iterator.hasNext()) {
							Map.Entry<T, AsyncCooldownEntry<T>> entry = iterator.next();
							T key = entry.getKey();
							AsyncCooldownEntry<T> value = entry.getValue();
							double durationValue = value.getCurrentDuration();
							double timeLeft = twoDecimals(durationValue);
							double safeTimeLeft = timeLeft < 0.0 ? 0.0 : timeLeft;
							Consumer<Double> repeatingConsumer = value.getRepeatingConsumer();
							Runnable finishRunnable = value.getWhenDone();
							AsyncCooldownEntry<T> entryRefreshed = processEntry(key, timeLeft);
							entryRefreshed.orElseRepeat(repeatingConsumer);
							entryRefreshed.whenDone(finishRunnable);
							if (timeLeft + 0.1 < 0.0) {
								if (finishRunnable != null) finishRunnable.run();
								removeCooldown(key);
								return;
							} else {
								if (value.getCurrentDuration() == value.getStartDuration()) {
									Runnable runnable = getEntry(key).getRunnable();
									if (runnable != null) runnable.run();
									setCooldown(key, entryRefreshed);
									return;
								}
								Consumer<Double> consumer = getEntry(key).getConsumer();
								if (consumer != null) consumer.accept(safeTimeLeft);
								if (repeatingConsumer != null) repeatingConsumer.accept(safeTimeLeft);
							}
							setCooldown(key, entryRefreshed);
						}
					}
				}.runTaskTimerAsynchronously(plugin, 1L, 1L);
		}
	}

	/**
	 * Holds information of a scheduled entry
	 * 
	 * @param <T> Object type of the identifier {@code getIdentifier();}
	 */
	public static class AsyncCooldownEntry<T> {

		private T name;
		private double duration;
		private double startDuration;
		private Runnable runnable;
		private Runnable finishRunnable;
		private Consumer<Double> consumer;
		private Consumer<Double> repeatingConsumer;

		public AsyncCooldownEntry(T name, double duration) {
			this.name = name;
			this.duration = duration;
		}

		public T getIdentifier() {
			return this.name;
		}

		public double getCurrentDuration() {
			return this.duration;
		}

		public double setCurrentDuration(double duration) {
			return this.duration = duration;
		}

		public double getStartDuration() {
			return this.startDuration;
		}

		public double setStartDuration(double startDuration) {
			return this.startDuration = startDuration;
		}

		private Runnable getRunnable() {
			return this.runnable;
		}

		private Consumer<Double> getConsumer() {
			return this.consumer;
		}

		private Consumer<Double> getRepeatingConsumer() {
			return this.repeatingConsumer;
		}

		public Runnable getWhenDone() {
			return this.finishRunnable;
		}

		/**
		 * run the provided runnable when the scheduler gets called after cooldown has
		 * ended
		 * 
		 * @param runnable runnable to run (<i> () -> doSomething()</i> )
		 * @return AsyncCooldownEntry for further variable editing.
		 */
		public AsyncCooldownEntry<T> ifTrue(Runnable runnable) {
			this.runnable = runnable;
			return this;
		}

		/**
		 * consume the duration when the scheduler gets called if cooldown didn't end
		 * yet.
		 * 
		 * @param consumer consumer to consume (<i> duration ->
		 *                 doSomething(duration)</i> )
		 * @return AsyncCooldownEntry for further variable editing.
		 */
		public AsyncCooldownEntry<T> orElse(Consumer<Double> consumer) {
			this.consumer = consumer;
			return this;
		}

		/**
		 * consume the duration repeatedly until the cooldown ends.
		 * 
		 * @param consumer consumer to consume (<i> duration ->
		 *                 doSomething(duration)</i> )
		 * @return AsyncCooldownEntry for further variable editing.
		 */
		public AsyncCooldownEntry<T> orElseRepeat(Consumer<Double> consumer) {
			this.repeatingConsumer = consumer;
			return this;
		}

		/**
		 * run the provided runnable if cooldown has ended without the need to get
		 * called
		 * 
		 * @param runnable runnable to run (<i> () -> doSomething()</i> )
		 * @return AsyncCooldownEntry for further variable editing.
		 */
		public AsyncCooldownEntry<T> whenDone(Runnable runnable) {
			this.finishRunnable = runnable;
			return this;
		}

	}

	/**
	 * Holds information of a scheduled entry
	 * 
	 * @param <T> Object type of the identifier {@code getIdentifier();}
	 */
	public static class SimpleCooldownEntry<T> {

		private T name;
		private int duration;
		private long startSystemTime;
		private Runnable runnable;
		private Consumer<Long> consumer;

		public SimpleCooldownEntry(T name, int duration) {
			this.name = name;
			this.duration = duration;
			this.startSystemTime = System.currentTimeMillis();
		}

		public T getIdentifier() {
			return this.name;
		}

		public long getStartTime() {
			return startSystemTime;
		}

		public int getDuration() {
			return duration;
		}

		public SimpleCooldownEntry<T> setLiveDuration(long duration) {
			this.startSystemTime = System.currentTimeMillis() - ((-duration * THOUSAND) + (this.duration * THOUSAND));
			return this;
		}

		public SimpleCooldownEntry<T> setLiveDuration(long duration, long systemTime) {
			this.startSystemTime = systemTime - ((-duration * THOUSAND) + (this.duration * THOUSAND));
			return this;
		}

		public long getLiveDuration() {
			return ((startSystemTime / THOUSAND) + duration) - (System.currentTimeMillis() / THOUSAND);
		}

		public static long getLiveDuration(long systemTime, long duration) {
			return ((systemTime / THOUSAND) + duration) - (System.currentTimeMillis() / THOUSAND);
		}

		public SimpleCooldownEntry<T> setStartTime(long startSystemTime) {
			this.startSystemTime = startSystemTime;
			return this;
		}

		public SimpleCooldownEntry<T> setDuration(int duration) {
			this.duration = duration;
			return this;
		}

		/**
		 * run the provided runnable if cooldown has ended
		 * 
		 * @param runnable runnable to run (<i> () -> doSomething()</i> )
		 * @return SimpleCooldownEntry for further variable editing.
		 */
		public SimpleCooldownEntry<T> ifTrue(Runnable runnable) {
			this.runnable = runnable;
			return this;
		}

		/**
		 * consume the duration if cooldown didn't end yet.
		 * 
		 * @param consumer consumer to consume (<i> duration ->
		 *                 doSomething(duration)</i> )
		 * @return SimpleCooldownEntry for further variable editing.
		 */
		public SimpleCooldownEntry<T> orElse(Consumer<Long> consumer) {
			this.consumer = consumer;
			return this;
		}

	}

	/**
	 * @param <T>      identifier type
	 * @param name     player name or any other identifier from
	 * @param duration how long should it take for the cooldown to finish in
	 *                 seconds, so the wanted process can be performed again
	 * @return SimpleCooldownEntry in which you can perform certain actions
	 *         depending on {name} state regarding the cooldown.
	 */
	public static <T> SimpleCooldownEntry<T> schedule(T name, final int duration) {
		return (SimpleCooldownEntry<T>) SIMPLE_SCHEDULER.schedule(name, duration);
	}

	/**
	 * @param <T>      identifier type
	 * @param name     player name or the desired identifier
	 * @param duration how long does the cooldown last from 0.1 (one digit after
	 *                 decimal point) and up {0.1, 0.2, 0.3, 1.4, 4.5...}
	 * @return AsyncCooldownEntry in which you can perform certain actions depending
	 *         on {name} state regarding the cooldown.
	 */
	public static <T> AsyncCooldownEntry<T> scheduleAsync(final T name, final double duration) {
		return (AsyncCooldownEntry<T>) ASYNC_SCHEDULER.schedule(name, duration);
	}

	/**
	 * 
	 * @param <T> object type
	 * @return <b>SimpleCooldownScheduler<T></b> main simple scheduler that is used
	 *         with <i>schedule(T, int)</i> with T representing the type
	 */
	public static <T> SimpleCooldownScheduler<T> getSimpleScheduler() {
		return (SimpleCooldownScheduler<T>) SIMPLE_SCHEDULER;
	}

	/**
	 * 
	 * @param <T>  identifier object type
	 * @param type object type to pass to the scheduler
	 *             <p>
	 *             Examples: <i>new String()</i> passes a <i>String</i> type -
	 *             <i>0d</i> passes a <i>double</i> type
	 * @return <b>SimpleCooldownScheduler<T></b> main simple scheduler that is used
	 *         with <i>schedule(T, int)</i> with T representing the type
	 */
	public static <T> SimpleCooldownScheduler<T> getSimpleScheduler(T type) {
		return (SimpleCooldownScheduler<T>) SIMPLE_SCHEDULER;
	}

	/**
	 * 
	 * @param <T> object type
	 * @return <b>AsyncCooldownScheduler<T></b> main async scheduler that is used
	 *         with <i>scheduleAsync(T, double)</i> with T representing the type
	 */
	public static <T> AsyncCooldownScheduler<T> getAsyncScheduler() {
		return (AsyncCooldownScheduler<T>) ASYNC_SCHEDULER;
	}

	/**
	 * 
	 * @param <T>  identifier object type
	 * @param type object type to pass to the scheduler
	 *             <p>
	 *             Examples: <i>new String()</i> passes a <i>String</i> type -
	 *             <i>UUID.randomUUID()</i> passes a <i>UUID</i> type
	 * @return <b>AsyncCooldownScheduler<T></b> main async scheduler that is used
	 *         with <i>scheduleAsync(T, int)</i> with T representing the type
	 */
	public static <T> AsyncCooldownScheduler<T> getAsyncScheduler(T type) {
		return (AsyncCooldownScheduler<T>) ASYNC_SCHEDULER;
	}

}
