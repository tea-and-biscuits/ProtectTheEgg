package uk.co.harieo.quackbedwars.shops;

import org.bukkit.ChatColor;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

public enum ShopType {

	ITEMS("Item Shop", "item-shop", ChatColor.GREEN, Profession.WEAPONSMITH),
	UPGRADES("Team Upgrades", "upgrade-shop", ChatColor.YELLOW, Profession.CLERIC);

	private final String shopName;
	private final String mapValue;
	private final ChatColor color;
	private final Villager.Profession villagerProfession;
	private ShopMenu menu;

	/**
	 * Represents a type of shop that players can purchase things from
	 *
	 * @param shopName the name of the shop to be displayed in its menu
	 * @param mapValue the key to be used in a map to represent this shop's location
	 * @param villagerProfession the profession of villager to set the NPC as
	 */
	ShopType(String shopName, String mapValue, ChatColor color, Villager.Profession villagerProfession) {
		this.shopName = shopName;
		this.mapValue = mapValue;
		this.color = color;
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
	 * @return the chat color to represent this type
	 */
	public ChatColor getColor() {
		return color;
	}

	/**
	 * @return the profession of this villager
	 */
	public Villager.Profession getVillagerProfession() {
		return villagerProfession;
	}

	/**
	 * @return the menu for this shop
	 */
	public ShopMenu getMenu() {
		return menu;
	}

	/**
	 * Sets this shop's menu
	 *
	 * @param menu which displays this shop
	 */
	public void setMenu(ShopMenu menu) {
		this.menu = menu;
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
