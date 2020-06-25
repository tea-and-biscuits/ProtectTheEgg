package uk.co.harieo.quackbedwars.commands.maps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;
import java.util.Set;
import uk.co.harieo.minigames.commands.Subcommand;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.shops.ShopType;
import uk.co.harieo.quackbedwars.shops.ShopHandler;

public class ShopNPCSubcommands implements Subcommand {

	@Override
	public Set<String> getSubcommandAliases() {
		return Sets.newHashSet("setshop", "deleteshop");
	}

	@Override
	public String getUsage() {
		return "<setshop/deleteshop> [type]";
	}

	@Override
	public String getRequiredPermission() {
		return null;
	}

	@Override
	public void onSubcommand(CommandSender sender, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
		}
	}

	/**
	 * The 'setshop' sub-command which allows a player to set their current location as a {@link ShopType} where an NPC
	 * will spawn
	 *
	 * @param player who has issued the command
	 * @param args supplied with the command
	 */
	private void setShopSubcommand(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(ProtectTheEgg
					.formatMessage(ChatColor.RED + "Insufficient Arguments. Expected: /maps setshop <type>"));
		} else {
			String rawType = args[1].toUpperCase();

			ShopType type;
			try {
				type = ShopType.valueOf(rawType);
			} catch (IllegalArgumentException ignored) {
				player.sendMessage(ProtectTheEgg
						.formatMessage(ChatColor.RED + "You have provided an invalid shop type: " + rawType));
				sendAllTypes(player);
				return;
			}

			MapImpl map = MapImpl.get(player.getWorld());
			Location location = player.getLocation();
			for (LocationPair pair : map.getLocationPairs(location)) {
				if (isShopLocation(pair)) {
					player.sendMessage(
							ProtectTheEgg.formatMessage(ChatColor.RED + "This is already the location of a shop!"));
					return;
				}
			}

			map.addLocation(location, ShopHandler.SHOP_SPAWN_KEY, type.getMapValue());
		}
	}

	/**
	 * The 'deleteshop' sub-command which allows a player to delete any shop NPC spawn marked at their location
	 *
	 * @param player who has issued the command
	 */
	private void deleteShopSubcommand(Player player) {
		MapImpl map = MapImpl.get(player.getWorld());

		boolean removedOnce = false;
		for (LocationPair pair : map.getLocationPairs(player.getLocation())) {
			if (isShopLocation(pair)) {
				map.removeLocation(pair);
				removedOnce = true;
			}
		}

		if (removedOnce) {
			player.sendMessage(
					ProtectTheEgg.formatMessage(ChatColor.GRAY + "Deleted the shop NPC spawn at your location!"));
		} else {
			player.sendMessage(
					ProtectTheEgg.formatMessage(ChatColor.RED + "There was no NPC spawn marked at your location!"));
		}
	}

	/**
	 * Checks whether a {@link LocationPair} marks the location of a shop NPC
	 *
	 * @param pair to be checked
	 * @return whether there is a shop location marked in the specified pair
	 */
	private boolean isShopLocation(LocationPair pair) {
		return pair.getKey().equals(ShopHandler.SHOP_SPAWN_KEY);
	}

	private String typesString; // Cache this string as its contents are constant (based off an enum)

	/**
	 * Sends a message to the player to inform them of all the possible {@link ShopType} instances they can choose from
	 *
	 * @param player to send the message to
	 */
	private void sendAllTypes(Player player) {
		if (typesString == null) {
			StringBuilder builder = new StringBuilder();
			ShopType[] types = ShopType.values();

			for (int i = 0; i < types.length; i++) {
				builder.append(types[i].name().toLowerCase());
				if (i + 1 < types.length) {
					builder.append(", ");
				}
			}
			typesString = builder.toString();
		}

		player.sendMessage(ProtectTheEgg
				.formatMessage(ChatColor.GREEN + "Available Shop Types: " + ChatColor.GRAY + typesString));
	}

}
