package uk.co.harieo.quackbedwars.teams;

import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

public enum BedWarsTeamData {

	GREEN('G', "Green", ChatColor.GREEN, Color.LIME, Material.GREEN_STAINED_GLASS),
	DARK_GREEN('G', "Dark Green", ChatColor.DARK_GREEN, Color.GREEN, Material.GREEN_STAINED_GLASS),
	YELLOW('Y', "Yellow", ChatColor.YELLOW, Color.YELLOW, Material.YELLOW_STAINED_GLASS),
	RED('R', "Red", ChatColor.RED, Color.RED, Material.RED_STAINED_GLASS),
	DARK_RED('R', "Dark Red", ChatColor.DARK_RED, Color.RED, Material.RED_STAINED_GLASS),
	GRAY('G', "Grey", ChatColor.GRAY, Color.GRAY, Material.LIGHT_GRAY_STAINED_GLASS),
	DARK_GRAY('G', "Dark Grey", ChatColor.DARK_GRAY, Color.GRAY, Material.GRAY_STAINED_GLASS),
	AQUA('A', "Aqua", ChatColor.AQUA, Color.AQUA, Material.LIGHT_BLUE_STAINED_GLASS),
	BLUE('B', "Blue", ChatColor.BLUE, Color.BLUE, Material.BLUE_STAINED_GLASS),
	DARK_BLUE('B', "Dark Blue", ChatColor.DARK_BLUE, Color.BLUE, Material.BLUE_STAINED_GLASS),
	PURPLE('P', "Purple", ChatColor.LIGHT_PURPLE, Color.PURPLE, Material.PURPLE_STAINED_GLASS),
	DARK_PURPLE('P', "Dark Purple", ChatColor.DARK_PURPLE, Color.PURPLE, Material.PURPLE_STAINED_GLASS),
	GOLD('O', "Orange", ChatColor.GOLD, Color.ORANGE, Material.ORANGE_STAINED_GLASS);

	public static final Set<PlayerBasedTeam> allTeams = new HashSet<>();

	static {
		for (BedWarsTeamData data : values()) {
			allTeams.add(data.getTeam());
		}
	}

	private final char teamChar;
	private final Material cageMaterial;
	private final PlayerBasedTeam team;

	/**
	 * A set of data for a {@link PlayerBasedTeam} with extra data that is required for BedWars such as a char for the
	 * scoreboard
	 *
	 * @param teamChar a character which can represent this team
	 * @param name the name of the team, excluding the word 'team'
	 * @param chatColor the colour which represents this team
	 * @param armourColor the colour which represents this team for objects which use the wider range of colours
	 * @param cageMaterial the default material that players in this team should be caged in pre-game
	 */
	BedWarsTeamData(char teamChar, String name, ChatColor chatColor, Color armourColor, Material cageMaterial) {
		this.teamChar = teamChar;
		this.cageMaterial = cageMaterial;
		this.team = new PlayerBasedTeam(name, chatColor, armourColor);
	}

	/**
	 * @return the character which represents this team
	 */
	public char getTeamChar() {
		return teamChar;
	}

	/**
	 * @return the default cage material for this team
	 */
	public Material getCageMaterial() {
		return cageMaterial;
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
		return ordinal() < ProtectTheEgg.getInstance().getGameConfig().getMaxTeams()
				&& !team.getSpawns().isEmpty();
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
