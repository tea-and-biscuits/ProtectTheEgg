package uk.co.harieo.quackbedwars.shops.config;

import org.bukkit.entity.Player;

import uk.co.harieo.minigames.menus.MenuItem;

/**
 * This interface indicates that the implementing class can clone a {@link MenuItem} which represents itself and also
 * includes dynamic content specific to each player which it is cloned for.
 */
public interface CloneableDynamicItem {

	/**
	 * Clones a {@link MenuItem} with content designed to be seen by the specified player
	 *
	 * @param player to clone the item for
	 * @return the cloned item
	 */
	MenuItem cloneForPlayer(Player player);

}
