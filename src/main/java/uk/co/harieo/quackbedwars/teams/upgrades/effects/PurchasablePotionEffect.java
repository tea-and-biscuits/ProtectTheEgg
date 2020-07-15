package uk.co.harieo.quackbedwars.teams.upgrades.effects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.teams.TeamGameData;
import uk.co.harieo.quackbedwars.teams.menu.UpgradeCategory;
import uk.co.harieo.quackbedwars.teams.upgrades.TeamUpgrade;

public enum PurchasablePotionEffect implements TeamUpgrade {

	SPEED_ONE("Speed I", "Gain a permanent Speed 1 effect", 2,
			new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0)),
	SPEED_TWO("Speed II", "Gain a permanent Speed 2 effect", 4,
			new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), SPEED_ONE),
	JUMP_ONE("Jump Boost", "Gain a permanent Jump Boost", 8,
			new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));

	private static final UpgradeCategory CATEGORY;

	static {
		ItemStack displayItem = new ItemStack(Material.GLASS_BOTTLE);
		ItemMeta meta = displayItem.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Potion Effects");
			meta.setLore(Collections.singletonList(ChatColor.GRAY + "Permanent potion effects for team members"));
			displayItem.setItemMeta(meta);
		}
		CATEGORY = new UpgradeCategory(displayItem, Arrays.asList(values()));

		for (PurchasablePotionEffect effect : values()) {
			if (effect.hasPrerequisiteEffect()) {
				effect.getPrerequisiteEffect().child = effect;
			}
		}
	}

	private final String name;
	private final String description;
	private final int diamondCost;
	private final PotionEffect potionEffect;
	private final PurchasablePotionEffect prerequisite;

	private PurchasablePotionEffect child;

	/**
	 * A permanent {@link PotionEffect} which a team can purchase for all its members
	 *
	 * @param name of this upgrade
	 * @param description of this upgrade
	 * @param diamondCost of this upgrade
	 * @param potionEffect which will be applied to all players in a purchasing team
	 * @param prerequisite which must be purchased before this upgrade, if applicable
	 */
	PurchasablePotionEffect(String name, String description, int diamondCost, PotionEffect potionEffect,
			PurchasablePotionEffect prerequisite) {
		this.name = name;
		this.description = description;
		this.diamondCost = diamondCost;
		this.potionEffect = potionEffect;
		this.prerequisite = prerequisite;
	}

	/**
	 * An overload of {@link #PurchasablePotionEffect(String, String, int, PotionEffect, PurchasablePotionEffect)} where
	 * there is no prerequisite
	 *
	 * @param name of this upgrade
	 * @param description of this upgrade
	 * @param diamondCost of this upgrade
	 * @param potionEffect which will be applied to all players
	 */
	PurchasablePotionEffect(String name, String description, int diamondCost, PotionEffect potionEffect) {
		this(name, description, diamondCost, potionEffect, null);
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
		return diamondCost;
	}

	@Override
	public boolean isUnlocked(Team team) {
		return TeamGameData.getGameData(team).getPurchasedPotionEffects().contains(this)
				&& (!hasPrerequisiteEffect() || getPrerequisiteEffect().isUnlocked(team));
	}

	@Override
	public boolean canUnlock(Team team) {
		return (!hasPrerequisiteEffect() || getPrerequisiteEffect().isUnlocked(team)) && (child == null || !child
				.isUnlocked(team));
	}

	@Override
	public void activateUpgrade(Team team) {
		TeamGameData gameData = TeamGameData.getGameData(team);
		gameData.addPurchasedPotionEffect(this);
		if (hasPrerequisiteEffect()) {
			gameData.removePurchasedPotionEffect(getPrerequisiteEffect());
		}

		team.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull)
				.forEach(player -> player.addPotionEffect(potionEffect));
	}

	/**
	 * @return the prerequisite upgrade which must be purchased before this upgrade or null if there is no prerequisite
	 */
	public PurchasablePotionEffect getPrerequisiteEffect() {
		return prerequisite;
	}

	/**
	 * @return whether {@link #getPrerequisiteEffect()} is not null
	 */
	public boolean hasPrerequisiteEffect() {
		return getPrerequisiteEffect() != null;
	}

	public static UpgradeCategory getCategory() {
		return CATEGORY;
	}

}
