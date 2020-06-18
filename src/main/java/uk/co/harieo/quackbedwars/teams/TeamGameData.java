package uk.co.harieo.quackbedwars.teams;

import java.util.HashMap;
import java.util.Map;
import uk.co.harieo.quackbedwars.teams.upgrades.CurrencyUpgrade;

public class TeamGameData {

	private static final Map<BedWarsTeam, TeamGameData> data = new HashMap<>();

	private CurrencyUpgrade currencyUpgrade;
	private boolean eggIntact = true;

	public TeamGameData(BedWarsTeam team) {
		data.put(team, this);
	}

	public boolean isEggIntact() {
		return eggIntact;
	}

	public void setEggIntact(boolean eggIntact) {
		this.eggIntact = eggIntact;
	}

	public CurrencyUpgrade getCurrencyUpgrade() {
		return currencyUpgrade;
	}

	public void setCurrencyUpgrade(CurrencyUpgrade currencyUpgrade) {
		this.currencyUpgrade = currencyUpgrade;
	}

	public static TeamGameData getGameData(BedWarsTeam team) {
		return data.getOrDefault(team, new TeamGameData(team));
	}

}
