package uk.co.harieo.quackbedwars.teams.handlers;

import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;

public class TeamSpawnHandler {

	public static final String SPAWN_KEY = "team-spawn";

	/**
	 * Parses a {@link MapImpl} for the spawn locations of each team in {@link BedWarsTeamData}
	 *
	 * @param map to be parsed
	 */
	public static void parseSpawnLocations(MapImpl map) {
		for (LocationPair pair : map.getLocationsByKey(TeamSpawnHandler.SPAWN_KEY)) {
			BedWarsTeamData team = BedWarsTeamData.getByName(pair.getValue());
			if (team != null) {
				team.getTeam().getSpawns().addSpawn(pair.getLocation());
			} else {
				ProtectTheEgg.getInstance().getLogger()
						.warning("The game world has a spawn with an unknown team: " + pair.getValue());
			}
		}
	}

}
