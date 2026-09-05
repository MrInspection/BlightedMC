package fr.moussax.blightedMod.moderator.listeners;

import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.blightedMod.BlightedMod;
import fr.moussax.blightedMod.moderator.BlightedModerator;
import fr.moussax.blightedMod.moderator.ModerationManager;
import fr.moussax.blightedMod.moderator.menus.InvSeeMenu;
import fr.moussax.blightedMod.moderator.punishments.DurationParser;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import fr.moussax.blightedMod.moderator.punishments.PunishmentManager;
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
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static fr.moussax.bedrock.text.Messenger.warn;

public final class ModerationListener implements Listener {

    private final ModerationManager moderationManager;
    private final PunishmentManager punishmentManager;
    private final Random random = new Random();

    public ModerationListener(ModerationManager moderationManager) {
        this.moderationManager = moderationManager;
        this.punishmentManager = moderationManager.getPunishmentManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!moderationManager.isModerator(player) && punishmentManager.isMuted(player.getUniqueId())) {
            event.setCancelled(true);
            PunishmentData mute = punishmentManager.getActivePunishment(player.getUniqueId(), PunishmentData.PunishmentType.MUTE);
            if (mute != null) {
                if (mute.isPermanent()) {
                    player.sendMessage(" §c⌚ §cYou are muted §cfor §b" + mute.reason() + "§c.");
                } else {
                    long remainingSeconds = Math.max(1, (mute.expiresAt() - System.currentTimeMillis()) / 1000L);
                    String durationText = DurationParser.formatShortDuration(remainingSeconds);
                    player.sendMessage(" §c⌚ §cYou are muted for §d" + durationText + " §cfor §b" + mute.reason() + "§c.");
                }
            }
            return;
        }

        if (moderationManager.isModerator(player) && moderationManager.getChatChannel(player) == ModerationManager.ChatChannel.STAFF) {
            event.setCancelled(true);
            String formatted = " §d§lSTAFF! §9" + player.getName() + "§f§l » §b" + event.getMessage();
            moderationManager.broadcastToModerators(formatted);
            return;
        }

        int remainingSlowmodeSeconds = moderationManager.getRemainingSlowmodeCooldown(player);
        if (remainingSlowmodeSeconds > 0) {
            event.setCancelled(true);
            player.sendMessage(" §c⌚ §cPlease wait §d" + remainingSlowmodeSeconds + "s §cbefore chatting again.");
            return;
        }

        String message = event.getMessage();
        if (message.startsWith("!!") && moderationManager.isModerator(player)) {
            event.setCancelled(true);
            String moderatorMessage = message.substring(2).trim();
            String formatted = " §d§lSTAFF! §9" + player.getName() + "§f§l » §b" + moderatorMessage;
            moderationManager.broadcastToModerators(formatted);
            return;
        }

