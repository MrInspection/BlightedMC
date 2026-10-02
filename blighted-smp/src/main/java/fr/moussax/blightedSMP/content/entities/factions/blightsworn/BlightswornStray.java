package fr.moussax.blightedSMP.content.entities.factions.blightsworn;

import fr.moussax.blightedSMP.engine.entities.spawnable.condition.SpawnRules;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import static fr.moussax.blightedSMP.engine.loot.decorators.EntityLootRarity.*;

public final class BlightswornStray extends BlightswornArcherArchetype {
    public BlightswornStray() {
        super("BLIGHTSWORN_STRAY", "Blightsworn Stray", EntityType.STRAY);
        setItemInMainHand(new ItemStack(Material.BOW));
        setDamage(6);
        setDroppedExp(12);
        loot(table -> table
                .maxDrops(4)
                .addLoot(Material.BONE, 2, 5, 1.0)
                .addLoot(Material.ARROW, 2, 5, 1.0)
                .addLoot(Material.TIPPED_ARROW, builder -> builder.setItemMeta(
                                meta -> ((PotionMeta) meta).setBasePotionType(PotionType.SLOWNESS)
                        ),
                        1,
                        3,
                        0.4
                )
                .addGems(5, 0.04, VERY_RARE)
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
