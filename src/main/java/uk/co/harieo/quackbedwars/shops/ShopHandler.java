package uk.co.harieo.quackbedwars.shops;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;

public class ShopHandler {

	public static final String SHOP_SPAWN_KEY = "bedwars-shop";

	private static final Map<Villager, ShopType> shopNPCs = new HashMap<>();

	public static void parseShopSpawns(MapImpl map) {
		Logger logger = ProtectTheEgg.getInstance().getLogger();

		int successes = 0;
		for (LocationPair pair : map.getLocationsByKey(SHOP_SPAWN_KEY)) {
			String rawType = pair.getValue();
			try {
				ShopType type = ShopType.valueOf(rawType);
				Location location = pair.getLocation();
				World world = location.getWorld();
				if (world != null) {
					Villager villager = (Villager) world.spawnEntity(location, EntityType.VILLAGER);
					villager.setProfession(type.getVillagerProfession());
					villager.setAI(false);
					shopNPCs.put(villager, type);
					successes++;
				} else {
					logger.warning("Failed to load shop spawn because the location was missing a world");
				}
			} catch (IllegalArgumentException ignored) {
				logger.warning("Failed to load shop spawn due to invalid type: " + rawType);
			}
		}

		logger.info("Parsed " + successes + " shop NPC spawns successfully!");
	}

}
