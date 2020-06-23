package uk.co.harieo.quackbedwars.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnHandler;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;

public class ResourceSpawnSubcommands {

	public static final String SET_RESOURCE_SPAWN = "setresource";
	public static final String DELETE_RESOURCE_SPAWN = "deleteresource";

	// Note: Argument array includes the base sub-command

	public static void setResourceSpawnCommand(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ProtectTheEgg
					.formatMessage(
							ChatColor.RED + "Insufficient Arguments. Expected: /maps setresource <resource/team>"));
		} else {
			StringBuilder builder = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				builder.append(args[i]);
				builder.append(" ");
			}
			String trimmedArgument = builder.toString().trim();

			Currency currency;
			try {
				currency = Currency.valueOf(trimmedArgument.toUpperCase());
				setResourceSpawn(player, currency);
				return;
			} catch (IllegalArgumentException ignored) {
			} // Assuming they specified a team instead

			BedWarsTeam team = BedWarsTeam.getByName(trimmedArgument);
			if (team != null) {
				setTeamSpawn(player, team);
			} else { // No valid arguments
				player.sendMessage(ProtectTheEgg.formatMessage(
						ChatColor.RED + "Unknown argument, please provide the name of a resource or team!"));
			}
		}
	}

	private static void setResourceSpawn(Player player, Currency currency) {
		addLocationIfNotSpawner(player, CurrencySpawnHandler.CURRENCY_SPAWN_KEY, currency.name());
	}

	private static void setTeamSpawn(Player player, BedWarsTeam team) {
		addLocationIfNotSpawner(player, CurrencySpawnHandler.TEAM_SPAWN_KEY, team.name());
	}

	private static void addLocationIfNotSpawner(Player player, String key, String value) {
		MapImpl map = MapImpl.get(player.getWorld());
		Location location = player.getLocation();
		if (isCurrencySpawn(map, location)) {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "That is already a spawner location!"));
		} else {
			map.addLocation(location, key, value);
			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.GRAY + "Set your current location as a resource spawner (type: " + ChatColor.YELLOW
							+ value + ChatColor.GRAY + ")"));
		}
	}

	public static void deleteResourceSpawnCommand(Player player) {
		MapImpl map = MapImpl.get(player.getWorld());

		boolean removedOnce = false;
		for (LocationPair pair : map.getLocationPairs(player.getLocation())) {
			if (isCurrencySpawn(pair)) {
				map.removeLocation(pair);
				removedOnce = true;
			}
		}

		if (removedOnce) {
			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.GRAY + "This is " + ChatColor.RED + "no longer " + ChatColor.GRAY
							+ " a resource spawner!"));
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "There are no spawners at this location!"));
		}
	}

	private static boolean isCurrencySpawn(LocationPair pair) {
		String key = pair.getKey();
		return key.equals(CurrencySpawnHandler.CURRENCY_SPAWN_KEY) || key.equals(CurrencySpawnHandler.TEAM_SPAWN_KEY);
	}

	private static boolean isCurrencySpawn(MapImpl map, Location location) {
		for (LocationPair pair : map.getLocationPairs(location)) {
			if (isCurrencySpawn(pair)) {
				return true;
			}
		}

		return false;
	}

}
