package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import fr.moussax.blightedMC.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.core.entities.loot.LootTable;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnConditions;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BlightedZombie extends BlightedFaction {

    public BlightedZombie() {
        super("BLIGHTED_ZOMBIE", "Blighted Zombie", 30, EntityType.ZOMBIE);
        setDamage(8);
        setDroppedExp(10);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 0));
    }

    private LootTable createLootTable() {
        return new LootTable()
            .setMaxDrop(2)
            .addLoot(Material.ROTTEN_FLESH, 1, 2, 1, LootDropRarity.COMMON)
            .addGemsLoot(5, 0.03, LootDropRarity.EXTRAORDINARY);
    }

    @Override
    protected void setupSpawnConditions() {
        addSpawnCondition(
            SpawnConditions.biome(
                Biome.PLAINS,
                Biome.SUNFLOWER_PLAINS,
                Biome.FOREST,
                Biome.FLOWER_FOREST,
                Biome.BIRCH_FOREST,
                Biome.OLD_GROWTH_BIRCH_FOREST,
                Biome.DARK_FOREST,
                Biome.TAIGA,
                Biome.OLD_GROWTH_SPRUCE_TAIGA,
                Biome.SNOWY_TAIGA,
                Biome.BAMBOO_JUNGLE,
                Biome.JUNGLE,
                Biome.SPARSE_JUNGLE,
                Biome.WINDSWEPT_HILLS,
                Biome.WINDSWEPT_FOREST,
                Biome.WINDSWEPT_GRAVELLY_HILLS,
                Biome.WINDSWEPT_SAVANNA,
                Biome.STONY_PEAKS,
                Biome.JAGGED_PEAKS,
                Biome.FROZEN_PEAKS,
                Biome.SNOWY_SLOPES,
                Biome.MEADOW,
                Biome.GROVE,
                Biome.SAVANNA,
                Biome.SAVANNA_PLATEAU,
                Biome.SWAMP,
                Biome.MANGROVE_SWAMP,
                Biome.BEACH,
                Biome.SNOWY_BEACH,
                Biome.STONY_SHORE,
                Biome.RIVER,
                Biome.FROZEN_RIVER,
                Biome.OCEAN,
                Biome.COLD_OCEAN,
                Biome.FROZEN_OCEAN,
                Biome.LUKEWARM_OCEAN,
                Biome.WARM_OCEAN,
                Biome.DEEP_OCEAN,
                Biome.DEEP_COLD_OCEAN,
                Biome.DEEP_FROZEN_OCEAN,
                Biome.DEEP_LUKEWARM_OCEAN,
                Biome.LUSH_CAVES,
                Biome.DRIPSTONE_CAVES
            ).and(SpawnConditions.notInWater())
        );
    }
}
