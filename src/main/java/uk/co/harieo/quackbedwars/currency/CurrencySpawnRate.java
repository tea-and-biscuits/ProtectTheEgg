package uk.co.harieo.quackbedwars.currency;

public class CurrencySpawnRate {

	private final Currency currency;
	private final int secondsPerSpawn;
	private final int amountPerSpawn;

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

}
