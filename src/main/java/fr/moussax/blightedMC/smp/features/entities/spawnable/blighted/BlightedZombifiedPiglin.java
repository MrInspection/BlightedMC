package fr.moussax.blightedMC.smp.features.entities.spawnable.blighted;

import fr.moussax.blightedMC.smp.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.smp.core.entities.loot.LootTable;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnConditionFactory;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public final class BlightedZombifiedPiglin extends BlightedCreature {
    public BlightedZombifiedPiglin() {
        super("BLIGHTED_ZOMBIFIED_PIGLIN", "Blighted Zombified Piglin", EntityType.ZOMBIFIED_PIGLIN);
        itemInMainHand = new ItemStack(Material.GOLDEN_SWORD);
        setDamage(8);
        setDroppedExp(16);
        setLootTable(createLootTable());
    }

    private LootTable createLootTable() {
        return new LootTable()
            .setMaxDrop(4)
            .addLoot(Material.ROTTEN_FLESH, 2, 6, 1.0, LootDropRarity.COMMON)
            .addLoot(Material.GOLD_NUGGET, 2, 6, 1.0, LootDropRarity.COMMON)
            .addLoot(Material.GOLD_INGOT, 1, 2, 0.15, LootDropRarity.RARE)
            .addGemsLoot(5, 0.03, LootDropRarity.VERY_RARE);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 0));
        Objects.requireNonNull(entity.getEquipment()).setItemInMainHand(
            new ItemBuilder(Material.GOLDEN_SWORD).addEnchantment(Enchantment.FIRE_ASPECT, 1).toItemStack()
        );
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
            SpawnConditionFactory.biome(Biome.NETHER_WASTES, Biome.CRIMSON_FOREST)
                .or(SpawnConditionFactory.insideStructure(Structure.FORTRESS))
                .and(SpawnConditionFactory.maxBlockLight(11))
                .and(SpawnConditionFactory.notInLiquid())
        );
    }
}
