package uk.co.harieo.quackbedwars.currency;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class CurrencySpawnRate {

	private final Currency currency;
	private final int secondsPerSpawn;
	private final int amountPerSpawn;

	private int internalSecond = 0;

	public CurrencySpawnRate(Currency currency, int secondsPerSpawn, int amountPerSpawn) {
		this.currency = currency;
		this.secondsPerSpawn = secondsPerSpawn;
		this.amountPerSpawn = amountPerSpawn;
	}

	public Currency getCurrency() {
		return currency;
	}

	public int getSecondsPerSpawn() {
		return secondsPerSpawn;
	}

	public int getAmountPerSpawn() {
		return amountPerSpawn;
	}

	/**
	 * Gets the value which stores the internal time since this spawn rate last triggered a spawn
	 *
	 * @return the internal time in seconds
	 */
	public int getInternalSecond() {
		return internalSecond;
	}

	/**
	 * Adds 1 to the internal time of this spawn rate. If this increment would put the internal time more than the
	 * {@link #getSecondsPerSpawn()} then it will reset the count to 0 instead.
	 */
	public void incrementInternalSecond() {
		if (internalSecond + 1 > getSecondsPerSpawn()) {
			internalSecond = 0;
		} else {
			internalSecond++;
		}
	}

	public void dropItems(Location location) {
		World world = location.getWorld();
		if (world != null) {
			world.dropItem(location, new ItemStack(getCurrency().getMaterial(), getAmountPerSpawn()));
		}
	}

}
