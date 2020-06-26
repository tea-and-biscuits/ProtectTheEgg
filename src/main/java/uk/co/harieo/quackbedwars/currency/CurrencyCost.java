package uk.co.harieo.quackbedwars.currency;

import java.util.HashMap;
import java.util.Map;

/**
 * A class which represents a cost in one or more {@link Currency}
 */
public class CurrencyCost {

	private final Map<Currency, Integer> costMap = new HashMap<>();

	/**
	 * Gets the integer cost of a currency. If a currency isn't used, this will return 0 instead.
	 *
	 * @param currency to get its amount counterpart
	 * @return the amount of the specified currency which this cost represents
	 */
	public int getCost(Currency currency) {
		return costMap.getOrDefault(currency, 0);
	}

	/**
	 * Sets the amount of a {@link Currency} which this cost represents
	 *
	 * @param currency to set the amount of
	 * @param amount the amount of currency
	 * @return this instance
	 */
	public CurrencyCost setCost(Currency currency, int amount) {
		costMap.put(currency, amount);
		return this;
	}

	/**
	 * @return a map of all costs represented in this class in the format (currency, amount)
	 */
	public Map<Currency, Integer> getAllCosts() {
		return costMap;
	}

}
