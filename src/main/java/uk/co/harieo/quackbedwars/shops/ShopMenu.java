package uk.co.harieo.quackbedwars.shops;

import org.bukkit.entity.Player;

import uk.co.harieo.minigames.menus.MenuFactory;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

public class ShopMenu extends MenuFactory {

	private final MenuItem[] items;

	public ShopMenu(ShopType type, int rows) {
		super(type.getShopName(), rows);
		items = new MenuItem[rows * 9];
		registerDefaultInteractionListener(ProtectTheEgg.getInstance());
	}

	public void setStaticItem(int index, MenuItem item) {
		items[index] = item;
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
