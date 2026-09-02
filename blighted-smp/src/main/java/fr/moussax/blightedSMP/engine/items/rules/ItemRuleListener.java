package fr.moussax.blightedSMP.engine.items.rules;

import fr.moussax.blightedSMP.engine.items.BlightedItem;
import fr.moussax.blightedSMP.engine.items.registry.ItemRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static fr.moussax.blightedSMP.engine.items.BlightedItem.BLIGHTED_ID_KEY;

/**
 * Event listener enforcing item rules and processing custom item consumption callbacks.
 */
public final class ItemRuleListener implements Listener {

    private BlightedItem getManager(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) return null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        String id = itemMeta.getPersistentDataContainer().get(BLIGHTED_ID_KEY, PersistentDataType.STRING);
        if (id == null) return null;
        return ItemRegistry.getItem(id);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        BlightedItem manager = getManager(itemStack);

        if (manager != null && manager.shouldRestrictInteract(event, itemStack)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;

        boolean mainHandRestricted = checkAndRestrictHandItem(player, player.getInventory().getItemInMainHand(), event);
        boolean offHandRestricted = checkAndRestrictHandItem(player, player.getInventory().getItemInOffHand(), event);

        if (mainHandRestricted || offHandRestricted) {
            event.setCancelled(true);
        }
    }

    private boolean checkAndRestrictHandItem(Player player, ItemStack itemStack, Event event) {
        BlightedItem manager = getManager(itemStack);
        if (manager != null && manager.shouldRestrictUse(event, itemStack)) {
            player.setCooldown(itemStack.getType(), 0);
            return true;
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        ItemStack handItem = event.getPlayer().getInventory().getItem(event.getHand());
        BlightedItem manager = getManager(handItem);
        if (manager != null && manager.shouldRestrictUse(event, handItem)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        BlightedItem manager = getManager(event.getItemInHand());
        if (manager != null && manager.shouldRestrictPlace(event, event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        BlightedItem manager = getManager(event.getItemDrop().getItemStack());
        if (manager != null && manager.shouldRestrictUse(event, event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        BlightedItem manager = getManager(event.getItem());
        if (manager == null) return;

        if (manager.shouldRestrictUse(event, event.getItem())) {
            event.setCancelled(true);
            return;
        }

        if (manager.getConsumeHandler() != null) {
            manager.getConsumeHandler().onConsume(event.getPlayer(), event.getItem());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        BlightedItem manager = getManager(event.getCurrentItem());
        if (manager != null && manager.shouldRestrictUse(event, event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }
}
