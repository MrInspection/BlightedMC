package fr.moussax.blightedMC.smp.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class AbilityListener implements Listener {
    private final Set<UUID> dirtyArmorPlayers = new HashSet<>();
    private boolean updateTaskScheduled = false;

    private void scheduleArmorUpdate(Player player) {
        dirtyArmorPlayers.add(player.getUniqueId());

        if (!updateTaskScheduled) {
            updateTaskScheduled = true;
            Bukkit.getScheduler().runTask(BlightedMC.getInstance(), this::processArmorUpdates);
        }
    }

    private void processArmorUpdates() {
        updateTaskScheduled = false;
        if (dirtyArmorPlayers.isEmpty()) return;

        for (UUID uuid : dirtyArmorPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;

            BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
            if (blightedPlayer == null) blightedPlayer = new BlightedPlayer(player);

            ArmorManager.updatePlayerArmor(blightedPlayer);
        }
        dirtyArmorPlayers.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) scheduleArmorUpdate(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) scheduleArmorUpdate(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        scheduleArmorUpdate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        scheduleArmorUpdate(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        scheduleArmorUpdate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        scheduleArmorUpdate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmorEquip(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) return;
        if (event.getItem() == null) return;

        String type = event.getItem().getType().name();
        if (!type.endsWith("_HELMET")
            && !type.endsWith("_CHESTPLATE")
            && !type.endsWith("_LEGGINGS")
            && !type.endsWith("_BOOTS")) return;

        scheduleArmorUpdate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSneakToggle(PlayerToggleSneakEvent e) {
        BlightedPlayer bp = BlightedPlayer.getBlightedPlayer(e.getPlayer());
        if (bp != null) {
            ArmorManager.handleSneakUpdate(bp, e.isSneaking());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        trigger(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) trigger(player, event);
    }

    private <T extends Event> void trigger(Player player, T event) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        if (blightedPlayer == null) blightedPlayer = new BlightedPlayer(player);

        BlightedItem blightedItem;
        if (event instanceof PlayerInteractEvent interactEvent) {
            if (interactEvent.getItem() == null) return;
            blightedItem = BlightedItem.fromItemStack(interactEvent.getItem());
        } else {
            blightedItem = blightedPlayer.getEquippedItemManager();
        }

        if (blightedItem == null) return;

        List<Ability> abilities = blightedItem.getAbilities();
        if (abilities.isEmpty()) return;

        Ability bestMatch = null;

        for (Ability ability : abilities) {
            if (!ability.type().matches(event)) continue;

            if (bestMatch == null || isMoreSpecific(ability.type(), bestMatch.type())) {
                bestMatch = ability;
            }
        }

        if (bestMatch == null) return;

        AbilityExecutor.execute(bestMatch, blightedPlayer, event);
    }

    /**
     * Determines if 'candidate' is a higher priority match than 'current'.
     * Logic: A SNEAK variant is more specific than a normal variant.
     */
    private boolean isMoreSpecific(AbilityType candidate, AbilityType current) {
        return candidate.name().startsWith("SNEAK_") && !current.name().startsWith("SNEAK_");
    }
}
