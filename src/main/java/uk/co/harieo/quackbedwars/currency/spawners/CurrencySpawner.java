package uk.co.harieo.quackbedwars.currency.spawners;

import org.bukkit.ChatColor;

import java.util.Set;
import uk.co.harieo.minigames.holograms.Hologram;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

/**
 * A location at which {@link Currency} spawns
 */
public interface CurrencySpawner {

	/**
	 * @return the first line of the hologram for this spawner
	 */
	String getHologramName();

	/**
	 * @return the second line of the hologram for this spawner
	 */
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

	/**
	 * @return the spawn rates of the currencies which should be spawned here
	 */
	Set<CurrencySpawnRate> getSpawnRates();

	/**
	 * @return the hologram which is displayed over this spawner
	 */
	Hologram getHologram();

	/**
	 * Formats this spawner's hologram with its name and sub-heading
	 */
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
