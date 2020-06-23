package uk.co.harieo.quackbedwars.currency.spawners;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

public class SingleCurrencySpawner implements CurrencySpawner {

	private final String name;
	private final Location location;
	private final Set<CurrencySpawnRate> spawnRates = new HashSet<>();

	public SingleCurrencySpawner(Location location, Currency currency) {
		this.name = currency.getColor() + ChatColor.BOLD.toString() + currency.getName() + " Spawner";
		this.location = location;
		spawnRates.add(currency.getBaseSpawnRate());
	}

	@Override
	public String getHologramName() {
		return name;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public Set<CurrencySpawnRate> getSpawnRates() {
		return spawnRates;
	}

}
