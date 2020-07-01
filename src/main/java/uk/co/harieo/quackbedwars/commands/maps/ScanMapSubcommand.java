package uk.co.harieo.quackbedwars.commands.maps;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;
import java.util.Set;
import uk.co.harieo.minigames.commands.Subcommand;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.config.GameWorldConfig;

public class ScanMapSubcommand implements Subcommand {

	@Override
	public Set<String> getSubcommandAliases() {
		return Sets.newHashSet("scan");
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getRequiredPermission() {
		return null;
	}

	@Override
	public void onSubcommand(CommandSender sender, String label, String[] args) {
		Player player = (Player) sender;
		MapImpl map = MapImpl.get(player.getWorld());
		sender.sendMessage(ProtectTheEgg.formatMessage(ChatColor.GRAY + "Parsing your current world..."));
		GameWorldConfig.parseMap(map);
		sender.sendMessage(ProtectTheEgg.formatMessage(ChatColor.GREEN + "Parsed. See console for detailed rundown!"));
	}

}
