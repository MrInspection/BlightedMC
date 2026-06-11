package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightswornZombie extends BlightswornBruteArchetype {

    public BlightswornZombie() {
        super("BLIGHTSWORN_ZOMBIE", "Blightsworn Zombie", EntityType.ZOMBIE);
        setDamage(6);
        setDroppedExp(12);
        setLootTable(new EntityLootTableBuilder()
                .setMaxDrop(3)
                .addLoot(Material.ROTTEN_FLESH, 1, 2, 1.0, COMMON)
                .addLoot(Material.POTATO, 1, 1, 0.025, UNCOMMON)
                .addLoot(Material.CARROT, 1, 1, 0.025, UNCOMMON)
                .addLoot(Material.IRON_INGOT, 1, 1, 0.02, RARE)
                .addGemsLoot(2, 0.01, VERY_RARE)
                .build()
        );
    }

    @Override
    protected void applySurgeHitEffects(Player player) {
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
                SpawnRules.biome(
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
                        )
                        .and(SpawnRules.overworldHostile())
        );
    }
}
