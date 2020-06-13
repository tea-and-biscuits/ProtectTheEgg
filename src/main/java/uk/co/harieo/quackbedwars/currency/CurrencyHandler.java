package uk.co.harieo.quackbedwars.currency;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CurrencyHandler {

	public static boolean hasAmountOfCurrency(Player player, Currency currency, int amount) {
		return player.getInventory().contains(currency.getMaterial(), amount);
	}

	public static boolean subtractCurrency(Player player, Currency currency, int amountToSubtract) {
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

			return true;
		} else {
			return false;
		}
	}

}
