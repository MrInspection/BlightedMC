package fr.moussax.blightedSMP.content.items.abilities;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.engine.items.BlightedItem;
import fr.moussax.blightedSMP.engine.items.abilities.AbilityManager;
import fr.moussax.blightedSMP.engine.items.abilities.AbilityType;
import fr.moussax.blightedSMP.engine.player.BlightedPlayer;
import fr.moussax.bedrock.text.Formatter;
import fr.moussax.bedrock.text.Messenger;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WitherImpactAbility implements AbilityManager<PlayerInteractEvent>, Listener {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final double TELEPORT_DISTANCE = 10.0;
    private final double TELEPORT_STEP = 0.5;
    private final double MIN_DAMAGE = 15000.0;
    private final double MAX_DAMAGE = 150000.0;
    private final double DAMAGE_RANGE = 5.0;
    private final long HEALING_COOLDOWN = 5000L;

    @Override
    public String getName() {
        return "Wither Impact";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.RIGHT_CLICK;
    }

    @Override
    public boolean triggerAbility(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return false;
        }

        Player player = event.getPlayer();

        if (!isHoldingHyperion(player)) return false;
        teleport(player);
        damageNearbyEntities(player);

        if (canUseHealingAbility(player)) {
            applyHealingEffect(player);
            setHealingCooldown(player);
        }
        return true;
    }

    private boolean isHoldingHyperion(Player player) {
        BlightedItem blightedItem = BlightedItem.fromItemStack(player.getInventory().getItemInMainHand());
        return blightedItem != null && "HYPERION".equals(blightedItem.getItemId());
    }

    private void teleport(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        Location teleportDestination = findTeleportDestination(player, direction);
        player.teleport(teleportDestination);
        World world = player.getWorld();
        world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 100.0F, 1.0F);
        world.spawnParticle(Particle.EXPLOSION, player.getLocation(), 5);
    }

    private Location findTeleportDestination(Player player, Vector direction) {
        Location location = player.getLocation();
        for (double i = 0; i < TELEPORT_DISTANCE; i += TELEPORT_STEP) {
            Location testLocation = location.clone().add(direction.clone().multiply(i));
            if (!testLocation.getBlock().isPassable())
                return location.clone().add(direction.clone().multiply(i - TELEPORT_STEP));
        }
        return location.clone().add(direction.clone().multiply(TELEPORT_DISTANCE));
    }

    private double damageNearbyEntities(Player origin) {
        double damage = MIN_DAMAGE + (Math.random() * (MAX_DAMAGE - MIN_DAMAGE));
        double totalDamageDealt = 0.0;
        int entitiesDamaged = 0;

        for (Entity entity : origin.getNearbyEntities(DAMAGE_RANGE, DAMAGE_RANGE, DAMAGE_RANGE)) {
            if (entity instanceof LivingEntity livingEntity && !(entity instanceof Player)) {
                livingEntity.damage(damage, origin);
                totalDamageDealt += damage;
                entitiesDamaged++;
            }
        }
        notifyPlayerOfAbilityDamage(origin, entitiesDamaged, totalDamageDealt);
        return totalDamageDealt;
    }

    private void notifyPlayerOfAbilityDamage(Player player, int entitiesDamaged, double totalDamage) {
        if (entitiesDamaged > 0) {
            Messenger.inform(player, "Your implosion hit §d" + entitiesDamaged + " §7enem" + (entitiesDamaged > 1 ? "ies" : "y") + " for §d" + Formatter.formatDecimalWithCommas(totalDamage) + " §7damage.");
        }
    }

    private boolean canUseHealingAbility(Player player) {
        Long lastUsed = cooldowns.get(player.getUniqueId());
        return lastUsed == null || System.currentTimeMillis() >= lastUsed;
    }

    private void applyHealingEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 10));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 1));
        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        world.playSound(playerLocation, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);
        world.spawnParticle(Particle.EXPLOSION, playerLocation, 1);
    }

    private void setHealingCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 5000L);
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldowns.remove(player.getUniqueId());
            }
        }.runTaskLater(BlightedSMP.getInstance(), 100L);
    }
}
