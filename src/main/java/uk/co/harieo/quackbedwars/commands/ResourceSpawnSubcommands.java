package uk.co.harieo.quackbedwars.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnHandler;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;

public class ResourceSpawnSubcommands {

	// Note: Argument array includes the base sub-command

	public static void setResourceSpawn(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.RED + "Insufficient Arguments. Expected: /maps setresource <resource> [team]"));
		} else {
			Currency currency;
			try {
				currency = Currency.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException ignored) {
				player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "Unknown currency: " + args[1]));
				return;
			}

			String rawTeam = null;
			BedWarsTeam team = null;
			if (args.length > 2) {
				StringBuilder builder = new StringBuilder();
				for (int i = 2; i < args.length; i++) {
					builder.append(args[i]);
					builder.append(" ");
				}
				rawTeam = builder.toString().trim();
			}

			if (currency.isTeamBased()) {
				if (rawTeam == null) {
					player.sendMessage(ProtectTheEgg.formatMessage(
							ChatColor.RED + "This currency requires that you specify which team it is for!"));
					return;
				} else {
					team = BedWarsTeam.getByName(rawTeam);
					if (team == null) {
						player.sendMessage(
								ProtectTheEgg.formatMessage(ChatColor.RED + "Unable to find team: " + rawTeam));
						return;
					}
				}
			}

			boolean teamSelected = team != null;

			MapImpl map = MapImpl.get(player.getWorld());
			map.addLocation(player.getLocation(), CurrencySpawnHandler.SPAWN_KEY,
					currency.name() + "," + (teamSelected ? team.name() : "none"));
			player.sendMessage(ProtectTheEgg.formatMessage(
					ChatColor.GRAY + "Set this location as " + currency.getColor() + currency.getName() + " Spawner"
							+ ChatColor.GRAY + (teamSelected ? " for the " + team.getFormattedName() : "")));
		}
	}

}
