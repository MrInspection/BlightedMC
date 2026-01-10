package fr.moussax.blightedMC.smp.core.player.mod;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.player.mod.menus.InvSeeMenu;
import fr.moussax.blightedMC.smp.core.player.mod.punishments.PunishmentData;
import fr.moussax.blightedMC.smp.core.player.mod.punishments.PunishmentManager;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class ModerationListener implements Listener {
    private static final String MODERATOR_PERMISSION = "blightedmc.moderator";
    private static final String PREFIX = " §9§lMOD §f| §7";

    private final ModerationManager moderationManager = ModerationManager.getInstance();
    private final PunishmentManager punishmentManager = moderationManager.getPunishmentManager();
    private final Random random = new Random();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (punishmentManager.isMuted(player.getUniqueId())) {
            event.setCancelled(true);
            PunishmentData mute = punishmentManager.getActivePunishment(player.getUniqueId(), PunishmentData.PunishmentType.MUTE);
            if (mute != null) {
                String durationText = mute.isPermanent() ? "permanently" : "temporarily";
                player.sendMessage("§cYou are " + durationText + " muted for: §f" + mute.getReason());
            }
            return;
        }

        if (message.startsWith("!!") && moderationManager.isModerator(player)) {
            event.setCancelled(true);
            String modMessage = message.substring(2).trim();
            String formatted = " §9§lMOD §f| §9" + player.getName() + "§f§l » §b" + modMessage;
            moderationManager.broadcastToModerators(formatted);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String ipAddress = PunishmentManager.getPlayerIp(player);

        PunishmentData ipBan = punishmentManager.getActiveIpBan(ipAddress);
        if (ipBan != null) {
            String durationText = ipBan.isPermanent() ? "Permanent" : "Temporary";
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                "§c§lIP BANNED\n\n§7Reason: §f" + ipBan.getReason() +
                    "\n§7Duration: §f" + durationText +
                    "\n\n§7Appeal on our Discord if you believe this was a mistake.");
            return;
        }

        PunishmentData ban = punishmentManager.getActivePunishment(player.getUniqueId(), PunishmentData.PunishmentType.BAN);
        if (ban != null) {
            String durationText = ban.isPermanent() ? "Permanent" : "Temporary";
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                "§c§lBANNED\n\n§7Reason: §f" + ban.getReason() +
                    "\n§7Duration: §f" + durationText +
                    "\n\n§7Appeal on our Discord if you believe this was a mistake.");
        }
    }

    @EventHandler
    public void onToolInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!moderationManager.isInModerationMode(player)) return;

        event.setCancelled(true);

        ItemStack item = event.getItem();
        if (item == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        handleToolInteraction(player, item.getType());
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!moderationManager.isInModerationMode(player)) return;

        event.setCancelled(true);

        if (!(event.getRightClicked() instanceof Player target)) return;

        Material tool = player.getInventory().getItemInMainHand().getType();
        handleEntityInteraction(player, target, tool);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        boolean hasModeratorPermission = joiningPlayer.hasPermission(MODERATOR_PERMISSION);

        if (hasModeratorPermission) return;

        BlightedMC pluginInstance = BlightedMC.getInstance();
        Bukkit.getOnlinePlayers().stream()
            .map(BlightedPlayer::getBlightedPlayer)
            .filter(Objects::nonNull)
            .filter(BlightedPlayer::isModerator)
            .map(BlightedPlayer::getModerator)
            .filter(BlightedModerator::isVanished)
            .forEach(moderator -> joiningPlayer.hidePlayer(pluginInstance, moderator.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (moderationManager.isInModerationMode(player)) {
            moderationManager.disableModeration(player);
        }
    }

    @EventHandler
    public void onFrozenPlayerMove(PlayerMoveEvent event) {
        if (!moderationManager.isFrozen(event.getPlayer())) return;

        boolean movedHorizontally = event.getFrom().getX() != Objects.requireNonNull(event.getTo()).getX()
            || event.getFrom().getZ() != event.getTo().getZ();

        if (movedHorizontally) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onFrozenPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!moderationManager.isFrozen(attacker)) return;

        event.setCancelled(true);
        attacker.sendMessage("§cYou cannot attack while frozen.");
    }

    @EventHandler
    public void onModeratorDropItem(PlayerDropItemEvent event) {
        if (moderationManager.isInModerationMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onModeratorPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && moderationManager.isInModerationMode(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onModeratorBreakBlock(BlockBreakEvent event) {
        if (moderationManager.isInModerationMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onModeratorPlaceBlock(BlockPlaceEvent event) {
        if (moderationManager.isInModerationMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onModeratorTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && moderationManager.isInModerationMode(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onModeratorAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!moderationManager.isInModerationMode(attacker)) return;

        boolean isUsingKnockbackStick = attacker.getInventory().getItemInMainHand().getType() == Material.STICK;
        if (!isUsingKnockbackStick) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onModeratorInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && moderationManager.isInModerationMode(player)) {
            event.setCancelled(true);
        }
    }

    private void handleToolInteraction(Player player, Material tool) {
        switch (tool) {
            case ENDER_EYE:
                teleportToRandomPlayer(player);
                break;
            case PURPLE_DYE:
            case GRAY_DYE:
                toggleVanish(player);
                break;
        }
    }

    private void handleEntityInteraction(Player player, Player target, Material tool) {
        switch (tool) {
            case CHEST:
                openInventory(player, target);
                break;
            case PACKED_ICE:
                toggleFreeze(player, target);
                break;
        }
    }

    private void toggleVanish(Player player) {
        BlightedModerator moderator = moderationManager.getModerator(player);
        if (moderator == null) return;

        boolean newVanishState = !moderator.isVanished();
        moderator.setVanished(newVanishState);
    }

    private void openInventory(Player moderator, Player target) {
        BlightedMC.menuManager().openMenu(new InvSeeMenu(target), moderator);
    }

    private void toggleFreeze(Player moderator, Player target) {
        boolean isFrozen = moderationManager.toggleFreeze(target);
        String status = isFrozen ? "§bFROZEN" : "§aUNFROZEN";

        moderator.sendMessage(PREFIX + target.getName() + " is now " + status);

        if (isFrozen) {
            target.sendMessage("§c§lYOU HAVE BEEN FROZEN BY A MODERATOR!");
            target.sendMessage("§cDo not log out or you will be banned.");
        } else {
            target.sendMessage("§aYou have been unfrozen.");
        }
    }

    private void teleportToRandomPlayer(Player moderator) {
        List<Player> eligiblePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        eligiblePlayers.remove(moderator);

        if (eligiblePlayers.isEmpty()) {
            moderator.sendMessage("§cNo other players online.");
            return;
        }

        Player target = eligiblePlayers.get(random.nextInt(eligiblePlayers.size()));
        moderator.teleport(target.getLocation());
        moderator.sendMessage(PREFIX + "Teleported to §d" + target.getName());
    }
}
