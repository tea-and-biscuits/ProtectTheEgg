package uk.co.harieo.quackbedwars.teams.upgrades;

import java.util.Map;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

public interface CurrencyUpgrade extends TeamUpgrade {

	Map<Currency, CurrencySpawnRate> getChangedSpawnRates();

}
