package uk.co.harieo.quackbedwars.commands.maps;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;
import java.util.Set;
import uk.co.harieo.minigames.commands.Subcommand;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnHandler;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamGameData;
import uk.co.harieo.quackbedwars.teams.TeamSpawnHandler;

public class MapInfoSubcommand implements Subcommand {

	@Override
	public Set<String> getSubcommandAliases() {
		return Sets.newHashSet("info");
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getRequiredPermission() {
		return null;
	}

	@Override
	public void onSubcommand(CommandSender sender, String label, String[] args) {
		if (sender instanceof Player) {
			info((Player) sender);
		}
	}

	/**
	 * Provides information to the player on whether this map will work in a BedWars game and reports whether the map
	 * should be committed or not based on this
	 *
	 * @param player to send the information to
	 */
	private void info(Player player) {
		MapImpl map = MapImpl.get(player.getWorld());

		boolean isValid = true;

		// Make sure the map has a name
		if (map.getFullName() != null) {
			player.sendMessage(
					ProtectTheEgg.formatMessage(ChatColor.GREEN + "The map has a name (" + map.getFullName() + ")"));
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "The map does not have a name!"));
			isValid = false;
		}

		// Make sure the map has at least 1 author
		if (!map.getAuthors().isEmpty()) {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.GREEN + "The map has at least 1 author"));
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "The map doesn't have any authors!"));
			isValid = false;
		}

		BedWarsTeam[] teams = BedWarsTeam.values();

		// Counts how many teams have a player spawn and an egg spawn
		int teamsWithSpawns = 0;
		int teamsWithEggs = 0;
		for (BedWarsTeam team : teams) {
			if (!TeamSpawnHandler.getSpawnLocations(team).isEmpty()) {
				teamsWithSpawns++;
			}

			if (TeamGameData.getGameData(team).getEggData() != null) { // If it has egg data, it has an egg
				teamsWithEggs++;
			}
		}

		int halfAmountOfTeams = teams.length / 2;

		// Make sure that there are at least 3 teams with spawns but still warn if it's less than half of teams
		if (teamsWithSpawns < 3) {
			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.RED + "There are less than 3 teams with valid spawns (" + teamsWithSpawns + ")"));
			isValid = false;
		} else if (teamsWithSpawns < halfAmountOfTeams) {
			player.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.YELLOW + "Less than half of teams have spawns (" + teamsWithSpawns + ")"));
		} else {
			player.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.GREEN + "There are " + teamsWithSpawns + " team(s) with valid spawns"));
		}

		if (teamsWithEggs < halfAmountOfTeams) {
			player.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.RED
							+ "Less than half of teams have an egg spawn location!"));
			isValid = false;
		} else {
			player.sendMessage(
					ProtectTheEgg.formatMessage(ChatColor.GREEN + "At least half of teams have egg spawn locations!"));
		}

		// Make sure there is at least 1 currency spawner somewhere on the map
		if (!CurrencySpawnHandler.getSpawnerLocations().isEmpty()) {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.GREEN + "There is at least 1 currency spawner"));
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.RED + "There are no currency spawners set. Use /maps setresource <currency/team>"));
			isValid = false;
		}

		// Finally, report whether this map should be committed or not
		if (isValid) {
			player.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.GREEN + "This map is valid and can be used with /maps commit"));
		} else {
			player.sendMessage(
					ProtectTheEgg.formatMessage(ChatColor.RED + "This map is not valid, please see above for errors!"));
		}
	}

}
