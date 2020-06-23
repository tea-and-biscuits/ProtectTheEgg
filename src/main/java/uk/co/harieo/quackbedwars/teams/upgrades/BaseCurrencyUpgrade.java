package uk.co.harieo.quackbedwars.teams.upgrades;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

public class BaseCurrencyUpgrade implements CurrencyUpgrade {

	// Values are constant so no point instantiating every time
	public static final BaseCurrencyUpgrade INSTANCE = new BaseCurrencyUpgrade();

	private final Set<CurrencySpawnRate> spawnRates = Sets.newHashSet(Currency.IRON.getBaseSpawnRate());

	@Override
	public Set<CurrencySpawnRate> getChangedSpawnRates() {
		return ImmutableSet.copyOf(spawnRates);
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
