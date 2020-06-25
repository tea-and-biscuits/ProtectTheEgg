package uk.co.harieo.quackbedwars.currency.spawners;

import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import uk.co.harieo.minigames.holograms.Hologram;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

public class TeamSpawner implements CurrencySpawner {

	private final String name;
	private final TeamGameData gameData;
	private final Hologram hologram = new Hologram();

	public TeamSpawner(BedWarsTeam team) {
		this.name = team.getChatColor() + ChatColor.BOLD.toString() + team.getName() + "'s Spawner";
		this.gameData = TeamGameData.getGameData(Objects.requireNonNull(team));
	}

	@Override
	public String getHologramName() {
		return name;
	}

	@Override
	public Set<CurrencySpawnRate> getSpawnRates() {
		return new HashSet<>(gameData.getCurrencyUpgrade().getChangedSpawnRates().values());
	}

	@Override
	public Hologram getHologram() {
		return hologram;
	}

}
