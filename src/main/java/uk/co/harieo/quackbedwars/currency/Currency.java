package uk.co.harieo.quackbedwars.currency;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Currency {

	IRON("Iron", ChatColor.WHITE, Material.IRON_INGOT, 4, 1, true),
	GOLD("Gold", ChatColor.GOLD, Material.GOLD_INGOT, 5, 1, true),
	DIAMOND("Diamond", ChatColor.BLUE, Material.DIAMOND, 20, 1, false),
	EMERALD("Emerald", ChatColor.GREEN, Material.EMERALD, 20, 1, false);

	private final String name;
	private final ChatColor color;
	private final Material material;
	private final CurrencySpawnRate baseSpawnRate;
	private final boolean teamBased;

	Currency(String name, ChatColor color, Material itemMaterial, int baseSecondsPerSpawn, int baseAmountPerSpawn,
			boolean isTeamBased) {
		this.name = name;
		this.color = color;
		this.material = itemMaterial;
		this.baseSpawnRate = new CurrencySpawnRate(this, baseSecondsPerSpawn, baseAmountPerSpawn);
		this.teamBased = isTeamBased;
	}

	public String getName() {
		return name;
	}

	public ChatColor getColor() {
		return color;
	}

	public Material getMaterial() {
		return material;
	}

	public CurrencySpawnRate getBaseSpawnRate() {
		return baseSpawnRate;
	}

	public boolean isTeamBased() {
		return teamBased;
	}

}
