package fr.moussax.blightedMC.content.entities.ravenous;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Objects;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class RavenousStray extends RavenousCreature {
    public RavenousStray() {
        super("RAVENOUS_STRAY", "Ravenous Stray", EntityType.STRAY);
        itemInMainHand = new ItemStack(Material.BOW);
        setDamage(6);
        setDroppedExp(12);
        setLootTable(new EntityLootTableBuilder()
            .setMaxDrop(4)
            .addLoot(Material.BONE, 2, 5, 1.0, COMMON)
            .addLoot(Material.ARROW, 2, 5, 1.0, COMMON)
            .addLoot(
                Material.TIPPED_ARROW, b -> b.setItemMeta(
                    meta -> ((PotionMeta) meta).setBasePotionType(PotionType.SLOWNESS)
                ),
                1,
                3,
                0.4,
                UNCOMMON
            )
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
            SpawnRules.biome(
                    Biome.SNOWY_PLAINS,
                    Biome.ICE_SPIKES,
                    Biome.FROZEN_OCEAN,
                    Biome.DEEP_FROZEN_OCEAN,
                    Biome.FROZEN_RIVER,
                    Biome.SNOWY_SLOPES,
                    Biome.JAGGED_PEAKS,
                    Biome.FROZEN_PEAKS
                )
                .and(SpawnRules.maxBlockLight(0))
                .and(SpawnRules.maxLightLevel(7))
                .and(SpawnRules.skyExposed())
                .and(SpawnRules.notInLiquid())
        );
    }
}
