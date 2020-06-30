package uk.co.harieo.quackbedwars.teams.upgrades.currency;

import java.util.Map;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;
import uk.co.harieo.quackbedwars.teams.upgrades.TeamUpgrade;

public interface CurrencyUpgrade extends TeamUpgrade {

	Map<Currency, CurrencySpawnRate> getChangedSpawnRates();

}
