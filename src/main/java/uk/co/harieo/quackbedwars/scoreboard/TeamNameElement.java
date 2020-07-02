package uk.co.harieo.quackbedwars.scoreboard;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

public class TeamNameElement implements RenderableElement {

	private final boolean bold;

	/**
	 * An element which shows the name of this team with its chat colour
	 *
	 * @param bold whether the name should be bold as well as coloured
	 */
	public TeamNameElement(boolean bold) {
		this.bold = bold;
	}

	@Override
	public String getText(Player player) {
		PlayerBasedTeam team = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);
		return team == null ? "No Team"
				: team.getChatColor() + (bold ? ChatColor.BOLD.toString() : "") + team.getName();
	}

}
