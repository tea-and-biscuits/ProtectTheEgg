package uk.co.harieo.quackbedwars.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;
import uk.co.harieo.quackbedwars.players.Statistic;

public class StatisticsElement implements RenderableElement {

	private final Statistic statistic;

	public StatisticsElement(Statistic statistic) {
		this.statistic = statistic;
	}

	@Override
	public String getText(Player player) {
		return ChatColor.WHITE + String.valueOf(statistic.getValue(player));
	}

}
