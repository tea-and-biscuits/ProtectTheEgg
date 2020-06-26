package uk.co.harieo.quackbedwars.stages;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;

import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;

public class GameEndStage {

	private static final GameBoard endingScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);

	public static void declareWinningTeam(BedWarsTeam team) {
		// TODO
	}

}
