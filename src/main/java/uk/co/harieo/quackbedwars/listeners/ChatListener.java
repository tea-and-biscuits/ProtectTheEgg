package uk.co.harieo.quackbedwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.function.Predicate;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.players.DeathTracker;

public class ChatListener implements Listener {

	@EventHandler
	public void onAsyncChat(AsyncPlayerChatEvent event) {
		ProtectTheEgg plugin = ProtectTheEgg.getInstance();
		event.setCancelled(true);
		if (plugin.getGameStage() != GameStage.LOBBY && plugin.getGameConfig().getPlayersPerTeam() > 1) {
			formatForTeams(event, plugin);
		} else {
			Bukkit.broadcastMessage(formatPlayerMessage(event.getPlayer(), event.getMessage()));
		}
	}

	private void formatForTeams(AsyncPlayerChatEvent event, ProtectTheEgg plugin) {
		Player player = event.getPlayer();
		String message = event.getMessage();

		if (DeathTracker.isAlive(player)) {
			PlayerBasedTeam team = plugin.getTeamHandler().getTeam(player);
			String formattedMessage = formatPlayerMessage(player, message);

			if (message.startsWith("!")) {
				Bukkit.broadcastMessage(ChatColor.YELLOW + "Shout ｜ " + formattedMessage.replaceFirst("!", ""));
			} else {
				conditionalBroadcast(
						team.getColour().getChatColor() + "Team ｜ " + formattedMessage,
						team::isMember);
			}
		} else {
			conditionalBroadcast(ChatColor.GRAY + "[Spectator] " + player.getName() + " " + ChatColor.DARK_GRAY
							+ ProtectTheEgg.ARROWS + ChatColor.GRAY + " " + message,
					potentialPlayer -> !DeathTracker.isAlive(potentialPlayer));
		}
	}

	private void conditionalBroadcast(String message, Predicate<Player> shouldSend) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (shouldSend.test(player)) {
				player.sendMessage(message);
			}
		}
	}

	private String formatPlayerMessage(Player player, String message) {
		PlayerBasedTeam team = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);
		return (team != null ? team.getColour().getChatColor() + "[" + team.getName() + "] " : "") + ChatColor.WHITE + player.getName() + ChatColor.DARK_GRAY
				+ " " + ProtectTheEgg.ARROWS + " " + ChatColor.WHITE + message;
	}

}
