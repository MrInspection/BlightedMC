package fr.moussax.blightedMC.smp.features.entities.spawnable.powerful;

import fr.moussax.blightedMC.smp.core.entities.EntityImmunities;
import fr.moussax.blightedMC.smp.core.entities.EntityNameTag;
import fr.moussax.blightedMC.smp.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.smp.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.smp.core.entities.loot.LootTable;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnConditionFactory;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.smp.features.entities.spawnable.Watchling;
import fr.moussax.blightedMC.utils.Utilities;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.golem.EntityGolem;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@EntityImmunities(EntityImmunities.ImmunityType.PROJECTILE)
public class VoidlingDefender extends SpawnableEntity {

    private int projectileHits = 0;
    private boolean enraged = false;
    private boolean isEscaping = false;
    private List<LivingEntity> watchlings = new ArrayList<>();
    private long lastTeleportSmash = 0;
    private int escapeTicks = 0;

    private static final long SMASH_COOLDOWN = 5000;

    public VoidlingDefender() {
        super("VOIDLING_DEFENDER", "Voidling Defender", 200, EntityType.IRON_GOLEM, 0.002);
        setDamage(20);
        setDroppedExp(40);
        setDefense(15);

        addAttribute(Attribute.FOLLOW_RANGE, 50);
        addAttribute(Attribute.KNOCKBACK_RESISTANCE, 1.0);
        addAttribute(Attribute.MOVEMENT_SPEED, 0.25);

        setLootTable(createLootTable());
        setNameTagType(EntityNameTag.BOSS);
        setupBehavior();
    }

    private LootTable createLootTable() {
        return new LootTable()
            .setMaxDrop(2)
            .addLoot(Material.ENDER_PEARL, 4, 8, 1.0, LootDropRarity.COMMON)
            .addLoot(Material.ENDER_EYE, 1, 3, 0.31, LootDropRarity.UNCOMMON)
            .addLoot("ENCHANTED_ENDER_PEARL", 1, 4, 0.11, LootDropRarity.RARE)
            .addGemsLoot(30, 0.03, LootDropRarity.EXTRAORDINARY);
    }

    private void setupBehavior() {
        addRepeatingTask(() -> new BukkitRunnable() {
            @Override
            public void run() {
                if (isNotAlive()) {
                    cancel();
                    return;
                }
                tickSmash();
                tickEscape();
            }
        }, 20L, 20L);
    }

    private void tickSmash() {
        if (!enraged || isEscaping) return;

        if (System.currentTimeMillis() - lastTeleportSmash < SMASH_COOLDOWN) return;

        Player target = Utilities.getNearestPlayer(entity, 20);
        if (target == null) return;

        double distance = entity.getLocation().distance(target.getLocation());
        if (distance > 4) {
            performTeleportSmash(target);
        }
    }

    private void performTeleportSmash(Player target) {
        lastTeleportSmash = System.currentTimeMillis();

        entity.swingMainHand();

        Location behind = target.getLocation().add(target.getLocation().getDirection().multiply(-1.5));
        behind.setY(target.getLocation().getY());

        entity.teleport(behind);
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        Utilities.delay(() -> {
            if (isNotAlive()) return;
            entity.getWorld().spawnParticle(Particle.EXPLOSION, entity.getLocation(), 1);
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);

            for (Entity nearby : entity.getNearbyEntities(3, 2, 3)) {
                if (nearby instanceof LivingEntity living && living != entity && !(nearby instanceof Player p && p.getGameMode().equals(GameMode.SPECTATOR))) {
                    if (BlightedEntitiesListener.getBlightedEntity(living) instanceof Watchling) continue;
                    living.damage(14, entity);
                }
            }
        }, 9);
    }

    private void tickEscape() {
        if (!isEscaping) return;

        escapeTicks += 20;

        watchlings.removeIf(e -> e.isDead() || !e.isValid());

        if (watchlings.isEmpty() || escapeTicks >= 300) {
            endDeadlyEscape();
        }
    }

    @Override
    public void onDamageTaken(EntityDamageEvent event) {
        if (enraged || isEscaping) return;

        boolean trigger = false;
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            trigger = true;
        } else if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            projectileHits++;
            if (projectileHits >= 7) {
                trigger = true;
            }
        }

        if (trigger) {
            startDeadlyEscape();
        }
    }

    private void startDeadlyEscape() {
        enraged = true;
        isEscaping = true;
        escapeTicks = 0;

        Location loc = entity.getLocation();

        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
        for (Entity nearby : entity.getNearbyEntities(4, 3, 4)) {
            if (nearby instanceof LivingEntity living && living != entity) {
                if (BlightedEntitiesListener.getBlightedEntity(living) instanceof Watchling) continue;
                living.damage(14, entity);
            }
        }

        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.setAI(false);
        entity.teleport(loc.clone().add(0, 50, 0));

        int count = 3 + new Random().nextInt(4); // 3 to 6
        for (int i = 0; i < count; i++) {
            Watchling watchling = new Watchling();
            LivingEntity wEntity = watchling.spawn(loc.clone().add((Math.random() - 0.5) * 2, 0, (Math.random() - 0.5) * 2));
            watchlings.add(wEntity);
        }
    }

    private void endDeadlyEscape() {
        if (!isEscaping) return;
        isEscaping = false;

        for (LivingEntity watchling : watchlings) {
            if (!watchling.isDead()) {
                watchling.setHealth(0);
                watchling.remove();
            }
        }
        watchlings.clear();

        Player target = Utilities.getNearestPlayer(entity, 60);
        Location targetLoc = (target != null) ? target.getLocation() : entity.getLocation().subtract(0, 50, 0);

        Location reappearLoc = targetLoc.clone().add((Math.random() - 0.5) * 4, 0, (Math.random() - 0.5) * 4);
        reappearLoc.setY(targetLoc.getY());

        entity.teleport(reappearLoc);
        entity.setInvisible(false);
        entity.setInvulnerable(false);
        entity.setAI(true);

        entity.getWorld().playSound(reappearLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
    }

    @Override
    public LivingEntity spawn(Location location) {
        LivingEntity spawnedEntity = super.spawn(location);
        if (!(spawnedEntity instanceof CraftMob craftMob)) {
            return spawnedEntity;
        }

        EntityGolem nmsEndersent = (EntityGolem) craftMob.getHandle();
        nmsEndersent.cs.a(goal -> true);
        nmsEndersent.ct.a(goal -> true);

        nmsEndersent.cs.a(1, new PathfinderGoalMeleeAttack(nmsEndersent, 1.0D, false));
        nmsEndersent.cs.a(7, new PathfinderGoalRandomStrollLand(nmsEndersent, 1.0D));
        nmsEndersent.cs.a(8, new PathfinderGoalLookAtPlayer(nmsEndersent, EntityHuman.class, 8.0F));

        nmsEndersent.ct.a(2, new PathfinderGoalNearestAttackableTarget<>(nmsEndersent, EntityHuman.class, true));

        return spawnedEntity;
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnConditionFactory.biome(Biome.END_MIDLANDS));
    }

    @Override
    public VoidlingDefender clone() {
        VoidlingDefender clone = (VoidlingDefender) super.clone();
        clone.watchlings = new ArrayList<>();
        clone.projectileHits = 0;
        clone.enraged = false;
        clone.isEscaping = false;
        clone.lastTeleportSmash = 0;
        clone.escapeTicks = 0;
        clone.setupBehavior();
        return clone;
    }
}
