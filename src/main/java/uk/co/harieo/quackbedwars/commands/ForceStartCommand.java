package uk.co.harieo.quackbedwars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.timing.LobbyTimer;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.stages.GameStartStage;

public class ForceStartCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		ProtectTheEgg plugin = ProtectTheEgg.getInstance();
		if (!sender.hasPermission("quacktopia.minigames.force")) {
			sender.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "You do not have permission to do that!"));
		} else if (plugin.getGameStage() != GameStage.LOBBY) {
			sender.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "You may only do this in the lobby stage!"));
		} else {
			int seconds = 0;
			if (args.length > 0) {
				try {
					seconds = Integer.parseInt(args[0]);
				} catch (NumberFormatException ignored) {
				}
			}

			LobbyTimer lobbyTimer = plugin.getLobbyTimer();
			if (seconds == 0) {
				GameStartStage.startGame();
				lobbyTimer.pause();
				sender.sendMessage(ProtectTheEgg.formatMessage(
						ChatColor.GRAY + "The game has been " + ChatColor.RED + "forcefully started."));
			} else {
				lobbyTimer.forceTime(seconds);
				sender.sendMessage(ProtectTheEgg.formatMessage(
						ChatColor.GRAY + "You have set the lobby timer to " + ChatColor.YELLOW + seconds + " seconds. "
								+ ChatColor.RED + "This action is irreversible!"));
			}
		}
		return false;
	}

}
