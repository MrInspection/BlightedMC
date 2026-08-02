package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightswornHusk extends BlightswornBruteArchetype {

    public BlightswornHusk() {
        super("BLIGHTSWORN_HUSK", "Blightsworn Husk", EntityType.HUSK);
        setDamage(6);
        setDroppedExp(12);
        setLootTable(new EntityLootTableBuilder()
                .addLoot(Material.ROTTEN_FLESH, 2, 5, 1.0, COMMON)
                .addLoot(Material.SAND, 1, 3, 0.3, UNCOMMON)
                .addLoot(Material.IRON_INGOT, 1, 2, 0.1, RARE)
                .addGemsLoot(5, 0.04, VERY_RARE)
                .build()
        );
    }

    @Override
    protected void applySurgeHitEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 80, 1));
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnRules.biome(Biome.DESERT).and(SpawnRules.overworldSurfaceHostile()));
    }
}
