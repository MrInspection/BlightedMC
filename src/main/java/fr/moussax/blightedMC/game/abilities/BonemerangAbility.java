package fr.moussax.blightedMC.game.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.abilities.AbilityManager;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BonemerangAbility implements AbilityManager<PlayerInteractEvent> {
    private static final NamespacedKey UUID_KEY = new NamespacedKey(BlightedMC.getInstance(), "bonemerang_uuid");
    private static final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    private static final int OUTBOUND_TICKS = 13;
    private static final int RETURN_TICKS = 13;
    private static final double PROJECTILE_SPEED = 1.16;
    private static final double COLLISION_RADIUS = 1.15;
    private static final double DAMAGE_AMOUNT = 16.0;
    private static final double SPAWN_HEIGHT_OFFSET = 1.1;
    private static final double ROTATION_SPEED = 35.0;

    @Override
    public boolean triggerAbility(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return false;
        }

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand.getType() != Material.BONE || hand.getAmount() < 1) {
            return false;
        }

        String itemUuid = getOrAssignItemUuid(hand);

        if (isItemOnCooldown(player, itemUuid)) {
            return false;
        }

        setItemCooldown(player, itemUuid, getCooldownSeconds());

        ItemStack thrownCopy = hand.clone();
        reduceStackInHand(player, hand);
        setThrownVisualInHand(player, thrownCopy);
        launchProjectile(player, thrownCopy);

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

    private boolean isItemOnCooldown(Player player, String uuid) {
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());
        long currentTime = System.currentTimeMillis();
        return playerCooldowns.getOrDefault(uuid, 0L) > currentTime;
    }

    private void setItemCooldown(Player player, String uuid, int seconds) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>())
            .put(uuid, System.currentTimeMillis() + (seconds * 1000L));
    }

    private String getOrAssignItemUuid(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return "LEGACY";
        }

        var container = meta.getPersistentDataContainer();
        if (!container.has(UUID_KEY, PersistentDataType.STRING)) {
            container.set(UUID_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
            item.setItemMeta(meta);
        }
        return container.get(UUID_KEY, PersistentDataType.STRING);
    }

    private void reduceStackInHand(Player player, ItemStack hand) {
        if (hand.getAmount() <= 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            hand.setAmount(hand.getAmount() - 1);
        }
    }

    private void setThrownVisualInHand(Player player, ItemStack from) {
        ItemStack nugget = from.clone();
        nugget.setType(Material.IRON_NUGGET);
        var meta = from.getItemMeta();
        if (meta != null) {
            nugget.setItemMeta(meta.clone());
        }
        player.getInventory().setItemInMainHand(nugget);
    }

    private void launchProjectile(Player player, ItemStack toRestore) {
        ArmorStand projectile = player.getWorld().spawn(
            player.getLocation().add(0, SPAWN_HEIGHT_OFFSET, 0),
            ArmorStand.class,
            this::configureArmorStand
        );

        player.playSound(player.getLocation(), Sound.BLOCK_BONE_BLOCK_BREAK, 2.0f, 1.75f);

        Vector direction = player.getLocation().getDirection().normalize();
        String playerName = player.getName();

        new BukkitRunnable() {
            int tick = 0;
            boolean returning = false;

            @Override
            public void run() {
                tick++;

                if (!projectile.isValid() || projectile.isDead()) {
                    cancel();
                    returnItem(player, playerName, toRestore);
                    return;
                }

                if (!projectile.getLocation().getBlock().isPassable()) {
                    projectile.remove();
                    returnItem(player, playerName, toRestore);
                    cancel();
                    return;
                }

                if (tick == OUTBOUND_TICKS) {
                    returning = true;
                }

                updateProjectilePosition(projectile, player, direction, returning);
                updateProjectileRotation(projectile, tick);
                damageNearbyEntities(projectile, player);
                spawnParticles(projectile, returning);

                if (tick >= OUTBOUND_TICKS + RETURN_TICKS) {
                    projectile.remove();
                    returnItem(player, playerName, toRestore);
                    cancel();
                }
            }
        }.runTaskTimer(BlightedMC.getInstance(), 1, 1);
    }

    private void configureArmorStand(ArmorStand stand) {
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setMarker(true);
        stand.setGravity(false);
        stand.setSmall(false);
        stand.setArms(true);
        stand.setBasePlate(false);

        var equipment = stand.getEquipment();
        if (equipment != null) {
            equipment.setItemInMainHand(new ItemStack(Material.BONE));
        }

        stand.setRightArmPose(new EulerAngle(0, Math.toRadians(90), 0));
    }

    private void updateProjectilePosition(ArmorStand projectile, Player player, Vector direction, boolean returning) {
        Vector movement = returning
            ? player.getLocation().subtract(projectile.getLocation()).toVector().normalize().multiply(PROJECTILE_SPEED)
            : direction.clone().multiply(PROJECTILE_SPEED);

        projectile.teleport(projectile.getLocation().add(movement));
    }

    private void updateProjectileRotation(ArmorStand projectile, int tick) {
        projectile.setRightArmPose(new EulerAngle(
            0,
            Math.toRadians(90 + (ROTATION_SPEED * tick)),
            0
        ));
    }

    private void damageNearbyEntities(ArmorStand projectile, Player player) {
        for (Entity entity : projectile.getNearbyEntities(COLLISION_RADIUS, 1, COLLISION_RADIUS)) {
            if (entity != player && entity instanceof LivingEntity damageable) {
                damageable.damage(DAMAGE_AMOUNT, player);
            }
        }
    }

    private void spawnParticles(ArmorStand projectile, boolean returning) {
        if (returning) {
            projectile.getWorld().spawnParticle(Particle.CRIT, projectile.getLocation(), 3, 0.1, 0.1, 0.1, 0.02);
        } else {
            projectile.getWorld().spawnParticle(Particle.SWEEP_ATTACK, projectile.getLocation(), 2, 0.05, 0.05, 0.05, 0.01);
        }
    }

    private void returnItem(Player player, String expectedName, ItemStack original) {
        if (player == null || !player.getName().equals(expectedName)) {
            return;
        }

        var inventory = player.getInventory();
        if (replaceNuggetWithOriginal(inventory, original)) {
            return;
        }

        Map<Integer, ItemStack> leftovers = inventory.addItem(original);
        if (!leftovers.isEmpty()) {
            leftovers.values().forEach(item ->
                player.getWorld().dropItemNaturally(player.getLocation(), item)
            );
        }
    }

    private boolean replaceNuggetWithOriginal(org.bukkit.inventory.Inventory inventory, ItemStack original) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack slot = inventory.getItem(i);
            if (isSameCustomNugget(slot, original)) {
                inventory.setItem(i, original);
                return true;
            }
        }
        return false;
    }

    private boolean isSameCustomNugget(ItemStack a, ItemStack b) {
        if (a == null || b == null || a.getType() != Material.IRON_NUGGET) {
            return false;
        }

        var aMeta = a.getItemMeta();
        var bMeta = b.getItemMeta();

        return aMeta != null && aMeta.equals(bMeta);
    }
}