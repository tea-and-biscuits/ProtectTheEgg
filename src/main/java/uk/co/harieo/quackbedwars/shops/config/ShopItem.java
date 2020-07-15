package uk.co.harieo.quackbedwars.shops.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencyCost;
import uk.co.harieo.quackbedwars.currency.handlers.CurrencyHandler;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;

public class ShopItem extends MenuItem implements CloneableDynamicItem {

	private static final Set<Player> purchaseBuffer = new HashSet<>(); // Buffer prevents spam clicking to bypass cost

	private final ItemStack purchasableItem;
	private final CurrencyCost purchaseCost;

	/**
	 * A {@link MenuItem} which shows an item that can be purchased and the price it can be purchased for. If the item
	 * is clicked, this class will attempt to sell the item to the player.
	 *
	 * @param purchasableItem which can be purchased by the player
	 * @param cost the amount this item costs
	 */
	public ShopItem(ItemStack purchasableItem, CurrencyCost cost) {
		super(purchasableItem);
		this.purchasableItem = purchasableItem;
		this.purchaseCost = cost;
		setOnClick(this::onPurchaseAttempt);
	}

	/**
	 * @return the item which can be purchased
	 */
	public ItemStack getPurchasableItem() {
		return purchasableItem;
	}

	/**
	 * @return the cost of this item
	 */
	public CurrencyCost getPurchaseCost() {
		return purchaseCost;
	}

	@Override
	public MenuItem cloneForPlayer(Player player) {
		if (Tag.WOOL.isTagged(purchasableItem.getType())) {
			PlayerBasedTeam team = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);
			if (team != null) {
				MenuItem clone = new MenuItem(this);
				ItemStack item = clone.getItem();
				item.setType(team.getColour().getWoolType());
				item.setAmount(purchasableItem.getAmount());
				return clone;
			}
		}

		return new MenuItem(this); // Not enough info to edit so return direct copy
	}

	/**
	 * Attempts to deduct the cost of the purchasable item from the player then gives the item to them in return
	 *
	 * @param player who is attempting to purchase this item
	 */
	private void onPurchaseAttempt(Player player) {
		if (purchaseBuffer.contains(player)) { // Prevents any possible simultaneous purchases
			player.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.RED + "Please wait a moment before purchasing something else!"));
		} else {
			purchaseBuffer.add(player); // Add to buffer while we check they have the currency needed
			for (Entry<Currency, Integer> costEntry : purchaseCost.getAllCosts().entrySet()) {
				Currency currency = costEntry.getKey();
				int amount = costEntry.getValue();
				if (!CurrencyHandler.hasAmountOfCurrency(player, currency, amount)) {
					player.sendMessage(ProtectTheEgg.formatMessage(
							ChatColor.RED + "You don't have enough " + currency.getName() + ", you need " + amount
									+ " to buy that!"));
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 1F);
					purchaseBuffer.remove(player); // Transaction failed but still complete
					return;
				} else {
					CurrencyHandler.subtractCurrency(player, currency, amount);
				}
			}

			player.getInventory().addItem(purchasableItem.clone());
			purchaseBuffer.remove(player); // Transaction complete, they can now buy the next item
		}
	}

	/**
	 * Parses a {@link ConfigurationSection} for the values needed to return an instance of {@link ShopItem} and employs
	 * strict error handling
	 *
	 * The keys that this method will search for: All keys required in {@link ItemsConfig#parseItem(ConfigurationSection)}
	 * (required) cost - A section of integers formatted 'currency: amount' which show the item's cost (required)
	 *
	 * @param section to be parsed
	 * @return the formatted ShopItem or null if there was an error
	 */
	public static ShopItem parseShopItem(ConfigurationSection section) {
		Logger logger = ProtectTheEgg.getInstance().getLogger();
		String sectionName = section.getName();

		ItemStack purchasableItem = ItemsConfig.parseItem(section); // Get the item which can be purchased
		if (purchasableItem != null) {
			ConfigurationSection costSection = section.getConfigurationSection("cost");
			if (costSection != null) {
				CurrencyCost cost = new CurrencyCost();

				// Parse the cost section for all the individual currency costs
				Set<String> currencyKeys = costSection.getKeys(false);
				for (String currencyKey : currencyKeys) {
					Currency currency;
					try {
						currency = Currency.valueOf(currencyKey.toUpperCase());
					} catch (IllegalArgumentException ignored) {
						logger.warning(
								"The cost section of a shop item uses an unrecognised currency: "
										+ sectionName + ", " + currencyKey);
						continue;
					}

					int amount = costSection.getInt(currencyKey);
					cost.setCost(currency, amount);
				}

				// Appends the cost string onto the current item's lore
				ItemMeta meta = purchasableItem.getItemMeta();
				if (meta != null) {
					List<String> currentLore = meta.getLore();
					List<String> loreToEdit = new ArrayList<>();
					if (currentLore != null) {
						loreToEdit.addAll(currentLore);
					}

					loreToEdit.add("");
					loreToEdit.add(convertCostToString(cost));
					meta.setLore(loreToEdit);
					purchasableItem.setItemMeta(meta);
				}

				return new ShopItem(purchasableItem, cost);
			} else {
				logger.warning("A shop item had no section for its cost: " + sectionName);
			}
		} else {
			logger.warning("A shop item wasn't properly formatted: " + sectionName);
		}

		return null;
	}

	/**
	 * Takes a {@link CurrencyCost} then converts it into a user-friendly String which shows the cost separated by
	 * commas
	 *
	 * @param totalCost the cost to be converted
	 * @return the user-friendly representation of the cost
	 */
	private static String convertCostToString(CurrencyCost totalCost) {
		StringBuilder builder = new StringBuilder(ChatColor.GRAY + "Cost: ");

		Set<Entry<Currency, Integer>> entrySet = totalCost.getAllCosts().entrySet();
		int iterations = 0;
		for (Entry<Currency, Integer> currencyCost : entrySet) {
			Currency currency = currencyCost.getKey();
			int amount = currencyCost.getValue();

			ChatColor color = currency.getColor();
			builder.append(color);
			builder.append(amount);
			builder.append(" ");
			builder.append(currency.getName());
			if (iterations + 1 < entrySet.size()) { // If there is something after this
				builder.append(", ");
			}
		}

		return builder.toString().trim();
	}

}
