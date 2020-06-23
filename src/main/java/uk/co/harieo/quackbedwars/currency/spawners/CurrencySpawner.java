package uk.co.harieo.quackbedwars.currency.spawners;

import org.bukkit.Location;

import java.util.Set;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;

public interface CurrencySpawner {

	String getHologramName();

	Location getLocation();

	Set<CurrencySpawnRate> getSpawnRates();

}
