package uk.co.harieo.quackbedwars.shops;

import org.bukkit.entity.Villager.Profession;

public enum ShopType {

	ITEMS("Item Shop", "item-shop", Profession.WEAPONSMITH),
	UPGRADES("Team Upgrades", "upgrade-shop", Profession.CLERIC);

	private final String shopName;
	private final String mapValue;
	private final Profession villagerProfession;

	/**
	 * Represents a type of shop that players can purchase things from
	 *
	 * @param shopName the name of the shop to be displayed in its menu
	 * @param mapValue the key to be used in a map to represent this shop's location
	 * @param villagerProfession the profession of the villager NPC which owns this shop
	 */
	ShopType(String shopName, String mapValue, Profession villagerProfession) {
		this.shopName = shopName;
		this.mapValue = mapValue;
		this.villagerProfession = villagerProfession;
	}

	/**
	 * @return the user-friendly name of this shop type
	 */
	public String getShopName() {
		return shopName;
	}

	/**
	 * @return the {@link uk.co.harieo.minigames.maps.LocationPair} value to represent this shop
	 */
	public String getMapValue() {
		return mapValue;
	}

	/**
	 * @return the profession of this villager
	 */
	public Profession getVillagerProfession() {
		return villagerProfession;
	}

	/**
	 * Retrieves a type by its {@link #getMapValue()} which matches the specified value
	 *
	 * @param mapValue to be compared to other types
	 * @return the retrieved type or null if none match
	 */
	public static ShopType getByMapValue(String mapValue) {
		for (ShopType type : values()) {
			if (type.getMapValue().equals(mapValue)) {
				return type;
			}
		}
		return null;
	}

}
