package uk.co.harieo.quackbedwars.currency.handlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import uk.co.harieo.quackbedwars.currency.Currency;

/**
 * A handler for checking the amount of {@link Currency} a player has and subtracting from it for purchases
 */
public class CurrencyHandler {

	/**
	 * Checks whether a player has the specified amount of currency in their inventory
	 *
	 * @param player to check the inventory of
	 * @param currency to check if they have it
	 * @param amount of currency to check for
	 * @return whether the specified player has the specified amount of currency
	 */
	public static boolean hasAmountOfCurrency(Player player, Currency currency, int amount) {
		return player.getInventory().contains(currency.getMaterial(), amount);
	}

	/**
	 * Subtracts an amount of currency from a player
	 *
	 * @param player to subtract currency from
	 * @param currency to be subtracted
	 * @param amountToSubtract to be subtracted
	 */
	public static void subtractCurrency(Player player, Currency currency, int amountToSubtract) {
		if (hasAmountOfCurrency(player, currency, amountToSubtract)) {
			PlayerInventory inventory = player.getInventory();

			int amountRemaining = amountToSubtract;
			for (int i = 0; i < inventory.getSize() && amountRemaining > 0; i++) {
				ItemStack item = inventory.getItem(i);
				if (item != null && item.getType() == currency.getMaterial()) {
					int stackSize = item.getAmount();
					inventory.setItem(i, null);
					if (stackSize == amountRemaining) { // This item clears the debt (perfect outcome)
						amountRemaining = 0;
					} else if (stackSize < amountRemaining) { // This covers some, but not all, of the debt
						inventory.setItem(i, null);
						amountRemaining -= stackSize;
					} else { // This covers the debt with some remaining
						int updatedStackSize = stackSize - amountRemaining;
						inventory.setItem(i, new ItemStack(currency.getMaterial(), updatedStackSize));
						amountRemaining = 0;
					}
				}
			}

		}
	}

}
