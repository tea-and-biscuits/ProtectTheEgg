package uk.co.harieo.quackbedwars.teams.menu;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.upgrades.TeamUpgrade;

public class UpgradeCategory extends MenuItem {

	private final List<TeamUpgrade> upgrades;
	private final Map<BedWarsTeam, List<UpgradeItem>> upgradeItemMap = new HashMap<>(); // Caches menu items per team

	public UpgradeCategory(ItemStack displayItem, List<TeamUpgrade> upgrades) {
		super(displayItem);
		this.upgrades = upgrades;
	}

	public List<UpgradeItem> getUpgradeItems(BedWarsTeam team) {
		List<UpgradeItem> items;
		if (upgradeItemMap.containsKey(team)) {
			items = upgradeItemMap.get(team);
			items.forEach(UpgradeItem::update);
		} else {
			items = new ArrayList<>();
			for (TeamUpgrade upgrade : upgrades) {
				items.add(new UpgradeItem(team, upgrade));
			}
			upgradeItemMap.put(team, items);
		}

		return items;
	}

}
