package uk.co.harieo.quackbedwars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.harieo.minigames.commands.CommandUtils;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.handlers.TeamHandler;

public class TeamSelectCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.RED + "What team would you like to join? Expected: /team <team>"));
		} else if (sender instanceof Player) {
			Player player = (Player) sender;

			String teamName = CommandUtils.concatenateArguments(args, 0);
			BedWarsTeam team = BedWarsTeam.getByName(teamName);
			if (team != null) {
				if (team.isFull()) {
					sender.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "That team is full at the moment!"));
				} else if (!team.isTeamActive()) {
					sender.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED
							+ "This team isn't available. Check the selection menu for available options!"));
				} else {
					TeamHandler.setTeam(player, team);
					sender.sendMessage(ProtectTheEgg
							.formatMessage(ChatColor.GRAY + "You have joined the " + team.getFormattedName()));
				}
			} else {
				sender.sendMessage(
						ProtectTheEgg.formatMessage(ChatColor.RED + "We didn't recognise that team: " + teamName));
			}
		} else {
			sender.sendMessage("You must be a player to do this!");
		}
		return false;
	}

}
