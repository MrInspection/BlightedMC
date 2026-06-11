package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightswornStray extends BlightswornArcherArchetype {
    public BlightswornStray() {
        super("BLIGHTSWORN_STRAY", "Blightsworn Stray", EntityType.STRAY);
        itemInMainHand = new ItemStack(Material.BOW);
        setDamage(6);
        setDroppedExp(12);
        setLootTable(new EntityLootTableBuilder()
                .setMaxDrop(4)
                .addLoot(Material.BONE, 2, 5, 1.0, COMMON)
                .addLoot(Material.ARROW, 2, 5, 1.0, COMMON)
                .addLoot(Material.TIPPED_ARROW, builder -> builder.setItemMeta(
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
    protected void applyArrowEffects(Arrow arrow, boolean isPhaseTwo) {

    }

    @Override
    protected void onEnrage(LivingEntity entity) {

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
                        .and(SpawnRules.overworldSurfaceHostile())
        );
    }
}
