package me.redstonepvpcore.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.PropertyType;
import xyz.xenondevs.particle.data.color.NoteColor;
import xyz.xenondevs.particle.data.texture.BlockTexture;
import xyz.xenondevs.particle.data.texture.ItemTexture;

public class ParticleReader {

	private final static Plugin PLUGIN = JavaPlugin.getProvidingPlugin(ParticleReader.class);
	private final static BukkitScheduler SCHEDULER = PLUGIN.getServer().getScheduler();

	// simple: <ParticleName>
	// with stuff: <ParticleName> <property>=<value> <property2>=<value2>
	// <property....>
	public static ParticleBuilder parse(String particleName) {
		if (particleName == null || particleName.isEmpty()) return null;
		if (!particleName.contains(" ")) return new ParticleBuilder(ParticleEffect.valueOf(particleName.toUpperCase()))
				.setLocation(new Location(null, 0d, 0d, 0d));
		String[] properties = particleName.split(" ");
		ParticleEffect particleEffect = null;
		ParticleBuilder builder = null;
		for (String property : properties) {
			if (!property.contains("=")) {
				particleEffect = ParticleEffect.valueOf(property.toUpperCase());
				builder = new ParticleBuilder(particleEffect).setLocation(new Location(null, 0d, 0d, 0d));
			} else {
				String propertyName = property.split("=")[0];
				String value = property.split("=")[1];
				if (particleEffect.hasProperty(PropertyType.COLORABLE)) {
					if (propertyName.equalsIgnoreCase("color")) {
						builder = builder.setColor(parseColor(value));
					}
				}
				if (particleEffect.hasProperty(PropertyType.RESIZEABLE)) {
					if (propertyName.equalsIgnoreCase("size")) {
						builder = builder.setOffsetX(Float.valueOf(value));
					}
				}
				if (particleEffect.hasProperty(PropertyType.DIRECTIONAL)) {
					if (propertyName.equalsIgnoreCase("direction")) {
						builder = builder.setOffset(parseVector(value));
					}
				}
				if (propertyName.equalsIgnoreCase("location") || propertyName.equalsIgnoreCase("loc")) {
					builder = builder.setLocation(parseLocation(value));
				}
				if (propertyName.equalsIgnoreCase("speed")) {
					builder = builder.setSpeed(Float.valueOf(value));
				}
				if (propertyName.equalsIgnoreCase("amount")) {
					builder = builder.setAmount(Integer.parseInt(value));
				}
				if (particleEffect.hasProperty(PropertyType.DUST)) {
					if (propertyName.equalsIgnoreCase("dust")) {
						builder = builder.setParticleData(new NoteColor(Integer.parseInt(value)));
					}
				}
				if (particleEffect.hasProperty(PropertyType.REQUIRES_BLOCK)) {
					if (propertyName.equalsIgnoreCase("texture")) {
						builder = builder.setParticleData(new BlockTexture(Material.matchMaterial(value)));
					}
				}
				if (particleEffect.hasProperty(PropertyType.REQUIRES_ITEM)) {
					if (propertyName.equalsIgnoreCase("texture")) {
						builder = builder
								.setParticleData(new ItemTexture(new ItemStack(Material.matchMaterial(value))));
					}
				}
			}
		}
		return builder;
	}

	public static List<ParticleBuilder> parseAll(String particleString) {
		List<ParticleBuilder> list = null;
		if (particleString != null && !particleString.isEmpty()) {
			if (particleString.contains("|")) {
				list = new ArrayList<>();
				String[] particleNames = particleString.split("\\|");
				for (String particleName : particleNames) {
					list.add(parse(particleName));
				}
			} else {
				list = new ArrayList<>();
				list.add(parse(particleString));
			}
		}
		return list;
	}

	public static boolean sendParticlesAsynchronously(Player player, List<ParticleBuilder> particles) {
		if (particles == null || particles.isEmpty()) return true;
		SCHEDULER.runTaskAsynchronously(PLUGIN, () -> particles.forEach(particle -> spawnParticle(player, particle)));
		return true;
	}

	public static boolean sendParticles(Player player, List<ParticleBuilder> particles) {
		if (particles == null || particles.isEmpty()) return true;
		particles.forEach(particle -> spawnParticle(player, particle));
		return true;
	}

	public static void spawnParticle(Player player, ParticleBuilder particleBuilder) {
		Location loc = particleBuilder.getLocation();
		loc.setWorld(player.getWorld());
		particleBuilder.setLocation(player.getLocation().add(loc)).display();
		particleBuilder.setLocation(loc);
	}

	public static void spawnParticle(Location loc, ParticleBuilder particleBuilder) {
		particleBuilder.setLocation(loc).display();
	}

	public static void spawnParticles(Location loc, List<ParticleBuilder> particleBuilders) {
		particleBuilders.forEach(particle -> particle.setLocation(loc).display());
	}

	public static Color parseColor(String colorString) {
		colorString = colorString.replace(", ", ",");
		if (colorString.contains(",")) {
			String[] split = colorString.split(",");
			float[] values = new float[3];
			values = Color.RGBtoHSB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]),
					null);
			return Color.getHSBColor(values[0], values[1], values[2]);
		} else if (colorString.contains("~")) {
			String[] split = colorString.split("~");
			return Color.getHSBColor(Float.valueOf(split[0]), Float.valueOf(split[1]), Float.valueOf(split[2]));
		}
		return Color.getColor(colorString);
	}

	public static Vector parseVector(String vectorString) {
		vectorString = vectorString.replace(", ", ",");
		String[] offsets = vectorString.split(",");
		return new Vector(Integer.parseInt(offsets[0]), Integer.parseInt(offsets[1]), Integer.parseInt(offsets[2]));
	}

	public static Location parseLocation(String locationString) {
		locationString = locationString.replace(", ", ",");
		String[] offsets = locationString.split(",");
		return new Location(null, Double.parseDouble(offsets[0]), Double.parseDouble(offsets[1]),
				Double.parseDouble(offsets[2]));
	}

}
