package uk.co.harieo.quackbedwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import uk.co.harieo.minigames.events.MinigameStartEvent;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.hotbar.Hotbar;
import uk.co.harieo.minigames.hotbar.HotbarTracker;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.menu.TeamChangeMenu;

public class LobbyHotbarListener implements Listener {

	private static final Hotbar lobbyHotbar = new Hotbar();

	private final HotbarTracker tracker = new HotbarTracker();

	public LobbyHotbarListener(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(tracker, plugin);
	}

	static {
		MenuItem teamChangeItem = new MenuItem(Material.GREEN_WOOL);
		teamChangeItem.setName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Change Team");
		teamChangeItem.setLore(Collections.singletonList(ChatColor.GRAY + "Right Click to Change Teams"));
		teamChangeItem.setOnClick(clicker -> TeamChangeMenu.INSTANCE.getOrCreateMenu(clicker).showInventory());
		lobbyHotbar.setItem(4, teamChangeItem);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (ProtectTheEgg.getInstance().getGameStage() == GameStage.LOBBY) {
			tracker.setHotbar(player, lobbyHotbar);
		}
	}

	@EventHandler
	public void onGameStart(MinigameStartEvent event) {
		tracker.clearHotbars();
	}

}
