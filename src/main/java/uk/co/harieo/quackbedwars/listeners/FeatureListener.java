package uk.co.harieo.quackbedwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import uk.co.harieo.quackbedwars.ProtectTheEgg;

public class FeatureListener implements Listener {

	@EventHandler
	public void onWaterBucketUse(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			PlayerInventory inventory = player.getInventory();

			ItemStack mainHandItem = inventory.getItemInMainHand();
			if (isWaterBucket(mainHandItem) || isWaterBucket(inventory.getItemInOffHand())) {
				Bukkit.getScheduler().runTaskLater(ProtectTheEgg.getInstance(),
						() -> inventory.setItem(inventory.getHeldItemSlot(), null), 5);
			}
		}
	}

	@EventHandler
	public void onTNTPlace(BlockPlaceEvent event) {
		Block blockPlaced = event.getBlockPlaced();
		if (blockPlaced.getType() == Material.TNT) {
			event.setCancelled(true);

			Location location = blockPlaced.getLocation();
			World world = location.getWorld();
			if (world != null) {
				world.spawnEntity(location, EntityType.PRIMED_TNT);
				subtractFromItemStack(event.getPlayer(), event.getItemInHand());
			}
		}
	}

	private boolean isWaterBucket(ItemStack itemStack) {
		return itemStack != null && itemStack.getType().equals(Material.WATER_BUCKET);
	}

	private void subtractFromItemStack(Player player, ItemStack itemStack) {
		int amount = itemStack.getAmount();
		if (amount > 1) {
			itemStack.setAmount(amount - 1);
		} else {
			player.getInventory().remove(itemStack);
		}
	}

}
