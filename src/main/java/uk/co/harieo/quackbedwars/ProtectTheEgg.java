package uk.co.harieo.quackbedwars;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.List;
import java.util.Random;
import uk.co.harieo.minigames.games.DefaultMinigame;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.games.Minigame;
import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.minigames.timing.LobbyTimer;
import uk.co.harieo.quackbedwars.commands.ForceStartCommand;
import uk.co.harieo.quackbedwars.commands.MapCommand;
import uk.co.harieo.quackbedwars.commands.TeamSelectCommand;
import uk.co.harieo.quackbedwars.config.GameConfig;
import uk.co.harieo.quackbedwars.config.GameWorldConfig;
import uk.co.harieo.quackbedwars.egg.EggListener;
import uk.co.harieo.quackbedwars.listeners.ConnectionListener;
import uk.co.harieo.quackbedwars.listeners.LobbyHotbarListener;
import uk.co.harieo.quackbedwars.listeners.WorldProtectionListener;
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
import uk.co.harieo.quackbedwars.teams.upgrades.currency.PurchasableCurrencyUpgrade;
import uk.co.harieo.quackbedwars.teams.upgrades.traps.PurchasableTraps;
import uk.co.harieo.quackbedwars.teams.upgrades.traps.TrapListener;

public class ProtectTheEgg extends DefaultMinigame {

	public static final char ARROWS = '»';
	public static final String PREFIX =
			ChatColor.GOLD.toString() + ChatColor.BOLD + "Protect the Egg " + ChatColor.DARK_GRAY + ARROWS + " ";
	public static final ConstantElement IP_ELEMENT = new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString()
			+ "  Quacktopia" + ChatColor.GRAY + ".com");
	public static final Random RANDOM = new Random();

	private static final GameBoard lobbyScoreboard = new GameBoard(
			ChatColor.GOLD + ChatColor.BOLD.toString() + "Protect the Egg", DisplaySlot.SIDEBAR);
	private static ProtectTheEgg instance;

	private GameStage stage = GameStage.STARTING;
	private GameConfig config;
	private LobbyTimer lobbyTimer;
	private int maxPlayers = 12;
	private boolean isDevelopmentMode = true;

	@Override
	public void onEnable() {
		instance = this;

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

		ShopMenu upgradesMenu = new ShopMenu(ShopType.UPGRADES, 1);
		ShopType.UPGRADES.setMenu(upgradesMenu);
		upgradesMenu.setStaticItem(0, PurchasableCurrencyUpgrade.getCategory());
		upgradesMenu.setStaticItem(1, PurchasableTraps.getCategory());

		setupScoreboard();
		registerListeners(new ConnectionListener(), new WorldProtectionListener(), new EggListener(),
				new DeathTracker(), new ShopNPCListener(), new TrapListener(), new PlayerEffects(), new LobbyHotbarListener(this));
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
		lobbyScoreboard.getTabListFactory().injectProcessor(BedWarsProcessor.INSTANCE);
	}

	public void setLobbyScoreboard(Player player) {
		lobbyScoreboard.render(this, player, 20);
	}

	public GameConfig getGameConfig() {
		return config;
	}

	public GameWorldConfig getGameWorldConfig() {
		return getGameConfig().getGameWorldConfig();
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

	@Override
	public GameStage getGameStage() {
		return stage;
	}

	public static ProtectTheEgg getInstance() {
		return instance;
	}

	public static String formatMessage(String message) {
		return PREFIX + message;
	}

	public static void updateTabListProcessors() {
		lobbyScoreboard.getTabListFactory().injectAllPlayers();
		GameStartStage.updateTabListHandler();
		GameEndStage.updateTabListHandler();
	}

}
