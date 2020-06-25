package uk.co.harieo.quackbedwars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;
import java.util.Set;
import uk.co.harieo.minigames.commands.CommandUtils;
import uk.co.harieo.minigames.commands.Subcommand;
import uk.co.harieo.minigames.maps.MetadataSubcommand;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.commands.maps.*;

public class MapCommand implements CommandExecutor {

	private static final Set<Subcommand> subcommands = Sets
			.newHashSet(new ResourceSpawnSubcommands(), new EggSpawnSubcommands(),
					new MetadataSubcommand(ProtectTheEgg.PREFIX), new PlayerSpawnSubcommands(), new MapInfoSubcommand(),
					new ShopNPCSubcommands());

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!sender.hasPermission("quacktopia.minigames.maps")) {
			sender.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "You do not have permission to do that!"));
		} else if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can set locations!");
		} else {
			Player player = (Player) sender;
			if (args.length < 1) {
				player.sendMessage(ProtectTheEgg
						.formatMessage(ChatColor.RED + "Insufficient Arguments. Expected: /maps <subcommand>"));
			} else {
				String subCommand = args[0];
				Set<Subcommand> chosenSubcommands = CommandUtils.matchSubcommands(subCommand, subcommands);

				if (chosenSubcommands.isEmpty()) {
					player.sendMessage(ProtectTheEgg.formatMessage(ChatColor.RED + "Unknown Subcommad: " + subCommand));
				} else {
					chosenSubcommands.forEach(executor -> executor.onSubcommand(sender, subCommand, args));
				}
			}
		}
		return false;
	}

}
