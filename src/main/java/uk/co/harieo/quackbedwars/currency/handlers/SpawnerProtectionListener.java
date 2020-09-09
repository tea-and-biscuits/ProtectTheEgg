package uk.co.harieo.quackbedwars.currency.handlers;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SpawnerProtectionListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (isBelowSpawner(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	private boolean isBelowSpawner(Location location) {
		Location newLocation = location.clone().add(0, 1, 0);
		for (Location spawnerLocation : CurrencySpawnHandler.getSpawnerLocations().keySet()) {
			if (spawnerLocation.getBlockX() == newLocation.getBlockX() && spawnerLocation.getBlockZ() == newLocation
					.getBlockZ()) {
				return true;
			}
		}

		return false;
	}

}
