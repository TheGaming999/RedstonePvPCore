package me.redstonepvpcore.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.md_5.bungee.api.ChatColor;

public class Colorizer {

	private static final char COLOR_CHAR = '\u00A7';
	private static final String EMPTY = "";
	private static final String startTag = "&#";
	private static final String endTag = "";
	private static final Pattern HEX_PATTERN = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
	
	/**
	 * 
	 * @param textToColorize string to translate color codes in
	 * @return text with colors
	 * <p> "" if textToColorize is null
	 */
	@Nonnull
	public static String colorize(@Nullable String textToColorize) {
		if(textToColorize == null) return EMPTY;
		return ChatColor.translateAlternateColorCodes('&', colorizeHex(textToColorize));
	}
	
	@Nullable
	public static List<String> colorize(@Nullable List<String> textToColorize) {
		if(textToColorize == null) return null;
		return textToColorize.stream().map(Colorizer::colorize).collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Nullable
	public static List<String> colorize(@Nullable String... textToColorize) {
		if(textToColorize == null) return null;
		return colorize(Arrays.asList(textToColorize));
	}
	
	private static String colorizeHex(String message)
	{
		if(message == null || message.isEmpty() || !message.contains("&#")) {
			return message;
		}
		Matcher matcher = HEX_PATTERN.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find())
		{
			String group = matcher.group(1);
			matcher.appendReplacement(buffer, COLOR_CHAR + "x"
					+ COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
					+ COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
					+ COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
					);
		}
		return matcher.appendTail(buffer).toString();
	}
	
}
