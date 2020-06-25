package uk.co.harieo.quackbedwars.teams.upgrades;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

public class BaseCurrencyUpgrade implements CurrencyUpgrade {

	// Values are constant so no point instantiating every time
	public static final BaseCurrencyUpgrade INSTANCE = new BaseCurrencyUpgrade();

	private static final Map<Currency, CurrencySpawnRate> spawnRates = new HashMap<>();

	static {
		spawnRates.put(Currency.IRON, Currency.IRON.getBaseSpawnRate());
	}

	@Override
	public Map<Currency, CurrencySpawnRate> getChangedSpawnRates() {
		return ImmutableMap.copyOf(spawnRates);
	}

	@Override
	public String getName() {
		return "Team Spawner";
	}

	@Override
	public int getDiamondCost() {
		return -1;
	}

}
