package fr.moussax.blightedMC.smp.features.entities.spawnable;

import fr.moussax.blightedMC.smp.core.entities.BlightedLootBuilder;
import fr.moussax.blightedMC.smp.core.entities.spawnable.condition.SpawnConditionFactory;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.utils.Utilities;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntityEnderman;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftMob;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.COMMON;
import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.VERY_RARE;

public class Watchling extends SpawnableEntity {

    private long lastTeleportTime = 0;
    private static final long TELEPORT_COOLDOWN = 4000;
    private final Random random = new Random();

    public Watchling() {
        super("WATCHLING", "§dWatchling", 20, EntityType.ENDERMAN, 0.001);
        addAttribute(Attribute.SCALE, 0.7);
        addAttribute(Attribute.MOVEMENT_SPEED, 0.35);
        addAttribute(Attribute.FOLLOW_RANGE, 50);
        setDamage(10);
        setDroppedExp(10);

        setLootTable(new BlightedLootBuilder()
            .setMaxDrop(2)
            .addLoot(Material.ENDER_PEARL, 1, 2, 1.0, COMMON)
            .addGemsLoot(5, 0.03, VERY_RARE)
            .build()
        );
        setupBehavior();
    }

    private void setupBehavior() {
        addRepeatingTask(() -> new BukkitRunnable() {
            @Override
            public void run() {
                if (isNotAlive()) {
                    cancel();
                    return;
                }
                handleCombatLogic();
            }
        }, 5L, 5L);
    }

    private void handleCombatLogic() {
        if (!(((Mob) entity).getTarget() instanceof Player target)) return;

        double distance = entity.getLocation().distance(target.getLocation());

        if (distance > 6 && distance < 20 && canTeleport()) {
            teleportToTarget(target);
        }
    }

    private boolean canTeleport() {
        return System.currentTimeMillis() - lastTeleportTime > TELEPORT_COOLDOWN;
    }

    private void teleportToTarget(Player target) {
        lastTeleportTime = System.currentTimeMillis();

        // Find a location slightly offset from the target
        Location loc = target.getLocation().add(
            (random.nextDouble() - 0.5) * 2,
            0,
            (random.nextDouble() - 0.5) * 2
        );

        entity.teleport(loc);
        entity.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);

        // Decide between Basic or Heavy attack immediately after teleport
        if (random.nextDouble() < 0.3) {
            performHeavyAttack(target);
        }
    }

    private void performHeavyAttack(Player target) {
        entity.swingMainHand();
        entity.swingOffHand();

        Utilities.delay(() -> {
            if (isNotAlive() || target.getLocation().distance(entity.getLocation()) > 3) return;

            target.damage(getDamage() * 2, entity);
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5f, 1.2f);

            if (random.nextBoolean()) {
                executeEscapeTeleport();
            }
        }, 10);
    }

    private void executeEscapeTeleport() {
        Location escapeLoc = entity.getLocation().add(
            (random.nextDouble() - 0.5) * 15,
            0,
            (random.nextDouble() - 0.5) * 15
        );
        entity.teleport(escapeLoc);
        entity.setInvisible(true);
        Utilities.delay(() -> {
            if (!isNotAlive()) entity.setInvisible(false);
        }, 40); // Reappear after 2 seconds
    }

    @Override
    public LivingEntity spawn(Location location) {
        LivingEntity spawnedEntity = super.spawn(location);
        if (!(spawnedEntity instanceof CraftMob craftMob)) {
            return spawnedEntity;
        }

        EntityEnderman nmsWatchling = (EntityEnderman) craftMob.getHandle();

        nmsWatchling.cs.a(goal -> true);
        nmsWatchling.ct.a(goal -> true);

        nmsWatchling.cs.a(1, new PathfinderGoalMeleeAttack(nmsWatchling, 1.2D, false));
        nmsWatchling.cs.a(7, new PathfinderGoalRandomStrollLand(nmsWatchling, 1.0D));
        nmsWatchling.cs.a(8, new PathfinderGoalLookAtPlayer(nmsWatchling, EntityHuman.class, 8.0F));

        nmsWatchling.ct.a(2, new PathfinderGoalNearestAttackableTarget<>(nmsWatchling, EntityHuman.class, true));

        return spawnedEntity;
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnConditionFactory
            .biome(Biome.END_BARRENS, Biome.END_MIDLANDS)
            .or(SpawnConditionFactory.insideStructure(Structure.END_CITY))
        );
    }

    @Override
    public Watchling clone() {
        Watchling clone = (Watchling) super.clone();
        clone.lastTeleportTime = 0;
        clone.setupBehavior();
        return clone;
    }
}
