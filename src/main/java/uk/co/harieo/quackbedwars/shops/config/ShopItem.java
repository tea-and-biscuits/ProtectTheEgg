package uk.co.harieo.quackbedwars.shops.config;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencyCost;
import uk.co.harieo.quackbedwars.currency.handlers.CurrencyHandler;

public class ShopItem extends MenuItem {

	private static final Set<Player> purchaseBuffer = new HashSet<>(); // Buffer prevents spam clicking to bypass cost

	private final ItemStack purchasableItem;
	private final CurrencyCost purchaseCost;

	public ShopItem(ItemStack purchasableItem, CurrencyCost cost) {
		super(purchasableItem);
		this.purchasableItem = purchasableItem;
		this.purchaseCost = cost;

		setOnClick(player -> {
			if (!purchaseBuffer.contains(player)) {
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

				player.getInventory().addItem(purchasableItem);
				purchaseBuffer.remove(player); // Transaction complete, they can now buy the next item
			} else {
				player.sendMessage(ProtectTheEgg
						.formatMessage(ChatColor.RED + "Please wait a moment before purchasing something else!"));
			}
		});
	}

	public ItemStack getPurchasableItem() {
		return purchasableItem;
	}

	public CurrencyCost getPurchaseCost() {
		return purchaseCost;
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

		ItemStack purchasableItem = ItemsConfig.parseItem(section);
		if (purchasableItem != null) {
			ConfigurationSection costSection = section.getConfigurationSection("cost");
			if (costSection != null) {
				CurrencyCost cost = new CurrencyCost();

				StringBuilder costStringBuilder = new StringBuilder();
				costStringBuilder.append(ChatColor.GRAY);
				costStringBuilder.append("Cost: ");

				Set<String> currencyKeys = costSection.getKeys(false);
				int iterations = 0;

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

					ChatColor color = currency.getColor();
					costStringBuilder.append(color);
					costStringBuilder.append(amount);
					costStringBuilder.append(" ");
					costStringBuilder.append(currency.getName());
					if (iterations + 1 < currencyKeys.size()) { // If there is something after this
						costStringBuilder.append(", ");
					}

					iterations++;
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
					loreToEdit.add(costStringBuilder.toString());
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

}
