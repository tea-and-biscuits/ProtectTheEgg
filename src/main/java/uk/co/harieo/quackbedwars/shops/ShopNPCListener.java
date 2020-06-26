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

public class ShopNPCListener implements Listener {

	@EventHandler
	public void onNPCDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.VILLAGER) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onNPCInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		if (entity instanceof Villager) {
			event.setCancelled(true);

			Villager villager = (Villager) entity;
			ShopType type = ShopHandler.getShopType(villager);
			if (type != null) {
				ShopMenu menu = type.getMenu();
				if (menu != null) {
					menu.getOrCreateMenu(player).showInventory();
				} else {
					player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "This villager isn't open for trade!"));
				}
			}
		}
	}

}
