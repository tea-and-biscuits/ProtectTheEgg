package uk.co.harieo.quackbedwars.egg;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.Objects;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.players.Statistic;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamHandler;

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
		if (block.getType() == Material.DRAGON_EGG) {
			EggData eggData = EggData.getFromCachedBlock(block);
			if (eggData != null) {
				Player player = event.getPlayer();

				BedWarsTeam eggOwnerTeam = eggData.getTeam();
				BedWarsTeam destroyerTeam = TeamHandler.getTeam(player);
				if (eggOwnerTeam == destroyerTeam) { // Make sure this isn't friendly fire
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
					color = destroyerTeam.getChatColor();
				} else {
					color = ChatColor.GREEN;
				}

				Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
						color + player.getName() + ChatColor.GRAY + " has destroyed the " +
								eggOwnerTeam.getFormattedName() + "'s Egg"));
				Statistic.EGGS_BROKEN.addValue(player, 1);
			} else {
				ProtectTheEgg.getInstance().getLogger()
						.warning("A dragon egg was broken but it doesn't belong to any team");
			}
		}
	}

}
