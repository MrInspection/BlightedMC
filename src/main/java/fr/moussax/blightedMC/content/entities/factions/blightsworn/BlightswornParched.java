package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightswornParched extends BlightswornArcherArchetype {

    public BlightswornParched() {
        super("BLIGHTSWORN_PARCHED", "Blightsworn Parched", EntityType.PARCHED);
        itemInMainHand = new ItemStack(Material.BOW);
        setDamage(6);
        setDroppedExp(12);
        setLootTable(new EntityLootTableBuilder()
                .addLoot(Material.BONE, 2, 5, 1.0, COMMON)
                .addLoot(Material.ARROW, 2, 5, 1.0, COMMON)
                .addLootWithDurabilityRange(Material.BOW, 0.10, 0.75, 0.15, RARE)
                .addGemsLoot(5, 0.04, VERY_RARE)
                .build()
        );
    }

    @Override
    protected void applyArrowEffects(Arrow arrow, boolean isPhaseTwo) {
        int duration = isPhaseTwo ? 100 : 80;
        int amplifier = isPhaseTwo ? 1 : 0;

        arrow.addCustomEffect(new PotionEffect(PotionEffectType.HUNGER, duration, amplifier), true);

        if (isPhaseTwo) {
            arrow.setFireTicks(100);
        }
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        Location loc = entity.getLocation().add(0, 1, 0);
        entity.getWorld().playSound(loc, Sound.ENTITY_SKELETON_DEATH, 1.0f, 0.5f);
        entity.getWorld().playSound(loc, Sound.ITEM_FIRECHARGE_USE, 1.0f, 0.8f);

        entity.getWorld().spawnParticle(Particle.FLAME, loc, 30, 0.5, 1.0, 0.5, 0.05);
        entity.getWorld().spawnParticle(Particle.DUST, loc, 30, 0.5, 1.0, 0.5, 0.0, BLIGHT_DUST);
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnRules.biome(Biome.DESERT).and(SpawnRules.overworldSurfaceHostile()));
    }
}
