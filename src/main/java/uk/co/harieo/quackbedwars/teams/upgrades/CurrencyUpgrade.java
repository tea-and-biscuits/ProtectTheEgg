package uk.co.harieo.quackbedwars.teams.upgrades;

import com.google.common.collect.Sets;
import java.util.Set;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

public enum CurrencyUpgrade implements TeamUpgrade {

	RESOURCES_ONE("Resources I", 2, new CurrencySpawnRate(Currency.IRON, 3, 1)),
	RESOURCES_TWO("Resources II", 4, RESOURCES_ONE,
			new CurrencySpawnRate(Currency.IRON, 2, 1),
			new CurrencySpawnRate(Currency.GOLD, 3, 1)),
	RESOURCES_THREE("Resources III", 6, RESOURCES_TWO,
			new CurrencySpawnRate(Currency.IRON, 1, 1),
			new CurrencySpawnRate(Currency.GOLD, 3, 2)),
	RESOURCES_FOUR("Resources IV", 8, RESOURCES_THREE, new CurrencySpawnRate(Currency.EMERALD, 3, 2));

	private final String name;
	private final int diamondCost;
	private final CurrencyUpgrade parent;
	private final Set<CurrencySpawnRate> newSpawnRates;

	CurrencyUpgrade(String name, int diamondCost, CurrencyUpgrade parent, CurrencySpawnRate... newSpawnRates) {
		this.name = name;
		this.diamondCost = diamondCost;
		this.parent = parent;
		this.newSpawnRates = Sets.newHashSet(newSpawnRates);
	}

	CurrencyUpgrade(String name, int diamondCost, CurrencySpawnRate... newSpawnRates) {
		this(name, diamondCost, null, newSpawnRates);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getDiamondCost() {
		return diamondCost;
	}

	public Set<CurrencySpawnRate> getNewSpawnRates() {
		return newSpawnRates;
	}

	public CurrencyUpgrade getParent() {
		return parent;
	}

}
