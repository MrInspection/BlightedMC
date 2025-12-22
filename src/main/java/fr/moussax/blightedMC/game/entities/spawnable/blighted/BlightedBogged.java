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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public final class BlightedBogged extends BlightedCreature {
    public BlightedBogged() {
        super("BLIGHTED_BOGGED", "Blighted Bogged", EntityType.BOGGED);
        setDamage(6);
        setDroppedExp(12);
        itemInMainHand = new ItemStack(Material.BOW);
        setLootTable(createLootTable());
    }

    private LootTable createLootTable() {
        ItemStack poisonArrow = new ItemBuilder(Material.TIPPED_ARROW)
            .setItemMeta(meta -> {
                if (meta instanceof org.bukkit.inventory.meta.PotionMeta potionMeta) {
                    potionMeta.setBasePotionType(org.bukkit.potion.PotionType.POISON);
                }
            })
            .toItemStack();

        return new LootTable()
            .setMaxDrop(4)
            .addLoot(Material.BONE, 2, 4, 1.0, LootDropRarity.COMMON)
            .addLoot(Material.ARROW, 2, 5, 1.0, LootDropRarity.COMMON)
            .addLoot(poisonArrow, 1, 3, 0.5, LootDropRarity.UNCOMMON)
            .addLoot(Material.BOW, 1, 1, 0.15, LootDropRarity.RARE)
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
