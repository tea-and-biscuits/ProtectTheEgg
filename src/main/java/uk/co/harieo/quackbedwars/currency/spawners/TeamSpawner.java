package uk.co.harieo.quackbedwars.currency.spawners;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Objects;
import java.util.Set;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

public class TeamSpawner implements CurrencySpawner {

	private final String name;
	private final Location location;
	private final TeamGameData gameData;

	public TeamSpawner(Location location, BedWarsTeam team) {
		this.name = team.getChatColor() + ChatColor.BOLD.toString() + team.getName() + "'s Spawner";
		this.location = Objects.requireNonNull(location);
		this.gameData = TeamGameData.getGameData(Objects.requireNonNull(team));
	}

	@Override
	public String getHologramName() {
		return name;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public Set<CurrencySpawnRate> getSpawnRates() {
		return gameData.getCurrencyUpgrade().getChangedSpawnRates();
	}

}
