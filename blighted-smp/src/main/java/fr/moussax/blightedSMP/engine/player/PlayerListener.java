package fr.moussax.blightedSMP.engine.player;

import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedSMP.engine.items.registry.ItemRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BlightedPlayer.get(event.getPlayer());
        event.setJoinMessage(" §f" + event.getPlayer().getName() + " §7joined the SMP.");

        if (!player.hasPlayedBefore()) {
            player.getInventory().setHelmet(ItemRegistry.getItem("BLIGHTED_BANNER").toItemStack());
            player.getInventory().setItemInOffHand(ItemRegistry.getItem("BLIGHTED_CODEX").toItemStack());

            player.setHealth(4.0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 140, 0, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 1, false, false, false));

            player.sendTitle("§f§lThe Blight Awakens", "§7The whispers fade into reality...", 10, 80, 20);

            player.sendMessage("\n \n");
            player.sendMessage(" §f" + player.getName() + "§7, the §5§lBlight §7has already begun to take root...\n ");
            player.sendMessage(" §8§oYou claw your way back to consciousness.");
            player.sendMessage(" §cYour vitality is drained§8§o, your vision clouded...");
            player.sendMessage(" §8§o...and a §5chilling tome §8§ois bound to your left hand.");
            player.sendMessage("\n \n");

            player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.4f, 0.5f);
            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.0f, 0.5f);
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.0f, 0.5f);
            player.playSound(player.getLocation(), Sound.AMBIENT_CRIMSON_FOREST_MOOD, 1.5f, 0.7f);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        clearTargetedMobs(player);

        BlightedPlayer blighted = BlightedPlayer.getBlightedPlayer(player);
        if (blighted != null) {
            blighted.saveData();
            BlightedPlayer.removePlayer(player);
        }
        event.setQuitMessage(" §f" + player.getName() + " §7left the SMP.");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getEntity();

        clearTargetedMobs(deadPlayer);

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

        String strippedMessage = ChatColor.stripColor(deathMessage);
        String strippedKiller = ChatColor.stripColor(customNameWithHealth != null ? customNameWithHealth : killer.getName());

        if (strippedMessage.contains(strippedKiller)) {
            String action = strippedMessage.replace(victimName, "").replace(strippedKiller, "").trim();
            event.setDeathMessage(String.format("§r%s %s %s", victimName, action, blightedCreature));
        }
    }

    private void clearTargetedMobs(Player targetPlayer) {
        for (BlightedEntity blighted : BlightedEntitiesListener.getActiveEntities()) {
            LivingEntity entity = blighted.getEntity();
            if (entity instanceof Mob mob && targetPlayer.equals(mob.getTarget())) {
                mob.setTarget(null);
            }
        }
    }
}
