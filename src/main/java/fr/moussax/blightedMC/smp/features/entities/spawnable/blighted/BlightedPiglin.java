package fr.moussax.blightedMC.smp.features.entities.spawnable.blighted;

import fr.moussax.blightedMC.smp.core.entities.BlightedLootBuilder;
import fr.moussax.blightedMC.smp.core.entities.spawnable.condition.SpawnConditionFactory;
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

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightedPiglin extends BlightedCreature {
    public BlightedPiglin() {
        super("BLIGHTED_PIGLIN", "Blighted Piglin", EntityType.PIGLIN);
        itemInMainHand = new ItemStack(Material.GOLDEN_SWORD);
        setLootTable(new BlightedLootBuilder()
            .addLoot(Material.GOLD_NUGGET, 2, 6, 1.0, COMMON)
            .addLoot(Material.GOLD_INGOT, 1, 3, 0.4, UNCOMMON)
            .addLoot(Material.CROSSBOW, 1, 1, 0.1, RARE)
            .addGemsLoot(5, 0.03, VERY_RARE)
            .build()
        );
        setDamage(8);
        setDroppedExp(16);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        Objects.requireNonNull(entity.getEquipment()).setItemInMainHand(
            new ItemBuilder(Material.GOLDEN_SWORD).addEnchantment(Enchantment.FIRE_ASPECT, 1).toItemStack()
        );
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
            SpawnConditionFactory
                .biome(Biome.NETHER_WASTES, Biome.CRIMSON_FOREST)
                .and(SpawnConditionFactory.maxBlockLight(11))
                .and(SpawnConditionFactory.notInLiquid())
        );
    }
}
