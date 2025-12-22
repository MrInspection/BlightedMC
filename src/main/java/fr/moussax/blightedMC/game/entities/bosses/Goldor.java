package fr.moussax.blightedMC.game.entities.bosses;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.EntityNameTag;
import fr.moussax.blightedMC.core.entities.rituals.AncientCreature;
import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Goldor extends AncientCreature {

    private List<Giant> orbitingSwords = new ArrayList<>();
    private List<SwordProjectile> activeProjectiles = new ArrayList<>();
    private BukkitRunnable abilityRunnable;

    private static final int SWORD_COUNT = 3;
    private static final double ORBIT_RADIUS = 3;
    private static final double ORBIT_SPEED = 0.12;
    private static final double GIANT_HAND_HEIGHT_OFFSET = 7.5;

    private double currentAngle = 0.0;

    public Goldor() {
        super("Goldor", 250, 50, 40, EntityType.WITHER);
        setNameTagType(EntityNameTag.BOSS);

        addAttribute(Attribute.MOVEMENT_SPEED, 0.4);
        addAttribute(Attribute.KNOCKBACK_RESISTANCE, 1.0);
        addAttribute(Attribute.SCALE, 1.2);

        setupAbilityTask(this);
    }

    private void setupAbilityTask(Goldor instance) {
        instance.addRepeatingTask(() -> {
            instance.abilityRunnable = new BukkitRunnable() {
                private int tickCounter = 0;

                @Override
                public void run() {
                    if (instance.entity == null || instance.entity.isDead()) {
                        instance.stopAbility();
                        cancel();
                        return;
                    }

                    if (instance.orbitingSwords.isEmpty() && tickCounter == 0) {
                        instance.spawnSwords();
                    }

                    instance.performOrbit();

                    if (tickCounter > 0 && tickCounter % 80 == 0) {
                        instance.performThrowAbility();
                    }

                    if (tickCounter % 20 == 0) {
                        instance.regenerateSwords();
                    }

                    tickCounter++;
                }
            };
            return instance.abilityRunnable;
        }, 0L, 1L);
    }

    @Override
    public String getEntityId() {
        return "GOLDOR_WITHER_BOSS";
    }

    @Override
    public void kill() {
        stopAbility();
        super.kill();
    }

    @Override
    public void onDeath(Location location) {
        stopAbility();
        super.onDeath(location);
    }

    private void stopAbility() {
        if (abilityRunnable != null && !abilityRunnable.isCancelled()) {
            try {
                abilityRunnable.cancel();
            } catch (Exception ignored) {
            }
            abilityRunnable = null;
        }

        for (Giant sword : orbitingSwords) {
            if (sword != null) sword.remove();
        }
        orbitingSwords.clear();

        for (SwordProjectile proj : new ArrayList<>(activeProjectiles)) {
            proj.cancel();
        }
        activeProjectiles.clear();
    }

    // --- LOGIC METHODS ---

    private void spawnSwords() {
        for (int i = 0; i < SWORD_COUNT; i++) {
            createGiantSword();
        }
    }

    private void createGiantSword() {
        Location spawnLoc = entity.getLocation().subtract(0, GIANT_HAND_HEIGHT_OFFSET, 0);
        Giant giant = (Giant) entity.getWorld().spawnEntity(spawnLoc, EntityType.GIANT);

        giant.setInvisible(true);
        giant.setAI(false);
        giant.setGravity(false);
        giant.setSilent(true);
        giant.setInvulnerable(true);
        giant.setCollidable(false);
        giant.setPersistent(true);
        giant.setRemoveWhenFarAway(false);

        if (giant.getEquipment() != null) {
            giant.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
            giant.getEquipment().setItemInMainHandDropChance(0f);
        }

        orbitingSwords.add(giant);
    }

    private void performOrbit() {
        currentAngle += ORBIT_SPEED;
        Location center = entity.getLocation().add(0, 1.5 - GIANT_HAND_HEIGHT_OFFSET, 0);
        double angleStep = (2 * Math.PI) / SWORD_COUNT;

        Iterator<Giant> it = orbitingSwords.iterator();
        int index = 0;

        while (it.hasNext()) {
            Giant giant = it.next();
            if (giant == null || giant.isDead()) {
                it.remove();
                continue;
            }

            double offsetAngle = currentAngle + (index * angleStep);
            double x = Math.cos(offsetAngle) * ORBIT_RADIUS;
            double z = Math.sin(offsetAngle) * ORBIT_RADIUS;

            Location target = center.clone().add(x, 0, z);
            float yaw = (float) Math.toDegrees(Math.atan2(z, x));
            target.setYaw(yaw);
            target.setPitch(0);

            giant.teleport(target);
            index++;
        }
    }

    private void performThrowAbility() {
        if (orbitingSwords.isEmpty()) return;

        Player target = Utilities.getNearestPlayer(entity, 40);
        if (target == null) return;

        Giant sword = orbitingSwords.removeFirst();
        activeProjectiles.add(new SwordProjectile(sword, target));

        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 0.5f);
    }

    private void regenerateSwords() {
        if (orbitingSwords.size() < SWORD_COUNT && Math.random() < 0.15) {
            createGiantSword();
            entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1f, 0.5f);
        }
    }

    @Override
    public Goldor clone() {
        Goldor clone = (Goldor) super.clone();

        clone.abilityRunnable = null;
        clone.orbitingSwords = new ArrayList<>();
        clone.activeProjectiles = new ArrayList<>();

        setupAbilityTask(clone);

        return clone;
    }

    private class SwordProjectile extends BukkitRunnable {
        private final Giant sword;
        private final Vector direction;
        private int life = 0;

        public SwordProjectile(Giant sword, Player target) {
            this.sword = sword;

            Location start = sword.getLocation();
            Location end = target.getLocation().add(0, 1 - GIANT_HAND_HEIGHT_OFFSET, 0);
            this.direction = end.toVector().subtract(start.toVector()).normalize().multiply(1.5);

            start.setDirection(direction);
            sword.teleport(start);

            this.runTaskTimer(BlightedMC.getInstance(), 0L, 1L);
        }

        @Override
        public void run() {
            if (life++ > 60 || sword.isDead() || !sword.isValid()) {
                explode();
                return;
            }

            sword.teleport(sword.getLocation().add(direction));

            Location effectLoc = sword.getLocation().add(0, GIANT_HAND_HEIGHT_OFFSET, 0);
            sword.getWorld().spawnParticle(Particle.CRIT, effectLoc, 5, 0.5, 0.5, 0.5, 0);

            for (Entity e : sword.getWorld().getNearbyEntities(effectLoc, 2.0, 2.0, 2.0)) {
                if (e instanceof LivingEntity victim
                    && !victim.getUniqueId().equals(entity.getUniqueId())
                    && !victim.equals(sword)
                    && !(victim instanceof Giant)) {

                    victim.damage(30, entity);
                    explode();
                    return;
                }
            }

            if (effectLoc.getBlock().getType().isSolid()) {
                explode();
            }
        }

        private void explode() {
            if (sword != null && !sword.isDead()) {
                Location loc = sword.getLocation().add(0, GIANT_HAND_HEIGHT_OFFSET, 0);
                sword.getWorld().createExplosion(loc, 2.5f, false, false, entity);
                sword.remove();
            }
            activeProjectiles.remove(this);
            this.cancel();
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            if (sword != null && !sword.isDead()) sword.remove();
        }
    }
}