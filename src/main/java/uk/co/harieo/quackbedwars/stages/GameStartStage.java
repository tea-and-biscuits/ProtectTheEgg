package uk.co.harieo.quackbedwars.stages;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;

import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.players.Statistic;
import uk.co.harieo.quackbedwars.scoreboard.EggStatusElement;
import uk.co.harieo.quackbedwars.scoreboard.StatisticsElement;
import uk.co.harieo.quackbedwars.scoreboard.TeamNameElement;

public class GameStartStage {

	private static final GameBoard teamStatusScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);

	static {
		teamStatusScoreboard.addBlankLine();
		teamStatusScoreboard.addLine(new TeamNameElement(true));
		teamStatusScoreboard.addLine(new EggStatusElement());
		teamStatusScoreboard.addBlankLine();
		teamStatusScoreboard.addLine(new ConstantElement(ChatColor.GREEN + ChatColor.BOLD.toString() + "Your Kills"));
		teamStatusScoreboard.addLine(new StatisticsElement(Statistic.KILLS));
		teamStatusScoreboard.addBlankLine();
		teamStatusScoreboard.addLine(ProtectTheEgg.IP_ELEMENT);
	}

}
