package me.redstonepvpcore.messages;

public class TimeFormatter {

	/**
	 * 
	 * @param totalSeconds seconds to format into hours, minutes and seconds.
	 * @return Long format: 00 hours 00 minutes 00 seconds
	 */
	public final static String formatLong(long totalSeconds) {
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return String.format("%02d hours %02d minutes %02d seconds", hours, minutes, seconds);
	}

	/**
	 * 
	 * @param totalSeconds      seconds to format into hours, minutes and seconds.
	 * @param removeUnnecessary removes parts that are equal or less than 0 hence
	 *                          the "unnecessary"
	 * @return Long format: 00 minutes 00 seconds
	 */
	public final static String formatLong(long totalSeconds, boolean removeUnnecessary) {
		if (!removeUnnecessary) return formatLong(totalSeconds);
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return hours <= 0
				? minutes <= 0 ? String.format("%02d seconds", seconds)
						: String.format("%02d minutes %02d seconds", minutes, seconds)
				: String.format("%02d hours %02d minutes %02d seconds", hours, minutes, seconds);
	}

	/**
	 * 
	 * @param totalSeconds seconds to format into hours, minutes and seconds.
	 * @return Short format: 00h00m00s
	 */
	public final static String formatShort(long totalSeconds) {
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return String.format("%02dh%02dm%02ds", hours, minutes, seconds);
	}

	/**
	 * 
	 * @param totalSeconds      seconds to format into hours, minutes and seconds.
	 * @param removeUnnecessary removes parts that are equal or less than 0.
	 * @return Short format: 00m00s
	 */
	public final static String formatShort(long totalSeconds, boolean removeUnnecessary) {
		if (!removeUnnecessary) return formatShort(totalSeconds);
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return hours <= 0
				? minutes <= 0 ? String.format("%02s", seconds) : String.format("%02dm%02ds", minutes, seconds)
				: String.format("%02dh%02dm%02ds", hours, minutes, seconds);
	}

	/**
	 * 
	 * @param totalSeconds seconds to format into hours, minutes and seconds.
	 * @return Short spaced format: 00 h 00 m 00 s
	 */
	public final static String formatShortSpaced(long totalSeconds) {
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return String.format("%02d h %02d m %02d s", hours, minutes, seconds);
	}

	/**
	 * 
	 * @param totalSeconds      seconds to format into hours, minutes and seconds.
	 * @param removeUnnecessary removes parts that are equal or less than 0.
	 * @return Short spaced format: 00 h 00 m 00 s
	 */
	public final static String formatShortSpaced(long totalSeconds, boolean removeUnnecessary) {
		if (!removeUnnecessary) return formatShortSpaced(totalSeconds);
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return hours <= 0
				? minutes <= 0 ? String.format("%02 s", seconds) : String.format("%02d m %02d s", minutes, seconds)
				: String.format("%02d h %02d m %02d s", hours, minutes, seconds);
	}

	/**
	 * 
	 * @param totalSeconds seconds to format into hours, minutes and seconds.
	 * @return Short split format: 00:00:00
	 */
	public final static String formatShortSplit(long totalSeconds) {
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	/**
	 * 
	 * @param totalSeconds      seconds to format into hours, minutes and seconds.
	 * @param removeUnnecessary removes parts that are equal or less than 0.
	 * @return Short split format: 00:00
	 */
	public final static String formatShortSplit(long totalSeconds, boolean removeUnnecessary) {
		if (!removeUnnecessary) return formatShortSplit(totalSeconds);
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return hours <= 0 ? minutes <= 0 ? String.format("%02d", seconds) : String.format("%02d:%02d", minutes, seconds)
				: String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

}
