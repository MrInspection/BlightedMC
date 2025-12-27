package fr.moussax.blightedMC.smp.features.entities.spawnable.blighted;

import fr.moussax.blightedMC.smp.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.smp.core.entities.loot.LootTable;
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

public final class BlightedParched extends BlightedCreature {
    public BlightedParched() {
        super("BLIGHTED_PARCHED", "Blighted Parched", EntityType.PARCHED);
        itemInMainHand = new ItemStack(Material.BOW);
        setDamage(6);
        setDroppedExp(12);
        setLootTable(createLootTable());
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
        Objects.requireNonNull(entity.getEquipment()).setItemInMainHand(
            new ItemBuilder(Material.BOW).addEnchantment(Enchantment.POWER, 1).toItemStack()
        );
    }

    private LootTable createLootTable() {
        return new LootTable()
            .setMaxDrop(4)
            .addLoot(Material.BONE, 2, 5, 1.0, LootDropRarity.COMMON)
            .addLoot(Material.ARROW, 2, 5, 1.0, LootDropRarity.COMMON)
            .addGemsLoot(5, 0.03, LootDropRarity.VERY_RARE);
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
            SpawnConditionFactory.biome(Biome.DESERT)
                .and(SpawnConditionFactory.maxBlockLight(0))
                .and(SpawnConditionFactory.maxLightLevel(7))
                .and(SpawnConditionFactory.skyExposed())
                .and(SpawnConditionFactory.notInLiquid())
        );
    }
}
