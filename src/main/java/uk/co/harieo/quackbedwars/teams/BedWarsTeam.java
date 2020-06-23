package uk.co.harieo.quackbedwars.teams;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

public enum BedWarsTeam {

	GREEN("Green", ChatColor.GREEN, Color.LIME, Material.GREEN_STAINED_GLASS),
	DARK_GREEN("Dark Green", ChatColor.DARK_GREEN, Color.GREEN, Material.GREEN_STAINED_GLASS),
	YELLOW("Yellow", ChatColor.YELLOW, Color.YELLOW, Material.YELLOW_STAINED_GLASS),
	RED("Red", ChatColor.RED, Color.RED, Material.RED_STAINED_GLASS),
	DARK_RED("Dark Red", ChatColor.DARK_RED, Color.RED, Material.RED_STAINED_GLASS),
	GRAY("Grey", ChatColor.GRAY, Color.GRAY, Material.LIGHT_GRAY_STAINED_GLASS),
	DARK_GRAY("Dark Grey", ChatColor.DARK_GRAY, Color.GRAY, Material.GRAY_STAINED_GLASS),
	AQUA("Aqua", ChatColor.AQUA, Color.AQUA, Material.LIGHT_BLUE_STAINED_GLASS),
	BLUE("Blue", ChatColor.BLUE, Color.BLUE, Material.BLUE_STAINED_GLASS),
	DARK_BLUE("Dark Blue", ChatColor.DARK_BLUE, Color.BLUE, Material.BLUE_STAINED_GLASS),
	PURPLE("Purple", ChatColor.LIGHT_PURPLE, Color.PURPLE, Material.PURPLE_STAINED_GLASS),
	DARK_PURPLE("Dark Purple", ChatColor.DARK_PURPLE, Color.PURPLE, Material.PURPLE_STAINED_GLASS),
	GOLD("Orange", ChatColor.GOLD, Color.ORANGE, Material.ORANGE_STAINED_GLASS);

	private final String name;
	private final ChatColor chatColor;
	private final Color armourColor;
	private final Material cageMaterial;

	private final Team team;

	BedWarsTeam(String name, ChatColor chatColor, Color armourColor, Material cageMaterial) {
		this.name = name;
		this.chatColor = chatColor;
		this.armourColor = armourColor;
		this.cageMaterial = cageMaterial;
		this.team = new Team(name, chatColor, armourColor,
				ProtectTheEgg.getInstance().getGameConfig().getPlayersPerTeam());
	}

	public String getName() {
		return name;
	}

	public ChatColor getChatColor() {
		return chatColor;
	}

	public String getFormattedName() {
		return getChatColor() + getName() + " Team";
	}

	public Color getArmourColor() {
		return armourColor;
	}

	public Material getCageMaterial() {
		return cageMaterial;
	}

	public List<UUID> getMembers() {
		return team.getTeamMembers();
	}

	public void addMember(Player player) {
		team.addTeamMember(player);
	}

	public void removeMember(Player player) {
		team.removeTeamMember(player);
	}

	public boolean isTeamActive() {
		return ordinal() < ProtectTheEgg.getInstance().getGameConfig().getMaxTeams();
	}

	@Override
	public String toString() {
		return getChatColor() + getName();
	}

	public static BedWarsTeam getByName(String name) {
		return getByPredicate(team -> team.getName().equalsIgnoreCase(name));
	}

	private static BedWarsTeam getByPredicate(Predicate<BedWarsTeam> teamPredicate) {
		for (BedWarsTeam team : values()) {
			if (teamPredicate.test(team)) {
				return team;
			}
		}

		return null;
	}

}
