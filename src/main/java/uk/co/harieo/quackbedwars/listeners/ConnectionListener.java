package uk.co.harieo.quackbedwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.timing.LobbyTimer;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.TeamHandler;

public class ConnectionListener implements Listener {

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		ProtectTheEgg plugin = ProtectTheEgg.getInstance();

		String kickReason = null;
		if (plugin.getGameStage() == GameStage.LOBBY && Bukkit.getOnlinePlayers().size() > plugin.getMaxPlayers()) {
			kickReason = "This game is full!";
		} else if (plugin.getGameStage() != GameStage.LOBBY && !player.hasPermission("quacktopia.minigames.join")) {
			kickReason = "This game has already started!";
		}

		if (kickReason != null) {
			event.disallow(Result.KICK_FULL, kickReason);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ProtectTheEgg plugin = ProtectTheEgg.getInstance();

		player.setGameMode(GameMode.SURVIVAL);
		player.setFoodLevel(20);
		player.setHealth(20);

		GameStage stage = plugin.getGameStage();
		if (stage == GameStage.LOBBY) {
			event.setJoinMessage(ProtectTheEgg.formatMessage(
					ChatColor.GREEN + player.getName() + ChatColor.GRAY + " is prepared to defend their egg!"));
			plugin.setLobbyScoreboard(player);
			// TODO teleport to lobby spawn

			int playerCount = Bukkit.getOnlinePlayers().size();
			updateTimer(plugin, playerCount);
		} else {
			event.setJoinMessage(null);
			player.setGameMode(GameMode.SPECTATOR);
			// TODO set game scoreboard

			player.sendMessage(
					ProtectTheEgg.formatMessage(ChatColor.GRAY + "You have joined mid-game so we've made you a spectator!"));
			Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.hidePlayer(plugin, player));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		ProtectTheEgg plugin = ProtectTheEgg.getInstance();

		event.setQuitMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " has flown away from the nest!");
		TeamHandler.unsetTeam(player);

		if (plugin.getGameStage() == GameStage.LOBBY) {
			int playerCount = Bukkit.getOnlinePlayers().size() - 1;
			updateTimer(plugin, playerCount);
		}
	}

	private void updateTimer(ProtectTheEgg plugin, int playerCount) {
		LobbyTimer timer = plugin.getLobbyTimer();
		if (playerCount >= plugin.getMaxPlayers()) {
			timer.updateToFull();
		} else if (playerCount >= plugin.getOptimalPlayers()) {
			timer.updateToOptimal();
		} else {
			timer.updateToInsufficient();
		}
	}

}
