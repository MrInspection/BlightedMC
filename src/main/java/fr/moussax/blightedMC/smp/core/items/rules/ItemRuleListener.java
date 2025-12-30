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
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();
        BlightedItem manager = getManager(stack);

        if (manager != null && manager.canUse(event, stack)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;

        boolean cancelled = false;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        BlightedItem mainManager = getManager(mainHand);
        if (mainManager != null && mainManager.canUse(event, mainHand)) {
            cancelled = true;
            player.setCooldown(mainHand.getType(), 0);
        }

        ItemStack offHand = player.getInventory().getItemInOffHand();
        BlightedItem offManager = getManager(offHand);
        if (offManager != null && offManager.canUse(event, offHand)) {
            cancelled = true;
            player.setCooldown(offHand.getType(), 0);
        }

        if (cancelled) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        BlightedItem manager = getManager(event.getItemStack());
        if (manager != null && manager.canUse(event, event.getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        BlightedItem manager = getManager(event.getItemInHand());
        if (manager != null && manager.canPlace(event, event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        BlightedItem manager = getManager(event.getItemDrop().getItemStack());
        if (manager != null && manager.canUse(event, event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        BlightedItem manager = getManager(event.getItem());
        if (manager != null && manager.canUse(event, event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        BlightedItem manager = getManager(event.getCurrentItem());
        if (manager != null && manager.canUse(event, event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }
}
