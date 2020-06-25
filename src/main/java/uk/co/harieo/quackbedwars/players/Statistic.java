package uk.co.harieo.quackbedwars.players;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum Statistic {

	KILLS("Kills"),
	DEATHS("Deaths"),
	EGGS_BROKEN("Eggs Smashed");

	private final Map<UUID, Integer> playerStatistics = new HashMap<>();
	private final String name;

	Statistic(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setValue(Player player, int value) {
		playerStatistics.put(player.getUniqueId(), value);
	}

	public int getValue(Player player) {
		return playerStatistics.getOrDefault(player.getUniqueId(), 0);
	}

	public void addValue(Player player, int addition) {
		setValue(player, getValue(player) + addition);
	}

}
