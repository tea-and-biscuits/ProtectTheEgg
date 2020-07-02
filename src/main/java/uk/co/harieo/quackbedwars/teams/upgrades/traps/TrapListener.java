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
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.egg.EggData;
import uk.co.harieo.quackbedwars.players.DeathTracker;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

public class TrapListener implements Listener {

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (ProtectTheEgg.getInstance().getGameStage() != GameStage.IN_GAME) {
			return;
		}

		Player player = event.getPlayer();
		if (!DeathTracker.isAlive(player)) {
			return;
		}

		Map<Block, EggData> eggDataMap = EggData.getCachedEggs();
		for (Block block : eggDataMap.keySet()) {
			PlayerBasedTeam eggOwnerTeam = eggDataMap.get(block).getTeam();
			PlayerBasedTeam triggersTeam = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);

			// Make sure they're not triggering their own trap
			if (eggOwnerTeam != triggersTeam) {
				Location blockLocation = block.getLocation();
				World blockWorld = blockLocation.getWorld();

				// Make sure they're in the same world as this egg
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
