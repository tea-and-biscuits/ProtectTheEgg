package uk.co.harieo.quackbedwars.commands.maps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;
import java.util.Set;
import uk.co.harieo.minigames.commands.CommandUtils;
import uk.co.harieo.minigames.commands.Subcommand;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnHandler;
import uk.co.harieo.quackbedwars.currency.spawners.SingleCurrencySpawner;
import uk.co.harieo.quackbedwars.currency.spawners.TeamSpawner;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;

public class ResourceSpawnSubcommands implements Subcommand {

	@Override
	public Set<String> getSubcommandAliases() {
		return Sets.newHashSet("setresource", "deleteresource");
	}

	@Override
	public String getUsage() {
		return "<setresource/deleteresource> [resource/team]";
	}

	@Override
	public String getRequiredPermission() {
		return null;
	}

	@Override
	public void onSubcommand(CommandSender sender, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			switch (label.toLowerCase()) {
				case "setresource":
					setResourceSpawnCommand(player, args);
					break;
				case "deleteresource":
					deleteResourceSpawnCommand(player);
					break;
				default:
					throw new IllegalArgumentException("Provided unrecognised sub-command alias");
			}
		}
	}

	/**
	 * The 'setresource' sub-command to the maps command which allows the player to set the location of a resource
	 * spawner either for a {@link BedWarsTeam} or for a specific {@link Currency}
	 *
	 * @param player who has issued the command
	 * @param args which were supplied with the command
	 */
	private void setResourceSpawnCommand(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ProtectTheEgg
					.formatMessage(
							ChatColor.RED + "Insufficient Arguments. Expected: /maps setresource <resource/team>"));
		} else {
			String name = CommandUtils.concatenateArguments(args, 1);

			Currency currency;
			try {
				currency = Currency.valueOf(name.toUpperCase());
				setResourceSpawn(player, currency);
				return;
			} catch (IllegalArgumentException ignored) {
			} // Assuming they specified a team instead

			BedWarsTeam team = BedWarsTeam.getByName(name);
			if (team != null) {
				setTeamSpawn(player, team);
			} else { // No valid arguments
				player.sendMessage(ProtectTheEgg.formatMessage(
						ChatColor.RED + "Unknown argument, please provide the name of a resource or team!"));
			}
		}
	}

	/**
	 * Sets the location for a {@link Currency} based resource spawner
	 *
	 * @param player who has issued the command
	 * @param currency which should be spawned at this spawner
	 */
	private void setResourceSpawn(Player player, Currency currency) {
		addLocationIfNotSpawner(player, CurrencySpawnHandler.CURRENCY_SPAWN_KEY, currency.name());
		CurrencySpawnHandler.addSpawner(player.getLocation(), new SingleCurrencySpawner(currency));
	}

	/**
	 * Sets the location for a {@link BedWarsTeam} based resource spawner
	 *
	 * @param player who has issued the command
	 * @param team which this spawner will belong to
	 */
	private void setTeamSpawn(Player player, BedWarsTeam team) {
		addLocationIfNotSpawner(player, CurrencySpawnHandler.TEAM_SPAWN_KEY, team.name());
		CurrencySpawnHandler.addSpawner(player.getLocation(), new TeamSpawner(team));
	}

	/**
	 * Adds a {@link LocationPair} as long as it has not been set previously (e.g it is already a resource spawner) and
	 * reports the result to the player
	 *
	 * @param player who issued the command
	 * @param key for the location pair
	 * @param value for the location pair
	 */
	private void addLocationIfNotSpawner(Player player, String key, String value) {
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

	/**
	 * The 'deleteresource' sub-command to the maps command which allows a player to delete a resource spawner at their
	 * location
	 *
	 * @param player who is issuing the command
	 */
	private void deleteResourceSpawnCommand(Player player) {
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

	/**
	 * Checks whether a {@link LocationPair} is marked as either a {@link Currency} spawner or a {@link BedWarsTeam}
	 * spawner
	 *
	 * @param pair to be checked
	 * @return whether the pair is a recognised resource spawner
	 */
	private boolean isCurrencySpawn(LocationPair pair) {
		String key = pair.getKey();
		return key.equals(CurrencySpawnHandler.CURRENCY_SPAWN_KEY) || key.equals(CurrencySpawnHandler.TEAM_SPAWN_KEY);
	}

	private boolean isCurrencySpawn(MapImpl map, Location location) {
		for (LocationPair pair : map.getLocationPairs(location)) {
			if (isCurrencySpawn(pair)) {
				return true;
			}
		}

		return false;
	}

}
