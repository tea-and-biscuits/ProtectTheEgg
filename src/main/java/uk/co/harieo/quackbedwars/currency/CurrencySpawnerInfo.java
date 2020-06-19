package uk.co.harieo.quackbedwars.currency;

import uk.co.harieo.quackbedwars.teams.BedWarsTeam;

public class CurrencySpawnerInfo {

	private final Currency currency;
	private final BedWarsTeam team;

	public CurrencySpawnerInfo(Currency currency, BedWarsTeam team) {
		this.currency = currency;
		this.team = team;
	}

	public Currency getCurrency() {
		return currency;
	}

	public BedWarsTeam getTeam() {
		return team;
	}

}
