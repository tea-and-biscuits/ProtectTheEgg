package uk.co.harieo.quackbedwars.teams.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.quackbedwars.shops.ShopType;

public class BackButtonItem extends MenuItem {

	/**
	 * An item which returns a player to a specific menu
	 *
	 * @param type which holds the menu to return the player to
	 */
	public BackButtonItem(ShopType type) {
		super(Material.BARRIER);
		setName(ChatColor.RED + ChatColor.BOLD.toString() + "Go Back");
		setLore(Collections.singletonList(ChatColor.GRAY + "Go back to the previous section"));
		setOnClick(player -> type.getMenu().getOrCreateMenu(player).showInventory());
	}

}
