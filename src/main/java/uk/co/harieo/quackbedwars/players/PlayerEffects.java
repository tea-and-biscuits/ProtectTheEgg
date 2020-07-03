package uk.co.harieo.quackbedwars.players;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.HashSet;
import java.util.Set;

/**
 * A class which handles minor effects which can be applied to a player that aren't natural to vanilla
 */
public class PlayerEffects implements Listener {

	private static final Set<Player> fallImmunity = new HashSet<>();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.FALL) {
			Entity entity = event.getEntity();
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (fallImmunity.contains(player)) {
					event.setCancelled(true);
					fallImmunity.remove(player);
				}
			}
		}
	}

	/**
	 * Plays {@link Sound#ENTITY_EXPERIENCE_ORB_PICKUP} to all online players
	 */
	public static void pingAll() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.5F);
		}
	}

	/**
	 * Adds a player to a set that grants them immunity to their next instance of fall damage
	 *
	 * @param player to grant fall immunity to
	 */
	public static void giveOneTimeFallImmunity(Player player) {
		fallImmunity.add(player);
	}

}
