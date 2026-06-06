package fr.moussax.blightedMC.engine.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class AbilityListener implements Listener {
    private final Set<UUID> dirtyArmorPlayers = new HashSet<>();
    private boolean updateTaskScheduled = false;

    private void scheduleArmorUpdate(Player player) {
        if (player == null) return;
        dirtyArmorPlayers.add(player.getUniqueId());

        if (updateTaskScheduled) return;

        updateTaskScheduled = true;
        Bukkit.getScheduler().runTask(BlightedMC.getInstance(), this::processArmorUpdates);
    }

    private void processArmorUpdates() {
        updateTaskScheduled = false;
        if (dirtyArmorPlayers.isEmpty()) return;

        for (UUID uuid : dirtyArmorPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;

            BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
            if (blightedPlayer == null) continue;

            ArmorManager.updatePlayerArmor(blightedPlayer);
        }
        dirtyArmorPlayers.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        boolean isArmorSlot = event.getSlotType() == InventoryType.SlotType.ARMOR;
        boolean isNumberKey = event.getClick().name().contains("NUMBER_KEY");

        if (isArmorSlot || event.isShiftClick() || isNumberKey) {
            scheduleArmorUpdate(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        for (int slot : event.getRawSlots()) {
            if (slot >= 5 && slot <= 8) { // Standard survival inventory armor slots
                scheduleArmorUpdate(player);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDispenseArmor(BlockDispenseArmorEvent event) {
        if (event.getTargetEntity() instanceof Player player) {
            scheduleArmorUpdate(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        dirtyArmorPlayers.remove(uniqueId);
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
    public void onSneakToggle(PlayerToggleSneakEvent e) {
        BlightedPlayer bp = BlightedPlayer.getBlightedPlayer(e.getPlayer());
        if (bp != null) {
            ArmorManager.handleSneakUpdate(bp, e.isSneaking());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && isArmorMaterial(event.getItem().getType().name())) {
            scheduleArmorUpdate(event.getPlayer());
        }

        // Prevent double triggering from off-hand interactions
        if (event.getHand() != EquipmentSlot.HAND) return;

        trigger(event.getPlayer(), event);
    }

    private boolean isArmorMaterial(String name) {
        return name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE")
                || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS") || name.equals("ELYTRA");
    }

    private <T extends Event> void trigger(Player player, T event) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        if (blightedPlayer == null) return;

        BlightedItem blightedItem;
        if (event instanceof PlayerInteractEvent ie) {
            if (ie.getItem() == null) return;
            blightedItem = BlightedItem.fromItemStack(ie.getItem());
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

        if (bestMatch != null) {
            AbilityExecutor.execute(bestMatch, blightedPlayer, event);
        }
    }

    private boolean isMoreSpecific(AbilityType candidate, AbilityType current) {
        return candidate.name().startsWith("SNEAK_") && !current.name().startsWith("SNEAK_");
    }
}
