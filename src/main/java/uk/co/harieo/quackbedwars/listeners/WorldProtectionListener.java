package uk.co.harieo.quackbedwars.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

public class WorldProtectionListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (isOutOfGame()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (isOutOfGame()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (isOutOfGame() && event.getEntityType() == EntityType.PLAYER) {
			event.setCancelled(true);
		}
	}

	private boolean isOutOfGame() {
		return ProtectTheEgg.getInstance().getGameStage() != GameStage.IN_GAME;
	}

}
