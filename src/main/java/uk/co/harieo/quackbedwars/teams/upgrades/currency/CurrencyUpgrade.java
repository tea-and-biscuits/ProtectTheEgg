package uk.co.harieo.quackbedwars.teams.upgrades.currency;

import java.util.Map;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;
import uk.co.harieo.quackbedwars.teams.upgrades.TeamUpgrade;

public interface CurrencyUpgrade extends TeamUpgrade {

	/**
	 * @return a map of currencies and their changed spawn rate. If a currency is not included in this map, it is
	 * assumed that its spawn rate has not changed in this upgrade.
	 */
	Map<Currency, CurrencySpawnRate> getChangedSpawnRates();

}
