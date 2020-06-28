package uk.co.harieo.quackbedwars.currency;

import org.bukkit.Bukkit;
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
			try {
				BedWarsTeam team = BedWarsTeam.valueOf(teamName);
				addSpawner(pair.getLocation(), new TeamSpawner(team));
				successes++;
			} catch (IllegalArgumentException ignored) {
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
				addSpawner(pair.getLocation(), new SingleCurrencySpawner(currency));
				successes++;
			} catch (IllegalArgumentException ignored) {
				logger.warning("Failed to parse currency spawn due to unknown currency: " + currencyName);
			}
		}

		return successes;
	}

	public static void addSpawner(Location location, CurrencySpawner spawnerInfo) {
		Location clonedLocation = location.clone();
		// Center the location and raise it 1 off the floor for precision
		clonedLocation.setX(clonedLocation.getBlockX() + 0.5);
		clonedLocation.setY(clonedLocation.getBlockY() + 1.0);
		clonedLocation.setZ(clonedLocation.getBlockZ() + 0.5);
		spawnerLocations.put(clonedLocation, spawnerInfo);
	}

	public static void removeSpawner(Location location) {
		spawnerLocations.remove(location);
	}

	public static Map<Location, CurrencySpawner> getSpawnerLocations() {
		return spawnerLocations;
	}

	/**
	 * Sets up the resource spawner holograms and begins to spawn resources based on their respective spawn rates
	 *
	 * @param ticksDelay the amount of ticks before items are dropped
	 */
	public static void startSpawning(int ticksDelay) {
		if (spawningTask == null) { // Make sure this hasn't already happened
			for (Entry<Location, CurrencySpawner> spawners : spawnerLocations.entrySet()) {
				Location location = spawners.getKey().clone();
				World world = location.getWorld();
				if (world == null) {
					throw new NullPointerException("No world in currency spawner location");
				}

				CurrencySpawner spawner = spawners.getValue();
				spawner.getHologram().setLocation(location);
				spawner.formatHologram();
			}

			spawningTask = Bukkit.getScheduler().runTaskTimer(ProtectTheEgg.getInstance(), () -> {
				for (Entry<Location, CurrencySpawner> spawners : spawnerLocations.entrySet()) {
					CurrencySpawner spawner = spawners.getValue();
					spawner.formatHologram();

					for (CurrencySpawnRate spawnRate : spawner.getSpawnRates()) {
						if (spawnRate.getInternalSecond() == spawnRate.getSecondsPerSpawn()) {
							spawnRate.dropItems(spawners.getKey());
						}

						spawnRate.incrementInternalSecond();
					}
				}
			}, ticksDelay, 20);
		}
	}

}
