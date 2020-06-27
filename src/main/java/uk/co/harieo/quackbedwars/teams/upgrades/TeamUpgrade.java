package uk.co.harieo.quackbedwars.teams.upgrades;

import uk.co.harieo.quackbedwars.teams.BedWarsTeam;

public interface TeamUpgrade {

	String getName();

	String getDescription();

	int getDiamondCost();

	boolean isUnlocked(BedWarsTeam team);

	void activateUpgrade(BedWarsTeam team);

}
