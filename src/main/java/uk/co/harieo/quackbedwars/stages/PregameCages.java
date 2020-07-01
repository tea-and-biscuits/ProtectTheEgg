package uk.co.harieo.quackbedwars.stages;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

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

	public static void createCage(Location location) {
		for (Vector vector : cageVectors) {
			Location clonedLocation = location.clone().add(vector);
			clonedLocation.getBlock().setType(Material.GLASS);
		}
		centralCagePoints.add(location.clone());
	}

	public static void deleteCages() {
		for (Location cagePoint : centralCagePoints) {
			for (Vector vector : cageVectors) {
				cagePoint.clone().add(vector).getBlock().setType(Material.AIR);
			}
		}
	}

}
