package uk.co.harieo.quackbedwars.shops;

import org.bukkit.entity.Player;

import uk.co.harieo.minigames.menus.MenuFactory;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

public class ShopMenu extends MenuFactory {

	private final MenuItem[] items;

	/**
	 * An implementation of {@link MenuItem} for purchasable in-game items and upgrades, the GUI for a {@link ShopType}.
	 * This menu assumes that there are no dynamic items as to save the processing power which would be required to make
	 * a new item for each player.
	 *
	 * @param type of shop which this menu is
	 * @param rows in this menu's inventory
	 */
	public ShopMenu(ShopType type, int rows) {
		super(type.getShopName(), rows);
		items = new MenuItem[rows * 9];
		registerDefaultInteractionListener(ProtectTheEgg.getInstance());
	}

	/**
	 * Sets the {@link MenuItem} to be put in the inventory at the specified index
	 *
	 * @param index of the inventory to set the item into
	 * @param item to set into the inventory
	 */
	public void setStaticItem(int index, MenuItem item) {
		items[index] = item;
	}

	/**
	 * @return an array of all the set static items, always to the size of the inventory (empty slots will be null at
	 * their respective index in the array)
	 */
	public MenuItem[] getStaticItems() {
		return items;
	}

	@Override
	public void setPlayerItems(Player player, int page) {
		for (int i = 0; i < items.length; i++) {
			MenuItem menuItem = items[i];
			if (menuItem != null) {
				setItem(player, i, menuItem);
			}
		}
	}

}
