package uk.co.harieo.quackbedwars.teams.menu;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.shops.ShopMenu;
import uk.co.harieo.quackbedwars.shops.ShopType;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;
import uk.co.harieo.quackbedwars.teams.upgrades.TeamUpgrade;

public class UpgradeCategory extends MenuItem {

	private final List<TeamUpgrade> upgrades;
	private final Map<PlayerBasedTeam, ShopMenu> subMenuMap = new HashMap<>(); // Caches menu items per team

	/**
	 * A category of {@link TeamUpgrade} which a team can purchase
	 *
	 * @param displayItem to represent this category
	 * @param upgrades in this category
	 */
	public UpgradeCategory(ItemStack displayItem, List<TeamUpgrade> upgrades) {
		super(displayItem);
		this.upgrades = upgrades;
		setOnClick(this::showSubMenu);
	}

	/**
	 * Retrieves the {@link ShopMenu} for the specified player's {@link BedWarsTeamData}, or creates it if not cached,
	 * then updates all the instances of {@link UpgradeItem} to display up-to-date information. The menu is then shown
	 * to the player.
	 *
	 * @param player to show the menu to
	 */
	public void showSubMenu(Player player) {
		PlayerBasedTeam team = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);
		if (team != null) {
			ShopMenu teamMenu = subMenuMap.get(team);
			if (teamMenu == null) {
				teamMenu = new ShopMenu(ShopType.UPGRADES, (upgrades.size() + 1) / 9 + 1);
				for (int i = 0; i < upgrades.size(); i++) { // Add the base items to the new menu
					teamMenu.setStaticItem(i, new UpgradeItem(team, upgrades.get(i)));
				}
				teamMenu.setStaticItem(teamMenu.getSlotSize() - 1, new BackButtonItem(ShopType.UPGRADES));
				subMenuMap.put(team, teamMenu);
			}

			for (MenuItem menuItem : teamMenu.getStaticItems()) {
				if (menuItem instanceof UpgradeItem) {
					((UpgradeItem) menuItem).update(); // Refreshes the data on the item
				}
			}

			teamMenu.getOrCreateMenu(player).showInventory();
		}
	}

}
