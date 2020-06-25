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
import uk.co.harieo.minigames.timing.GameTimer;
import uk.co.harieo.minigames.timing.Timer;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnHandler;
import uk.co.harieo.quackbedwars.players.DeathTracker;
import uk.co.harieo.quackbedwars.players.PlayerEffects;
import uk.co.harieo.quackbedwars.players.Statistic;
import uk.co.harieo.quackbedwars.scoreboard.PlayersLeftElement;
import uk.co.harieo.quackbedwars.scoreboard.StatisticsElement;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamHandler;
import uk.co.harieo.quackbedwars.teams.TeamSpawnHandler;

public class GameStartStage {

	private static final GameBoard mainScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);
	private static final GameTimer gameTimer = new GameTimer(ProtectTheEgg.getInstance(), 20 * 60 * 20);

	static {
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(new ConstantElement(ChatColor.GREEN + ChatColor.BOLD.toString() + "Time Left"));
		mainScoreboard.addLine(gameTimer);
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Players Left"));
		mainScoreboard.addLine(new PlayersLeftElement());
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(new ConstantElement(ChatColor.GREEN + ChatColor.BOLD.toString() + "Your Kills"));
		mainScoreboard.addLine(new StatisticsElement(Statistic.KILLS));
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(ProtectTheEgg.IP_ELEMENT);

		gameTimer.setPrefix(ProtectTheEgg.PREFIX);
		gameTimer.setOnTimerTick(GameStartStage::onTick);
		// TODO onEnd
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
			DeathTracker.markAlive(player);
		}

		int seconds = 5;
		CurrencySpawnHandler.startSpawning(seconds * 20);
		new Timer(ProtectTheEgg.getInstance(), seconds)
				.setOnTimerEnd(end -> releasePlayers(plugin))
				.setOnTimerTick(tick -> {
					int secondsLeft = seconds - tick;
					if (secondsLeft <= 3 && secondsLeft > 0) {
						Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
								ChatColor.GRAY + "Dropping in " + ChatColor.YELLOW + secondsLeft + " second" + (
										secondsLeft != 1 ? "s" : "")));
						PlayerEffects.pingAll();
					}
				}).start();
	}

	private static void releasePlayers(ProtectTheEgg plugin) {
		plugin.setGameStage(GameStage.IN_GAME);
	}

	private static void onTick(int tick) {

	}

}
