package uk.co.harieo.quackbedwars;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.List;
import java.util.Random;

import uk.co.harieo.minigames.games.DefaultMinigame;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.minigames.teams.TeamHandler;
import uk.co.harieo.minigames.timing.LobbyTimer;
import uk.co.harieo.quackbedwars.commands.ForceStartCommand;
import uk.co.harieo.quackbedwars.commands.MapCommand;
import uk.co.harieo.quackbedwars.commands.TeamSelectCommand;
import uk.co.harieo.quackbedwars.config.GameConfig;
import uk.co.harieo.quackbedwars.config.GameWorldConfig;
import uk.co.harieo.quackbedwars.egg.EggListener;
import uk.co.harieo.quackbedwars.listeners.*;
import uk.co.harieo.quackbedwars.players.DeathTracker;
import uk.co.harieo.quackbedwars.players.PlayerEffects;
import uk.co.harieo.quackbedwars.scoreboard.BedWarsProcessor;
import uk.co.harieo.quackbedwars.scoreboard.PlayerCountElement;
import uk.co.harieo.quackbedwars.scoreboard.TeamNameElement;
import uk.co.harieo.quackbedwars.shops.ShopMenu;
import uk.co.harieo.quackbedwars.shops.ShopNPCListener;
import uk.co.harieo.quackbedwars.shops.ShopType;
import uk.co.harieo.quackbedwars.stages.GameEndStage;
import uk.co.harieo.quackbedwars.stages.GameStartStage;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;
import uk.co.harieo.quackbedwars.teams.upgrades.currency.PurchasableCurrencyUpgrade;
import uk.co.harieo.quackbedwars.teams.upgrades.effects.PurchasablePotionEffect;
import uk.co.harieo.quackbedwars.teams.upgrades.traps.PurchasableTraps;
import uk.co.harieo.quackbedwars.teams.upgrades.traps.TrapListener;

public class ProtectTheEgg extends DefaultMinigame {

