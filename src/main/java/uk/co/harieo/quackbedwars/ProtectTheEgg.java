package uk.co.harieo.quackbedwars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.games.Minigame;
import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.minigames.timing.LobbyTimer;
import uk.co.harieo.quackbedwars.config.GameConfig;
import uk.co.harieo.quackbedwars.scoreboard.PlayerCountElement;
import uk.co.harieo.quackbedwars.scoreboard.TeamNameElement;

public class ProtectTheEgg extends Minigame {

	public static final char ARROWS = '»';
	public static final String PREFIX =
			ChatColor.GOLD.toString() + ChatColor.BOLD + "Protect the Egg " + ChatColor.DARK_GRAY + ARROWS + " ";
	public static final ConstantElement IP_ELEMENT = new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString()
			+ "Quacktopia" + ChatColor.GRAY + ChatColor.BOLD + ".com");

	private static final GameBoard lobbyScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);
	private static ProtectTheEgg instance;

	private GameStage stage = GameStage.STARTING;
	private GameConfig config;
	private LobbyTimer lobbyTimer;
	private int maxPlayers = 12;

	@Override
	public void onEnable() {
		instance = this;

		config = new GameConfig(this);
		maxPlayers = config.getPlayersPerTeam() * config.getMaxTeams();

		lobbyTimer = new LobbyTimer(this);
		setupScoreboard();
	}

	private void setupScoreboard() {
		lobbyScoreboard.addBlankLine();
		lobbyScoreboard.addLine(new ConstantElement(ChatColor.GREEN + ChatColor.BOLD.toString() + "Players"));
		lobbyScoreboard.addLine(new PlayerCountElement());
		lobbyScoreboard.addBlankLine();
		lobbyScoreboard.addLine(new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Time Left"));
		lobbyScoreboard.addLine(lobbyTimer);
		lobbyScoreboard.addBlankLine();
		lobbyScoreboard.addLine(new ConstantElement(ChatColor.RED + ChatColor.BOLD.toString() + "Your Team"));
		lobbyScoreboard.addLine(new TeamNameElement());
		lobbyScoreboard.addBlankLine();
		lobbyScoreboard.addLine(IP_ELEMENT);
	}

	public void setLobbyScoreboard(Player player) {
		lobbyScoreboard.render(this, player, 20);
	}

	public GameConfig getGameConfig() {
		return config;
	}

	public GameStage getGameStage() {
		return stage;
	}

	public void setGameStage(GameStage gameStage) {
		this.stage = gameStage;
	}

	public LobbyTimer getLobbyTimer() {
		return lobbyTimer;
	}

	@Override
	public String getMinigameName() {
		return "Protect the Egg";
	}

	@Override
	public int getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public int getOptimalPlayers() {
		return maxPlayers / 2;
	}

	public static ProtectTheEgg getInstance() {
		return instance;
	}

	public static String formatMessage(String message) {
		return PREFIX + message;
	}

}
