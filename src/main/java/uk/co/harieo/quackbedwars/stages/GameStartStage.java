package uk.co.harieo.quackbedwars.stages;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;

import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.quackbedwars.scoreboard.TeamNameElement;

public class GameStartStage {

	private static final GameBoard teamStatusScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);

	static {
		teamStatusScoreboard.addBlankLine();
		teamStatusScoreboard.addLine(new TeamNameElement(true));
		// TODO egg status
		teamStatusScoreboard.addBlankLine();
		teamStatusScoreboard.addLine(new ConstantElement(ChatColor.GREEN + ChatColor.BOLD.toString() + "Your Kills"));
		// TODO kills stat
		teamStatusScoreboard.addBlankLine();
	}

}
