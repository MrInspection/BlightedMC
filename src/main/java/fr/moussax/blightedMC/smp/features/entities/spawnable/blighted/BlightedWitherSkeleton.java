package fr.moussax.blightedMC.smp.features.entities.spawnable.blighted;

import fr.moussax.blightedMC.smp.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.smp.core.entities.loot.LootTable;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnConditionFactory;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public final class BlightedWitherSkeleton extends BlightedCreature {
    public BlightedWitherSkeleton() {
        super("BLIGHTED_WITHER_SKELETON", "Blighted Wither Skeleton", EntityType.WITHER_SKELETON);
        setLootTable(createLootTable());
        setDamage(8);
        setDroppedExp(20);
        itemInMainHand = new ItemStack(Material.STONE_SWORD);
    }

    private LootTable createLootTable() {
        return new LootTable()
            .setMaxDrop(4)
            .addLoot(Material.BONE, 2, 5, 1.0, LootDropRarity.COMMON)
            .addLoot(Material.COAL, 1, 3, 0.5, LootDropRarity.UNCOMMON)
            .addLoot(Material.WITHER_SKELETON_SKULL, 1, 1, 0.03, LootDropRarity.EXTRAORDINARY)
            .addGemsLoot(5, 0.03, LootDropRarity.EXTRAORDINARY);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        Objects.requireNonNull(entity.getEquipment()).setItemInMainHand(
            new ItemBuilder(Material.STONE_SWORD).addEnchantment(Enchantment.FIRE_ASPECT, 1).toItemStack()
        );
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
            SpawnConditionFactory.insideStructure(Structure.FORTRESS)
                .and(SpawnConditionFactory.maxBlockLight(0))
        );
    }
}
