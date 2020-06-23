package uk.co.harieo.quackbedwars.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnHandler;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamSpawnHandler;

public class MapCommand implements CommandExecutor {

	private static final String SET_SPAWN = "setspawn";
	private static final String DELETE_SPAWN = "deletespawn";
	private static final String INFO = "info";
	private static final String AUTHOR = "author";
	private static final String SET_NAME = "setname";
	private static final String COMMIT = "commit";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!sender.hasPermission("quacktopia.minigames.maps")) {
			sender.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "You do not have permission to do that!"));
		} else if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can set locations!");
		} else {
			Player player = (Player) sender;
			if (args.length < 1) {
				player.sendMessage(ProtectTheEgg
						.formatMessage(ChatColor.RED + "Insufficient Arguments. Expected: /maps <subcommand>"));
			} else {
				String subCommand = args[0].toLowerCase();
				switch (subCommand) {
					case SET_SPAWN:
						setSpawn(player, args);
						break;
					case AUTHOR:
						author(player, args);
						break;
					case INFO:
						info(player);
						break;
					case SET_NAME:
						setName(player, args);
						break;
					case COMMIT:
						commit(player);
						break;
					case DELETE_SPAWN:
						deleteSpawn(player);
						break;
					case ResourceSpawnSubcommands.SET_RESOURCE_SPAWN:
						ResourceSpawnSubcommands.setResourceSpawnCommand(player, args);
						break;
					case ResourceSpawnSubcommands.DELETE_RESOURCE_SPAWN:
						ResourceSpawnSubcommands.deleteResourceSpawnCommand(player);
						break;
					default:
						player.sendMessage(
								ProtectTheEgg.formatMessage(ChatColor.RED + "Unrecognised sub-command: " + subCommand));
				}
			}
		}
		return false;
	}

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
			BedWarsTeam team = BedWarsTeam.getByName(teamName);
			if (team == null) {
				player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "Unknown team: " + teamName));
				return;
			}

			MapImpl map = MapImpl.get(player.getWorld());
			Location location = player.getLocation();
			if (map.isLocationPlotted(location)) {
				// Overwrite any previous spawns with the requested team
				for (LocationPair pair : map.getLocationPairs(location)) {
					if (isTeamSpawn(pair)) {
						pair.setValue(team.getName()); // Replace it with this team
					}
				}
			} else {
				map.addLocation(location, TeamSpawnHandler.SPAWN_KEY, team.getName());
			}

			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.GRAY + "Set current location to a " + team.getChatColor() + team.getName() + " "
							+ ChatColor.GRAY
							+ "spawn point!"));
			TeamSpawnHandler.addSpawnLocation(team, location); // Caches the location so it doesn't need re-parsing
		}
	}

	private void deleteSpawn(Player player) {
		Location location = player.getLocation();
		MapImpl map = MapImpl.get(player.getWorld());
		if (map.isLocationPlotted(location)) {
			boolean removedOnce = false;
			for (LocationPair pair : map.getLocationPairs(location)) {
				if (isTeamSpawn(pair)) {
					map.removeLocation(pair);
					removedOnce = true;

					BedWarsTeam team = BedWarsTeam.getByName(pair.getValue());
					if (team != null) {
						TeamSpawnHandler.removeSpawnLocation(team, pair.getLocation()); // Removes from the cache
					}
				}
			}

			if (removedOnce) {
				player.sendMessage(ProtectTheEgg.formatMessage(
						ChatColor.GRAY + "The spawn at your location has been " + ChatColor.RED + "deleted"));
			} else {
				player.sendMessage(
						ProtectTheEgg.formatMessage(ChatColor.RED + "There is no spawn marked at your location"));
			}
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "There is no spawn at your location!"));
		}
	}

	private boolean isTeamSpawn(LocationPair pair) {
		return pair.getKey().equalsIgnoreCase(TeamSpawnHandler.SPAWN_KEY);
	}

	private void author(Player player, String[] args) {
		MapImpl map = MapImpl.get(player.getWorld());
		if (args.length < 3) {
			StringBuilder authorsBuilder = new StringBuilder();
			List<String> authors = map.getAuthors();
			if (authors.isEmpty()) {
				authorsBuilder.append(ChatColor.RED);
				authorsBuilder.append("None");
			} else {
				for (int i = 0; i < authors.size(); i++) {
					authorsBuilder.append(authors.get(i));
					if (i + 1 < authors.size()) {
						authorsBuilder.append(", ");
					}
				}
			}
			player.sendMessage("Current Authors: " + authorsBuilder.toString());
		} else {
			String addRemove = args[1];
			boolean add;
			if (addRemove.equalsIgnoreCase("add")) {
				add = true;
			} else if (addRemove.equalsIgnoreCase("remove")) {
				add = false;
			} else {
				player.sendMessage(
						ProtectTheEgg
								.formatMessage(ChatColor.RED + "Neither add nor remove was specified: " + addRemove));
				return;
			}

			String username = args[2];
			if (add) {
				map.addAuthor(username);
				player.sendMessage(ProtectTheEgg.formatMessage(
						ChatColor.GREEN + "Added " + ChatColor.GRAY + "the user " + ChatColor.YELLOW + username
								+ ChatColor.GRAY + " as a map author"));
			} else {
				if (map.getAuthors().contains(username)) {
					map.removeAuthor(username);
					player.sendMessage(ProtectTheEgg.formatMessage(
							ChatColor.RED + "Removed " + ChatColor.GRAY + " the user " + ChatColor.YELLOW + username
									+ ChatColor.GRAY + " from being a map author"));
				} else {
					player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED
							+ "That user isn't currently an author, did you capitalize the name properly?"));
				}
			}
		}
	}

	private void setName(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(
					ProtectTheEgg
							.formatMessage(ChatColor.RED + "Insufficient Arguments. Expected: /maps setname <name>"));
		} else {
			StringBuilder nameBuilder = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				nameBuilder.append(args[i]);
				nameBuilder.append(" ");
			}
			String name = nameBuilder.toString();

			MapImpl map = MapImpl.get(player.getWorld());
			map.setFullName(name);
			player.sendMessage(
					ProtectTheEgg.formatMessage(ChatColor.GRAY + "Set the world name to " + ChatColor.GREEN + name));
		}
	}

	private void info(Player player) {
		MapImpl map = MapImpl.get(player.getWorld());

		boolean isValid = true;

		if (map.getFullName() != null) {
			player.sendMessage(
					ProtectTheEgg.formatMessage(ChatColor.GREEN + "The map has a name (" + map.getFullName() + ")"));
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "The map does not have a name!"));
			isValid = false;
		}

		if (!map.getAuthors().isEmpty()) {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.GREEN + "The map has at least 1 author"));
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "The map doesn't have any authors!"));
			isValid = false;
		}

		BedWarsTeam[] teams = BedWarsTeam.values();
		int teamsWithSpawns = 0;
		for (BedWarsTeam team : teams) {
			if (!TeamSpawnHandler.getSpawnLocations(team).isEmpty()) {
				teamsWithSpawns++;
			}
		}

		if (teamsWithSpawns < 3) {
			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.RED + "There are less than 3 teams with valid spawns (" + teamsWithSpawns + ")"));
			isValid = false;
		} else if (teamsWithSpawns < teams.length / 2) {
			player.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.YELLOW + "Less than half of teams have spawns (" + teamsWithSpawns + ")"));
		} else {
			player.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.GREEN + "There are " + teamsWithSpawns + " team(s) with valid spawns"));
		}

		if (!CurrencySpawnHandler.getSpawnerLocations().isEmpty()) {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.GREEN + "There is at least 1 currency spawner"));
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.RED + "There are no currency spawners set. Use /maps setresource <currency/team>"));
			isValid = false;
		}

		if (isValid) {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.GREEN + "This map is valid and can be used with /maps commit"));
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "This map is not valid, please see above for errors!"));
		}
	}

	private void commit(Player player) {
		MapImpl map = MapImpl.get(player.getWorld());
		if (map.isValid()) {
			try {
				boolean success = map.commitToFile();
				if (success) {
					player.sendMessage(ProtectTheEgg.formatMessage(
							ChatColor.GRAY + "The world has been " + ChatColor.GREEN + "successfully committed "
									+ ChatColor.GRAY + "to storage as an Protect The Egg game map!"));
				} else {
					player.sendMessage(ProtectTheEgg.formatMessage(
							ChatColor.RED + "An unexpected error occurred creating the data file!"));
				}
			} catch (FileAlreadyExistsException e) {
				e.printStackTrace();
				player.sendMessage(
						ChatColor.RED + "An internal storage error occurred: Unable to overwrite existing file");
			}
		} else {
			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.RED + "Your world is not a valid game map, consult '/maps info' for more information"));
		}
	}

}
