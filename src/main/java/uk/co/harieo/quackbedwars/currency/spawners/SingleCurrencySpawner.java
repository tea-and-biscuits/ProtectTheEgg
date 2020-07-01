package uk.co.harieo.quackbedwars.currency.spawners;

import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;
import uk.co.harieo.minigames.holograms.Hologram;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

public class SingleCurrencySpawner implements CurrencySpawner {

	private final String name;
	private final Set<CurrencySpawnRate> spawnRates = new HashSet<>();
	private final Hologram hologram = new Hologram();
	private final String subheading;

	public SingleCurrencySpawner(Currency currency) {
		this.name = currency.getColor() + ChatColor.BOLD.toString() + currency.getName() + " Spawner";

		CurrencySpawnRate spawnRate = currency.getBaseSpawnRate();
		int secondsPerSpawn = spawnRate.getSecondsPerSpawn();
		this.subheading =
				spawnRate.getAmountPerSpawn() + " " + currency.getColor() + currency.getName()
						+ ChatColor.WHITE + " Every " + (secondsPerSpawn == 1 ? "Second" : secondsPerSpawn + " Seconds");

		spawnRates.add(spawnRate);
	}

	@Override
	public String getHologramName() {
		return name;
	}

	@Override
	public String getHologramSubheading() {
		return subheading;
	}

	@Override
	public Set<CurrencySpawnRate> getSpawnRates() {
		return spawnRates;
	}

	@Override
	public Hologram getHologram() {
		return hologram;
	}

	@Override
	public boolean isActive() {
		return true;
	}

}
