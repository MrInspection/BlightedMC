package fr.moussax.blightedMC.content.entities.ravenous;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class RavenousHusk extends RavenousCreature {
    public RavenousHusk() {
        super("RAVENOUS_HUSK", "Ravenous Husk", EntityType.HUSK);
        setDamage(6);
        setDroppedExp(12);
        setLootTable(new EntityLootTableBuilder()
            .addLoot(Material.ROTTEN_FLESH, 2, 5, 1.0, COMMON)
            .addLoot(Material.SAND, 1, 3, 0.3, UNCOMMON)
            .addLoot(Material.IRON_INGOT, 1, 2, 0.1, RARE)
            .addGemsLoot(5, 0.04, VERY_RARE)
            .build()
        );
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 0));
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
            SpawnRules.biome(Biome.DESERT)
                .and(SpawnRules.maxBlockLight(0))
                .and(SpawnRules.maxLightLevel(7))
                .and(SpawnRules.skyExposed())
                .and(SpawnRules.notInLiquid())
        );
    }
}
