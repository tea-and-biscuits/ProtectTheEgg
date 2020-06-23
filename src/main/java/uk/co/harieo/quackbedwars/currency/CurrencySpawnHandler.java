package uk.co.harieo.quackbedwars.currency;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.spawners.CurrencySpawner;
import uk.co.harieo.quackbedwars.currency.spawners.SingleCurrencySpawner;
import uk.co.harieo.quackbedwars.currency.spawners.TeamSpawner;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;

public class CurrencySpawnHandler {

	public static final String TEAM_SPAWN_KEY = "team-resource-spawner";
	public static final String CURRENCY_SPAWN_KEY = "currency-resource-spawner";

	private static final Map<Location, CurrencySpawner> spawnerLocations = new HashMap<>();
	private static BukkitTask spawningTask;

	public static void parseSpawnerLocations(MapImpl map) {
		Logger logger = ProtectTheEgg.getInstance().getLogger();

		int successfulParses = 0;
		successfulParses += parseTeamSpawnerLocations(map);
		successfulParses += parseCurrencySpawnerLocations(map);
		logger.info("Parsed " + successfulParses + " resource spawner locations");
	}

	public static int parseTeamSpawnerLocations(MapImpl map) {
		Logger logger = ProtectTheEgg.getInstance().getLogger();

		int successes = 0;
		for (LocationPair pair : map.getLocationsByKey(TEAM_SPAWN_KEY)) {
			String teamName = pair.getValue();
			BedWarsTeam team = BedWarsTeam.getByName(teamName);
			if (team != null) {
				Location location = pair.getLocation();
				addSpawner(location, new TeamSpawner(location, team));
				successes++;
			} else {
				logger.warning("Failed to parse team spawn due to unknown team: " + teamName);
			}
		}

		return successes;
	}

	public static int parseCurrencySpawnerLocations(MapImpl map) {
		Logger logger = ProtectTheEgg.getInstance().getLogger();

		int successes = 0;
		for (LocationPair pair : map.getLocationsByKey(CURRENCY_SPAWN_KEY)) {
			String currencyName = pair.getValue();
			try {
				Currency currency = Currency.valueOf(currencyName);
				Location location = pair.getLocation();
				addSpawner(location, new SingleCurrencySpawner(location, currency));
				successes++;
			} catch (IllegalArgumentException ignored) {
				logger.warning("Failed to parse currency spawn due to unknown currency: " + currencyName);
			}
		}

		return successes;
	}

	public static void addSpawner(Location location, CurrencySpawner spawnerInfo) {
		spawnerLocations.put(location, spawnerInfo);
	}

	public static void removeSpawner(Location location) {
		spawnerLocations.remove(location);
	}

	public static Map<Location, CurrencySpawner> getSpawnerLocations() {
		return spawnerLocations;
	}

	public static void startSpawning() {
		if (spawningTask == null) { // Make sure this hasn't already happened
			for (Entry<Location, CurrencySpawner> spawners : spawnerLocations.entrySet()) {
				Location location = spawners.getKey();
				World world = location.getWorld();
				if (world == null) {
					throw new NullPointerException("No world in currency spawner location");
				}

				Location raisedLocation = new Location(world, location.getBlockX() + 0.5,
						location.getBlockY() + 2.0, location.getBlockZ() + 0.5);

				ArmorStand hologramStand = (ArmorStand) world.spawnEntity(raisedLocation, EntityType.ARMOR_STAND);
				hologramStand.setGravity(false);
				hologramStand.setVisible(false);
				hologramStand.setCustomName(spawners.getValue().getHologramName());
				hologramStand.setCustomNameVisible(true);
			}

			spawningTask = Bukkit.getScheduler().runTaskTimer(ProtectTheEgg.getInstance(), () -> {
				for (Entry<Location, CurrencySpawner> spawners : spawnerLocations.entrySet()) {
					CurrencySpawner spawner = spawners.getValue();
					for (CurrencySpawnRate spawnRate : spawner.getSpawnRates()) {
						if (spawnRate.getInternalSecond() == spawnRate.getSecondsPerSpawn()) {
							Location location = spawners.getKey();
							World world = location.getWorld();
							if (world != null) {
								Currency currency = spawnRate.getCurrency();
								world.dropItem(location,
										new ItemStack(currency.getMaterial(), spawnRate.getAmountPerSpawn()));
							}
						}

						spawnRate.incrementInternalSecond();
					}
				}
			}, 0, 20);
		}
	}

}
