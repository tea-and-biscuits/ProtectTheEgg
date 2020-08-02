package uk.co.harieo.quackbedwars.shops;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.players.DeathTracker;

/**
 * A listener which scans for interactions with a {@link Villager} on the assumption that it may be a shop NPC
 */
public class ShopNPCListener implements Listener {

	@EventHandler
	public void onNPCDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.VILLAGER) {
			event.setCancelled(true); // Prevent damage to all villagers
		}
	}

	@EventHandler
	public void onNPCInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (DeathTracker.isAlive(player)) {
			Entity entity = event.getRightClicked();
			if (entity instanceof Villager) { // If the right clicked entity is a villager
				event.setCancelled(true); // Prevent opening the villager trade menu

				Villager villager = (Villager) entity;
				ShopType type = ShopHandler.getShopType(villager);
				if (type != null) { // If this villager represents a known shop menu
					ShopMenu menu = type.getMenu();
					if (menu != null) { // And if the menu has been created
						menu.getOrCreateMenu(player).showInventory();
					} else {
						player.sendMessage(
								ProtectTheEgg.formatMessage(ChatColor.RED + "This villager isn't open for trade!"));
					}
				}
			}
		}
	}

}
