package uk.co.harieo.quackbedwars.egg;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;
import uk.co.harieo.quackbedwars.teams.TeamGameData;

public class EggData {

	public static final String EGG_KEY = "bedwars-egg";

	private static final Map<Block, EggData> eggDataCache = new HashMap<>();

	private final Block eggBlock;
	private final PlayerBasedTeam team;
	private boolean intact = true;

	/**
	 * A class which stores the block of a dragon egg and whether the egg is intact
	 *
	 * @param block to put the egg at
	 * @param team the team which this egg belongs to
	 */
	public EggData(Block block, PlayerBasedTeam team) {
		this.eggBlock = block;
		this.team = team;
		eggDataCache.put(block, this);
	}

	/**
	 * @return the block where the dragon egg is
	 */
	public Block getEggBlock() {
		return eggBlock;
	}

	/**
	 * @return whether the egg is intact
	 */
	public boolean isIntact() {
		return intact;
	}

	/**
	 * Sets whether the dragon egg is intact or broken
	 *
	 * @param intact whether the egg is intact
	 */
	public void setIntact(boolean intact) {
		this.intact = intact;
	}

	/**
	 * @return the team which owns this egg
	 */
	public PlayerBasedTeam getTeam() {
		return team;
	}

	/**
	 * Sets the material of the {@link #getEggBlock()} to a dragon egg and the block underneath to bedrock
	 */
	public void setBlockMaterial() {
		setEggMaterial(Material.DRAGON_EGG);
	}

	/**
	 * Sets the egg block as empty, removing any existing dragon egg
	 */
	public void removeEgg() {
		setEggMaterial(Material.AIR);
	}

	private void setEggMaterial(Material material) {
		eggBlock.setType(material);
	}

	/**
	 * Parses a map and retrieves all the locations where eggs spawn along with the team they belong to
	 *
	 * @param map to parse locations from
	 */
	public static void parseEggLocations(MapImpl map) {
		Logger logger = ProtectTheEgg.getInstance().getLogger();

		int successes = 0;
		for (LocationPair pair : map.getLocationsByKey(EGG_KEY)) {
			String teamName = pair.getValue();
			try {
				BedWarsTeamData teamData = BedWarsTeamData.valueOf(teamName);
				PlayerBasedTeam team = teamData.getTeam();
				Block block = pair.getLocation().getBlock();

				EggData eggData = new EggData(block, team);
				TeamGameData.getGameData(team).setEggData(eggData);
				successes++;
			} catch (IllegalArgumentException ignored) {
				logger.warning("Failed to load egg location, invalid team: " + teamName);
			}
		}

		logger.info("Loaded " + successes + " egg locations successfully!");
	}

	/**
	 * Retrieves an instance of this class based on its block
	 *
	 * @param block to compare with cached instances
	 * @return a matching instance or null if none found
	 */
	public static EggData getFromCachedBlock(Block block) {
		return eggDataCache.get(block);
	}

	/**
	 * @return a map of all cached eggs and their data
	 */
	public static Map<Block, EggData> getCachedEggs() {
		return eggDataCache;
	}

}
