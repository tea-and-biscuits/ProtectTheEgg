package uk.co.harieo.quackbedwars.teams.upgrades.traps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.Set;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.egg.EggData;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamGameData;
import uk.co.harieo.quackbedwars.teams.TeamHandler;

public class TrapListener implements Listener {

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (ProtectTheEgg.getInstance().getGameStage() != GameStage.IN_GAME) {
			return;
		}

		Player player = event.getPlayer();
		Map<Block, EggData> eggDataMap = EggData.getCachedEggs();
		for (Block block : eggDataMap.keySet()) {
			BedWarsTeam eggOwnerTeam = eggDataMap.get(block).getTeam();
			BedWarsTeam triggersTeam = TeamHandler.getTeam(player);
			if (eggOwnerTeam != triggersTeam) { // Make sure they're not triggering their own trap
				Location blockLocation = block.getLocation();
				World blockWorld = blockLocation.getWorld();
				if (blockWorld != null && blockWorld.equals(player.getWorld())) {
					double distance = blockLocation.distance(player.getLocation());
					if (distance < 5) {
						TeamGameData gameData = TeamGameData.getGameData(eggOwnerTeam);

						Set<PurchasableTraps> traps = gameData.getPurchasedTraps();
						if (!traps.isEmpty()) {
							traps.forEach(trap -> {
								trap.onTrigger(player);
								gameData.removePurchasedTrap(trap); // This set is cloned so no risk of concurrent modification
							});
							eggOwnerTeam.getOnlineMembers()
									.forEach(member -> member
											.sendTitle(ChatColor.RED + "Trap Triggered",
													ChatColor.GRAY + "Your egg is in danger",
													20, 20 * 3, 20));
						}
					}
				}
			}
		}
	}

}
