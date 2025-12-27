package fr.moussax.blightedMC.smp.core.items.rules;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import org.bukkit.entity.Player;
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
import org.bukkit.persistence.PersistentDataType;

import static fr.moussax.blightedMC.smp.core.items.BlightedItem.BLIGHTED_ID_KEY;

public class ItemRuleListener implements Listener {

    private BlightedItem getManager(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return null;

        var meta = stack.getItemMeta();
        if (meta == null) return null;

        String id = meta.getPersistentDataContainer().get(BLIGHTED_ID_KEY, PersistentDataType.STRING);
        if (id == null) return null;

        return ItemRegistry.getItem(id);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        BlightedItem mainManager = getManager(itemInMainHand);
        BlightedItem offManager = getManager(itemInOffHand);

        if (mainManager != null && mainManager.canUse(event, itemInMainHand)) {
            event.setCancelled(true);
            return;
        }

        if (offManager != null && offManager.canUse(event, itemInOffHand)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("UnstableApiUsage")
    public void onBlockPlace(BlockPlaceEvent event) {
        BlightedItem manager = getManager(event.getItemInHand());
        if (manager == null) return;

        if (manager.canPlace(event, event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory(); // Prevent ghost block
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack offHand = event.getPlayer().getInventory().getItemInOffHand();

        BlightedItem mainManager = getManager(mainHand);
        BlightedItem offManager = getManager(offHand);

        boolean cancel = false;

        if (mainManager != null && mainManager.canUse(event, mainHand)) cancel = true;
        if (offManager != null && offManager.canUse(event, offHand)) cancel = true;

        if (cancel) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack dropped = event.getItemDrop().getItemStack();
        BlightedItem manager = getManager(dropped);
        if (manager == null) return;

        if (manager.canUse(event, dropped)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        BlightedItem manager = getManager(event.getItem());
        if (manager == null) return;

        if (manager.canUse(event, event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        BlightedItem manager = getManager(current);
        if (manager == null) return;

        if (manager.canUse(event, current)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;

        // Check both hands
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        BlightedItem manager = getManager(mainHand);
        if (manager == null) manager = getManager(offHand);

        if (manager == null) return;

        // Apply rule for all items that have PreventProjectileLaunchRule
        if (manager.canUse(event, mainHand) || manager.canUse(event, offHand)) {
            event.setCancelled(true);

            // Remove cooldown to prevent client desync
            player.setCooldown(mainHand.getType(), 0);
            player.setCooldown(offHand.getType(), 0);
        }
    }
}
