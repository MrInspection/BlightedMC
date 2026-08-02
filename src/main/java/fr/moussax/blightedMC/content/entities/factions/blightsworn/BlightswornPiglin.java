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
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightswornPiglin extends BlightswornEliteArchetype {

    private static final double DASH_RANGE = 14.0;

    public BlightswornPiglin() {
        super("BLIGHTSWORN_PIGLIN", "Blightsworn Piglin", EntityType.PIGLIN);
        itemInMainHand = new ItemStack(Material.GOLDEN_SWORD);
        setLootTable(new EntityLootTableBuilder()
                .addLoot(Material.GOLD_NUGGET, 2, 6, 1.0, COMMON)
                .addLoot(Material.GOLD_INGOT, 1, 3, 0.4, UNCOMMON)
                .addLootWithDurabilityRange(Material.CROSSBOW, 0.10, 0.80, 0.1, RARE)
                .addGemsLoot(5, 0.04, VERY_RARE)
                .build()
        );
        setDamage(8);
        setDroppedExp(16);
    }

    @Override
    public LivingEntity spawn(Location location) {
        LivingEntity spawnedEntity = super.spawn(location);
        ((Piglin) spawnedEntity).setImmuneToZombification(true);
        return spawnedEntity;
    }

    @Override
    protected void onNormalBehavior() {
        addPhaseAbility(110L, 110L, () -> executeHunterDash(false));
    }

    @Override
    protected void onEnrageBehavior() {
        addPhaseAbility(80L, 80L, () -> executeHunterDash(true));
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        Location loc = entity.getLocation().add(0, 1, 0);
        entity.getWorld().playSound(loc, Sound.ENTITY_PIGLIN_ANGRY, 1.5f, 0.5f);
        entity.getWorld().playSound(loc, Sound.ITEM_ARMOR_EQUIP_GOLD, 1.0f, 0.5f);

        entity.getWorld().spawnParticle(Particle.CRIT, loc, 40, 0.5, 1.0, 0.5, 0.1);
        entity.getWorld().spawnParticle(Particle.DUST, loc, 30, 0.5, 1.0, 0.5, 0.0, BLIGHT_DUST);
    }

    private void executeHunterDash(boolean isPhaseTwo) {
        if (isNotAlive()) return;

        Player target = getNearestPlayer(DASH_RANGE);
        if (target == null || target.isDead() || target.getWorld() != entity.getWorld() || !hasLineOfSight(target)) return;

        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PIGLIN_JEALOUS, 1.0f, 1.2f);
        entity.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, entity.getLocation().add(0, 1, 0), 15, 0.5, 0.5, 0.5, 0.02);

        addCoreDelayedAction(12L, () -> {
            if (isNotAlive()) return;

            Vector dashDirection = target.getLocation().toVector().subtract(entity.getLocation().toVector());
            if (dashDirection.lengthSquared() > 0) {
                dashDirection.normalize().multiply(isPhaseTwo ? 1.5 : 1.2).setY(0.25);
                entity.setVelocity(dashDirection);

                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0f, 0.8f);

                if (isPhaseTwo) {
                    applyHuntingSnare();
                }
            }
        });
    }

    private void applyHuntingSnare() {
        getNearbyPlayers(4.0).forEach(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
            player.getWorld().spawnParticle(Particle.BLOCK, player.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1, Material.COBWEB.createBlockData());
        });
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnRules.biome(Biome.NETHER_WASTES, Biome.CRIMSON_FOREST).and(SpawnRules.netherHostile()));
    }
}
