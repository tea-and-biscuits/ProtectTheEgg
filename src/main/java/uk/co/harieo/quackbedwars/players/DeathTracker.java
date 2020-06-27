package uk.co.harieo.quackbedwars.players;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.stages.GameEndStage;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamGameData;
import uk.co.harieo.quackbedwars.teams.TeamHandler;
import uk.co.harieo.quackbedwars.teams.TeamSpawnHandler;

public class DeathTracker implements Listener {

	private static final Set<UUID> livingPlayers = new HashSet<>();
	private static final Set<UUID> spectators = new HashSet<>();

	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		Entity entityVictim = event.getEntity();
		Entity entityDamager = event.getDamager();
		if (entityVictim instanceof Player) {
			Player victim = (Player) event.getEntity();
			if (spectators.contains(victim.getUniqueId())) {
				event.setCancelled(true);
				return;
			}

			if (event.getFinalDamage() >= victim.getHealth()) { // If they're going to die
				event.setCancelled(true); // We will imitate death to prevent respawn locking

				String deathMessage;
				if (entityDamager instanceof Player) {
					Player damager = (Player) entityDamager;
					Statistic.KILLS.addValue(damager, 1);

					ChatColor color;
					BedWarsTeam damagerTeam = TeamHandler.getTeam(damager);
					if (damagerTeam != null) {
						color = damagerTeam.getChatColor();
					} else {
						color = ChatColor.YELLOW;
					}

					deathMessage = color + damager.getName() + ChatColor.GRAY + " has slain " + ChatColor.RED + victim
							.getName();
					damager.playSound(damager.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 0.5F);
				} else {
					deathMessage = ChatColor.RED + victim.getName() + ChatColor.GRAY + " has fallen in battle!";
				}

				Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(deathMessage));

				BedWarsTeam victimTeam = TeamHandler.getTeam(victim);
				if (victimTeam != null) {
					Location location = TeamSpawnHandler.getSpawn(victimTeam);
					if (location != null) {
						victim.teleport(location);
					}

					Statistic.DEATHS.addValue(victim, 1);
					if (TeamGameData.getGameData(victimTeam).isEggIntact()) {
						delayedRespawn(victim, location);
					} else {
						setSpectator(victim);
						victim.sendMessage(ProtectTheEgg.formatMessage(
								ChatColor.RED + "You have died without an egg! " + ChatColor.GRAY
										+ "We've put you into " + ChatColor.YELLOW + "Spectator Mode."));
						GameEndStage.checkForWinningTeam();
					}

					victim.playSound(victim.getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.5F, 0.5F);
				}
			}
		}
	}

	/**
	 * Sets a player into spectator mode then puts them back into survival mode 5 seconds later, a respawn delay
	 *
	 * @param player to be respawned
	 * @param respawnLocation where to teleport them after the 5 seconds
	 */
	private void delayedRespawn(Player player, Location respawnLocation) {
		setSpectator(player);
		int seconds = 5;
		player.sendMessage(ProtectTheEgg.formatMessage(
				ChatColor.YELLOW + "You are dead but your egg will resurrect you! " + ChatColor.GRAY + "Respawning in "
						+ seconds + " seconds."));
		Bukkit.getScheduler().runTaskLater(ProtectTheEgg.getInstance(), () -> {
			if (respawnLocation != null) {
				player.teleport(respawnLocation);
			}
			unsetSpectator(player);
		}, seconds * 20);
	}

	/**
	 * Makes a player hidden to all online players then sets them into spectator mode, removing them from the game
	 *
	 * @param player to set into spectator mode
	 */
	public static void setSpectator(Player player) {
		UUID uuid = player.getUniqueId();
		livingPlayers.remove(uuid);
		player.setGameMode(GameMode.SPECTATOR);
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer != player) {
				onlinePlayer.hidePlayer(ProtectTheEgg.getInstance(), player);
			}
		}
		spectators.add(uuid);
	}

	/**
	 * Shows a player to all online players then puts them into survival mode, returning them to the game
	 *
	 * @param player to set into survival mode
	 */
	public static void unsetSpectator(Player player) {
		UUID uuid = player.getUniqueId();
		player.setGameMode(GameMode.SURVIVAL);
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer != player) {
				onlinePlayer.showPlayer(ProtectTheEgg.getInstance(), player);
			}
		}
		spectators.remove(uuid);
		livingPlayers.add(uuid);
	}

	/**
	 * Adds a player to the list of living players
	 *
	 * @param player to be marked as alive
	 */
	public static void markAlive(Player player) {
		livingPlayers.add(player.getUniqueId());
	}

	/**
	 * Checks whether a player is marked as alive and in-game
	 *
	 * @param player to check
	 * @return if the player is alive
	 */
	public static boolean isAlive(Player player) {
		return livingPlayers.contains(player.getUniqueId());
	}

	/**
	 * @return a set of all living (and online) players
	 */
	public static Set<Player> getLivingPlayers() {
		return livingPlayers.stream()
				.map(Bukkit::getPlayer)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

}
