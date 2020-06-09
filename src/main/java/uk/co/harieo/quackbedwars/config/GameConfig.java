package uk.co.harieo.quackbedwars.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class GameConfig {

	private int playersPerTeam = 1;
	private int maxTeams = 12;

	public GameConfig(JavaPlugin plugin) {
		try {
			FileConfiguration configuration = getConfiguration(plugin);
			loadFields(configuration);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private FileConfiguration getConfiguration(JavaPlugin plugin) throws IOException {
		File dataFolder = plugin.getDataFolder();
		if (!dataFolder.exists()) {
			boolean success = dataFolder.mkdir();
			if (!success) {
				throw new IOException("Failed to create data folder");
			}
		}

		File file = new File(dataFolder, "config.yml");
		if (!file.exists()) {
			try (InputStream inputStream = plugin.getResource("config.yml")) {
				if (inputStream == null) {
					throw new IOException("Failed to get config.yml as resource");
				} else {
					Files.copy(inputStream, file.toPath());
				}
			}
		}

		return YamlConfiguration.loadConfiguration(file);
	}

	private void loadFields(FileConfiguration configuration) {
		playersPerTeam = configuration.getInt("players-per-team");
		maxTeams = configuration.getInt("max-teams");
	}

	public int getPlayersPerTeam() {
		return playersPerTeam;
	}

	public int getMaxTeams() {
		return maxTeams;
	}

}
