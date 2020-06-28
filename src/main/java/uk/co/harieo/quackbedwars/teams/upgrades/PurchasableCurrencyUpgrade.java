package uk.co.harieo.quackbedwars.teams.upgrades;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import uk.co.harieo.quackbedwars.currency.Currency;
import uk.co.harieo.quackbedwars.currency.CurrencySpawnRate;
import uk.co.harieo.quackbedwars.teams.BedWarsTeam;
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

	public static UpgradeCategory CATEGORY;

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

		CATEGORY = new UpgradeCategory(categoryItem, Arrays.asList(values()));
	}

	private final String name;
	private String description;
	private final int diamondCost;
	private final Map<Currency, CurrencySpawnRate> spawnRates = new HashMap<>(); // Key prevents duplicate spawn rates
	private final PurchasableCurrencyUpgrade parent;
	private PurchasableCurrencyUpgrade child;

	PurchasableCurrencyUpgrade(String name, int diamondCost, PurchasableCurrencyUpgrade parent, CurrencySpawnRate... newSpawnRates) {
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
	public boolean isUnlocked(BedWarsTeam team) {
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
	public boolean canUnlock(BedWarsTeam team) {
		PurchasableCurrencyUpgrade parent = getParent();
		if (parent != null) {
			return parent.isUnlocked(team); // If the upgrade before this is unlocked, this one is available
		} else {
			return !isUnlocked(team); // This is likely the first available upgrade
		}
	}

	@Override
	public void activateUpgrade(BedWarsTeam team) {
		TeamGameData.getGameData(team).setCurrencyUpgrade(this);
	}

	@Override
	public Map<Currency, CurrencySpawnRate> getChangedSpawnRates() {
		return spawnRates;
	}

	public PurchasableCurrencyUpgrade getParent() {
		return parent;
	}

	public PurchasableCurrencyUpgrade getChild() {
		return child;
	}

	private void setChild(PurchasableCurrencyUpgrade child) {
		this.child = child;
	}

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
			if (iterations + 2 < upgradesSize) {
				descriptionBuilder.append(ChatColor.GRAY);
				descriptionBuilder.append(", ");
			} else if (iterations + 1 < upgradesSize) {
				descriptionBuilder.append(ChatColor.GRAY);
				descriptionBuilder.append(" and ");
			}

			iterations++;
		}

		descriptionBuilder.append(ChatColor.GRAY);
		descriptionBuilder.append(" Production");
		this.description = descriptionBuilder.toString();
	}

}
