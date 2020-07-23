package uk.co.harieo.quackbedwars.stages;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * A class which adds and removes blocks at given vectors from the point at which a player will be teleported to imprison
 * them before the game starts
 */
public class PregameCages {

	private static final Set<Vector> cageVectors = new HashSet<>();
	private static final Set<Location> centralCagePoints = new HashSet<>();

	static {
		cageVectors.add(new Vector(0, -1, 0));
		cageVectors.add(new Vector(1, 0, 0));
		cageVectors.add(new Vector(-1, 0, 0));
		cageVectors.add(new Vector(0, 0, 1));
		cageVectors.add(new Vector(0, 0, -1));
		cageVectors.add(new Vector(1, 1, 0));
		cageVectors.add(new Vector(-1, 1, 0));
		cageVectors.add(new Vector(0, 1, 1));
		cageVectors.add(new Vector(0, 1, -1));
		cageVectors.add(new Vector(0, 2, 0));
	}

	/**
	 * Forms a cage around the specified location
	 *
	 * @param location the point at which a player will be teleported
	 */
	public static void createCage(Location location) {
		for (Vector vector : cageVectors) {
			addVectorThenSetType(location, vector, Material.GLASS);
		}
		centralCagePoints.add(location.clone());
	}

	/**
	 * Removes all blocks around the specified location matching the cage vectors
	 *
	 * @param centralPoint where the cage is centred
	 */
	public static void deleteCage(Location centralPoint) {
		for (Vector vector : cageVectors) {
			addVectorThenSetType(centralPoint, vector, Material.AIR);
		}
	}

	/**
	 * Calls {@link #deleteCage(Location)} on all cached cages
	 */
	public static void deleteCages() {
		for (Location cagePoint : centralCagePoints) {
			deleteCage(cagePoint);
		}
	}

	/**
	 * Takes a location, clones it, adds a vector to it then sets the block at the new location to the specified material
	 *
	 * @param centralPoint to add the vector to
	 * @param toAdd to be added to the central point
	 * @param type to set the block to at the new location
	 */
	private static void addVectorThenSetType(Location centralPoint, Vector toAdd, Material type) {
		centralPoint.clone().add(toAdd).getBlock().setType(type);
	}

}