	public static final char ARROWS = '»';
	public static final String PREFIX =
			ChatColor.GOLD.toString() + ChatColor.BOLD + "Protect the Egg " + ChatColor.DARK_GRAY + ARROWS + " ";
	public static final ConstantElement IP_ELEMENT = new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString()
			+ "Quacktopia" + ChatColor.GRAY + ".com");
	public static final Random RANDOM = new Random();

	private static final GameBoard lobbyScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);
	private static ProtectTheEgg instance;

	private GameConfig config;
	private LobbyTimer lobbyTimer;
	private TeamHandler<PlayerBasedTeam> teamHandler;
	private int maxPlayers = 12;
	private boolean isDevelopmentMode = true;

	@Override
	public void onEnable() {
		instance = this;
		setGameStage(GameStage.STARTING);

		config = new GameConfig(this);
		if (getGameWorldConfig().isLoaded()) {
			isDevelopmentMode = false;
			getLogger().info("All worlds have been successfully loaded!");
		}

		maxPlayers = config.getPlayersPerTeam() * config.getMaxTeams();
		lobbyTimer = new LobbyTimer(this);
		List<String> messages = config.getTimerMessages();
		if (messages.isEmpty()) {
			getLogger().warning("No timer messages found in config.yml, using default");
		} else {
			lobbyTimer.setCountdownMessages(messages);
		}
		lobbyTimer.setPrefix(PREFIX);
		lobbyTimer.setOnTimerEnd(end -> GameStartStage.startGame());

		teamHandler = new TeamHandler<>(BedWarsTeamData.allTeams, getGameConfig().getPlayersPerTeam());

		// Set the upgrade menu for all available TeamUpgrade instances
		ShopMenu upgradesMenu = new ShopMenu(ShopType.UPGRADES, 1);
		ShopType.UPGRADES.setMenu(upgradesMenu);
		upgradesMenu.setStaticItem(0, PurchasableCurrencyUpgrade.getCategory());
		upgradesMenu.setStaticItem(1, PurchasablePotionEffect.getCategory());
		upgradesMenu.setStaticItem(2, PurchasableTraps.getCategory());

		setupScoreboard();
		registerListeners(new ConnectionListener(), new WorldProtectionListener(), new EggListener(),
				new DeathTracker(), new ShopNPCListener(), new TrapListener(), new PlayerEffects(),
				new LobbyHotbarListener(this), new ChatListener(), new FeatureListener());
		registerCommand(new MapCommand(), "map", "maps");
		registerCommand(new ForceStartCommand(), "force", "forcestart");
		registerCommand(new TeamSelectCommand(), "team");
		setGameStage(isDevelopmentMode ? GameStage.ERROR : GameStage.LOBBY);
	}

	@Override
	public void onDisable() {
		getLogger().info("Deleting temporary game world...");
		getGameWorldConfig().unloadTemporaryWorld(this);
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

	/**
	 * Formats the lobby scoreboard based on whether this game is is in development mode or not
	 */
	private void setupScoreboard() {
		lobbyScoreboard.addBlankLine();
		lobbyScoreboard.addLine(new ConstantElement(ChatColor.GREEN + ChatColor.BOLD.toString() + "Players"));
		lobbyScoreboard.addLine(new PlayerCountElement());
		lobbyScoreboard.addBlankLine();
		if (isDevelopmentMode) {
			lobbyScoreboard.addLine(new ConstantElement(ChatColor.RED + ChatColor.BOLD.toString() + "Server Error"));
			lobbyScoreboard.addLine(new ConstantElement(ChatColor.WHITE + "Development Mode"));
		} else {
			lobbyScoreboard.addLine(new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Time Left"));
			lobbyScoreboard.addLine(lobbyTimer);
			lobbyScoreboard.addBlankLine();
			lobbyScoreboard.addLine(new ConstantElement(ChatColor.GOLD + ChatColor.BOLD.toString() + "Your Team"));
			lobbyScoreboard.addLine(new TeamNameElement(false));
		}
		lobbyScoreboard.addBlankLine();
		lobbyScoreboard.addLine(IP_ELEMENT);
		lobbyScoreboard.getTabListFactory().injectProcessor(new BedWarsProcessor());
	}

	/**
	 * Shows the lobby scoreboard to the specified player, refreshing once every 20 ticks
	 *
	 * @param player to show the scoreboard to
	 */
	public void setLobbyScoreboard(Player player) {
		lobbyScoreboard.render(this, player, 20);
	}

	/**
	 * @return the parsed configurable settings for this game
	 */
	public GameConfig getGameConfig() {
		return config;
	}

	/**
	 * @return the loaded configuration for both the lobby and game world
	 */
	public GameWorldConfig getGameWorldConfig() {
		return getGameConfig().getGameWorldConfig();
	}

	/**
	 * @return the timer which counts down to the {@link GameStartStage}
	 */
	public LobbyTimer getLobbyTimer() {
		return lobbyTimer;
	}

	/**
	 * @return the team handler with all teams for this game loaded into it ({@link BedWarsTeamData#allTeams})
	 */
	public TeamHandler<PlayerBasedTeam> getTeamHandler() {
		return teamHandler;
	}

	/**
	 * @return the enabled instance of this class
	 */
	public static ProtectTheEgg getInstance() {
		return instance;
	}

	/**
	 * Takes a message and adds the game's prefix
	 *
	 * @param message to add a prefix to
	 * @return the formatted message
	 */
	public static String formatMessage(String message) {
		return PREFIX + message;
	}

	/**
	 * Updates all tab list factories in the 3 game stages (lobby, in-game and end-game scoreboards)
	 */
	public static void updateTabListProcessors() {
		lobbyScoreboard.getTabListFactory().injectAllPlayers();
		GameStartStage.updateTabListHandler();
		GameEndStage.updateTabListHandler();
	}

}
