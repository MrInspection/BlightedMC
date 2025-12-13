package fr.moussax.blightedMC.game.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.abilities.AbilityManager;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.formatting.Formatter;
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
    public boolean triggerAbility(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return false;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isHoldingHyperion(player)) return false;
        teleport(player);
        int entitiesDamaged = damageNearbyEntities(player);
        notifyPlayerOfAbilityDamage(player, entitiesDamaged);

        if (canUseHealingAbility(player)) {
            applyHealingEffect(player);
            setHealingCooldown(player);
        }
        return true;
    }

    @Override
    public int getCooldownSeconds() {
        return 0;
    }

    @Override
    public int getManaCost() {
        return 0;
    }

    @Override
    public boolean canTrigger(BlightedPlayer player) {
        return true;
    }

    @Override
    public void start(BlightedPlayer player) {
    }

    @Override
    public void stop(BlightedPlayer player) {
    }

    private boolean isHoldingHyperion(Player player) {
        return player.getInventory().getItemInMainHand().equals(ItemDirectory.getItem("HYPERION").toItemStack());
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

    private int damageNearbyEntities(Player origin) {
        double damage = MIN_DAMAGE + (Math.random() * (MAX_DAMAGE - MIN_DAMAGE));
        int entitiesDamaged = 0;

        for (Entity entity : origin.getNearbyEntities(DAMAGE_RANGE, DAMAGE_RANGE, DAMAGE_RANGE)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                ((LivingEntity) entity).damage(damage);
                entitiesDamaged++;
            }
        }
        return entitiesDamaged;
    }

    private void notifyPlayerOfAbilityDamage(Player player, int entitiesDamaged) {
        if (entitiesDamaged > 0) {
            double totalDamage = (double) entitiesDamaged * (15000.0D + Math.random() * 135000.0D);
            Formatter.inform(player, "Your implosion hit §d" + entitiesDamaged + " §7enem" + (entitiesDamaged > 1 ? "ies" : "y") + " for §d" + Formatter.formatDouble(totalDamage, 2) + " §7damage.");
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
        }.runTaskLater(BlightedMC.getInstance(), 100L);
    }
}
