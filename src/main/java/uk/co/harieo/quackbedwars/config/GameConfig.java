package uk.co.harieo.quackbedwars.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import uk.co.harieo.quackbedwars.shops.config.ItemsConfig;

public class GameConfig {

	private int playersPerTeam = 1;
	private int maxTeams = 12;
	private List<String> timerMessages;
	private GameWorldConfig gameWorldConfig;
	private ItemsConfig itemsConfig;

	public GameConfig(JavaPlugin plugin) {
		try {
			FileConfiguration configuration = getConfiguration(plugin, "config.yml");
			loadFields(configuration);
			gameWorldConfig = new GameWorldConfig(plugin, configuration);
			itemsConfig = new ItemsConfig(plugin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadFields(FileConfiguration configuration) {
		playersPerTeam = configuration.getInt("players-per-team");
		maxTeams = configuration.getInt("max-teams");
		timerMessages = configuration.getStringList("timer-messages");
	}

	public int getPlayersPerTeam() {
		return playersPerTeam;
	}

	public int getMaxTeams() {
		return maxTeams;
	}

	public List<String> getTimerMessages() {
		return timerMessages;
	}

	public GameWorldConfig getGameWorldConfig() {
		return gameWorldConfig;
	}

	public static FileConfiguration getConfiguration(JavaPlugin plugin, String fileName) throws IOException {
		File dataFolder = plugin.getDataFolder();
		if (!dataFolder.exists()) {
			boolean success = dataFolder.mkdir();
			if (!success) {
				throw new IOException("Failed to create data folder");
			}
		}

		File file = new File(dataFolder, fileName);
		if (!file.exists()) {
			try (InputStream inputStream = plugin.getResource(fileName)) {
				if (inputStream == null) {
					throw new IOException("Failed to get config.yml as resource");
				} else {
					Files.copy(inputStream, file.toPath());
				}
			}
		}

		return YamlConfiguration.loadConfiguration(file);
	}

}
