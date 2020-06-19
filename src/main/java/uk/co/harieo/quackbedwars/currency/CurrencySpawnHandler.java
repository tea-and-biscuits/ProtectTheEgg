package uk.co.harieo.quackbedwars.currency;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;

public class CurrencySpawnHandler {

	public static final String SPAWN_KEY = "resource-spawner";
	public static final String ERROR_MESSAGE = "Invalid currency spawn location in game world";

	private static final Map<Location, CurrencySpawnerInfo> spawnerLocations = new HashMap<>();
	private static boolean spawning = false;

	public static void parseSpawnerLocations(MapImpl map) {
		Logger logger = ProtectTheEgg.getInstance().getLogger();

		int successfulParses = 0;
		for (LocationPair pair : map.getLocationsByKey(SPAWN_KEY)) {
			String[] values = pair.getValue().split(",");
			if (values.length < 2) {
				logger.warning(ERROR_MESSAGE + ": Only " + values.length + " values");
			} else {
				String currencyName = values[0];
				Currency currency;
				try {
					currency = Currency.valueOf(currencyName);
				} catch (IllegalArgumentException ignored) {
					logger.warning(ERROR_MESSAGE + ": Unrecognised currency called '" + currencyName + "'");
					continue;
				}

				String teamName = values[1]; // This may be "none" if it wasn't required to set it
				BedWarsTeam team = null;
				if (!teamName.equalsIgnoreCase("none")) {
					try {
						team = BedWarsTeam.valueOf(teamName);
					} catch (IllegalArgumentException ignored) {
						logger.warning(ERROR_MESSAGE + ": Expected a valid team but got '" + teamName + "' instead");
						if (currency.isTeamBased()) { // If this currency requires the team to be valid
							continue; // Skip as we cannot fulfil this requirement
						}
					}
				}

				CurrencySpawnerInfo teamPair = new CurrencySpawnerInfo(currency, team);
				spawnerLocations.put(pair.getLocation(), teamPair);
				successfulParses++;
			}
		}

		logger.info("Parsed " + successfulParses + " resource spawner locations");
	}

	public static void startSpawning() {
		if (!spawning) { // Make sure this hasn't already happened
			for (Entry<Location, CurrencySpawnerInfo> spawners : spawnerLocations.entrySet()) {
				Location location = spawners.getKey();
				World world = location.getWorld();
				if (world == null) {
					throw new NullPointerException();
				}

				Location raisedLocation = new Location(world, location.getBlockX() + 0.5,
						location.getBlockY() + 2.0, location.getBlockZ() + 0.5);
				Currency currency = spawners.getValue().getCurrency();

				ArmorStand hologramStand = (ArmorStand) world.spawnEntity(raisedLocation, EntityType.ARMOR_STAND);
				hologramStand.setGravity(false);
				hologramStand.setVisible(false);
				hologramStand.setCustomName(
						currency.getColor() + ChatColor.BOLD.toString() + currency.getName() + " Spawner");
				hologramStand.setCustomNameVisible(true);
			}

			spawning = true;
		}
	}

}
