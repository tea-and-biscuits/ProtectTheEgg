package uk.co.harieo.quackbedwars.teams.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencyHandler;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamHandler;
import uk.co.harieo.quackbedwars.teams.upgrades.TeamUpgrade;

public class UpgradeItem extends MenuItem {

	private static final Set<BedWarsTeam> purchaseBuffer = new HashSet<>();

	private final BedWarsTeam team;
	private final TeamUpgrade upgrade;

	public UpgradeItem(BedWarsTeam team, TeamUpgrade upgrade) {
		super(Material.YELLOW_STAINED_GLASS_PANE);
		this.team = team;
		this.upgrade = upgrade;

		update();
		setOnClick(this::onUpgrade);
	}

	public void update() {
		Material itemType;
		ChatColor nameColor;
		if (upgrade.isUnlocked(team)) {
			itemType = Material.GREEN_STAINED_GLASS_PANE;
			nameColor = ChatColor.GREEN;
		} else {
			itemType = Material.RED_STAINED_GLASS_PANE;
			nameColor = ChatColor.RED;
		}

		getItem().setType(itemType);
		setName(nameColor + ChatColor.BOLD.toString() + upgrade.getName());
		int cost = upgrade.getDiamondCost();
		setLore(Arrays.asList(upgrade.getDescription(),
				ChatColor.AQUA.toString() + cost + " Diamond" + (cost != 1 ? "s" : "")));
	}

	private void onUpgrade(Player player) {
		BedWarsTeam team = TeamHandler.getTeam(player);
		if (team != null && !purchaseBuffer.contains(team)) {
			purchaseBuffer.add(team); // Prevent other team members purchasing at the same time or they'll lose diamonds
			if (!upgrade.isUnlocked(team)) {
				int cost = upgrade.getDiamondCost();
				if (CurrencyHandler.hasAmountOfCurrency(player, Currency.DIAMOND, cost)) {
					CurrencyHandler.subtractCurrency(player, Currency.DIAMOND, cost);
					upgrade.activateUpgrade(team);
					team.getOnlineMembers().forEach(member -> {
						member.sendMessage(ProtectTheEgg.formatMessage(
								ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " has purchased " + ChatColor.AQUA
										+ upgrade.getName()));
						member.playSound(member.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.5F);
					});
				} else {
					player.sendMessage(ProtectTheEgg
							.formatMessage(ChatColor.RED + "You don't have enough diamonds for that upgrade!"));
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 1F);
				}
				purchaseBuffer.remove(team); // Transaction complete
			} else {
				player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "Your team already owns that upgrade!"));
			}
		} else {
			player.sendMessage(
					ProtectTheEgg.formatMessage(ChatColor.RED + "You must be in a team to use team upgrades!"));
		}
	}

}
