package uk.co.harieo.quackbedwars.teams.upgrades.currency;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableMap;
import java.util.*;
import java.util.Map.Entry;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;
import uk.co.harieo.quackbedwars.teams.BedWarsTeamData;
import uk.co.harieo.quackbedwars.teams.TeamGameData;
import uk.co.harieo.quackbedwars.teams.menu.UpgradeCategory;

public enum PurchasableCurrencyUpgrade implements CurrencyUpgrade {

	RESOURCES_ONE("Resources I", 2, new CurrencySpawnRate(Currency.IRON, 3, 1)),
	RESOURCES_TWO("Resources II", 4, RESOURCES_ONE,
			new CurrencySpawnRate(Currency.IRON, 2, 1),
			new CurrencySpawnRate(Currency.GOLD, 3, 1)),
	RESOURCES_THREE("Resources III", 6, RESOURCES_TWO,
			new CurrencySpawnRate(Currency.IRON, 1, 1),
			new CurrencySpawnRate(Currency.GOLD, 3, 2)),
	RESOURCES_FOUR("Resources IV", 8, RESOURCES_THREE, new CurrencySpawnRate(Currency.EMERALD, 3, 2));

	private static final UpgradeCategory category;

	static {
		// Set the children here to avoid backwards referencing
		for (PurchasableCurrencyUpgrade upgrade : values()) {
			PurchasableCurrencyUpgrade parent = upgrade.getParent();
			if (parent != null) {
				parent.setChild(upgrade);
			}
		}

		ItemStack categoryItem = new ItemStack(Material.DIAMOND);
		ItemMeta meta = categoryItem.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Currency Upgrades");
			meta.setLore(Collections.singletonList(ChatColor.GRAY + "Upgrade your Resource Spawner"));
			categoryItem.setItemMeta(meta);
		}

		category = new UpgradeCategory(categoryItem, Arrays.asList(values()));
	}

	private final String name;
	private String description;
	private final int diamondCost;
	private final Map<Currency, CurrencySpawnRate> spawnRates = new HashMap<>(); // Key prevents duplicate spawn rates
	private final PurchasableCurrencyUpgrade parent;
	private PurchasableCurrencyUpgrade child;

	/**
	 * An upgrade which a {@link BedWarsTeamData} can purchase to upgrade their individual currency spawner
	 *
	 * @param name of this upgrade
	 * @param diamondCost which this upgrade costs
	 * @param parent the parent upgrade, if applicable
	 * @param newSpawnRates the spawn rates which this adds or increases upon purchase
	 */
	PurchasableCurrencyUpgrade(String name, int diamondCost, PurchasableCurrencyUpgrade parent,
			CurrencySpawnRate... newSpawnRates) {
		this.name = name;
		this.diamondCost = diamondCost;
		this.parent = parent;

		// Add the new ones for this upgrade
		for (CurrencySpawnRate spawnRate : newSpawnRates) {
			this.spawnRates.put(spawnRate.getCurrency(), spawnRate);
		}

		// Form a description based on what currencies are being upgraded (excluding parent upgrades)
		setDescription();

		if (parent != null) {
			this.spawnRates.putAll(parent.getChangedSpawnRates()); // Add all the upgrades from parents
		}
	}

	/**
	 * An overload of this class which has no parent
	 *
	 * @param name of this upgrade
	 * @param diamondCost which this upgrade costs
	 * @param newSpawnRates the spawn rates which this adds or increases upon purchase
	 */
	PurchasableCurrencyUpgrade(String name, int diamondCost, CurrencySpawnRate... newSpawnRates) {
		this(name, diamondCost, null, newSpawnRates);
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
		TeamGameData gameData = TeamGameData.getGameData(team);
		CurrencyUpgrade currentUpgrade = gameData.getCurrencyUpgrade();
		if (currentUpgrade == this) {
			return true;
		} else {
			PurchasableCurrencyUpgrade child = getChild();
			while (child != null) {
				if (child == currentUpgrade) {
					return true;
				}
				child = child.getChild();
			}

			return false;
		}
	}

	@Override
	public boolean canUnlock(Team team) {
		PurchasableCurrencyUpgrade parent = getParent();
		if (parent != null) {
			return parent.isUnlocked(team); // If the upgrade before this is unlocked, this one is available
		} else {
			return !isUnlocked(team); // This is likely the first available upgrade
		}
	}

	@Override
	public void activateUpgrade(Team team) {
		TeamGameData.getGameData(team).setCurrencyUpgrade(this);
	}

	@Override
	public Map<Currency, CurrencySpawnRate> getChangedSpawnRates() {
		return ImmutableMap.copyOf(spawnRates);
	}

	/**
	 * @return the parent of this upgrade or null if no parent exists
	 */
	public PurchasableCurrencyUpgrade getParent() {
		return parent;
	}

	/**
	 * @return the child of this upgrade or null if none exists
	 */
	public PurchasableCurrencyUpgrade getChild() {
		return child;
	}

	/**
	 * Sets this upgrade's child
	 *
	 * @param child of this upgrade
	 */
	private void setChild(PurchasableCurrencyUpgrade child) {
		this.child = child;
	}

	/**
	 * Creates a default description for this upgrade based on its spawn rates. This is not done in the constructor
	 * because it is a sizable method, which impacts the readability.
	 */
	private void setDescription() {
		StringBuilder descriptionBuilder = new StringBuilder();
		descriptionBuilder.append(ChatColor.GRAY);
		descriptionBuilder.append("Increases your ");

		Set<Currency> upgradedCurrencies = spawnRates.keySet();
		int upgradesSize = upgradedCurrencies.size();
		int iterations = 0;

		for (Currency currency : upgradedCurrencies) {
			descriptionBuilder.append(currency.getColor());
			descriptionBuilder.append(currency.getName());
			if (iterations + 2 < upgradesSize) { // If there are at least 2 iterations left
				descriptionBuilder.append(ChatColor.GRAY);
				descriptionBuilder.append(", ");
			} else if (iterations + 1 < upgradesSize) { // If this is the second to last iteration
				descriptionBuilder.append(ChatColor.GRAY);
				descriptionBuilder.append(" and ");
			}

			iterations++;
		}

		descriptionBuilder.append(ChatColor.GRAY);
		descriptionBuilder.append(" Production");
		this.description = descriptionBuilder.toString();
	}

	/**
	 * @return the upgrade category which shows these upgrades
	 */
	public static UpgradeCategory getCategory() {
		return category;
	}

}
