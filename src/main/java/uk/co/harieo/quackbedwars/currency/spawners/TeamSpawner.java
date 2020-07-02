package uk.co.harieo.quackbedwars.currency.spawners;

import org.bukkit.ChatColor;

import java.util.*;
import uk.co.harieo.minigames.holograms.Hologram;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

public class TeamSpawner implements CurrencySpawner {

	private static final Map<Team, TeamSpawner> cache = new HashMap<>();

	private final String name;
	private final TeamGameData gameData;
	private final Hologram hologram = new Hologram();
	private boolean active = false;

	/**
	 * An instance of {@link CurrencySpawner} which is specific to a {@link BedWarsTeamData} and has its spawn rates based
	 * on {@link uk.co.harieo.quackbedwars.teams.upgrades.currency.CurrencyUpgrade}
	 *
	 * @param team which owns this spawner
	 */
	public TeamSpawner(Team team) {
		this.name = team.getChatColor() + ChatColor.BOLD.toString() + team.getName() + "'s Spawner";
		this.gameData = TeamGameData.getGameData(Objects.requireNonNull(team));
		cache.put(team, this);
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

	@Override
	public boolean isActive() {
		return active;
	}

	/**
	 * Set whether this spawner should actively spawn currency
	 *
	 * @param active whether to spawn items
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Retrieves a cached instance of this class, which are cached on instantiation
	 *
	 * @param team to get the spawner for
	 * @return the cached spawner or null if none are cached
	 */
	public static TeamSpawner getCachedSpawner(Team team) {
		return cache.get(team);
	}

}
