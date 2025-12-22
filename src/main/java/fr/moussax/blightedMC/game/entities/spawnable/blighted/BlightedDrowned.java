package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import fr.moussax.blightedMC.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.core.entities.loot.LootTable;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnConditionFactory;
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

public final class BlightedDrowned extends BlightedCreature {
    public BlightedDrowned() {
        super("BLIGHTED_DROWNED", "Blighted Drowned", EntityType.DROWNED);
        setLootTable(createLootTable());
        setDamage(6);
        setDroppedExp(12);
        itemInMainHand = new ItemStack(Material.AIR);
        addAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY, 1.8);

    }

    private LootTable createLootTable() {
        return new LootTable()
            .setMaxDrop(4)
            .addLoot(Material.ROTTEN_FLESH, 2, 5, 1.0, LootDropRarity.COMMON)
            .addLoot(Material.COPPER_INGOT, 1, 3, 0.4, LootDropRarity.UNCOMMON)
            .addLoot(Material.NAUTILUS_SHELL, 1, 1, 0.08, LootDropRarity.RARE)
            .addLoot(Material.TRIDENT, 1, 1, 0.02, LootDropRarity.EXTRAORDINARY)
            .addGemsLoot(5, 0.03, LootDropRarity.EXTRAORDINARY);
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
