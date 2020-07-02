package uk.co.harieo.quackbedwars.commands.maps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;
import java.util.Set;
import uk.co.harieo.minigames.commands.Subcommand;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;
import uk.co.harieo.quackbedwars.teams.handlers.TeamSpawnHandler;

public class PlayerSpawnSubcommands implements Subcommand {

	@Override
	public Set<String> getSubcommandAliases() {
		return Sets.newHashSet("setspawn", "deletespawn");
	}

	@Override
	public String getUsage() {
		return "<setspawn/deletespawn> [team]";
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
				case "setspawn":
					setSpawn(player, args);
					break;
				case "deletespawn":
					deleteSpawn(player);
					break;
				default:
					throw new IllegalArgumentException("Provided unknown sub-command alias");
			}
		}
	}

	/**
	 * The 'setspawn' sub-command which allows a player to set their current location to a team's spawn point
	 *
	 * @param player who has issued the command
	 * @param args supplied with the command
	 */
	private void setSpawn(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.RED
							+ "Please specify a team to set the island spawn for: /maps setspawn <team>"));
		} else {
			StringBuilder builder = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				builder.append(args[i]);
				builder.append(" ");
			}

			String teamName = builder.toString().trim();
			BedWarsTeamData teamData = BedWarsTeamData.getByName(teamName);
			if (teamData == null) {
				player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "Unknown team: " + teamName));
				return;
			}

			Team team = teamData.getTeam();
			String properTeamName = teamData.getTeam().getName(); // Properly formatted as it is the system copy

			MapImpl map = MapImpl.get(player.getWorld());
			Location location = player.getLocation();
			if (map.isLocationPlotted(location)) {
				// Overwrite any previous spawns with the requested team
				for (LocationPair pair : map.getLocationPairs(location)) {
					if (isTeamSpawn(pair)) {
						pair.setValue(teamData.name()); // Replace it with this team
					}
				}
			} else {
				map.addLocation(location, TeamSpawnHandler.SPAWN_KEY, properTeamName);
			}

			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.GRAY + "Set current location to a " + team.getChatColor() + properTeamName + " "
							+ ChatColor.GRAY
							+ "spawn point!"));
			team.getSpawns().addSpawn(location); // Caches the location so it doesn't need re-parsing
			location.clone().subtract(0, 1, 0).getBlock().setType(Material.GLASS);
		}
	}

	/**
	 * The 'deletespawn' sub-command which allows the player to delete a team's spawn at their current location
	 *
	 * @param player who has issued the command
	 */
	private void deleteSpawn(Player player) {
		Location location = player.getLocation();
		MapImpl map = MapImpl.get(player.getWorld());
		if (map.isLocationPlotted(location)) {
			boolean removedOnce = false;
			for (LocationPair pair : map.getLocationPairs(location)) {
				if (isTeamSpawn(pair)) {
					map.removeLocation(pair);
					removedOnce = true;

					BedWarsTeamData teamData = BedWarsTeamData.getByName(pair.getValue());
					if (teamData != null) {
						Team team = teamData.getTeam();
						team.getSpawns().removeSpawn(pair.getLocation()); // Removes from the cache
					}
				}
			}

			if (removedOnce) {
				player.sendMessage(ProtectTheEgg.formatMessage(
						ChatColor.GRAY + "The spawn at your location has been " + ChatColor.RED + "deleted"));
				location.clone().subtract(0, 1, 0).getBlock().setType(Material.AIR);
			} else {
				player.sendMessage(
						ProtectTheEgg.formatMessage(ChatColor.RED + "There is no spawn marked at your location"));
			}
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "There is no spawn at your location!"));
		}
	}

	/**
	 * Checks whether a {@link LocationPair} is a team spawn
	 *
	 * @param pair to be checked
	 * @return whether the pair is a team spawn
	 */
	private boolean isTeamSpawn(LocationPair pair) {
		return pair.getKey().equalsIgnoreCase(TeamSpawnHandler.SPAWN_KEY);
	}

}
