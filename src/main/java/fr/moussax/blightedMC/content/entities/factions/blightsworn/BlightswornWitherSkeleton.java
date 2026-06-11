package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightswornWitherSkeleton extends BlightswornEliteArchetype {

    private static final double LUNGE_RANGE = 12.0;

    public BlightswornWitherSkeleton() {
        super("BLIGHTSWORN_WITHER_SKELETON", "Blightsworn Wither Skeleton", EntityType.WITHER_SKELETON);
        setLootTable(new EntityLootTableBuilder()
                .setMaxDrop(4)
                .addLoot(Material.BONE, 2, 5, 1.0, COMMON)
                .addLoot(Material.COAL, 1, 3, 0.5, UNCOMMON)
                .addLoot(Material.WITHER_SKELETON_SKULL, 1, 1, 0.03, VERY_RARE)
                .addGemsLoot(5, 0.04, VERY_RARE)
                .build()
        );

        setDamage(8);
        setDroppedExp(20);
        itemInMainHand = new ItemStack(Material.STONE_SWORD);
    }

    @Override
    protected void onNormalBehavior() {
        addPhaseAbility(100L, 100L, () -> executePhantomLunge(false));
    }

    @Override
    protected void onEnrageBehavior() {
        addPhaseAbility(80L, 80L, () -> executePhantomLunge(true));
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        Location loc = entity.getLocation().add(0, 1, 0);
        entity.getWorld().playSound(loc, Sound.ENTITY_WITHER_SKELETON_DEATH, 1.0f, 0.5f);
        entity.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 0.5f, 1.5f);

        entity.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc, 50, 0.5, 1.0, 0.5, 0.05);
        entity.getWorld().spawnParticle(Particle.DUST, loc, 30, 0.5, 1.0, 0.5, 0.0, BLIGHT_DUST);
    }

    private void executePhantomLunge(boolean isPhaseTwo) {
        if (isNotAlive()) return;

        Player target = getNearestPlayer(LUNGE_RANGE);
        if (target == null || target.isDead() || target.getWorld() != entity.getWorld() || !hasLineOfSight(target)) return;

        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        entity.getWorld().spawnParticle(Particle.PORTAL, entity.getLocation().add(0, 1, 0), 20, 0.5, 1.0, 0.5, 0.1);

        addCoreDelayedAction(10L, () -> {
            if (isNotAlive()) return;

            Vector lungeDirection = target.getLocation().toVector().subtract(entity.getLocation().toVector());
            if (lungeDirection.lengthSquared() > 0) {
                lungeDirection.normalize().multiply(isPhaseTwo ? 1.6 : 1.2).setY(0.25);
                entity.setVelocity(lungeDirection);

                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.5f, 1.2f);

                if (isPhaseTwo) {
                    spawnWitherResidue();
                }
            }
        });
    }

    private void spawnWitherResidue() {
        AreaEffectCloud cloud = entity.getWorld().spawn(entity.getLocation(), AreaEffectCloud.class);
        cloud.setRadius(1.5f);
        cloud.setDuration(60);
        cloud.setWaitTime(0);
        cloud.setParticle(Particle.SMOKE);
        cloud.addCustomEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0), true);
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnRules.insideStructure(Structure.FORTRESS).and(SpawnRules.maxBlockLight(0)));
    }
}