        moderationManager.recordPlayerChat(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (moderationManager.isModerator(player)) {
            return;
        }

        String ipAddress = event.getAddress().getHostAddress() != null
                ? event.getAddress().getHostAddress()
                : PunishmentManager.getPlayerIp(player);

        PunishmentData ipBan = punishmentManager.getActiveIpBan(ipAddress);
        if (ipBan != null) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, buildBanMessage(ipBan, "IP BANNED"));
            return;
        }

        PunishmentData ban = punishmentManager.getActivePunishment(player.getUniqueId(), PunishmentData.PunishmentType.BAN);
        if (ban != null) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, buildBanMessage(ban, "BANNED"));
        }
    }

    private String buildBanMessage(PunishmentData ban, String title) {
        String durationText = ban.isPermanent() ? "Permanent" : "Temporary";
        return """
                §c§l%s
                
                §7Reason: §f%s
                §7Duration: §f%s
                
                §7Appeal on our Discord if you believe this was a mistake.""".formatted(title, ban.reason(), durationText);
    }

    @EventHandler
    public void onToolInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
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
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        if (!moderationManager.isInModerationMode(player)) return;

        event.setCancelled(true);

        if (!(event.getRightClicked() instanceof Player target) || target.equals(player)) return;

        BlightedModerator moderator = moderationManager.getModerator(player);
        boolean alreadyTargeted = Objects.equals(moderator.getTargetPlayer(), target);
        moderator.setTargetPlayer(target);

        Material tool = player.getInventory().getItemInMainHand().getType();
        if (tool != Material.CHEST && tool != Material.PACKED_ICE && tool != Material.ENCHANTED_BOOK && !alreadyTargeted) {
            InteractiveMessage.text(" §eTargeting §d" + target.getName() + " §ewith §fModeration HUD§e. ")
                    .hoverAndExecute("§3[INFO]", "§fClick to view information about §d" + target.getName() + "§f.", "/userinfo " + target.getName())
                    .send(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        if (moderationManager.isModerator(joiningPlayer)) {
            BlightedModerator moderator = moderationManager.getModerator(joiningPlayer);
            if (moderator.isVanished()) {
                event.setJoinMessage(null);
            }
            return;
        }

        moderationManager.getModeratorsView().values().stream()
                .filter(BlightedModerator::isVanished)
                .forEach(moderator -> joiningPlayer.hidePlayer(
                        BlightedMod.getInstance(), moderator.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player quittingPlayer = event.getPlayer();
        if (moderationManager.isModerator(quittingPlayer)) {
            BlightedModerator moderator = moderationManager.getModerator(quittingPlayer);
            if (moderator.isVanished()) {
                event.setQuitMessage(null);
            }
        }

        if (!moderationManager.isModerator(quittingPlayer) && moderationManager.isFrozen(quittingPlayer)) {
            moderationManager.toggleFreeze(quittingPlayer);
            String reason = "Disconnecting while frozen by a moderator";
            String ipAddress = PunishmentManager.getPlayerIp(quittingPlayer);
            punishmentManager.addPunishment(
                    quittingPlayer.getUniqueId(),
                    quittingPlayer.getName(),
                    PunishmentData.PunishmentType.BAN,
                    reason,
                    quittingPlayer.getUniqueId(),
                    "CONSOLE",
                    null,
                    ipAddress
            );

            String notification = " §6§lALERT! §d" + quittingPlayer.getName() + "§e was automatically banned for §fdisconnecting §ewhile being frozen by a moderator.";
            moderationManager.broadcastToModerators(notification);
        }

        moderationManager.handlePlayerQuit(quittingPlayer);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        Iterator<Player> iterator = event.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (moderationManager.isModerator(player)
                    && moderationManager.getModerator(player) instanceof BlightedModerator moderator
                    && moderator.isVanished()) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onFrozenPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (moderationManager.isModerator(player) || !moderationManager.isFrozen(player)) return;

        org.bukkit.Location frozenLocation = moderationManager.getFrozenLocation(player);
        if (frozenLocation == null) return;

        org.bukkit.Location to = event.getTo();
        if (to == null) return;

        double deltaX = to.getX() - frozenLocation.getX();
        double deltaY = to.getY() - frozenLocation.getY();
        double deltaZ = to.getZ() - frozenLocation.getZ();
        double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

        if (distanceSquared > 0.05) {
            org.bukkit.Location returnLocation = frozenLocation.clone();
            returnLocation.setYaw(to.getYaw());
            returnLocation.setPitch(to.getPitch());
            event.setTo(returnLocation);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPrivateMessageSpy(PlayerCommandPreprocessEvent event) {
        String rawCommandLine = event.getMessage();
        if (!rawCommandLine.startsWith("/")) {
            return;
        }

        String[] parts = rawCommandLine.substring(1).trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return;
        }

        String commandLabel = parts[0].toLowerCase(Locale.ROOT);
        Player sender = event.getPlayer();

        if (Set.of("msg", "tell", "w", "whisper", "pm").contains(commandLabel)) {
            if (parts.length < 3) {
                return;
            }

            String targetName = parts[1];
            Player targetPlayer = Bukkit.getPlayer(targetName);
            String recipientName = targetPlayer != null ? targetPlayer.getName() : targetName;
            String messageContent = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));

            if (targetPlayer != null) {
                moderationManager.setLastMessageTarget(sender.getUniqueId(), targetPlayer.getUniqueId());
            }

            moderationManager.broadcastSpyMessage(sender.getName(), recipientName, messageContent);
        } else if (Set.of("r", "reply").contains(commandLabel)) {
            if (parts.length < 2) {
                return;
            }

            UUID lastTargetId = moderationManager.getLastMessageTarget(sender.getUniqueId());
            Player lastTargetPlayer = lastTargetId != null ? Bukkit.getPlayer(lastTargetId) : null;
            String recipientName = lastTargetPlayer != null ? lastTargetPlayer.getName() : "Unknown";

            String messageContent = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            moderationManager.broadcastSpyMessage(sender.getName(), recipientName, messageContent);
        }
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
        if (moderationManager.isInModerationMode(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onModeratorPlaceBlock(BlockPlaceEvent event) {
        if (moderationManager.isInModerationMode(event.getPlayer())) event.setCancelled(true);
    }

    /**
     * Non-entity damage (fall, fire, drown, freeze, etc.) against a moderator in moderation mode or a frozen player.
     */
    @EventHandler
    public void onModeratorTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (moderationManager.isInModerationMode(player) || moderationManager.isFrozen(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onModeratorEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && moderationManager.isInModerationMode(victim)) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!moderationManager.isInModerationMode(attacker)) return;

        Material tool = attacker.getInventory().getItemInMainHand().getType();
        if (tool == Material.STICK && event.getEntity() instanceof Player victim) {
            moderationManager.getModerator(attacker).setTargetPlayer(victim);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onModeratorInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && moderationManager.isInModerationMode(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onModeratorFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && moderationManager.isInModerationMode(player)) {
            event.setCancelled(true);
        }
    }

    private void handleToolInteraction(Player player, Material tool) {
        switch (tool) {
            case ENDER_EYE -> teleportToRandomPlayer(player);
            case BOOK, NETHER_STAR -> openReportsMenu(player);
            case PURPLE_DYE, GRAY_DYE -> toggleVanish(player);
            case PACKED_ICE -> {
                Player target = getActiveTarget(player);
                if (target != null) {
                    player.performCommand("freeze " + target.getName());
                }
            }
            case CHEST -> {
                Player target = getActiveTarget(player);
                if (target != null) {
                    openInventory(player, target);
                }
            }
            case ENCHANTED_BOOK -> {
                Player target = getActiveTarget(player);
                if (target != null) {
                    openSanctionsMenu(player, target);
                }
            }
            default -> {
            }
        }
    }

    private Player getActiveTarget(Player moderator) {
        Player target = moderationManager.getModerator(moderator).getTargetPlayer();
        if (target == null || !target.isOnline()) {
            warn(moderator, "No target player selected. Right-click a player or use §e/target <player>§c.");
            return null;
        }
        return target;
    }

    private void openReportsMenu(Player moderator) {
        moderator.performCommand("reports");
    }


    private void toggleVanish(Player player) {
        BlightedModerator moderator = moderationManager.getModerator(player);
        moderator.setVanished(!moderator.isVanished());
    }

    private void openInventory(Player moderator, Player target) {
        moderationManager.getModerator(moderator).setTargetPlayer(target);
        new InvSeeMenu(target).open(moderator);
    }

    private void openSanctionsMenu(Player moderator, Player target) {
        moderationManager.getModerator(moderator).setTargetPlayer(target);
        moderator.performCommand("sanctions " + target.getName());
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
        moderationManager.getModerator(moderator).setTargetPlayer(target);

        InteractiveMessage.text(" §eRandomly teleported to §d" + target.getName() + "§e. ")
                .hoverAndExecute("§3[INFO]", "§fClick to view information about §d" + target.getName() + "§f.", "/userinfo " + target.getName())
                .send(moderator);
    }
}
