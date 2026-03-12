package fr.moussax.blightedMC.content.entities.ravenous;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
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

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class RavenousZombifiedPiglin extends RavenousCreature {
    public RavenousZombifiedPiglin() {
        super("RAVENOUS_ZOMBIFIED_PIGLIN", "Ravenous Zombified Piglin", EntityType.ZOMBIFIED_PIGLIN);
        itemInMainHand = new ItemStack(Material.GOLDEN_SWORD);
        setDamage(8);
        setDroppedExp(16);
        setLootTable(new EntityLootTableBuilder()
            .setMaxDrop(3)
            .addLoot(Material.ROTTEN_FLESH, 2, 6, 1.0, COMMON)
            .addLoot(Material.GOLD_NUGGET, 2, 6, 1.0, COMMON)
            .addLoot(Material.GOLD_INGOT, 1, 2, 0.15, RARE)
            .addGemsLoot(5, 0.04, VERY_RARE)
            .build()
        );
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
            SpawnRules.biome(Biome.NETHER_WASTES, Biome.CRIMSON_FOREST)
                .or(SpawnRules.insideStructure(Structure.FORTRESS))
                .and(SpawnRules.maxBlockLight(11))
                .and(SpawnRules.notInLiquid())
        );
    }
}
