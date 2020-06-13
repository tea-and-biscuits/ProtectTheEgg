package uk.co.harieo.quackbedwars.teams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uk.co.harieo.quackbedwars.teams.upgrades.TeamUpgrade;

public class TeamGameData {

	private static final Map<BedWarsTeam, TeamGameData> data = new HashMap<>();

	private final Set<TeamUpgrade> upgrades = new HashSet<>();
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

	public static TeamGameData getGameData(BedWarsTeam team) {
		return data.getOrDefault(team, new TeamGameData(team));
	}

}
