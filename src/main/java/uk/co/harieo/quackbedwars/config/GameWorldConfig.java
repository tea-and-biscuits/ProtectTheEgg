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
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnHandler;
import uk.co.harieo.quackbedwars.egg.EggData;
import uk.co.harieo.quackbedwars.shops.ShopHandler;
import uk.co.harieo.quackbedwars.teams.TeamSpawnHandler;

public class GameWorldConfig {

	private final JavaPlugin plugin;
	private World lobbyWorld;
	private MapImpl gameWorld;
	private File gameWorldDirectory;

	private boolean loaded = false;

	/**
	 * A handler which reads the config.yml file and parses selected worlds for all necessary game locations/data
	 *
	 * @param plugin which is running this game
	 * @param config which represents the config.yml file
	 */
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
				CurrencySpawnHandler.parseSpawnerLocations(gameWorld);
				EggData.parseEggLocations(gameWorld);
				ShopHandler.parseShopSpawns(gameWorld);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}

			loaded = true;
		}
	}

	/**
	 * Retrieves a random world name from a specified list of possibilities then retrives that world from Bukkit, loading
	 * it if necessary
	 *
	 * @param gameWorldNameList the list of world name possibilities
	 * @return the loaded game world
	 */
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
					try {
						FileUtils.deleteDirectory(file);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}

				try {
					FileUtils.copyDirectory(worldFolder, file);
					this.gameWorldDirectory = file;
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

	/**
	 * Sets a {@link World} to Peaceful difficulty if it's not null
	 *
	 * @param world to set the difficulty of
	 */
	private void setPeaceful(World world) {
		if (world != null) {
			world.setDifficulty(Difficulty.PEACEFUL);
		}
	}

	/**
	 * @return the world for players to spawn into in the lobby
	 */
	public World getLobbyWorld() {
		return lobbyWorld;
	}

	/**
	 * @return the game map with all parsed locations in it
	 */
	public MapImpl getGameMap() {
		return gameWorld;
	}

	/**
	 * @return whether both the lobby world and game map have been loaded successfully
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Unloads the temporary game world then deletes it. This allows it to be re-created from the original.
	 *
	 * @param plugin which is requesting the temporary world be unloaded
	 */
	public void unloadTemporaryWorld(JavaPlugin plugin) {
		World gameWorld = this.gameWorld.getWorld();
		if (gameWorld != null) {
			Bukkit.unloadWorld(gameWorld, false);
			try {
				FileUtils.deleteDirectory(gameWorldDirectory);
				plugin.getLogger().info("Deleted temporary game world successfully!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
