package uk.co.harieo.quackbedwars.currency.spawners;

import org.bukkit.ChatColor;

import java.util.Set;
import uk.co.harieo.minigames.holograms.Hologram;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

public interface CurrencySpawner {

	String getHologramName();

	default String getHologramSubheading() {
		StringBuilder builder = new StringBuilder();
		int loops = 0;
		Set<CurrencySpawnRate> spawnRates = getSpawnRates();
		for (CurrencySpawnRate spawnRate : spawnRates) {
			Currency currency = spawnRate.getCurrency();
			builder.append(currency.getColor());
			builder.append(currency.getName());
			if (loops + 1 < spawnRates.size()) {
				builder.append(ChatColor.GRAY);
				builder.append(" | ");
			}
		}
		return builder.toString();
	}

	Set<CurrencySpawnRate> getSpawnRates();

	Hologram getHologram();

	default void formatHologram() {
		Hologram hologram = getHologram();
		hologram.addLine(getHologramName());
		hologram.addLine(getHologramSubheading());
		hologram.updateLines();
	}

}
