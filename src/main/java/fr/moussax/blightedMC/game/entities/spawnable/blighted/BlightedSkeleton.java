package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import fr.moussax.blightedMC.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.core.entities.loot.LootTable;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnConditionFactory;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public final class BlightedSkeleton extends BlightedCreature {
    public BlightedSkeleton() {
        super("BLIGHTED_SKELETON", "Blighted Skeleton", EntityType.SKELETON);
        itemInMainHand = new ItemStack(Material.BOW);
        setDamage(6);
        setDroppedExp(12);
        setLootTable(createLootTable());
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
        Objects.requireNonNull(entity.getEquipment()).setItemInMainHand(
            new ItemBuilder(Material.BOW).addEnchantment(Enchantment.POWER, 1).toItemStack()
        );
    }

    private LootTable createLootTable() {
        return new LootTable()
            .setMaxDrop(4)
            .addLoot(Material.BONE, 2, 5, 1.0, LootDropRarity.COMMON)
            .addLoot(Material.ARROW, 2, 5, 1.0, LootDropRarity.COMMON)
            .addLoot(Material.BOW, 1, 1, 0.15, LootDropRarity.RARE)
            .addGemsLoot(5, 0.03, LootDropRarity.EXTRAORDINARY);
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
            SpawnConditionFactory.biome(
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
            .and(SpawnConditionFactory.maxBlockLight(0))
            .and(SpawnConditionFactory.maxLightLevel(7))
            .and(SpawnConditionFactory.notInLiquid())
        );
    }
}
