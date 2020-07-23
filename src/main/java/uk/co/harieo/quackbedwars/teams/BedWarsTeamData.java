package uk.co.harieo.quackbedwars.teams;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import uk.co.harieo.minigames.teams.ColourGroup;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

public enum BedWarsTeamData {

	GREEN('G', "Green", ColourGroup.GREEN),
	DARK_GREEN('G', "Dark Green", ColourGroup.DARK_GREEN),
	YELLOW('Y', "Yellow", ColourGroup.YELLOW),
	RED('R', "Red", ColourGroup.RED),
	DARK_RED('R', "Dark Red", ColourGroup.DARK_RED),
	GRAY('G', "Grey", ColourGroup.GRAY),
	DARK_GRAY('G', "Dark Grey", ColourGroup.DARK_GRAY),
	AQUA('A', "Aqua", ColourGroup.AQUA),
	BLUE('B', "Blue", ColourGroup.BLUE),
	DARK_BLUE('B', "Dark Blue", ColourGroup.DARK_BLUE),
	PURPLE('P', "Purple", ColourGroup.LIGHT_PURPLE),
	DARK_PURPLE('P', "Dark Purple", ColourGroup.DARK_PURPLE),
	GOLD('O', "Orange", ColourGroup.GOLD);

	public static final Set<PlayerBasedTeam> allTeams = new HashSet<>();

	static {
		for (BedWarsTeamData data : values()) {
			allTeams.add(data.getTeam());
		}
	}

	private final char teamChar;
	private final PlayerBasedTeam team;

	/**
	 * A set of data for a {@link PlayerBasedTeam} with extra data that is required for BedWars such as a char for the
	 * scoreboard
	 *
	 * @param teamChar a character which can represent this team
	 * @param name the name of the team, excluding the word 'team'
	 * @param colourGroup the colour which represents this team
	 */
	BedWarsTeamData(char teamChar, String name, ColourGroup colourGroup) {
		this.teamChar = teamChar;
		this.team = new PlayerBasedTeam(name, colourGroup);
	}

	/**
	 * @return the character which represents this team
	 */
	public char getTeamChar() {
		return teamChar;
	}

	/**
	 * @return the colours which represent this team
	 */
	public ColourGroup getColourGroup() {
		return team.getColour();
	}

	/**
	 * @return the team object which holds the generic team information such as members
	 */
	public PlayerBasedTeam getTeam() {
		return team;
	}

	/**
	 * @return whether this team's ordinal exceeds the maximum amount of teams which can be active (set in the config
	 * file) and whether this team has at least 1 spawn location
	 */
	public boolean isTeamActive() {
		return !team.getSpawns().isEmpty();
	}

	/**
	 * @return whether the amount of members in this team exceeds or equals the maximum amount of players allowed in
	 * any team (set in the config file)
	 */
	public boolean isFull() {
		return getTeam().getMembers().size() >= ProtectTheEgg.getInstance().getGameConfig().getPlayersPerTeam();
	}

	/**
	 * Retrieves an instance of this class with {@link PlayerBasedTeam#getName()} equal, ignoring case, to the specified name
	 *
	 * @param name to match to a team
	 * @return the found team or null if none have a matching name
	 */
	public static BedWarsTeamData getByName(String name) {
		return getByPredicate(teamData -> teamData.getTeam().getName().equalsIgnoreCase(name));
	}

	/**
	 * Retrieves an instance of this class by comparing {@link #getTeam()} with the specified team
	 *
	 * @param team to get the matching data for
	 * @return the matching data or null if none was found
	 */
	public static BedWarsTeamData getByTeam(PlayerBasedTeam team) {
		// The allTeams set is a collection of all the teams from their matching data enums. If the parameter team is
		// not a part of this set, it is not from this data class.
		if (allTeams.contains(team)) {
			return getByPredicate(data -> data.getTeam().equals(team));
		} else {
			return null;
		}
	}

	/**
	 * Retrieves the first instance of this class which matches the specified predicate
	 *
	 * @param teamPredicate to compare against known teams
	 * @return the matching instance or null if none match
	 */
	private static BedWarsTeamData getByPredicate(Predicate<BedWarsTeamData> teamPredicate) {
		for (BedWarsTeamData team : values()) {
			if (teamPredicate.test(team)) {
				return team;
			}
		}

		return null;
	}

}
