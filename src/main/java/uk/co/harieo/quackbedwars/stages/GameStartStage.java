package uk.co.harieo.quackbedwars.stages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Objects;
import uk.co.harieo.minigames.MinigamesCore;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnHandler;
import uk.co.harieo.quackbedwars.players.Statistic;
import uk.co.harieo.quackbedwars.scoreboard.EggStatusElement;
import uk.co.harieo.quackbedwars.scoreboard.StatisticsElement;
import uk.co.harieo.quackbedwars.scoreboard.TeamNameElement;
import uk.co.harieo.quackbedwars.scoreboard.TeamStatusElement;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamHandler;
import uk.co.harieo.quackbedwars.teams.TeamSpawnHandler;

public class GameStartStage {

	private static final GameBoard mainScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);
	private static final GameBoard teamStatusScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);

	static {
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(new TeamNameElement(true));
		mainScoreboard.addLine(new EggStatusElement());
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(new ConstantElement(ChatColor.GREEN + ChatColor.BOLD.toString() + "Your Kills"));
		mainScoreboard.addLine(new StatisticsElement(Statistic.KILLS));
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(ProtectTheEgg.IP_ELEMENT);

		teamStatusScoreboard.addBlankLine();
		teamStatusScoreboard.addLine(new ConstantElement(ChatColor.GREEN + ChatColor.BOLD.toString() + "Teams"));
		BedWarsTeam[] teams = BedWarsTeam.values();
		for (int i = 0; i < 10 && i < teams.length; i++) {
			teamStatusScoreboard.addLine(new TeamStatusElement(teams[i]));
		}
		teamStatusScoreboard.addBlankLine();
		teamStatusScoreboard.addLine(ProtectTheEgg.IP_ELEMENT);
	}

	public static void startGame() {
		ProtectTheEgg plugin = ProtectTheEgg.getInstance();
		plugin.setGameStage(GameStage.PRE_GAME);
		MinigamesCore.setAcceptingPlayers(false);

		MapImpl gameMap = plugin.getGameWorldConfig().getGameMap();
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
				ChatColor.GRAY + "You are playing on " + ChatColor.YELLOW + gameMap.getFullName() + ChatColor.GRAY
						+ " by "
						+ ChatColor.GREEN + gameMap.getAuthorsString()));
		Bukkit.broadcastMessage("");

		for (Player player : Bukkit.getOnlinePlayers()) {
			BedWarsTeam team = TeamHandler.getTeam(player);
			if (team == null) {
				team = TeamHandler.assignTeam(player);
				if (team == null) {
					player.kickPlayer(ChatColor.RED + "An error has occurred assigning you a team!");
					plugin.getLogger().severe("Failed to assign " + player.getName() + " a team");
					continue;
				} else {
					player.sendMessage(ProtectTheEgg.formatMessage(
							ChatColor.GRAY + "You have been auto-magically assigned to the " + team.getChatColor()
									+ team.getName() + " Team"));
				}
			}

			player.teleport(Objects.requireNonNull(TeamSpawnHandler.getSpawn(team),
					"No spawn available for " + team.getName() + " team"));
		}

		activateScoreboards();
	}

	private static void releasePlayers(ProtectTheEgg plugin) {
		plugin.setGameStage(GameStage.IN_GAME);
		CurrencySpawnHandler.startSpawning();
	}

	private static int scoreboardSwitchTimer = 0;

	private static void activateScoreboards() {
		Bukkit.getScheduler().runTaskTimer(ProtectTheEgg.getInstance(), () -> {
			GameBoard toCancel = null;
			GameBoard toSwitchTo = null;
			if (scoreboardSwitchTimer == 5) {
				toCancel = mainScoreboard;
				toSwitchTo = teamStatusScoreboard;
			} else if (scoreboardSwitchTimer == 0) {
				toCancel = teamStatusScoreboard;
				toSwitchTo = mainScoreboard;
			} else if (scoreboardSwitchTimer == 7) {
				scoreboardSwitchTimer = 0;
				return;
			}

			if (toCancel != null) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					toCancel.cancelScoreboard(player);
					toSwitchTo.render(ProtectTheEgg.getInstance(), player, 20);
				}
			}

			scoreboardSwitchTimer++;
		}, 20, 20);
	}

}
