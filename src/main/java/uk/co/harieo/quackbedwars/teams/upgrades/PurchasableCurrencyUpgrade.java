package uk.co.harieo.quackbedwars.teams.upgrades;

import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

public enum PurchasableCurrencyUpgrade implements CurrencyUpgrade {

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
	private final PurchasableCurrencyUpgrade parent;
	private final Map<Currency, CurrencySpawnRate> spawnRates = new HashMap<>(); // Key prevents duplicate spawn rates

	PurchasableCurrencyUpgrade(String name, int diamondCost, PurchasableCurrencyUpgrade parent, CurrencySpawnRate... newSpawnRates) {
		this.name = name;
		this.diamondCost = diamondCost;
		this.parent = parent;

		// Add the new ones for this upgrade
		for (CurrencySpawnRate spawnRate : newSpawnRates) {
			this.spawnRates.put(spawnRate.getCurrency(), spawnRate);
		}

		if (parent != null) {
			this.spawnRates.putAll(parent.getChangedSpawnRates()); // And all the ones before it (from the parent)
		}
	}

	PurchasableCurrencyUpgrade(String name, int diamondCost, CurrencySpawnRate... newSpawnRates) {
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

	@Override
	public Map<Currency, CurrencySpawnRate> getChangedSpawnRates() {
		return spawnRates;
	}

	public PurchasableCurrencyUpgrade getParent() {
		return parent;
	}

}
