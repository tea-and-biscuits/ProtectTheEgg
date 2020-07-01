package uk.co.harieo.quackbedwars.players;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.stages.GameEndStage;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamGameData;
import uk.co.harieo.quackbedwars.teams.handlers.TeamHandler;
import uk.co.harieo.quackbedwars.teams.handlers.TeamSpawnHandler;

public class DeathTracker implements Listener {

	private static final Set<UUID> livingPlayers = new HashSet<>();
	private static final Set<UUID> spectators = new HashSet<>();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (isInGame()) {
			Entity entityVictim = event.getEntity();
			if (entityVictim instanceof Player) {
				Player victim = (Player) entityVictim;
				if (!spectators.contains(victim.getUniqueId()) && willKill(event, victim)) {
					event.setCancelled(true);

					// Broadcast death message (entity attack is dealt with by EntityDamageByEntityEvent handler)
					if (event.getCause() != DamageCause.ENTITY_ATTACK) {
						Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
								ChatColor.RED + victim.getName() + ChatColor.GRAY + " has fallen in battle!"));
					}

					// Respawn the player and update their statistics
					BedWarsTeam victimTeam = TeamHandler.getTeam(victim);
					if (victimTeam != null) {
						Location location = TeamSpawnHandler.getSpawn(victimTeam);
						if (location != null) {
							victim.teleport(location);
						}

						Statistic.DEATHS.addValue(victim, 1);
						TeamGameData gameData = TeamGameData.getGameData(victimTeam);

						if (gameData.isEggIntact()) {
							delayedRespawn(victim, location);
						} else {
							setSpectator(victim);
							victim.sendMessage(ProtectTheEgg.formatMessage(
									ChatColor.RED + "You have died without an egg! " + ChatColor.GRAY
											+ "We've put you into " + ChatColor.YELLOW + "Spectator Mode."));
							gameData.decrementPlayersAlive(); // This player is dead forever
							GameEndStage.checkForWinningTeam();
						}

						victim.playSound(victim.getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.5F, 0.5F);
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if (isInGame()) {
			Entity entityVictim = event.getEntity();
			Entity entityDamager = event.getDamager();
			if (entityVictim instanceof Player) {
				Player victim = (Player) entityVictim;
				if (willKill(event, victim)) { // If they're going to die
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

						damager.playSound(damager.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 0.5F);
						Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
								color + damager.getName() + ChatColor.GRAY + " has slain " + ChatColor.RED + victim
										.getName()));
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		BedWarsTeam team = TeamHandler.getTeam(player);
		if (team != null) {
			player.teleport(Objects.requireNonNull(TeamSpawnHandler.getSpawn(team)));
		}
	}

	private boolean willKill(EntityDamageEvent event, LivingEntity victim) {
		return event.getFinalDamage() >= victim.getHealth();
	}

	private boolean isInGame() {
		return ProtectTheEgg.getInstance().getGameStage() == GameStage.IN_GAME;
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
			player.setHealth(20);
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
