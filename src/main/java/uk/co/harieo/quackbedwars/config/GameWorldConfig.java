package uk.co.harieo.quackbedwars.config;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.TeamSpawnHandler;

public class GameWorldConfig {

	private final JavaPlugin plugin;
	private World lobbyWorld;
	private MapImpl gameWorld;

	private boolean loaded = false;

	public GameWorldConfig(JavaPlugin plugin, FileConfiguration config) {
		this.plugin = plugin;
		Logger logger = plugin.getLogger();

		String lobbyWorldName = config.getString("lobby-world");
		if (lobbyWorldName == null) {
			logger.severe("Failed to load lobby world: None provided");
			return;
		} else {
			lobbyWorld = Bukkit.getWorld(lobbyWorldName);
			setPeaceful(lobbyWorld);
		}

		List<String> gameWorldNameList = config.getStringList("game-worlds");
		if (gameWorldNameList.isEmpty()) {
			logger.severe("Failed to load game world: List of options is empty or doesn't exist");
		} else {
			World world = getRandomGameWorld(gameWorldNameList);
			setPeaceful(world);
			if (world == null) {
				return;
			}

			try {
				gameWorld = MapImpl.parseWorld(world);
				TeamSpawnHandler.parseSpawnLocations(gameWorld);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}

			loaded = true;
		}
	}

	private World getRandomGameWorld(List<String> gameWorldNameList) {
		Logger logger = plugin.getLogger();

		String gameWorldName = gameWorldNameList.get(ProtectTheEgg.RANDOM.nextInt(gameWorldNameList.size()));
		World world = null;

		File dataFolder = plugin.getDataFolder();
		if (dataFolder.exists()) {
			File worldFolder = new File(dataFolder, gameWorldName);
			if (worldFolder.exists()) {
				File file = new File(Bukkit.getWorldContainer(), gameWorldName);
				if (file.exists()) {
					logger.warning("Temporary world was already cached, attempting to destroy for safety");
					if (!file.delete()) {
						logger.severe("Failed to load game world: Failed to delete already cached temporary world");
						return null;
					}
				}

				try {
					FileUtils.copyDirectory(worldFolder, file);
					world = Bukkit.createWorld(new WorldCreator(gameWorldName)); // Loads the new temporary world
					logger.info("Loaded game world as temporary directory: " + gameWorldName);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				logger.severe("Failed to load game world: World '" + gameWorldName + "' not found in plugin folder");
			}
		} else {
			logger.severe("Internal file error: Expected plugin folder but none found");
		}

		return world;
	}

	private void setPeaceful(World world) {
		if (world != null) {
			world.setDifficulty(Difficulty.PEACEFUL);
		}
	}

	public World getLobbyWorld() {
		return lobbyWorld;
	}

	public MapImpl getGameMap() {
		return gameWorld;
	}

	public boolean isLoaded() {
		return loaded;
	}

}
