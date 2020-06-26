package uk.co.harieo.quackbedwars.shops.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import uk.co.harieo.minigames.menus.MenuItem;
import uk.co.harieo.quackbedwars.ProtectTheEgg;
import uk.co.harieo.quackbedwars.shops.ShopMenu;
import uk.co.harieo.quackbedwars.shops.ShopType;

public class ShopCategory extends MenuItem {

	private final ItemStack displayItem;
	private final List<ShopItem> purchasableItems = new ArrayList<>(); // List to allow ordering of items
	private ShopMenu menu;

	/**
	 * A category which holds a set of {@link ShopItem} that can be purchased
	 *
	 * @param displayItem which represents the category in a menu
	 */
	public ShopCategory(ItemStack displayItem) {
		super(displayItem);
		this.displayItem = displayItem;
	}

	/**
	 * @return the item to be displayed in the category menu
	 */
	public ItemStack getDisplayItem() {
		return displayItem;
	}

	/**
	 * @return a list of the items in this category which can be purchased
	 */
	public List<ShopItem> getPurchasableItems() {
		return purchasableItems;
	}

	/**
	 * Sets the {@link ShopMenu} which contains all the {@link ShopItem} instances in this category. This class will
	 * then be set to open the created menu with {@link MenuItem#setOnClick(Consumer)} to be used in another {@link
	 * uk.co.harieo.minigames.menus.MenuFactory}
	 *
	 * @param type of shop which this category represents
	 */
	public void updateMenu(ShopType type) {
		int amountOfItems = purchasableItems.size();
		menu = new ShopMenu(type, amountOfItems / 9 + 1);
		for (int i = 0; i < amountOfItems; i++) {
			menu.setStaticItem(i, purchasableItems.get(i));
		}
		setOnClick(player -> menu.getOrCreateMenu(player).showInventory()); // Allows the menu to be opened
	}

	/**
	 * Adds an item to the list of items in the category
	 *
	 * @param item to be added
	 */
	public void addPurchasableItem(ShopItem item) {
		purchasableItems.add(item);
	}

	/**
	 * Parses a {@link FileConfiguration} matching the items.yml resource and retrieves all categories, including all
	 * sub sections of {@link ShopItem}, to be used in the item shop menu. Categories are retrieved in the order they
	 * are found in the configuration file, as are their items, so the order of display can be controlled by a user.
	 *
	 * @param configuration to be parsed
	 * @return a list of all successfully parsed ShopCategory instances
	 */
	public static List<ShopCategory> parseCategories(FileConfiguration configuration) {
		Logger logger = ProtectTheEgg.getInstance().getLogger();

		List<ShopCategory> categories = new ArrayList<>();
		for (String categoryKey : configuration.getKeys(false)) { // Retrieve all root level keys (the categories)
			ConfigurationSection categorySection = configuration.getConfigurationSection(categoryKey);
			if (categorySection != null) {
				ItemStack displayItem = ItemsConfig.parseItem(categorySection); // The category's display item
				ShopCategory category = new ShopCategory(displayItem);

				ConfigurationSection itemsSection = categorySection.getConfigurationSection("items");
				if (itemsSection != null) {
					for (String purchasableItemKey : itemsSection.getKeys(false)) { // Retrieve all the ShopItems
						ConfigurationSection shopItemSection = itemsSection.getConfigurationSection(purchasableItemKey);
						if (shopItemSection != null) {
							ShopItem shopItem = ShopItem.parseShopItem(shopItemSection);
							category.addPurchasableItem(shopItem);
						} else {
							logger.warning("A section of items in an category has a key which doesn't match a section: "
									+ categoryKey + ", " + purchasableItemKey);
						}
					}

					categories.add(category);
				} else {
					logger.warning("A category had no section for the items it should contain: " + categoryKey);
				}
			} else {
				logger.warning("Category in items.yml has a key which doesn't match a section: " + categoryKey);
			}
		}

		return categories;
	}

}
