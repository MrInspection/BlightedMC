package fr.moussax.blightedMC.smp.features.entities.spawnable.blighted;

import fr.moussax.blightedMC.smp.core.entities.BlightedLootBuilder;
import fr.moussax.blightedMC.smp.core.entities.spawnable.condition.SpawnConditionFactory;
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

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightedWitherSkeleton extends BlightedCreature {
    public BlightedWitherSkeleton() {
        super("BLIGHTED_WITHER_SKELETON", "Blighted Wither Skeleton", EntityType.WITHER_SKELETON);
        setLootTable(new BlightedLootBuilder()
            .setMaxDrop(4)
            .addLoot(Material.BONE, 2, 5, 1.0, COMMON)
            .addLoot(Material.COAL, 1, 3, 0.5, UNCOMMON)
            .addLoot(Material.WITHER_SKELETON_SKULL, 1, 1, 0.03, VERY_RARE)
            .addGemsLoot(5, 0.03, VERY_RARE)
            .build()
        );

        setDamage(8);
        setDroppedExp(20);
        itemInMainHand = new ItemStack(Material.STONE_SWORD);
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
