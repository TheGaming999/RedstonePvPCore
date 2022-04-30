package me.redstonepvpcore.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.bukkit.configuration.InvalidConfigurationException;

public class ConfigCreator {

	private final static Map<String, ConfigOptions> CONFIGS = new ConcurrentHashMap<>();
	private final static List<String> EMPTY_LIST = new ArrayList<>();
	private final static Plugin PLUGIN = JavaPlugin.getProvidingPlugin(ConfigCreator.class);

	/**
	 * Creates a non usable config file.
	 * @param configName config name to save from resources (dummyconfig.yml)
	 */
	public static void createDummyConfig(String configName) {
		File configFile = new File(PLUGIN.getDataFolder(), configName);
		if (!configFile.exists()) { configFile.getParentFile().mkdirs(); PLUGIN.saveResource(configName, false); }
	}

	/**
	 * Creates a config file and load it if it doesn't exist. Otherwise, just load it.
	 * <p>This is equivalent to: {
	 * <p><i>{@code	getConfig().options().copyDefaults(true);}</i>
	 * <p><i>{@code	saveDefaultConfig();}</i>
	 * <p>}
	 * <p>which is mostly written within onEnable()
	 * @param configName (config .yml name) (example: data.yml)
	 * @return FileConfiguration of the config file.
	 * @throws FileNotFoundException when file is not found within resources or didn't save correctly.
	 * @throws IOException config file didn't load properly.
	 * @throws InvalidConfigurationException config file is formatted incorrectly.
	 */
	public static FileConfiguration copyAndSaveDefaults(String configName) {
		return copyAndSaveDefaults(configName, EMPTY_LIST);
	}
	
	public static Map<String, FileConfiguration> copyAndSaveDefaults(String... configNames) {
		Map<String, FileConfiguration> createdConfigFiles = Maps.newHashMap();
		for (String configName : configNames) {
			createdConfigFiles.put(configName, copyAndSaveDefaults(configName));
		}
		return createdConfigFiles;
	}
	
	/**
	 * Creates a config file and load it if it doesn't exist. Otherwise, just load it.
	 * <p>This is equivalent to: {
	 * <p><i>{@code	getConfig().options().copyDefaults(true);}</i>
	 * <p><i>{@code	saveDefaultConfig();}</i>
	 * <p>}
	 * <p>which is mostly written within onEnable()
	 * @param configName (config .yml name) (example: data.yml)
	 * @param ignoreComments ignores updating comments.
	 * @return FileConfiguration of the config file.
	 * @throws FileNotFoundException when file is not found within resources or didn't save correctly.
	 * @throws IOException config file didn't load properly.
	 * @throws InvalidConfigurationException config file is formatted incorrectly.
	 */
	public static FileConfiguration copyAndSaveDefaults(String configName, boolean ignoreComments) {
		if(!ignoreComments) return copyAndSaveDefaults(configName);
		File configFile = new File(PLUGIN.getDataFolder(), configName);
		if (!configFile.exists()) { 
			configFile.getParentFile().mkdirs(); 
			PLUGIN.saveResource(configName, false);
		}
		FileConfiguration configYaml = new YamlConfiguration();
		try {
			configYaml.load(configFile);
		} catch (FileNotFoundException e) { 
			System.out.println("File not found <?>"); 
			e.printStackTrace(); 
		} catch (IOException e) {
			System.out.println("Read write failed <!>"); 
			e.printStackTrace(); 
		} catch (InvalidConfigurationException e) { 
			System.out.println("Corrupted configuration file <?>");
			e.printStackTrace(); 
		}
		configYaml.options().copyDefaults(true);
		ConfigOptions options = ConfigOptions.create(configYaml, ignoreComments, EMPTY_LIST);
		CONFIGS.put(configName, options);
		return configYaml;
	}
	
