package fr.moussax.blightedMC.core.items.rules;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.items.ItemsRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ItemRuleListener implements Listener {

  private static final NamespacedKey ID_KEY =
      new NamespacedKey(BlightedMC.getInstance(), "id");

  private ItemManager getManager(ItemStack stack) {
    if (stack == null || !stack.hasItemMeta()) return null;

    var meta = stack.getItemMeta();
    if (meta == null) return null;

    String id = meta.getPersistentDataContainer().get(ID_KEY, PersistentDataType.STRING);
    if (id == null) return null;

    return ItemsRegistry.BLIGHTED_ITEMS.get(id);
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    ItemManager manager = getManager(event.getItemInHand());
    if (manager == null) return;

    if (!manager.canPlace(event, event.getItemInHand())) {
      event.setCancelled(true);
      event.getPlayer().updateInventory(); // Prevent ghost block
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    ItemManager manager = getManager(event.getItem());
    if (manager == null) return;

    if (!manager.canInteract(event, event.getItem())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onItemDrop(PlayerDropItemEvent event) {
    ItemStack dropped = event.getItemDrop().getItemStack();
    ItemManager manager = getManager(dropped);
    if (manager == null) return;

    if (!manager.canUse(event, dropped)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onItemConsume(PlayerItemConsumeEvent event) {
    ItemManager manager = getManager(event.getItem());
    if (manager == null) return;

    if (!manager.canUse(event, event.getItem())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    ItemStack current = event.getCurrentItem();
    ItemManager manager = getManager(current);
    if (manager == null) return;

    if (!manager.canUse(event, current)) {
      event.setCancelled(true);
    }
  }
}
