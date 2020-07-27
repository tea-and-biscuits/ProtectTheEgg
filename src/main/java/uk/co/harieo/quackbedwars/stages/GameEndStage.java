package uk.co.harieo.quackbedwars.stages;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Collection;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.minigames.MinigamesCore;
import uk.co.harieo.minigames.events.MinigameEndEvent;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.minigames.timing.Timer;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.players.DeathTracker;
import uk.co.harieo.quackbedwars.players.Statistic;
import uk.co.harieo.quackbedwars.scoreboard.BedWarsProcessor;

/**
 * Handler for the end of game processes
 */
public class GameEndStage {

	private static final GameBoard endingScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);

	static {
		endingScoreboard.getTabListFactory().injectProcessor(new BedWarsProcessor());
	}

	/**
	 * Checks whether there is only 1 team standing, which is a win, and begins the end stage for that team with {@link
	 * #declareWinningTeam(PlayerBasedTeam)}. If a winner is found, the {@link ProtectTheEgg#getGameStage()} will be set
	 * to {@link GameStage#ENDING} to indicate this.
	 */
	public static void checkForWinningTeam() {
		PlayerBasedTeam lastTeamChecked = null;
		for (Player player : DeathTracker.getLivingPlayers()) {
			PlayerBasedTeam team = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);
			if (lastTeamChecked != null && lastTeamChecked != team) {
				return; // There's more than 1 team left
			}

			lastTeamChecked = team;
		}

		if (lastTeamChecked != null) {
			declareWinningTeam(lastTeamChecked);
			setEndingState();
		}
	}

	/**
	 * Declares a team as the winner of the game then begins to safely shutdown the server. This action cannot be
	 * reversed and will end the server.
	 *
	 * @param team who has won
	 */
	public static void declareWinningTeam(PlayerBasedTeam team) {
		sendWinnerMessagesAndScoreboard(team);

		FireworkEffect fireworkEffect = FireworkEffect.builder()
				.with(Type.BALL).withColor(team.getColour().getEquipmentColor()).trail(true).build();
		startFireworks(fireworkEffect, team.getOnlineMembers());

		startSelfDestruct();
	}

	/**
	 * Declares that nobody has won the game then begins to safely shutdown the server. This action cannot be
	 * reversed and will end the server.
	 */
	public static void forceDraw() {
		sendDrawMessagesAndScoreboard();

		FireworkEffect fireworkEffect = FireworkEffect.builder()
				.with(Type.BURST).withColor(Color.YELLOW).trail(true).build();
		startFireworks(fireworkEffect, DeathTracker.getLivingPlayers());

		startSelfDestruct();
	}

	/**
	 * Updates the tab list factory for the end of game scoreboard
	 */
	public static void updateTabListHandler() {
		endingScoreboard.getTabListFactory().injectAllPlayers();
	}

	/**
	 * Sends a message to all players and formats the ending scoreboard to show that the specified team has won,
	 * rendering it to everyone
	 *
	 * @param team who has won
	 */
	private static void sendWinnerMessagesAndScoreboard(PlayerBasedTeam team) {
		ChatColor color = team.getColour().getChatColor().asBungee();
		String boldColor = color + ChatColor.BOLD.toString();

		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(boldColor + team.getName() + " Team Wins!"));
		Player mostKillsPlayer = Statistic.KILLS.getHighestValuePlayer();
		if (mostKillsPlayer != null) {
			int kills = Statistic.KILLS.getValue(mostKillsPlayer);
			Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
					ChatColor.GRAY + "With " + ChatColor.YELLOW + kills + " Kills"
							+ ChatColor.GRAY + ", the " + ChatColor.GREEN + "MVP" + ChatColor.GRAY + " is "
							+ ChatColor.GOLD + mostKillsPlayer.getName()));
		} else {
			Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(ChatColor.GRAY + "To the victors, the spoils!"));
		}
		Bukkit.broadcastMessage("");

		endingScoreboard.addBlankLine();
		endingScoreboard.addLine(new ConstantElement(color + team.getName() + " Team"));
		endingScoreboard.addLine(new ConstantElement("has Won!"));
		endingScoreboard.addBlankLine();

		Set<Player> onlinePlayers = team.getOnlineMembers();
		int iterations = 0; // Can't use an incremental for loop with a set
		for (Player player : onlinePlayers) {
			if (iterations < 9) {
				endingScoreboard.addLine(new ConstantElement(player.getName()));
				iterations++;
			} else {
				break;
			}
		}

		endingScoreboard.addBlankLine();
		endingScoreboard.addLine(ProtectTheEgg.IP_ELEMENT);
		showScoreboard();
	}

	/**
	 * Sends a message to all online players and formats the scoreboard to indicate that nobody has won this game.
	 * The scoreboard will display players with the highest statistics rather than a winner.
	 */
	private static void sendDrawMessagesAndScoreboard() {
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(
				ProtectTheEgg.formatMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "The Game is a Draw!"));
		Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(ChatColor.GRAY + "Better luck next time!"));
		Bukkit.broadcastMessage("");

		endingScoreboard.addBlankLine();
		endingScoreboard.addLine(new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString() + "It's a Draw"));
		endingScoreboard.addLine(new ConstantElement(ChatColor.GRAY + "This was not the game..."));
		endingScoreboard.addBlankLine();

		Player mostEggsDestroyed = Statistic.EGGS_BROKEN.getHighestValuePlayer();
		endingScoreboard.addLine(new ConstantElement(ChatColor.GREEN + ChatColor.BOLD.toString() + "Most Eggs Broken"));
		if (mostEggsDestroyed != null) {
			int eggsDestroyed = Statistic.EGGS_BROKEN.getValue(mostEggsDestroyed);
			endingScoreboard.addLine(new ConstantElement(
					ChatColor.WHITE + mostEggsDestroyed.getName() + ChatColor.GRAY + " (" + eggsDestroyed + ")"));
		} else {
			endingScoreboard.addLine(new ConstantElement(ChatColor.WHITE + "Nobody"));
		}
		endingScoreboard.addBlankLine();

		Player mostKills = Statistic.KILLS.getHighestValuePlayer();
		endingScoreboard.addLine(new ConstantElement(ChatColor.GOLD + ChatColor.BOLD.toString() + "Most Kills"));
		if (mostKills != null) {
			int kills = Statistic.KILLS.getValue(mostKills);
			endingScoreboard.addLine(
					new ConstantElement(ChatColor.WHITE + mostKills.getName() + ChatColor.GRAY + " (" + kills + ")"));
		} else {
			endingScoreboard.addLine(new ConstantElement(ChatColor.WHITE + "Nobody"));
		}
		endingScoreboard.addBlankLine();
		endingScoreboard.addLine(ProtectTheEgg.IP_ELEMENT);
	}

	/**
	 * Shows the ending scoreboard to all online players
	 */
	private static void showScoreboard() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			endingScoreboard.render(ProtectTheEgg.getInstance(), player, 20 * 5);
		}
	}

	/**
	 * Sets the state of this minigame to ending
	 */
	private static void setEndingState() {
		ProtectTheEgg plugin = ProtectTheEgg.getInstance();
		plugin.setGameStage(GameStage.ENDING);
		Bukkit.getPluginManager().callEvent(new MinigameEndEvent(plugin));
	}

	/**
	 * Spawns a firework at the location of each of the specified players, if they are online, every 3 seconds
	 *
	 * @param effect for the firework
	 * @param forPlayers list of players to spawn the firework at, if they are online
	 */
	private static void startFireworks(FireworkEffect effect, Collection<Player> forPlayers) {
		Bukkit.getScheduler().runTaskTimer(ProtectTheEgg.getInstance(), () -> {
			for (Player player : forPlayers) {
				if (player.isOnline()) {
					Firework firework = (Firework) player.getWorld()
							.spawnEntity(player.getLocation(), EntityType.FIREWORK);
					FireworkMeta meta = firework.getFireworkMeta();
					meta.setPower(1);
					meta.addEffect(effect);
					firework.setFireworkMeta(meta);
				}
			}
		}, 0, 20 * 3);
	}

	/**
	 * Starts a 15 second timer which will send all players to a fallback server at 10 seconds then end the server at
	 * the end of the time.
	 */
	private static void startSelfDestruct() {
		ProtectTheEgg plugin = ProtectTheEgg.getInstance();
		new Timer(plugin, 20 * 15)
				.setOnTimerTick(tick -> {
					if (tick == 10) {
						MinigamesCore.sendAllPlayersToFallbackServer();
					}
				})
				.setOnTimerEnd(end -> {
					plugin.getGameWorldConfig().unloadTemporaryWorld(plugin);
					Bukkit.shutdown();
				})
				.start();
	}

}
