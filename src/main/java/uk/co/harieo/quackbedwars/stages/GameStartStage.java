package uk.co.harieo.quackbedwars.stages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import uk.co.harieo.quackbedwars.currency.handlers.CurrencySpawnHandler;
import uk.co.harieo.quackbedwars.currency.spawners.TeamSpawner;
import uk.co.harieo.quackbedwars.egg.EggData;
import uk.co.harieo.quackbedwars.players.DeathTracker;
import uk.co.harieo.quackbedwars.players.PlayerEffects;
import uk.co.harieo.quackbedwars.players.Statistic;
import uk.co.harieo.quackbedwars.scoreboard.PlayersLeftElement;
import uk.co.harieo.quackbedwars.scoreboard.StatisticsElement;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamGameData;
import uk.co.harieo.quackbedwars.teams.handlers.TeamHandler;
import uk.co.harieo.quackbedwars.teams.handlers.TeamSpawnHandler;

public class GameStartStage {

	private static final GameBoard mainScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);
	private static final GameTimer gameTimer = new GameTimer(ProtectTheEgg.getInstance(), 60 * 20);

	static {
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(new ConstantElement(ChatColor.GREEN + ChatColor.BOLD.toString() + "Time Left"));
		mainScoreboard.addLine(gameTimer);
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Players Left"));
		mainScoreboard.addLine(new PlayersLeftElement());
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(new ConstantElement(ChatColor.GOLD + ChatColor.BOLD.toString() + "Your Kills"));
		mainScoreboard.addLine(new StatisticsElement(Statistic.KILLS));
		mainScoreboard.addBlankLine();
		mainScoreboard.addLine(ProtectTheEgg.IP_ELEMENT);

		gameTimer.setPrefix(ProtectTheEgg.PREFIX);
		gameTimer.setOnTimerTick(GameStartStage::onTick);
		gameTimer.setOnTimerEnd(end -> GameEndStage.forceDraw());
	}

	public static void startGame() {
		ProtectTheEgg plugin = ProtectTheEgg.getInstance();
		plugin.setGameStage(GameStage.PRE_GAME);
		MinigamesCore.setAcceptingPlayers(false);

		// Announce the map name and author to give them credit
		MapImpl gameMap = plugin.getGameWorldConfig().getGameMap();
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
				ChatColor.GRAY + "You are playing on " + ChatColor.YELLOW + gameMap.getFullName() + ChatColor.GRAY
						+ " by "
						+ ChatColor.GREEN + gameMap.getAuthorsString()));
		Bukkit.broadcastMessage("");

		for (Player player : Bukkit.getOnlinePlayers()) {
			assignPlayerTeam(plugin, player); // Assign a team
			DeathTracker.markAlive(player); // Mark the player as in the game
			showScoreboard(player); // Show the scoreboard
		}

		// Activate team assets if the team has at least 1 member playing in it
		for (BedWarsTeam team : BedWarsTeam.values()) {
			if (team.getOnlineMembers().size() > 0) {
				// Activate team's spawner
				TeamSpawner spawner = TeamSpawner.getCachedSpawner(team);
				if (spawner != null) {
					spawner.setActive(true);
				}

				// Set team's egg
				EggData eggData = TeamGameData.getGameData(team).getEggData();
				if (eggData != null) {
					eggData.setBlockMaterial();
				}
			}
		}

		// Starting spawning items and release players after small delay
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

	/**
	 * Sets the game to in-game, starts the timer then frees players into the game
	 *
	 * @param plugin which is running this game
	 */
	private static void releasePlayers(ProtectTheEgg plugin) {
		plugin.setGameStage(GameStage.IN_GAME);
		gameTimer.start();
		PregameCages.deleteCages();
	}

	/**
	 * Broadcasts when time is running out at certain intervals
	 *
	 * @param tick on the timer
	 */
	private static void onTick(int tick) {
		int secondsLeft = gameTimer.getSecondsLeft();
		if (secondsLeft == 60 * 2) {
			Bukkit.broadcastMessage(ProtectTheEgg
					.formatMessage(ChatColor.GRAY + "There are only " + ChatColor.YELLOW + "2 minutes left!"));
		} else if (secondsLeft == 60) {
			Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
					ChatColor.GRAY + "The game is almost over, only " + ChatColor.GOLD + "1 minute left!"));
		} else if (secondsLeft == 30 || (secondsLeft > 0 && secondsLeft <= 10)) {
			Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
					ChatColor.GRAY + "The game will be " + ChatColor.RED + "a draw " + ChatColor.GRAY + "in "
							+ secondsLeft + " seconds!"));
		}
	}

	/**
	 * Assigns a {@link BedWarsTeam} to a player if they don't already have one then teleports them to that team's
	 * spawn
	 *
	 * @param plugin which is starting the game
	 * @param player to assign a team to
	 */
	private static void assignPlayerTeam(ProtectTheEgg plugin, Player player) {
		BedWarsTeam team = TeamHandler.getTeam(player);
		if (team == null) {
			team = TeamHandler.assignTeam(player);
			if (team == null) {
				player.kickPlayer(ChatColor.RED + "An error has occurred assigning you a team!");
				plugin.getLogger().severe("Failed to assign " + player.getName() + " a team");
				return; // Can't proceed to spawn if the team is null
			} else {
				player.sendMessage(ProtectTheEgg.formatMessage(
						ChatColor.GRAY + "You have been auto-magically assigned to the " + team.getChatColor()
								+ team.getName() + " Team"));
			}
		}

		Location blockSpawn = Objects
				.requireNonNull(TeamSpawnHandler.getSpawn(team), "No spawn available for " + team.getName() + " team");
		Location centeredSpawn = new Location(blockSpawn.getWorld(), blockSpawn.getX() + 0.5, blockSpawn.getY(),
				blockSpawn.getZ() + 0.5);
		PregameCages.createCage(centeredSpawn);
		player.teleport(centeredSpawn);
		PlayerEffects.giveOneTimeFallImmunity(player); // Prevent them taking fall damage when the cage is deleted
	}

	/**
	 * Shows the in-game scoreboard to a player
	 */
	public static void showScoreboard(Player player) {
		mainScoreboard.render(ProtectTheEgg.getInstance(), player, 20);
	}

}
