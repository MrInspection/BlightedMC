package fr.moussax.blightedMC.smp.core.player;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import org.bukkit.Bukkit;
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
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class BlightedPlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore() && BlightedMC.getInstance().getSettings().hasBannersOnJoin()) {
            PlayerInventory inv = player.getInventory();
            inv.setHelmet(
                new ItemBuilder(Material.BLACK_BANNER, "§dBlighted Banner")
                    .addBannerPatterns(List.of(
                        new Pattern(DyeColor.PURPLE, PatternType.CURLY_BORDER),
                        new Pattern(DyeColor.BLACK, PatternType.BRICKS),
                        new Pattern(DyeColor.BLACK, PatternType.SMALL_STRIPES),
                        new Pattern(DyeColor.BLACK, PatternType.GUSTER),
                        new Pattern(DyeColor.PURPLE, PatternType.CIRCLE),
                        new Pattern(DyeColor.BLACK, PatternType.FLOW))
                    ).addItemFlag(ItemFlag.HIDE_BANNER_PATTERNS)
                    .editEquippable(equippable -> {
                        equippable.setSlot(EquipmentSlot.HEAD);
                    })
                    .addLore("§7An eerie looking banner.")
                    .toItemStack());
        }

        new BlightedPlayer(event.getPlayer());
        event.setJoinMessage("§8 ■ §f" + event.getPlayer().getName() + " §7joined the SMP.");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BlightedPlayer player = BlightedPlayer.getBlightedPlayer(event.getPlayer());
        if (player != null) {
            player.saveData();
            BlightedPlayer.removePlayer(event.getPlayer());
        }
        event.setQuitMessage("§8 ■ §f" + event.getPlayer().getName() + " §7left the SMP.");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        event.setFormat(" §7" + player.getName() + "§8 » §f" + message);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;

        if (event.getDamager() instanceof Arrow arrow) {
            if (!(arrow.getShooter() instanceof Player damager)) return;
            if (damaged == damager) return;

            double targetHealth = damaged.getHealth() - event.getFinalDamage();
            if (targetHealth < 0) targetHealth = 0;

            double maxHealth = Objects.requireNonNull(damaged.getAttribute(Attribute.MAX_HEALTH)).getValue();
            double percentage = (targetHealth > 0) ? (targetHealth / maxHealth) * 100.0 : 0.0;
            String colorPrefix = percentage <= 20 ? "§c" : (percentage <= 50 ? "§e" : "§a");
            String formattedMessage = "§c \uD83C\uDFF9 §f" + damaged.getName() + " §7is now at " + colorPrefix + (int) targetHealth + "❤" + "§7.";
            damager.sendMessage(formattedMessage);
        }
    }

    private final Map<UUID, Entity> lastDamagerMap = new HashMap<>();

    @EventHandler
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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        String vanillaMessage = event.getDeathMessage();
        if (vanillaMessage == null) return;

        event.setDeathMessage(null);

        Entity killer = lastDamagerMap.get(victim.getUniqueId());
        lastDamagerMap.remove(victim.getUniqueId());

        TextComponent message = new TextComponent("§c ☠ §f" + victim.getName());

        if (killer != null && killer.equals(victim)) {
            message.addExtra(" §7took their own life.");
            broadcast(message);
            return;
        }

        String killerDisplayName;
        if (killer != null) {
            AbstractBlightedEntity blighted = BlightedEntitiesListener.getBlightedEntity(killer);
            killerDisplayName = (blighted != null) ? blighted.getName() : killer.getName();
        } else {
            String rest = vanillaMessage.replaceFirst(java.util.regex.Pattern.quote(victim.getName()), "");
            message.addExtra("§7" + rest + "§7.");
            broadcast(message);
            return;
        }

        message.addExtra("§7 was slain by §f" + killerDisplayName);

        if (killer instanceof Player playerKiller) {
            org.bukkit.inventory.ItemStack itemStack = playerKiller.getInventory().getItemInMainHand();

            if (itemStack.getType() != org.bukkit.Material.AIR) {
                message.addExtra(" §7using ");

                String displayName = itemStack.getType().name().toLowerCase().replace("_", " ");
                org.bukkit.inventory.meta.ItemMeta meta = itemStack.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    displayName = meta.getDisplayName();
                }

                TextComponent itemBracket = new TextComponent("[" + displayName + "]");
                itemBracket.setColor(ChatColor.WHITE);

                Item itemContent = new Item(
                    itemStack.getType().getKey().toString(),
                    itemStack.getAmount(),
                    ItemTag.ofNbt(itemStack.getItemMeta().getAsString())
                );

                itemBracket.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_ITEM,
                    itemContent
                ));
                message.addExtra(itemBracket);
            }
        }

        message.addExtra("§7.");
        broadcast(message);
    }

    private void broadcast(BaseComponent component) {
        Bukkit.spigot().broadcast(component);
    }
}
