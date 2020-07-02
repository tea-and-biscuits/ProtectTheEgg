package uk.co.harieo.quackbedwars.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

public class EggStatusElement implements RenderableElement {

	@Override
	public String getText(Player player) {
		Team team = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);
		if (team != null) {
			TeamGameData gameData = TeamGameData.getGameData(team);
			return gameData.isEggIntact() ? ChatColor.GREEN + "Egg Intact" : ChatColor.RED + "Egg Smashed";
		} else {
			return ChatColor.WHITE + "No Team";
		}
	}

}
