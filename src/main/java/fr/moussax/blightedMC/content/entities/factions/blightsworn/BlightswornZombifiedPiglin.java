package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightswornZombifiedPiglin extends BlightswornEliteArchetype {

    private static final double CHARGE_RANGE = 12.0;

    public BlightswornZombifiedPiglin() {
        super("BLIGHTSWORN_ZOMBIFIED_PIGLIN", "Blightsworn Zombified Piglin", EntityType.ZOMBIFIED_PIGLIN);
        itemInMainHand = new ItemStack(Material.GOLDEN_SWORD);
        setDamage(8);
        setDroppedExp(16);
        setLootTable(new EntityLootTableBuilder()
                .setMaxDrop(3)
                .addLoot(Material.ROTTEN_FLESH, 2, 6, 1.0, COMMON)
                .addLoot(Material.GOLD_NUGGET, 2, 6, 1.0, COMMON)
                .addLoot(Material.GOLD_INGOT, 1, 2, 0.15, RARE)
                .addGemsLoot(5, 0.04, VERY_RARE)
                .build()
        );
    }

    @Override
    protected void onNormalBehavior() {
        addPhaseAbility(120L, 120L, () -> executeInfernalCharge(false));
    }

    @Override
    protected void onEnrageBehavior() {
        addPhaseAbility(90L, 90L, () -> executeInfernalCharge(true));
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        Location loc = entity.getLocation().add(0, 1, 0);
        entity.getWorld().playSound(loc, Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 1.5f, 0.5f);
        entity.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.8f);

        entity.getWorld().spawnParticle(Particle.LAVA, loc, 30, 0.5, 1.0, 0.5, 0.1);
        entity.getWorld().spawnParticle(Particle.DUST, loc, 30, 0.5, 1.0, 0.5, 0.0, BLIGHT_DUST);
    }

    private void executeInfernalCharge(boolean isPhaseTwo) {
        if (isNotAlive()) return;

        Player target = getNearestPlayer(CHARGE_RANGE);
        if (target == null || target.isDead() || target.getWorld() != entity.getWorld() || !hasLineOfSight(target)) return;

        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 1.0f, 1.2f);
        entity.getWorld().spawnParticle(Particle.FLAME, entity.getLocation().add(0, 1, 0), 20, 0.5, 1.0, 0.5, 0.05);

        addCoreDelayedAction(15L, () -> {
            if (isNotAlive()) return;

            Vector chargeDirection = target.getLocation().toVector().subtract(entity.getLocation().toVector());
            if (chargeDirection.lengthSquared() > 0) {
                chargeDirection.normalize().multiply(isPhaseTwo ? 1.6 : 1.2).setY(0.2);
                entity.setVelocity(chargeDirection);

                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 0.5f);

                if (isPhaseTwo) {
                    executePhaseTwoIgnition();
                }
            }
        });
    }

    private void executePhaseTwoIgnition() {
        Location loc = entity.getLocation();
        entity.getWorld().spawnParticle(Particle.LAVA, loc.add(0, 1, 0), 15, 0.5, 0.5, 0.5, 0.1);

        getNearbyPlayers(3.0).forEach(player -> {
            player.damage(this.damage * 0.5, entity);
            player.setFireTicks(80); // 4 seconds of fire
        });
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
                SpawnRules.biome(Biome.NETHER_WASTES, Biome.CRIMSON_FOREST)
                        .or(SpawnRules.insideStructure(Structure.FORTRESS))
                        .and(SpawnRules.netherHostile())
        );
    }
}
