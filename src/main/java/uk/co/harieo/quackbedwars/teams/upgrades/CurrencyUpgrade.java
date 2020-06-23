package uk.co.harieo.quackbedwars.teams.upgrades;

import java.util.Set;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

public interface CurrencyUpgrade extends TeamUpgrade {

	Set<CurrencySpawnRate> getChangedSpawnRates();

}
