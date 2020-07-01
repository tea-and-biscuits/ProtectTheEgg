package uk.co.harieo.quackbedwars.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

public class TeamStatusElement implements RenderableElement {

	private final BedWarsTeam team;
	private final String teamName;

	public TeamStatusElement(BedWarsTeam team) {
		this.team = team;
		this.teamName = team.getChatColor().toString() + team.getTeamChar() + ChatColor.WHITE + ": " + team.getName();
	}

	@Override
	public String getText(Player player) {
		TeamGameData gameData = TeamGameData.getGameData(team);
		String status;
		if (gameData.isEggIntact()) {
			status = ChatColor.GREEN + ChatColor.BOLD.toString() + "✓";
		} else if (gameData.getPlayersAlive() > 0) {
			status = ChatColor.YELLOW + String.valueOf(gameData.getPlayersAlive());
		} else {
			status = ChatColor.RED + ChatColor.BOLD.toString() + "✗";
		}
		return teamName + ChatColor.WHITE + ": " + status;
	}

}
