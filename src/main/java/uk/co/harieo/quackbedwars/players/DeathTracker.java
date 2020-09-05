package uk.co.harieo.quackbedwars.players;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.minigames.teams.TeamHandler;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.stages.GameEndStage;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

/**
 * A listener which tracks all damage being dealt in the game then handles the process of a player dying to that damage
 */
public class DeathTracker implements Listener {

	private static final Set<UUID> livingPlayers = new HashSet<>();
	private static final Set<UUID> spectators = new HashSet<>();
	private static final Set<UUID> playing = new HashSet<>();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (isInGame()) {
			Entity entityVictim = event.getEntity();
			if (entityVictim instanceof Player) {
				Player victim = (Player) entityVictim;
				Team victimTeam = ProtectTheEgg.getInstance().getTeamHandler().getTeam(victim);
				boolean isSpectator = spectators.contains(victim.getUniqueId());

				// If the victim isn't a spectator and this event will kill them
				if (!isSpectator && willKill(event, victim)) {
					event.setCancelled(true);

					// Broadcast death message (entity attack is dealt with by EntityDamageByEntityEvent handler)
					if (event.getCause() != DamageCause.ENTITY_ATTACK) {
						Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
								ChatColor.RED + victim.getName() + ChatColor.GRAY + " has fallen in battle!"));
					}

					// Respawn the player and update their statistics
					if (victimTeam != null) {
						Statistic.DEATHS.addValue(victim, 1);
						TeamGameData gameData = TeamGameData.getGameData(victimTeam);

						spillInventory(victim);
						teleportToNextSpawn(victim, victimTeam);
						if (gameData.isEggIntact()) {
							delayedRespawn(victim, victimTeam);
						} else {
							setSpectator(victim);
							victim.sendMessage(ProtectTheEgg.formatMessage(
									ChatColor.RED + "You have died without an egg! " + ChatColor.GRAY
											+ "We've put you into " + ChatColor.YELLOW + "Spectator Mode."));
							gameData.decrementPlayersAlive(); // This player is dead forever
							playing.remove(victim.getUniqueId());
							GameEndStage.checkForWinningTeam();
						}

						victim.playSound(victim.getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.5F, 0.5F);
					}
				} else if (isSpectator) {
					event.setCancelled(true);

					if (victimTeam != null) {
						teleportToNextSpawn(victim, victimTeam);
					} else {
						victim.teleport(victim.getWorld().getSpawnLocation());
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
				if (entityDamager instanceof Player) {
					Player damager = (Player) entityDamager;

					ChatColor color;
					TeamHandler<PlayerBasedTeam> teamHandler = ProtectTheEgg.getInstance().getTeamHandler();
					Team damagerTeam = teamHandler.getTeam(damager);
					Team victimTeam = teamHandler.getTeam(victim);

					if (damagerTeam != null && damagerTeam.equals(victimTeam)) { // If friendly fire
						event.setCancelled(true);
						return;
					}

					if (willKill(event, victim)) {
						if (damagerTeam != null) {
							color = damagerTeam.getColour().getChatColor().asBungee();
						} else {
							color = ChatColor.YELLOW;
						}

						Statistic.KILLS.addValue(damager, 1);
						damager.playSound(damager.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 0.5F);
						Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
								color + damager.getName() + ChatColor.GRAY + " has slain " + ChatColor.RED + victim
										.getName()));
					}
				}
			}
		}
	}

	private void teleportToNextSpawn(Player player, Team team) {
		Location location = team.getSpawns().getNextSpawn();
		if (location != null) {
			player.teleport(location);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null); // This should be handled above
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Team team = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);
		if (team != null) {
			event.setRespawnLocation(Objects.requireNonNull(team.getSpawns().getNextSpawn()));
		}
	}

	@EventHandler
	public void onSpectatorTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.SPECTATE) {
			event.setCancelled(true);
		}
	}

	/**
	 * Checks whether an {@link EntityDamageEvent} contains enough damage to kill a {@link LivingEntity}
	 *
	 * @param event which represents the damage being dealt
	 * @param victim who is taking the damage
	 * @return whether the event will kill the person based on its damage
	 */
	private boolean willKill(EntityDamageEvent event, LivingEntity victim) {
		return event.getFinalDamage() >= victim.getHealth();
	}

	/**
	 * @return whether this game is in the stage {@link GameStage#IN_GAME}
	 */
	private boolean isInGame() {
		return ProtectTheEgg.getInstance().getGameStage() == GameStage.IN_GAME;
	}

	/**
	 * Sets a player into spectator mode then puts them back into survival mode 5 seconds later, a respawn delay
	 *
	 * @param player to be respawned
	 * @param team the player's non-null team
	 */
	private void delayedRespawn(Player player, Team team) {
		setSpectator(player);
		int seconds = 5;
		player.sendMessage(ProtectTheEgg.formatMessage(
				ChatColor.YELLOW + "You are dead but your egg will resurrect you! " + ChatColor.GRAY + "Respawning in "
						+ seconds + " seconds."));
		Bukkit.getScheduler().runTaskLater(ProtectTheEgg.getInstance(), () -> {
			teleportToNextSpawn(player, team);
			unsetSpectator(player);
			player.setHealth(20);
		}, seconds * 20);
	}

	/**
	 * Drops all items in a player's inventory at their location then clears it
	 *
	 * @param player to spill the inventory of
	 */
	private void spillInventory(Player player) {
		Location deathLocation = player.getLocation().clone(); // Makes sure it doesn't change
		World world = player.getWorld();
		PlayerInventory inventory = player.getInventory();
		for (ItemStack item : inventory) {
			if (item != null && item.getType() != Material.WOODEN_SWORD) {
				world.dropItemNaturally(deathLocation, item);
			}
		}
		inventory.clear();
		inventory.addItem(new ItemStack(Material.WOODEN_SWORD));
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
		UUID uuid = player.getUniqueId();
		livingPlayers.add(uuid);
		playing.add(uuid);
		player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
	}

	/**
	 * Removes this player from the list of living players
	 *
	 * @param player which is no longer alive
	 */
	public static void unmarkAlive(Player player) {
		livingPlayers.remove(player.getUniqueId());
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
	 * Shows whether a player is still playing the game
	 *
	 * @param player to check
	 * @return whether they are in-game as a temporary spectator or living player
	 */
	public static boolean isPlaying(Player player) {
		return playing.contains(player.getUniqueId());
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
