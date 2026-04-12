package fr.moussax.blightedMC.engine.player;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

public final class PlayerListener implements Listener {

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
        event.setQuitMessage("§8 ■ §f" + player.getName() + " §7left the SMP.");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String deathMessage = event.getDeathMessage();
        if (deathMessage == null) return;

        Entity killer = event.getEntity().getKiller();
        if (killer == null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent) {
            killer = damageEvent.getDamager();
            if (killer instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) {
                killer = shooter;
            }
        }

        if (killer == null) return;

        BlightedEntity blighted = BlightedEntitiesListener.getBlightedEntity(killer);
        if (blighted == null) return;

        String victimName = event.getEntity().getName();
        String blightedCreature = blighted.getName();
        String customNameWithHealth = killer.getCustomName();

        if (customNameWithHealth != null && deathMessage.contains(customNameWithHealth)) {
            event.setDeathMessage(deathMessage.replace(customNameWithHealth, blightedCreature));
            return;
        }

        String strippedMsg = ChatColor.stripColor(deathMessage);
        String strippedKiller = ChatColor.stripColor(customNameWithHealth != null ? customNameWithHealth : killer.getName());

        if (strippedMsg.contains(strippedKiller)) {
            String action = strippedMsg.replace(victimName, "").replace(strippedKiller, "").trim();
            event.setDeathMessage(String.format("§r%s %s %s", victimName, action, blightedCreature));
        }
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
