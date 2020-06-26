package uk.co.harieo.quackbedwars.shops;

import org.bukkit.ChatColor;
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

public class ShopHandler {

	public static final String SHOP_SPAWN_KEY = "bedwars-shop";

	private static final Map<Villager, ShopType> shopNPCs = new HashMap<>();

	public static ShopType getShopType(Villager villager) {
		return shopNPCs.get(villager);
	}

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
					Villager villager = (Villager) world.spawnEntity(location, EntityType.VILLAGER);
					villager.setProfession(type.getVillagerProfession());
					villager.setAI(false);
					shopNPCs.put(villager, type);

					Hologram hologram = new Hologram().setLocation(location.clone().add(0, 1, 0));
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
