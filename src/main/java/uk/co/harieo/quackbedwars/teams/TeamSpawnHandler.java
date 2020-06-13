package uk.co.harieo.quackbedwars.teams;

import org.bukkit.Location;

import java.util.*;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

public class TeamSpawnHandler {

	public static final String SPAWN_KEY = "team-spawn";

	private static final Map<BedWarsTeam, List<Location>> spawnLocations = new HashMap<>();
	private static final Map<BedWarsTeam, Integer> lastIndexMap = new HashMap<>();

	public static List<Location> getSpawnLocations(BedWarsTeam team) {
		return spawnLocations.getOrDefault(team, Collections.emptyList());
	}

	public static void addSpawnLocation(BedWarsTeam team, Location location) {
		if (spawnLocations.containsKey(team)) {
			spawnLocations.get(team).add(location);
		} else {
			List<Location> locations = new ArrayList<>();
			locations.add(location);
			spawnLocations.put(team, locations);
		}
	}

	public static Location getSpawn(BedWarsTeam team) {
		int index = getNextIndex(team);
		List<Location> locations = getSpawnLocations(team);
		if (index < locations.size()) {
			return locations.get(index);
		} else {
			ProtectTheEgg.getInstance().getLogger().severe("No spawns exist for team: " + team.getName());
			return null;
		}
	}

	private static int getNextIndex(BedWarsTeam team) {
		int lastIndex = lastIndexMap.getOrDefault(team, -1);
		int newIndex = lastIndex + 1;
		if (newIndex >= getSpawnLocations(team).size()) { // If we've reached the last available index
			newIndex = 0; // Reset to start
		}
		lastIndexMap.put(team, newIndex);
		return newIndex;
	}

}
