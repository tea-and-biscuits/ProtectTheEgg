package uk.co.harieo.quackbedwars.shops;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import uk.co.harieo.minigames.holograms.Hologram;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

/**
 * A handler which spawns a {@link Villager} to represent a {@link ShopMenu}
 */
public class ShopHandler {

	public static final String SHOP_SPAWN_KEY = "bedwars-shop";

	private static final Map<Villager, ShopType> shopNPCs = new HashMap<>();

	/**
	 * Retrieves the type of shop the specified villager should open, if any
	 *
	 * @param villager which may represent a shop
	 * @return the type of shop the villager should open if clicked or null if this villager isn't recognised
	 */
	public static ShopType getShopType(Villager villager) {
		return shopNPCs.get(villager);
	}

	/**
	 * Parses a map for all the locations to spawn villagers and what shops those villagers should open if clicked. For
	 * each location, a villager is loaded without AI, data from the discovered {@link ShopType} is loaded into it, a
	 * {@link Hologram} is spawned above it to show what shop is available then the villager is cached for use in the
	 * {@link ShopNPCListener}
	 *
	 * @param map to parse locations from
	 */
	public static void parseShopSpawns(MapImpl map) {
		Logger logger = ProtectTheEgg.getInstance().getLogger();

		int successes = 0;
		for (LocationPair pair : map.getLocationsByKey(SHOP_SPAWN_KEY)) {
			String rawType = pair.getValue();
			ShopType type = ShopType.getByMapValue(rawType);
			if (type != null) {
				Location location = pair.getLocation();
				World world = location.getWorld();
				if (world != null) {
					// Makes sure the entity is spawn at the absolute center of a block because it looks nicer
					Location centeredLocation = new Location(world, location.getX() + 0.5, location.getY(),
							location.getZ() + 0.5, location.getYaw(), location.getPitch());

					Villager villager = (Villager) world.spawnEntity(centeredLocation, EntityType.VILLAGER);
					villager.setProfession(type.getVillagerProfession());
					villager.setAI(false);
					shopNPCs.put(villager, type);

					Hologram hologram = new Hologram().setLocation(centeredLocation.clone().add(0, 1, 0));
					hologram.addLine(type.getColor() + type.getShopName());
					hologram.updateLines();

					successes++;
				} else {
					logger.warning("Failed to load shop spawn because the location was missing a world");
				}
			} else {
				logger.warning("Failed to load shop spawn due to invalid type: " + rawType);
			}
		}

		logger.info("Parsed " + successes + " shop NPC spawns successfully!");
	}

}
