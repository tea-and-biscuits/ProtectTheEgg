package uk.co.harieo.quackbedwars.teams;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

public class TeamHandler {

	private static final Map<UUID, BedWarsTeam> playerTeams = new HashMap<>();

	public static BedWarsTeam getTeam(Player player) {
		return playerTeams.get(player.getUniqueId());
	}

	public static BedWarsTeam assignTeam(Player player) {
		int maxPlayers = ProtectTheEgg.getInstance().getGameConfig().getPlayersPerTeam();
		for (BedWarsTeam team : BedWarsTeam.values()) {
			if (team.getMembers().size() < maxPlayers
					&& team.isTeamActive()
					&& TeamSpawnHandler.getSpawnLocations(team).size() > 0) {
				setTeam(player, team);
				return team;
			}
		}

		return null;
	}

	public static void setTeam(Player player, BedWarsTeam team) {
		unsetTeam(player);
		playerTeams.put(player.getUniqueId(), team);
		team.addMember(player);
		TeamGameData.getGameData(team).incrementPlayersAlive();
	}

	public static void unsetTeam(Player player) {
		UUID uuid = player.getUniqueId();
		if (playerTeams.containsKey(uuid)) {
			playerTeams.get(uuid).removeMember(player);
			playerTeams.remove(uuid);
		}
	}

}
