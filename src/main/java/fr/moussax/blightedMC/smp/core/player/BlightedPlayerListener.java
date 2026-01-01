package fr.moussax.blightedMC.smp.core.player;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class BlightedPlayerListener implements Listener {
    private final Map<UUID, Entity> lastDamagerMap = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore() && BlightedMC.getInstance().getSettings().hasBannersOnJoin()) {
            giveStarterBanner(player);
        }

        new BlightedPlayer(event.getPlayer());
        event.setJoinMessage("§8 ■ §f" + event.getPlayer().getName() + " §7joined the SMP.");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        BlightedPlayer blighted = BlightedPlayer.getBlightedPlayer(player);
        if (blighted != null) {
            blighted.saveData();
            BlightedPlayer.removePlayer(player);
        }

        lastDamagerMap.remove(player.getUniqueId());
        event.setQuitMessage("§8 ■ §f" + player.getName() + " §7left the SMP.");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(" §7" + event.getPlayer().getName() + "§8 » §f" + event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Entity damager = event.getDamager();

        if (damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Entity shooterEntity) {
                lastDamagerMap.put(victim.getUniqueId(), shooterEntity);
                return;
            }
        }

        lastDamagerMap.put(victim.getUniqueId(), damager);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player damager)) return;
        if (damaged.equals(damager)) return;

        double targetHealth = Math.max(0, damaged.getHealth() - event.getFinalDamage());
        double maxHealth = Objects.requireNonNull(damaged.getAttribute(Attribute.MAX_HEALTH)).getValue();

        double percentage = (targetHealth / maxHealth) * 100.0;
        String colorPrefix = percentage <= 20 ? "§c" : (percentage <= 50 ? "§e" : "§a");

        damager.sendMessage("§c \uD83C\uDFF9 §f" + damaged.getName() + " §7is now at " + colorPrefix + (int) targetHealth + "❤" + "§7.");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        String vanillaMessage = event.getDeathMessage();

        Entity killer = lastDamagerMap.remove(victim.getUniqueId());

        if (vanillaMessage == null) return;

        String prefix = "§c ☠ §f" + victim.getName();
        String finalMessage;

        if (killer == null) {
            String rest = vanillaMessage.replace(victim.getName(), "");
            finalMessage = prefix + "§7" + rest + "§7.";
        } else if (killer.equals(victim)) {
            finalMessage = prefix + " §7took their own life.";
        } else {
            String killerName = resolveKillerName(killer);
            finalMessage = prefix + "§7 was slain by §f" + killerName + "§7.";
        }

        event.setDeathMessage(finalMessage);
    }

    private String resolveKillerName(Entity killer) {
        if (killer instanceof Player) return killer.getName();

        AbstractBlightedEntity blighted = BlightedEntitiesListener.getBlightedEntity(killer);
        if (blighted != null) {
            return blighted.getName();
        }

        return (killer.getCustomName() != null) ? killer.getCustomName() : killer.getName();
    }

    private void giveStarterBanner(Player player) {
        player.getInventory().setHelmet(
            new ItemBuilder(Material.BLACK_BANNER, "§dBlighted Banner")
                .addBannerPatterns(List.of(
                    new Pattern(DyeColor.PURPLE, PatternType.CURLY_BORDER),
                    new Pattern(DyeColor.BLACK, PatternType.BRICKS),
                    new Pattern(DyeColor.BLACK, PatternType.SMALL_STRIPES),
                    new Pattern(DyeColor.BLACK, PatternType.GUSTER),
                    new Pattern(DyeColor.PURPLE, PatternType.CIRCLE),
                    new Pattern(DyeColor.BLACK, PatternType.FLOW))
                ).addItemFlag(ItemFlag.HIDE_BANNER_PATTERNS)
                .editEquippable(equippable -> equippable.setSlot(EquipmentSlot.HEAD))
                .addLore("§7An eerie looking banner.")
                .toItemStack()
        );
    }
}
