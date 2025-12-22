package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import fr.moussax.blightedMC.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.core.entities.loot.LootTable;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnConditionFactory;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public final class BlightedHusk extends BlightedCreature {
    public BlightedHusk() {
        super("BLIGHTED_HUSK", "Blighted Husk", EntityType.HUSK);
        setDamage(6);
        setDroppedExp(12);
        setLootTable(createLootTable());
    }

    private LootTable createLootTable() {
        return new LootTable()
            .setMaxDrop(4)
            .addLoot(Material.ROTTEN_FLESH, 2, 5, 1.0, LootDropRarity.COMMON)
            .addLoot(Material.SAND, 1, 3, 0.3, LootDropRarity.UNCOMMON)
            .addLoot(Material.IRON_INGOT, 1, 2, 0.1, LootDropRarity.RARE)
            .addGemsLoot(5, 0.03, LootDropRarity.EXTRAORDINARY);
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
            SpawnConditionFactory.biome(Biome.DESERT)
                .and(SpawnConditionFactory.maxBlockLight(0))
                .and(SpawnConditionFactory.maxLightLevel(7))
                .and(SpawnConditionFactory.skyExposed())
                .and(SpawnConditionFactory.notInLiquid())
        );
    }
}
