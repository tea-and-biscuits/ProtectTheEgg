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

		Set<CurrencySpawnRate> spawnRates = getSpawnRates();
		int loops = 0;
		for (CurrencySpawnRate spawnRate : spawnRates) {
			Currency currency = spawnRate.getCurrency();
			builder.append(currency.getColor());
			builder.append(currency.getName());
			if (loops + 1 < spawnRates.size()) {
				builder.append(ChatColor.GRAY);
				builder.append(" ｜ ");
			}

			loops++;
		}
		return builder.toString();
	}

	Set<CurrencySpawnRate> getSpawnRates();

	Hologram getHologram();

	default void formatHologram() {
		Hologram hologram = getHologram();
		if (hologram.getLines().size() == 2) { // This indicates that this method has been run before
			hologram.setLine(0, getHologramName());
			hologram.setLine(1, getHologramSubheading());
		} else {
			hologram.addLine(getHologramName());
			hologram.addLine(getHologramSubheading());
		}
		hologram.updateLines();
	}

}
