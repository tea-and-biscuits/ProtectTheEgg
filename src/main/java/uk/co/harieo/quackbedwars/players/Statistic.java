package uk.co.harieo.quackbedwars.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public enum Statistic {

	KILLS("Kills"),
	DEATHS("Deaths"),
	EGGS_BROKEN("Eggs Smashed");

	private final Map<UUID, Integer> playerStatistics = new HashMap<>();
	private final String name;

	/**
	 * Holds an integer with a name for each player as a statistic
	 *
	 * @param name user-friendly name of the statistic being tracked
	 */
	Statistic(String name) {
		this.name = name;
	}

	/**
	 * @return the user friendly name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of this statistic for the specified player
	 *
	 * @param player to set the value for
	 * @param value to set the value to
	 */
	public void setValue(Player player, int value) {
		playerStatistics.put(player.getUniqueId(), value);
	}

	/**
	 * Returns the value of this statistic for the specified player
	 *
	 * @param player to get the value for
	 * @return the stored value or 0 by default
	 */
	public int getValue(Player player) {
		return playerStatistics.getOrDefault(player.getUniqueId(), 0);
	}

	/**
	 * Adds an amount to the current value for a player
	 *
	 * @param player to set the value for
	 * @param addition the amount to add to the current value
	 */
	public void addValue(Player player, int addition) {
		setValue(player, getValue(player) + addition);
	}

	/**
	 * Retrieves the online player who has the highest value for this statistic. If all stored players are offline, this
	 * will return null instead.
	 *
	 * @return the player with the highest stored value or null if none are online
	 */
	public Player getHighestValuePlayer() {
		Player player = null;
		int highestAmount = -1;
		for (Entry<UUID, Integer> entries : playerStatistics.entrySet()) {
			int amount = entries.getValue();
			if (amount > highestAmount || player == null) {
				player = Bukkit.getPlayer(entries.getKey());
				highestAmount = amount;
			}
		}

		return player;
	}

}
