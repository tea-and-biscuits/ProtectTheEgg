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

	/**
	 * An item which represents a single {@link TeamUpgrade} and allows it to be purchased for its diamond cost,
	 * assuming it has not already been unlocked
	 *
	 * @param team which can purchase this upgrade
	 * @param upgrade that can be purchased
	 */
	public UpgradeItem(BedWarsTeam team, TeamUpgrade upgrade) {
		super(Material.YELLOW_STAINED_GLASS_PANE);
		this.team = team;
		this.upgrade = upgrade;

		update();
		setOnClick(this::onUpgrade);
	}

	/**
	 * Updates the name, lore and material of the item to represent its cost as well as whether the upgrade can be
	 * purchased or not
	 */
	public void update() {
		boolean isUnlocked = upgrade.isUnlocked(team);
		boolean canUpgrade = upgrade.canUnlock(team);

		Material itemType;
		ChatColor nameColor;
		if (isUnlocked || !canUpgrade) {
			itemType = Material.RED_STAINED_GLASS_PANE;
			nameColor = ChatColor.RED;
		} else {
			itemType = Material.LIME_STAINED_GLASS_PANE;
			nameColor = ChatColor.GREEN;
		}

		getItem().setType(itemType);
		setName(nameColor + ChatColor.BOLD.toString() + upgrade.getName());
		int cost = upgrade.getDiamondCost();

		String lastLine;
		if (isUnlocked) {
			lastLine = ChatColor.AQUA + "Already Unlocked";
		} else if (!canUpgrade) {
			lastLine = ChatColor.RED + "Not Available Yet";
		} else {
			lastLine = ChatColor.GRAY + "Cost: " + ChatColor.AQUA.toString() + cost + " Diamond"
					+ (cost != 1 ? "s" : "");
		}

		setLore(Arrays.asList(upgrade.getDescription(), "", lastLine));
	}

	/**
	 * Allows the player to attempt to purchase this upgrade
	 *
	 * @param player who is attempting to purchase this upgrade
	 */
	private void onUpgrade(Player player) {
		BedWarsTeam team = TeamHandler.getTeam(player);
		if (purchaseBuffer.contains(team)) {
			player.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.RED + "Please wait while someone on your team purchases an upgrade..."));
		} else {
			if (team != null) {
				// Prevent other team members purchasing at the same time or they'll lose diamonds
				purchaseBuffer.add(team);

				String error = null;
				if (upgrade.isUnlocked(team)) {
					error = "Your team already owns that upgrade!";
				} else if (!upgrade.canUnlock(team)) {
					error = "That upgrade isn't available. Did you unlock the one before it?";
				}

				if (error == null) { // No error
					int cost = upgrade.getDiamondCost();
					if (CurrencyHandler.hasAmountOfCurrency(player, Currency.DIAMOND, cost)) {
						CurrencyHandler.subtractCurrency(player, Currency.DIAMOND, cost);
						upgrade.activateUpgrade(team);
						team.getOnlineMembers().forEach(member -> {
							member.sendMessage(ProtectTheEgg.formatMessage(
									ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " has purchased the "
											+ ChatColor.AQUA + upgrade.getName() + ChatColor.GRAY + " upgrade"));
							member.playSound(member.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.5F);
						});
						player.getOpenInventory().close(); // Closes the menu to force an update to its contents
					} else {
						player.sendMessage(ProtectTheEgg
								.formatMessage(ChatColor.RED + "You don't have enough diamonds for that upgrade!"));
						player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 1F);
					}
				} else {
					player.sendMessage(ProtectTheEgg.formatMessage(error));
				}

				purchaseBuffer.remove(team); // Transaction complete, allow the next purchase attempt
			} else {
				player.sendMessage(
						ProtectTheEgg.formatMessage(ChatColor.RED + "You must be in a team to use team upgrades!"));
			}
		}
	}

}
