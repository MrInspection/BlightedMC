package fr.moussax.blightedMC.engine.player;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.listeners.BlightedEntitiesListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public final class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new BlightedPlayer(event.getPlayer());
        event.setJoinMessage("§8 ■ §f" + event.getPlayer().getName() + " §7joined the SMP.");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Clear NMS AI targets to prevent stalling on offline players
        for (BlightedEntity blighted : BlightedEntitiesListener.getActiveEntities()) {
            LivingEntity entity = blighted.getEntity();
            if (entity instanceof Mob mob && player.equals(mob.getTarget())) {
                mob.setTarget(null);
            }
        }

        BlightedPlayer blighted = BlightedPlayer.getBlightedPlayer(player);
        if (blighted != null) {
            blighted.saveData();
            BlightedPlayer.removePlayer(player);
        }
        event.setQuitMessage("§8 ■ §f" + player.getName() + " §7left the SMP.");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getEntity();

        // Clear NMS AI targets to prevent pathfinding to the distant respawn location
        for (BlightedEntity blighted : BlightedEntitiesListener.getActiveEntities()) {
            LivingEntity entity = blighted.getEntity();
            if (entity instanceof Mob mob && deadPlayer.equals(mob.getTarget())) {
                mob.setTarget(null);
            }
        }

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
}
