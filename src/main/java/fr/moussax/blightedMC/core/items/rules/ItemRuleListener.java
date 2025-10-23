package fr.moussax.blightedMC.core.items.rules;

import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.registry.ItemsRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static fr.moussax.blightedMC.core.items.registry.ItemsRegistry.ID_KEY;

public class ItemRuleListener implements Listener {

  private ItemTemplate getManager(ItemStack stack) {
    if (stack == null || !stack.hasItemMeta()) return null;

    var meta = stack.getItemMeta();
    if (meta == null) return null;

    String id = meta.getPersistentDataContainer().get(ID_KEY, PersistentDataType.STRING);
    if (id == null) return null;

    return ItemsRegistry.REGISTERED_ITEMS.get(id);
  }

  @EventHandler(ignoreCancelled = true)
  @SuppressWarnings("UnstableApiUsage")
  public void onBlockPlace(BlockPlaceEvent event) {
    ItemTemplate manager = getManager(event.getItemInHand());
    if (manager == null) return;

    if (!manager.canPlace(event, event.getItemInHand())) {
      event.setCancelled(true);
      event.getPlayer().updateInventory(); // Prevent ghost block
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
    ItemStack offHand = event.getPlayer().getInventory().getItemInOffHand();

    ItemTemplate mainManager = getManager(mainHand);
    ItemTemplate offManager = getManager(offHand);

    boolean cancel = false;

    if (mainManager != null && !mainManager.canUse(event, mainHand)) cancel = true;
    if (offManager != null && !offManager.canUse(event, offHand)) cancel = true;

    if (cancel) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onItemDrop(PlayerDropItemEvent event) {
    ItemStack dropped = event.getItemDrop().getItemStack();
    ItemTemplate manager = getManager(dropped);
    if (manager == null) return;

    if (!manager.canUse(event, dropped)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onItemConsume(PlayerItemConsumeEvent event) {
    ItemTemplate manager = getManager(event.getItem());
    if (manager == null) return;

    if (!manager.canUse(event, event.getItem())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    ItemStack current = event.getCurrentItem();
    ItemTemplate manager = getManager(current);
    if (manager == null) return;

    if (!manager.canUse(event, current)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onProjectileLaunch(ProjectileLaunchEvent event) {
    if (!(event.getEntity().getShooter() instanceof Player player)) return;

    // Check both hands
    ItemStack mainHand = player.getInventory().getItemInMainHand();
    ItemStack offHand = player.getInventory().getItemInOffHand();

    ItemTemplate manager = getManager(mainHand);
    if (manager == null) manager = getManager(offHand);

    if (manager == null) return;

    // Apply rule for all items that have PreventProjectileLaunchRule
    if (!manager.canUse(event, mainHand) || !manager.canUse(event, offHand)) {
      event.setCancelled(true);

      // Remove cooldown to prevent client desync
      if (mainHand != null) player.setCooldown(mainHand.getType(), 0);
      if (offHand != null) player.setCooldown(offHand.getType(), 0);
    }
  }

}
