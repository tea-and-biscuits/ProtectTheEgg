package uk.co.harieo.quackbedwars.teams;

import java.util.HashMap;
import java.util.Map;
import uk.co.harieo.quackbedwars.egg.EggData;
import uk.co.harieo.quackbedwars.teams.upgrades.BaseCurrencyUpgrade;
import uk.co.harieo.quackbedwars.teams.upgrades.CurrencyUpgrade;

public class TeamGameData {

	private static final Map<BedWarsTeam, TeamGameData> data = new HashMap<>();

	private CurrencyUpgrade currencyUpgrade = BaseCurrencyUpgrade.INSTANCE;
	private EggData eggData;
	private int playersAlive = 0;

	public TeamGameData(BedWarsTeam team) {
		data.put(team, this);
	}

	public boolean isEggIntact() {
		return eggData != null && eggData.isIntact();
	}

	public EggData getEggData() {
		return eggData;
	}

	public void setEggData(EggData eggData) {
		this.eggData = eggData;
	}

	public CurrencyUpgrade getCurrencyUpgrade() {
		return currencyUpgrade;
	}

	public void setCurrencyUpgrade(CurrencyUpgrade currencyUpgrade) {
		this.currencyUpgrade = currencyUpgrade;
	}

	public int getPlayersAlive() {
		return playersAlive;
	}

	public void incrementPlayersAlive() {
		setPlayersAlive(getPlayersAlive() + 1);
	}

	public void decrementPlayersAlive() {
		setPlayersAlive(getPlayersAlive() - 1);
	}

	public void setPlayersAlive(int playersAlive) {
		this.playersAlive = playersAlive;
	}

	public static TeamGameData getGameData(BedWarsTeam team) {
		return data.getOrDefault(team, new TeamGameData(team));
	}

}