	/**
	 * Creates a config file and load it if it doesn't exist. Otherwise, just load it.
	 * <p>This is equivalent to: {
	 * <p><i>{@code	getConfig().options().copyDefaults(true);}</i>
	 * <p><i>{@code	saveDefaultConfig();}</i>
	 * <p>}
	 * <p>which is mostly written within onEnable()
	 * @param configName (config .yml name) (example: data.yml)
	 * @param ignoredSections sections to ignore copying
	 * @return FileConfiguration of the config file.
	 * @throws FileNotFoundException when file is not found within resources or didn't save correctly.
	 * @throws IOException config file didn't load properly.
	 * @throws InvalidConfigurationException config file is formatted incorrectly.
	 */
	public static FileConfiguration copyAndSaveDefaults(String configName, List<String> ignoredSections) {
		File configFile = new File(PLUGIN.getDataFolder(), configName);
		if (!configFile.exists()) { 
			configFile.getParentFile().mkdirs(); 
			PLUGIN.saveResource(configName, false);
		}
		try {
			ConfigUpdaterInternal.update(PLUGIN, configName, configFile, ignoredSections);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileConfiguration configYaml = new YamlConfiguration();
		try {
			configYaml.load(configFile);
		} catch (FileNotFoundException e) { 
			System.out.println("File not found <?>"); 
			e.printStackTrace(); 
		} catch (IOException e) {
			System.out.println("Read write failed <!>"); 
			e.printStackTrace(); 
		} catch (InvalidConfigurationException e) { 
			System.out.println("Corrupted configuration file <?>");
			e.printStackTrace(); 
		}
		configYaml.options().copyDefaults(true);
		ConfigOptions options = ConfigOptions.create(configYaml, false, ignoredSections);
		CONFIGS.put(configName, options);
		return configYaml;
	}

	/**
	 * Creates a config file and load it if it doesn't exist. Otherwise, just load it.
	 * <p>This is equivalent to: {
	 * <p><i>{@code	getConfig().options().copyDefaults(true);}</i>
	 * <p><i>{@code	saveDefaultConfig();}</i>
	 * <p>}
	 * <p>which is mostly written within onEnable()
	 * @param configName (config .yml name) (example: data.yml)
	 * @param savePath path to save config file to (example: somewhere/else) <p>Config file will be found on 'somewhere/else/data.yml' which is outside plugins folder</p>
	 * @return FileConfiguration of the config file.
	 * @throws FileNotFoundException when file is not found within resources or didn't save correctly.
	 * @throws IOException config file didn't load properly.
	 * @throws InvalidConfigurationException config file is formatted incorrectly.
	 */
	public static FileConfiguration createConfigFile(String configName, String savePath) {
		try {
			File pathCreationFile = new File(savePath);
			pathCreationFile.mkdirs();
			InputStream stream = PLUGIN.getResource(configName);
			Path filePath = Paths.get(savePath, configName);
			File file = new File(filePath.toString());
			if(!file.exists())
			Files.copy(stream, Paths.get(savePath, configName));
		} catch (IOException e) {
			System.out.print("Unable to load or copy configuration file <?>");
			e.printStackTrace();
		}
		File configFile = new File(savePath, configName);
		FileConfiguration configYaml = new YamlConfiguration();
		try {
			configYaml.load(configFile);
		} catch (FileNotFoundException e) { 
			System.out.print("File not found <?>"); 
			e.printStackTrace();
		} catch (IOException e) {
			System.out.print("Read write failed <!>");
			e.printStackTrace(); 
		} catch (InvalidConfigurationException e) {
			System.out.print("Corrupted configuration file <?>");
			e.printStackTrace(); 
		}
		ConfigOptions options = ConfigOptions.create(configYaml, true, EMPTY_LIST, savePath);
		CONFIGS.put(configName, options);
		return configYaml;
	}


	/**
	 * @param configName config file name to get (customconfig.yml)
	 * @return FileConfiguration config file retrieved from config name.
	 */
	public static FileConfiguration getConfig(String configName) {
		return CONFIGS.get(configName).getFileConfiguration();
	}

	/**
	 * Reloads a config file. Same as {@code reloadConfig();}
	 * @param configName config name to reload (customconfig.yml)
	 * @return reloaded FileConfiguration config file retrieved from config name.
	 */
	public static FileConfiguration reloadConfig(String configName) {
		ConfigOptions options = CONFIGS.get(configName);
		FileConfiguration configYaml = options.getFileConfiguration();
		File pathName = options.hasCustomPath() ? new File(options.getCustomPath()) : PLUGIN.getDataFolder();
		File file = new File(pathName, configName);
		if(!options.isIgnoreComments()) {
			try {
				ConfigUpdaterInternal.update(PLUGIN, configName, file, options.getIgnoredSections());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		configYaml = YamlConfiguration.loadConfiguration(file);
		options.setFileConfiguration(configYaml);
		CONFIGS.put(configName, options);
		return configYaml;
	}
	
	public static void reloadConfigs(String... configNames) {
		for (String configName : configNames)
			reloadConfig(configName);
	}
	
	public static void reloadConfigs() {
		for (String configName : CONFIGS.keySet())
			reloadConfig(configName);
	}

	/**
	 * Saves the loaded config to the plugin folder and returns it.
	 * @param configName config name to save (customconfig.yml)
	 * @return saved FileConfiguration config file retrieved from config name.
	 */
	public static FileConfiguration saveConfig(String configName) {
		ConfigOptions options = CONFIGS.get(configName);
		FileConfiguration configYaml = options.getFileConfiguration();
		File pathName = options.hasCustomPath() ? new File(options.getCustomPath()) : PLUGIN.getDataFolder();
		File file = new File(pathName, configName);
		try {
			configYaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!options.isIgnoreComments()) {
			try {
				ConfigUpdaterInternal.update(PLUGIN, configName, file, options.getIgnoredSections());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return configYaml;
	}
	
	public static void saveConfigs(String... configNames) {
		for (String configName : configNames) 
			saveConfig(configName);
	}
	
	public static void saveConfigs() {
		for (String configName : CONFIGS.keySet()) 
			saveConfig(configName);
	}
	
	/**
	 * Removes config file from map.
	 * @param configName config file of (configName) to release.
	 */
	public static void releaseConfigFile(String configName) {
		CONFIGS.remove(configName);
	}
	
	public static void releaseConfigFiles(String... configNames) {
		for (String configName : configNames)
			releaseConfigFile(configName);
	}
	
	public static void releaseConfigFiles() {
		for (String configName : CONFIGS.keySet())
			releaseConfigFile(configName);
	}
	
	public static boolean isIgnoringComments(String configName) {
		return CONFIGS.get(configName).isIgnoreComments();
	}
	
	public static List<String> getIgnoredSections(String configName) {
		return CONFIGS.get(configName).getIgnoredSections();
	}
	
}

/**
 * 
 * @author https://github.com/tchristofferson/Config-Updater
 *
 */
class ConfigUpdaterInternal {

    //Used for separating keys in the keyBuilder inside parseComments method
    private static final char SEPARATOR = '.';

    public static void update(Plugin plugin, String resourceName, File toUpdate, String... ignoredSections) throws IOException {
        update(plugin, resourceName, toUpdate, Arrays.asList(ignoredSections));
    }

    public static void update(Plugin plugin, String resourceName, File toUpdate, List<String> ignoredSections) throws IOException {
        Preconditions.checkArgument(toUpdate.exists(), "The toUpdate file doesn't exist!");

        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(resourceName), StandardCharsets.UTF_8));
        FileConfiguration currentConfig = YamlConfiguration.loadConfiguration(toUpdate);
        Map<String, String> comments = parseComments(plugin, resourceName, defaultConfig);
        Map<String, String> ignoredSectionsValues = parseIgnoredSections(toUpdate, currentConfig, comments, ignoredSections == null ? Collections.emptyList() : ignoredSections);

        // will write updated config file "contents" to a string
        StringWriter writer = new StringWriter();
        write(defaultConfig, currentConfig, new BufferedWriter(writer), comments, ignoredSectionsValues);
        String value = writer.toString(); // config contents

        Path toUpdatePath = toUpdate.toPath();
        if (!value.equals(new String(Files.readAllBytes(toUpdatePath), StandardCharsets.UTF_8))) { // if updated contents are not the same as current file contents, update
            Files.write(toUpdatePath, value.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void write(FileConfiguration defaultConfig, FileConfiguration currentConfig, BufferedWriter writer, Map<String, String> comments, Map<String, String> ignoredSectionsValues) throws IOException {
        //Used for converting objects to yaml, then cleared
        FileConfiguration parserConfig = new YamlConfiguration();

        keyLoop: for (String fullKey : defaultConfig.getKeys(true)) {
            String indents = KeyBuilder.getIndents(fullKey, SEPARATOR);

            if (ignoredSectionsValues.isEmpty()) {
                writeCommentIfExists(comments, writer, fullKey, indents);
            } else {
                for (Map.Entry<String, String> entry : ignoredSectionsValues.entrySet()) {
                    if (entry.getKey().equals(fullKey)) {
                        writer.write(entry.getValue() + "\n");
                        continue keyLoop;
                    } else if (KeyBuilder.isSubKeyOf(entry.getKey(), fullKey, SEPARATOR)) {
                        continue keyLoop;
                    } else {
                        writeCommentIfExists(comments, writer, fullKey, indents);
                        break;
                    }
                }
            }

            Object currentValue = currentConfig.get(fullKey);

            if (currentValue == null)
                currentValue = defaultConfig.get(fullKey);

            String[] splitFullKey = fullKey.split("[" + SEPARATOR + "]");
            String trailingKey = splitFullKey[splitFullKey.length - 1];

            if (currentValue instanceof ConfigurationSection) {
                writer.write(indents + trailingKey + ":");

                if (!((ConfigurationSection) currentValue).getKeys(false).isEmpty())
                    writer.write("\n");
                else
                    writer.write(" {}\n");

                continue;
            }

            parserConfig.set(trailingKey, currentValue);
            String yaml = parserConfig.saveToString();
            yaml = yaml.substring(0, yaml.length() - 1).replace("\n", "\n" + indents);
            String toWrite = indents + yaml + "\n";
            parserConfig.set(trailingKey, null);
            writer.write(toWrite);
        }

        String danglingComments = comments.get(null);

        if (danglingComments != null)
            writer.write(danglingComments);

        writer.close();
    }

    //Returns a map of key comment pairs. If a key doesn't have any comments it won't be included in the map.
    private static Map<String, String> parseComments(Plugin plugin, String resourceName, FileConfiguration defaultConfig) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(plugin.getResource(resourceName)));
        Map<String, String> comments = new LinkedHashMap<>();
        StringBuilder commentBuilder = new StringBuilder();
        KeyBuilder keyBuilder = new KeyBuilder(defaultConfig, SEPARATOR);

        String line;
        while ((line = reader.readLine()) != null) {
            String trimmedLine = line.trim();

            //Only getting comments for keys. A list/array element comment(s) not supported
            if (trimmedLine.startsWith("-")) {
                continue;
            }

            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {//Is blank line or is comment
                commentBuilder.append(trimmedLine).append("\n");
            } else {//is a valid yaml key
                keyBuilder.parseLine(trimmedLine);
                String key = keyBuilder.toString();

                //If there is a comment associated with the key it is added to comments map and the commentBuilder is reset
                if (commentBuilder.length() > 0) {
                    comments.put(key, commentBuilder.toString());
                    commentBuilder.setLength(0);
                }

                //Remove the last key from keyBuilder if current path isn't a config section or if it is empty to prepare for the next key
                if (!keyBuilder.isConfigSectionWithKeys()) {
                    keyBuilder.removeLastKey();
                }
            }
        }

        reader.close();

        if (commentBuilder.length() > 0)
            comments.put(null, commentBuilder.toString());

        return comments;
    }

    private static Map<String, String> parseIgnoredSections(File toUpdate, FileConfiguration currentConfig, Map<String, String> comments, List<String> ignoredSections) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(toUpdate));
        Map<String, String> ignoredSectionsValues = new LinkedHashMap<>(ignoredSections.size());
        KeyBuilder keyBuilder = new KeyBuilder(currentConfig, SEPARATOR);
        StringBuilder valueBuilder = new StringBuilder();

        String currentIgnoredSection = null;
        String line;
        lineLoop : while ((line = reader.readLine()) != null) {
            String trimmedLine = line.trim();

            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#"))
                continue;

            if (trimmedLine.startsWith("-")) {
                for (String ignoredSection : ignoredSections) {
                    boolean isIgnoredParent = ignoredSection.equals(keyBuilder.toString());

                    if (isIgnoredParent || keyBuilder.isSubKeyOf(ignoredSection)) {
                        valueBuilder.append("\n").append(line);
                        continue lineLoop;
                    }
                }
            }
            
            keyBuilder.parseLine(trimmedLine);
            String fullKey = keyBuilder.toString();

            //If building the value for an ignored section and this line is no longer a part of the ignored section,
            //  write the valueBuilder, reset it, and set the current ignored section to null
            if (currentIgnoredSection != null && !KeyBuilder.isSubKeyOf(currentIgnoredSection, fullKey, SEPARATOR)) {
                ignoredSectionsValues.put(currentIgnoredSection, valueBuilder.toString());
                valueBuilder.setLength(0);
                currentIgnoredSection = null;
            }

            for (String ignoredSection : ignoredSections) {
                boolean isIgnoredParent = ignoredSection.equals(fullKey);

                if (isIgnoredParent || keyBuilder.isSubKeyOf(ignoredSection)) {
                    if (valueBuilder.length() > 0)
                        valueBuilder.append("\n");

                    String comment = comments.get(fullKey);

                    if (comment != null) {
                        String indents = KeyBuilder.getIndents(fullKey, SEPARATOR);
                        valueBuilder.append(indents).append(comment.replace("\n", "\n" + indents));//Should end with new line (\n)
                        valueBuilder.setLength(valueBuilder.length() - indents.length());//Get rid of trailing \n and spaces
                    }

                    valueBuilder.append(line);

                    //Set the current ignored section for future iterations of while loop
                    //Don't set currentIgnoredSection to any ignoredSection sub-keys
                    if (isIgnoredParent)
                        currentIgnoredSection = fullKey;

                    break;
                }
            }
        }

        reader.close();

        if (valueBuilder.length() > 0)
            ignoredSectionsValues.put(currentIgnoredSection, valueBuilder.toString());

        return ignoredSectionsValues;
    }

    private static void writeCommentIfExists(Map<String, String> comments, BufferedWriter writer, String fullKey, String indents) throws IOException {
        String comment = comments.get(fullKey);

        //Comments always end with new line (\n)
        if (comment != null)
            //Replaces all '\n' with '\n' + indents except for the last one
            writer.write(indents + comment.substring(0, comment.length() - 1).replace("\n", "\n" + indents) + "\n");
    }

    //Input: 'key1.key2' Result: 'key1'
    @SuppressWarnings("unused")
	private static void removeLastKey(StringBuilder keyBuilder) {
        if (keyBuilder.length() == 0)
            return;

        String keyString = keyBuilder.toString();
        //Must be enclosed in brackets in case a regex special character is the separator
        String[] split = keyString.split("[" + SEPARATOR + "]");
        //Makes sure begin index isn't < 0 (error). Occurs when there is only one key in the path
        int minIndex = Math.max(0, keyBuilder.length() - split[split.length - 1].length() - 1);
        keyBuilder.replace(minIndex, keyBuilder.length(), "");
    }

    @SuppressWarnings("unused")
	private static void appendNewLine(StringBuilder builder) {
        if (builder.length() > 0)
            builder.append("\n");
    }

}

class KeyBuilder implements Cloneable {

    private final FileConfiguration config;
    private final char separator;
    private final StringBuilder builder;

    public KeyBuilder(FileConfiguration config, char separator) {
        this.config = config;
        this.separator = separator;
        this.builder = new StringBuilder();
    }

    private KeyBuilder(KeyBuilder keyBuilder) {
        this.config = keyBuilder.config;
        this.separator = keyBuilder.separator;
        this.builder = new StringBuilder(keyBuilder.toString());
    }

    public void parseLine(String line) {
        line = line.trim();
        String[] currentSplitLine = line.split(":");
        String key = currentSplitLine[0].replace("'", "");

        //Checks keyBuilder path against config to see if the path is valid.
        //If the path doesn't exist in the config it keeps removing last key in keyBuilder.
        while (builder.length() > 0 && !config.contains(builder.toString() + separator + key)) {
            removeLastKey();
        }

        //Add the separator if there is already a key inside keyBuilder
        //If currentSplitLine[0] is 'key2' and keyBuilder contains 'key1' the result will be 'key1.' if '.' is the separator
        if (builder.length() > 0)
            builder.append(separator);

        //Appends the current key to keyBuilder
        //If keyBuilder is 'key1.' and currentSplitLine[0] is 'key2' the resulting keyBuilder will be 'key1.key2' if separator is '.'
        builder.append(key);
    }

    public String getLastKey() {
        if (builder.length() == 0)
            return "";

        return builder.toString().split("[" + separator + "]")[0];
    }

    public boolean isEmpty() {
        return builder.length() == 0;
    }

    //Checks to see if the full key path represented by this instance is a sub-key of the key parameter
    public boolean isSubKeyOf(String parentKey) {
        return isSubKeyOf(parentKey, builder.toString(), separator);
    }

    //Checks to see if subKey is a sub-key of the key path this instance represents
    public boolean isSubKey(String subKey) {
        return isSubKeyOf(builder.toString(), subKey, separator);
    }

    public static boolean isSubKeyOf(String parentKey, String subKey, char separator) {
        if (parentKey.isEmpty())
            return false;

        return subKey.startsWith(parentKey)
                && subKey.substring(parentKey.length()).startsWith(String.valueOf(separator));
    }

    public static String getIndents(String key, char separator) {
        String[] splitKey = key.split("[" + separator + "]");
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < splitKey.length; i++) {
            builder.append("  ");
        }

        return builder.toString();
    }

    public boolean isConfigSection() {
        String key = builder.toString();
        return config.isConfigurationSection(key);
    }

    public boolean isConfigSectionWithKeys() {
        String key = builder.toString();
        return config.isConfigurationSection(key) && !config.getConfigurationSection(key).getKeys(false).isEmpty();
    }

    //Input: 'key1.key2' Result: 'key1'
    public void removeLastKey() {
        if (builder.length() == 0)
            return;

        String keyString = builder.toString();
        //Must be enclosed in brackets in case a regex special character is the separator
        String[] split = keyString.split("[" + separator + "]");
        //Makes sure begin index isn't < 0 (error). Occurs when there is only one key in the path
        int minIndex = Math.max(0, builder.length() - split[split.length - 1].length() - 1);
        builder.replace(minIndex, builder.length(), "");
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    protected KeyBuilder clone() {
        return new KeyBuilder(this);
    }
}

class ConfigOptions {
	
	private FileConfiguration fileConfiguration;
	private boolean ignoreComments;
	private List<String> ignoredSections;
	private boolean hasCustomPath;
	private String customPath;
	
	private ConfigOptions(FileConfiguration fileConfiguration, boolean ignoreComments, 
			List<String> ignoredSections) {
		this.fileConfiguration = fileConfiguration;
		this.ignoreComments = ignoreComments;
		this.ignoredSections = ignoredSections;
		this.hasCustomPath = false;
	}
	
	private ConfigOptions(FileConfiguration fileConfiguration, boolean ignoreComments, 
			List<String> ignoredSections, String customPath) {
		this.fileConfiguration = fileConfiguration;
		this.ignoreComments = ignoreComments;
		this.ignoredSections = ignoredSections;
		this.customPath = customPath;
		if(customPath != null)
		this.hasCustomPath = true;
	}
	
	public static ConfigOptions create(FileConfiguration config, boolean ignoreComments, 
			List<String> ignoredSections) {
		return new ConfigOptions(config, ignoreComments, ignoredSections);
	}
	
	public static ConfigOptions create(FileConfiguration config, boolean ignoreComments, 
			List<String> ignoredSections, String customPath) {
		return new ConfigOptions(config, ignoreComments, ignoredSections, customPath);
	}

	public FileConfiguration getFileConfiguration() {
		return fileConfiguration;
	}

	public void setFileConfiguration(FileConfiguration fileConfiguration) {
		this.fileConfiguration = fileConfiguration;
	}

	public boolean isIgnoreComments() {
		return ignoreComments;
	}

	public void setIgnoreComments(boolean ignoreComments) {
		this.ignoreComments = ignoreComments;
	}

	public List<String> getIgnoredSections() {
		return ignoredSections;
	}

	public void setIgnoredSections(List<String> ignoredSections) {
		this.ignoredSections = ignoredSections;
	}
	
	public String getCustomPath() {
		return customPath;
	}
	
	public void setCustomPath(String customPath) {
		this.customPath = customPath;
	}
	
	public boolean hasCustomPath() {
		return hasCustomPath;
	}
	
}
