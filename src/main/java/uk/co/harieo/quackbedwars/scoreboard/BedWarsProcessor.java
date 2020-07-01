package uk.co.harieo.quackbedwars.scoreboard;

import org.bukkit.entity.Player;

import java.util.*;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.minigames.scoreboards.tablist.modules.Affix;
import uk.co.harieo.minigames.scoreboards.tablist.modules.TabListProcessor;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
import uk.co.harieo.quackbedwars.teams.handlers.TeamHandler;

public class BedWarsProcessor extends TabListProcessor {

	public static final BedWarsProcessor INSTANCE = new BedWarsProcessor();

	private final Map<BedWarsTeam, Affix> teamAffixMap = new HashMap<>();

	private BedWarsProcessor() {
		for (BedWarsTeam team : BedWarsTeam.values()) {
			Affix affix = new Affix(team.name());
			ChatColor teamColor = team.getChatColor();
			String teamName = teamColor + team.getName() + " ";
			if (teamName.length() > 15) {
				affix.setPrefix(teamColor.toString());
			} else {
				affix.setPrefix(teamName);
			}

			teamAffixMap.put(team, affix);
		}
	}

	@Override
	protected List<Affix> getInitialAffixes() {
		return new ArrayList<>(teamAffixMap.values());
	}

	@Override
	public Optional<Affix> getAffixForPlayer(Player player) {
		BedWarsTeam team = TeamHandler.getTeam(player);
		if (team != null) {
			return Optional.of(teamAffixMap.get(team));
		} else {
			return Optional.empty();
		}
	}

}
