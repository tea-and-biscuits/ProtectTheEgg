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
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.egg.EggData;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

public class EggSpawnSubcommands implements Subcommand {

	@Override
	public Set<String> getSubcommandAliases() {
		return Sets.newHashSet("setegg", "deleteegg");
	}

	@Override
	public String getUsage() {
		return "<setegg/deleteegg> [team]";
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
				case "setegg":
					setEggSpawnSubcommand(player, args);
					break;
				case "deleteegg":
					deleteEggSpawnSubcommand(player);
					break;
				default:
					throw new IllegalArgumentException("Provided unrecognised sub-command alias");
			}
		}
	}

	/**
	 * The 'setegg' sub-command to the maps command which allows a player to set their current location as an egg spawn
	 * point for a {@link BedWarsTeamData}
	 *
	 * @param player who has issued the command
	 * @param args which were supplied with the command
	 */
	public void setEggSpawnSubcommand(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.RED + "Insufficient Arguments. Expected: /maps setegg <team>"));
		} else {
			String teamName = CommandUtils.concatenateArguments(args, 1);
			BedWarsTeamData teamData = BedWarsTeamData.getByName(teamName);
			if (teamData == null) {
				player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "Unknown team: " + teamName));
			} else {
				MapImpl map = MapImpl.get(player.getWorld());

				for (LocationPair pair : map.getLocationPairs(player.getLocation())) {
					if (isEggLocation(pair)) {
						player.sendMessage(
								ProtectTheEgg.formatMessage(ChatColor.RED + "This is already an egg spawn point!"));
						return;
					}
				}

				PlayerBasedTeam team = teamData.getTeam();

				Location location = player.getLocation();
				map.addLocation(location, EggData.EGG_KEY, teamData.name());
				TeamGameData.getGameData(team).setEggData(new EggData(location.getBlock(), team)); // Caches the location

				player.sendMessage(ProtectTheEgg.formatMessage(
						ChatColor.GRAY + "Set this to a " + team.getFormattedName() + ChatColor.GRAY
								+ " egg spawn point!"));
			}
		}
	}

	/**
	 * The 'deleteegg' sub-command to the maps command which allows a player to remove an egg spawn at their current
	 * location
	 *
	 * @param player who has issued the command
	 */
	public void deleteEggSpawnSubcommand(Player player) {
		MapImpl map = MapImpl.get(player.getWorld());

		boolean deletedOnce = false;
		for (LocationPair pair : map.getLocationPairs(player.getLocation())) {
			if (isEggLocation(pair)) {
				map.removeLocation(pair);
				deletedOnce = true;

				try {
					BedWarsTeamData teamData = BedWarsTeamData.valueOf(pair.getValue());
					TeamGameData.getGameData(teamData.getTeam()).setEggData(null); // Removes the location from the cache
				} catch (IllegalArgumentException ignored) { }
			}
		}

		if (deletedOnce) {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.GRAY + "Deleted this egg spawn location!"));
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "This is not an egg spawn location!"));
		}
	}

	/**
	 * Checks whether a {@link LocationPair} is an egg spawn location
	 *
	 * @param pair to be checked
	 * @return whether the location pair is an egg spawn location
	 */
	private boolean isEggLocation(LocationPair pair) {
		return pair.getKey().equals(EggData.EGG_KEY);
	}

}
