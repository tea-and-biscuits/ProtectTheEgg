package uk.co.harieo.quackbedwars.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamGameData;
import uk.co.harieo.quackbedwars.teams.handlers.TeamHandler;

public class EggStatusElement implements RenderableElement {

	@Override
	public String getText(Player player) {
		BedWarsTeam team = TeamHandler.getTeam(player);
		if (team != null) {
			TeamGameData gameData = TeamGameData.getGameData(team);
			return gameData.isEggIntact() ? ChatColor.GREEN + "Egg Intact" : ChatColor.RED + "Egg Smashed";
		} else {
			return ChatColor.WHITE + "No Team";
		}
	}

}
