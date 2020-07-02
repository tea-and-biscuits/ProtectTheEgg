package uk.co.harieo.quackbedwars.teams.upgrades;

import uk.co.harieo.minigames.teams.Team;

/**
 * An upgrade which a team can purchase with diamonds
 */
public interface TeamUpgrade {

	/**
	 * @return the name of this upgrade
	 */
	String getName();

	/**
	 * @return a description of what this upgrade does
	 */
	String getDescription();

	/**
	 * @return the amount this upgrade costs
	 */
	int getDiamondCost();

	/**
	 * Checks whether a team has unlocked this upgrade
	 *
	 * @param team to check
	 * @return whether the specified team has unlocked this upgrade
	 */
	boolean isUnlocked(Team team);

	/**
	 * Checks whether a team may unlock this upgrade
	 *
	 * @param team attempting to unlock this upgrade
	 * @return whether the team may unlock this upgrade
	 */
	boolean canUnlock(Team team);

	/**
	 * Executes the upgrade upon it being purchased
	 *
	 * @param team who has purchased this upgrade
	 */
	void activateUpgrade(Team team);

}
