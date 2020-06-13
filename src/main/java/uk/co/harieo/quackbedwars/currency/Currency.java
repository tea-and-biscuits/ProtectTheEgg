package uk.co.harieo.quackbedwars.currency;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Currency {

	IRON("Iron", ChatColor.WHITE, Material.IRON_INGOT),
	GOLD("Gold", ChatColor.GOLD, Material.GOLD_INGOT),
	DIAMOND("Diamond", ChatColor.BLUE, Material.DIAMOND),
	EMERALD("Emerald", ChatColor.GREEN, Material.EMERALD);

	private final String name;
	private final ChatColor color;
	private final Material material;

	Currency(String name, ChatColor color, Material itemMaterial) {
		this.name = name;
		this.color = color;
		this.material = itemMaterial;
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

}
