package uk.co.harieo.quackbedwars.teams.menu;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import uk.co.harieo.minigames.menus.MenuFactory;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;

public class TeamChangeMenu extends MenuFactory {

	public static final TeamChangeMenu INSTANCE = new TeamChangeMenu();

	private TeamChangeMenu() {
		super("Change Teams", BedWarsTeamData.values().length / 9 + 1);
		registerDefaultInteractionListener(ProtectTheEgg.getInstance());
	}

	@Override
	public void setPlayerItems(Player player, int page) {
		int index = 0;
		for (BedWarsTeamData teamData : BedWarsTeamData.values()) {
			Team team = teamData.getTeam();
			if (teamData.isTeamActive()) {
				MenuItem item = new MenuItem(teamData.getCageMaterial());
				item.setName(team.getChatColor() + ChatColor.BOLD.toString() + "Join " + team.getName() + " Team");

				String lore;
				if (teamData.isFull()) {
					lore = ChatColor.RED + "Full";
				} else {
					lore = ChatColor.GRAY + "Click to Join";
				}

				item.setLore(Collections.singletonList(lore));
				item.setOnClick(clicker -> {
					clicker.chat("/team " + team.getName());
					player.getOpenInventory().close();
				});

				setItem(player, index, item);
				index++;
			}
		}
	}

}
