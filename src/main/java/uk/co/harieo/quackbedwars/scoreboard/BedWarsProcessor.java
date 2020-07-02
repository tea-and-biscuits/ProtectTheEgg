package uk.co.harieo.quackbedwars.scoreboard;

import org.bukkit.entity.Player;

import java.util.*;
import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.minigames.scoreboards.tablist.modules.Affix;
import uk.co.harieo.minigames.scoreboards.tablist.modules.TabListProcessor;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;

public class BedWarsProcessor extends TabListProcessor {

	public static final BedWarsProcessor INSTANCE = new BedWarsProcessor();

	private final Map<PlayerBasedTeam, Affix> teamAffixMap = new HashMap<>();

	private BedWarsProcessor() {
		for (BedWarsTeamData teamData : BedWarsTeamData.values()) {
			PlayerBasedTeam team = teamData.getTeam();

			Affix affix = new Affix(teamData.name());

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
		PlayerBasedTeam team = ProtectTheEgg.getInstance().getTeamHandler().getTeam(player);
		if (team != null) {
			return Optional.of(teamAffixMap.get(team));
		} else {
			return Optional.empty();
		}
	}

}
