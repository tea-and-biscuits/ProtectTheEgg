package uk.co.harieo.quackbedwars.egg;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.handlers.CurrencySpawnHandler;
import uk.co.harieo.quackbedwars.players.DeathTracker;
import uk.co.harieo.quackbedwars.players.Statistic;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

public class EggListener implements Listener {

	@EventHandler
	public void onEggTeleport(BlockFromToEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.DRAGON_EGG) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEggBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (isEgg(block)) {
			Player player = event.getPlayer();
			EggData eggData = EggData.getFromCachedBlock(block);

			PlayerBasedTeam eggOwnerTeam = eggData.getTeam();
			PlayerBasedTeam destroyerTeam = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);
			if (eggOwnerTeam.equals(destroyerTeam)) { // Make sure this isn't friendly fire
				event.setCancelled(true);
				return;
			} else {
				eggData.setIntact(false);
				event.setDropItems(false); // Prevents people picking up the egg
			}

			// Make a sound at the location to indicate the egg's destruction
			Location blockLocation = block.getLocation();
			World blockWorld = blockLocation.getWorld();
			if (blockWorld != null) {
				blockWorld.playSound(blockLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5F, 0.5F);
			}

			// Send a red title to the egg's team
			eggOwnerTeam.getMembers().stream()
					.map(Bukkit::getPlayer)
					.filter(Objects::nonNull)
					.forEach(member ->
							member.sendTitle(ChatColor.RED + "Egg Destroyed",
									ChatColor.GRAY + "You can no longer respawn!",
									20, 20 * 3, 20));

			ChatColor color;
			if (destroyerTeam != null) {
				color = destroyerTeam.getColour().getChatColor().asBungee();
			} else {
				color = ChatColor.GREEN;
			}

			Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
					color + player.getName() + ChatColor.GRAY + " has destroyed the " +
							eggOwnerTeam.getFormattedName() + "'s Egg"));
			Statistic.EGGS_BROKEN.addValue(player, 1);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		// If the block above the broken block is a known egg, prevent it being broken as eggs will fall
		if (isBelowEgg(event.getBlock())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockExplode(EntityExplodeEvent event) {
		if (event.getEntityType() == EntityType.PRIMED_TNT) {
			// Stop any block from being broken if it's within 5 blocks of an egg or 3 blocks of a resource spawner
			event.blockList().removeIf(block -> {
				Location blockLocation = block.getLocation();

				for (Block eggBlock : EggData.getCachedEggs().keySet()) {
					Location eggLocation = eggBlock.getLocation();
					if (block.equals(eggBlock) || (eggLocation.distance(blockLocation) <= 5 && blockLocation.getY() < eggLocation.getY())) {
						return true;
					}
				}

				for (Location location : CurrencySpawnHandler.getSpawnerLocations().keySet()) {
					if (location.distance(blockLocation) <= 3) {
						return true;
					}
				}

				return false;
			});
		}
	}

	@EventHandler (priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerBasedTeam team = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);
		if (team != null) {
			EggData eggData = TeamGameData.getGameData(team).getEggData();
			if (eggData.isIntact() && team.getOnlineMembers().size() <= 1) {
				eggData.setIntact(false);
				eggData.removeEgg();
				Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
						ChatColor.GRAY + "The " + team.getFormattedName() + ChatColor.GRAY
								+ " has abandoned the match!"));

				if (DeathTracker.isAlive(player)) {
					DeathTracker.unmarkAlive(player);
				}
			}
		}
	}

	/**
	 * Checks whether the provided block is both a dragon egg and is cached by {@link EggData}
	 *
	 * @param block to be checked
	 * @return whether the specified block is a cached dragon egg
	 */
	private boolean isEgg(Block block) {
		if (block.getType() == Material.DRAGON_EGG) {
			EggData eggData = EggData.getFromCachedBlock(block);
			if (eggData != null) {
				return true;
			} else {
				ProtectTheEgg.getInstance().getLogger()
						.warning("A dragon egg was broken but it doesn't belong to any team");
			}
		}

		return false;
	}

	/**
	 * Checks whether the specified block is directly underneath an egg
	 *
	 * @param block to check
	 * @return whether the block is below a registered egg
	 */
	private boolean isBelowEgg(Block block) {
		Location location = block.getLocation().clone().add(0, 1, 0);
		return EggData.getFromCachedBlock(location.getBlock()) != null;
	}

}
