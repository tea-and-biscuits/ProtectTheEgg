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
				String subCommand = args[0];
				if (subCommand.equalsIgnoreCase(SET_SPAWN)) {
					setSpawn(player, args);
				} else if (subCommand.equalsIgnoreCase(AUTHOR)) {
					author(player, args);
				} else if (subCommand.equalsIgnoreCase(INFO)) {
					info(player);
				} else if (subCommand.equalsIgnoreCase(SET_NAME)) {
					setName(player, args);
				} else if (subCommand.equalsIgnoreCase(COMMIT)) {
					commit(player);
				} else if (subCommand.equals(DELETE_SPAWN)) {
					deleteSpawn(player);
				} else {
					player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "Unrecognised sub-command: " + subCommand));
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
						pair.setValue(team.getSpawnKey()); // Replace it with this team
					}
				}
			} else {
				map.addLocation(location, TeamSpawnHandler.SPAWN_KEY, team.getSpawnKey());
			}

			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.GRAY + "Set current location to a " + team.getChatColor() + team.getName() + " "
							+ ChatColor.GRAY
							+ "spawn point!"));
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
				}
			}

			if (removedOnce) {
				player.sendMessage(ProtectTheEgg.formatMessage(
						ChatColor.GRAY + "The spawn at your location has been " + ChatColor.RED + "deleted"));
			} else {
				player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "There is no spawn marked at your location"));
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
						ProtectTheEgg.formatMessage(ChatColor.RED + "Neither add nor remove was specified: " + addRemove));
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
					ProtectTheEgg.formatMessage(ChatColor.RED + "Insufficient Arguments. Expected: /maps setname <name>"));
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

		boolean hasName = map.getFullName() != null;
		boolean hasAuthor = !map.getAuthors().isEmpty();
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
