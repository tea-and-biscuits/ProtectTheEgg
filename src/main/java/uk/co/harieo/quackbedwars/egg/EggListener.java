package uk.co.harieo.quackbedwars.egg;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.players.Statistic;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamHandler;

public class EggListener implements Listener {

	@EventHandler
	public void onEggInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block != null && block.getType() == Material.DRAGON_EGG) {
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
				eggData.setIntact(false);

				ChatColor color;
				BedWarsTeam team = TeamHandler.getTeam(player);
				if (team != null) {
					color = team.getChatColor();
					team.getMembers().stream()
							.map(Bukkit::getPlayer)
							.filter(Objects::nonNull)
							.forEach(member -> {
								member.sendTitle(ChatColor.RED + "Egg Destroyed",
										ChatColor.GRAY + "You can no longer respawn!",
										20, 20 * 3, 20);
								member.playSound(member.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.25F, 0.25F);
							});
				} else {
					color = ChatColor.GREEN;
				}

				Bukkit.broadcastMessage(ProtectTheEgg.formatMessage(
						color + player.getName() + ChatColor.GRAY + " has destroyed the " + eggData.getTeam()
								.getFormattedName() + "'s Egg"));
				Statistic.EGGS_BROKEN.addValue(player, 1);
			} else {
				ProtectTheEgg.getInstance().getLogger()
						.warning("A dragon egg was broken but it doesn't belong to any team");
			}
		}
	}

}
