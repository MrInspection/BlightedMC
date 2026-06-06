package fr.moussax.blightedMC.content.entities.frenzied;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class FrenziedPiglin extends FrenziedAmbusher {
    public FrenziedPiglin() {
        super("FRENZIED_PIGLIN", "Frenzied Piglin", EntityType.PIGLIN);
        itemInMainHand = new ItemStack(Material.GOLDEN_SWORD);
        setLootTable(new EntityLootTableBuilder()
            .addLoot(Material.GOLD_NUGGET, 2, 6, 1.0, COMMON)
            .addLoot(Material.GOLD_INGOT, 1, 3, 0.4, UNCOMMON)
            .addLootWithDurabilityRange(Material.CROSSBOW, 0.10, 0.80, 0.1, RARE)
            .addGemsLoot(5, 0.04, VERY_RARE)
            .build()
        );
        setDamage(8);
        setDroppedExp(16);
    }

    @Override
    public LivingEntity spawn(Location location) {
        LivingEntity spawnedEntity = super.spawn(location);
        ((Piglin) spawnedEntity).setImmuneToZombification(true);
        return spawnedEntity;
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
            SpawnRules
                .biome(Biome.NETHER_WASTES, Biome.CRIMSON_FOREST)
                .and(SpawnRules.maxBlockLight(11))
                .and(SpawnRules.notInLiquid())
        );
    }
}
