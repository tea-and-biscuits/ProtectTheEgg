package uk.co.harieo.quackbedwars.teams.upgrades.traps;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.teams.TeamGameData;
import uk.co.harieo.quackbedwars.teams.menu.UpgradeCategory;
import uk.co.harieo.quackbedwars.teams.upgrades.TeamUpgrade;

public enum PurchasableTraps implements TeamUpgrade {

	BLINDNESS("Blindness Trap", "Blinds anyone who comes near your egg", 4,
			player -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 1))),
	MINING_FATIGUE("Fatigue Trap", "Applies Mining Fatigue to anyone who comes near your egg", 4,
			player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3, 3))),
	LIGHTNING("Lightning Trap", "Strikes the power of zeus into anyone who comes near your egg", 2,
			player -> player.getWorld().strikeLightning(player.getLocation()));

	private static final UpgradeCategory category;

	static {
		ItemStack displayItem = new ItemStack(Material.COBWEB);
		ItemMeta meta = displayItem.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Egg Traps");
			meta.setLore(Collections.singletonList(ChatColor.GRAY + "Apply effects to enemies near your egg"));
			displayItem.setItemMeta(meta);
		}
		category = new UpgradeCategory(displayItem, Arrays.asList(values()));
	}

	private final String name;
	private final String description;
	private final int cost;
	private final Consumer<Player> trigger;

	/**
	 * Represents a purchasable trap which a player can put on their egg and will trigger when another player from
	 * another team enters the egg's radius
	 *
	 * @param name of this trap
	 * @param description of this trap
	 * @param diamondCost the cost of this trap
	 * @param onTrigger what happens when the trap is triggered
	 */
	PurchasableTraps(String name, String description, int diamondCost, Consumer<Player> onTrigger) {
		this.name = name;
		this.description = description;
		this.cost = diamondCost;
		this.trigger = onTrigger;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getDiamondCost() {
		return cost;
	}

	@Override
	public boolean isUnlocked(Team team) {
		return TeamGameData.getGameData(team).getPurchasedTraps().contains(this);
	}

	@Override
	public boolean canUnlock(Team team) {
		return !isUnlocked(team);
	}

	@Override
	public void activateUpgrade(Team team) {
		TeamGameData.getGameData(team).addPurchasedTrap(this);
	}

	/**
	 * Triggers this trap onto the specified player
	 *
	 * @param player who has triggered this trap
	 */
	public void onTrigger(Player player) {
		trigger.accept(player);
	}

	public static UpgradeCategory getCategory() {
		return category;
	}

}
