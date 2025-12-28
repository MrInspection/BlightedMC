package fr.moussax.blightedMC.smp.features.entities.spawnable.blighted;

import fr.moussax.blightedMC.smp.core.entities.BlightedLootBuilder;
import fr.moussax.blightedMC.smp.core.entities.spawnable.condition.SpawnConditionFactory;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightedDrowned extends BlightedCreature {
    public BlightedDrowned() {
        super("BLIGHTED_DROWNED", "Blighted Drowned", EntityType.DROWNED);
        setLootTable(new BlightedLootBuilder()
            .addLoot(Material.ROTTEN_FLESH, 2, 5, 1.0, COMMON)
            .addLoot(Material.COPPER_INGOT, 1, 3, 0.4, UNCOMMON)
            .addLoot(Material.NAUTILUS_SHELL, 1, 1, 0.08, RARE)
            .addLoot(Material.TRIDENT, 1, 1, 0.02, VERY_RARE)
            .addGemsLoot(5, 0.03, VERY_RARE)
            .build()
        );
        setDamage(6);
        setDroppedExp(12);
        itemInMainHand = new ItemStack(Material.AIR);
        addAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY, 1.8);

    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
        Objects.requireNonNull(entity.getEquipment()).setItemInMainHand(
            new ItemBuilder(Material.TRIDENT).addEnchantment(Enchantment.IMPALING, 2).toItemStack()
        );
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
            SpawnConditionFactory.biome(
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
                    Biome.DRIPSTONE_CAVES
                )
                .and(SpawnConditionFactory.maxBlockLight(0))
                .and(SpawnConditionFactory.maxLightLevel(7))
                .and(SpawnConditionFactory.notInLiquid().not())
        );
    }
}
