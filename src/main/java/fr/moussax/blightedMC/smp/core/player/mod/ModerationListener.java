package fr.moussax.blightedMC.smp.core.player.mod;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.server.PluginPermissions;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.player.mod.menus.InvSeeMenu;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class ModerationListener implements Listener {
    private static final String PREFIX = " §9§lMOD §f| §7";

    private final ModerationManager moderationManager = ModerationManager.getInstance();
    private final Random random = new Random();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().startsWith("!!") && moderationManager.isModerator(player)) {
            event.setCancelled(true);
            String modMessage = event.getMessage().substring(2).trim();
            moderationManager.broadcastToModerators(" §9§lMOD §f| §9" + player.getName() + "§f§l » §b" + modMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerListPing(ServerListPingEvent event) {
        int vanishedCount = moderationManager.getVanishedCount();
        if (vanishedCount > 0) {
            Iterator<Player> iterator = event.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                if (moderationManager.isVanished(player)) {
                    iterator.remove();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onToolInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!moderationManager.isInModerationMode(player)) return;
        ItemStack item = event.getItem();
        if (item == null) return;

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            handleToolInteraction(player, item.getType());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!moderationManager.isInModerationMode(player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        event.setCancelled(true);
        if (event.getRightClicked() instanceof Player target) {
            Material tool = player.getInventory().getItemInMainHand().getType();
            handleEntityInteraction(player, target, tool);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        if (joiningPlayer.hasPermission(PluginPermissions.MODERATOR.getPermission())) return;

        Bukkit.getOnlinePlayers().stream()
            .map(p -> BlightedPlayer.getBlightedPlayer(p).getModerator())
            .filter(Objects::nonNull)
            .filter(BlightedModerator::isVanished)
            .forEach(mod -> joiningPlayer.hidePlayer(BlightedMC.getInstance(), mod.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (moderationManager.isInModerationMode(player)) {
            BlightedModerator moderator = moderationManager.getModerator(player);
            if (moderator != null) {
                moderator.disable();
            }
            moderationManager.cleanupPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFrozenPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!moderationManager.isFrozen(player)) return;

        Location freezeLocation = moderationManager.getFreezeLocation(player);
        if (freezeLocation == null) return;

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;

        boolean moved = from.getX() != to.getX() || from.getZ() != to.getZ() || Math.abs(from.getY() - to.getY()) > 0.1;
        if (moved) event.setTo(freezeLocation);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onModeratorDropItem(PlayerDropItemEvent event) {
        if (moderationManager.isInModerationMode(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onModeratorPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player p && moderationManager.isInModerationMode(p)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onModeratorBreakBlock(BlockBreakEvent event) {
        if (moderationManager.isInModerationMode(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onModeratorPlaceBlock(BlockPlaceEvent event) {
        if (moderationManager.isInModerationMode(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onModeratorTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player p && moderationManager.isInModerationMode(p)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onModeratorAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && moderationManager.isInModerationMode(attacker)) {
            if (attacker.getInventory().getItemInMainHand().getType() != Material.STICK) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onModeratorInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player p && moderationManager.isInModerationMode(p)) {
            event.setCancelled(true);
        }
    }

    private void handleToolInteraction(Player player, Material tool) {
        switch (tool) {
            case ENDER_EYE -> teleportToRandomPlayer(player);
            case PURPLE_DYE, GRAY_DYE -> toggleVanish(player);
        }
    }

    private void handleEntityInteraction(Player player, Player target, Material tool) {
        switch (tool) {
            case CHEST -> BlightedMC.menuManager().openMenu(new InvSeeMenu(target), player);
            case PACKED_ICE -> toggleFreeze(player, target);
        }
    }

    private void toggleVanish(Player player) {
        BlightedModerator mod = moderationManager.getModerator(player);
        if (mod != null) mod.setVanished(!mod.isVanished());
    }

    private void toggleFreeze(Player moderator, Player target) {
        boolean isFrozen = moderationManager.toggleFreeze(target);
        String status = isFrozen ? "§b§lFROZEN" : "§f§lUNFROZEN";
        moderator.sendMessage(PREFIX + target.getName() + " is now " + status);

        if (isFrozen) {
            Formatter.warn(target, "You have been §4FROZEN §cby a moderator!");
        } else {
            Formatter.inform(target, "You have been unfrozen.");
        }
    }

    private void teleportToRandomPlayer(Player moderator) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.remove(moderator);

        if (players.isEmpty()) {
            Formatter.warn(moderator, "No other players online.");
            return;
        }

        Player target = players.get(random.nextInt(players.size()));
        moderator.teleport(target.getLocation());
        moderator.sendMessage(PREFIX + "Teleported to §d" + target.getName() + "§7.");
        BlightedPlayer.getBlightedPlayer(moderator).getActionBarManager().setModTarget(target);
    }
}
