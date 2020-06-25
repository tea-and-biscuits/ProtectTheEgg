package uk.co.harieo.quackbedwars.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;
import uk.co.harieo.quackbedwars.players.DeathTracker;

public class PlayersLeftElement implements RenderableElement {

	@Override
	public String getText(Player player) {
		return ChatColor.WHITE + String.valueOf(DeathTracker.getLivingPlayers().size());
	}

}
