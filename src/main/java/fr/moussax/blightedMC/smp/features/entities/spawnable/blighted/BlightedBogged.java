package fr.moussax.blightedMC.smp.features.entities.spawnable.blighted;

import fr.moussax.blightedMC.smp.core.entities.EntityLootTableBuilder;
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

public final class BlightedBogged extends BlightedCreature {
    public BlightedBogged() {
        super("BLIGHTED_BOGGED", "Blighted Bogged", EntityType.BOGGED);
        setDamage(6);
        setDroppedExp(12);
        itemInMainHand = new ItemStack(Material.BOW);
        setLootTable(new EntityLootTableBuilder()
            .addLoot(Material.BONE, 2, 4, 1.0, COMMON)
            .addLoot(Material.ARROW, 2, 5, 1.0, COMMON)
            .addLoot(Material.BOW, 1, 1, 0.15, RARE)
            .addGemsLoot(5, 0.03, VERY_RARE)
            .build());
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
        Objects.requireNonNull(entity.getEquipment()).setItemInMainHand(
            new ItemBuilder(Material.BOW).addEnchantment(Enchantment.POWER, 1).toItemStack()
        );
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
            SpawnConditionFactory.biome(
                    Biome.SWAMP,
                    Biome.MANGROVE_SWAMP
                )
                .and(SpawnConditionFactory.maxBlockLight(0))
                .and(SpawnConditionFactory.maxLightLevel(7))
                .and(SpawnConditionFactory.notInLiquid())
        );
    }
}
