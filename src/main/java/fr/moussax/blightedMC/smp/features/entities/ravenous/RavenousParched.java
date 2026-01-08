package fr.moussax.blightedMC.smp.features.entities.ravenous;

import fr.moussax.blightedMC.smp.core.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.smp.core.entities.spawnable.condition.SpawnRules;
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

public final class RavenousParched extends RavenousCreature {
    public RavenousParched() {
        super("RAVENOUS_PARCHED", "Ravenous Parched", EntityType.PARCHED);
        itemInMainHand = new ItemStack(Material.BOW);
        setDamage(6);
        setDroppedExp(12);
        setLootTable(new EntityLootTableBuilder()
            .addLoot(Material.BONE, 2, 5, 1.0, COMMON)
            .addLoot(Material.ARROW, 2, 5, 1.0, COMMON)
            .addGemsLoot(5, 0.04, VERY_RARE)
            .build()
        );
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
        Objects.requireNonNull(entity.getEquipment()).setItemInMainHand(
            new ItemBuilder(Material.BOW).addEnchantment(Enchantment.POWER, 2).toItemStack()
        );
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
