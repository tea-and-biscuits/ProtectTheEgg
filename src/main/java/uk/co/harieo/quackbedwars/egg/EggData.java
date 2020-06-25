package uk.co.harieo.quackbedwars.egg;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.logging.Logger;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

public class EggData {

	public static final String EGG_KEY = "bedwars-egg";

	private final Block eggBlock;
	private boolean intact = true;

	public EggData(Block block) {
		this.eggBlock = block;
	}

	public Block getEggBlock() {
		return eggBlock;
	}

	public boolean isIntact() {
		return intact;
	}

	public void setIntact(boolean intact) {
		this.intact = intact;
	}

	public void setBlockMaterial() {
		eggBlock.setType(Material.DRAGON_EGG);
	}

	public static void parseEggLocations(MapImpl map) {
		Logger logger = ProtectTheEgg.getInstance().getLogger();

		int successes = 0;
		for (LocationPair pair : map.getLocationsByKey(EGG_KEY)) {
			String teamName = pair.getValue();
			try {
				BedWarsTeam team = BedWarsTeam.valueOf(teamName);
				Block block = pair.getLocation().getBlock();

				EggData eggData = new EggData(block);
				eggData.setBlockMaterial();
				TeamGameData.getGameData(team).setEggData(eggData);
				successes++;
			} catch (IllegalArgumentException ignored) {
				logger.warning("Failed to load egg location, invalid team: " + teamName);
			}
		}

		logger.info("Loaded " + successes + " egg locations successfully!");
	}

}
