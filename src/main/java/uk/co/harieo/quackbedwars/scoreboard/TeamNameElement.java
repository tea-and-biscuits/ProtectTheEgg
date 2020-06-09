package uk.co.harieo.quackbedwars.scoreboard;

import org.bukkit.entity.Player;

import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamHandler;

public class TeamNameElement implements RenderableElement {

	@Override
	public String getText(Player player) {
		BedWarsTeam team = TeamHandler.getTeam(player);
		return team == null ? "No Team" : team.getChatColor() + team.getName();
	}

}
