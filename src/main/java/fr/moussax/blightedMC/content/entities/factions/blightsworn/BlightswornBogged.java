package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class BlightswornBogged extends BlightswornArcherArchetype {

    public BlightswornBogged() {
        super("BLIGHTSWORN_BOGGED", "Blightsworn Bogged", EntityType.BOGGED);
        setDamage(6);
        setDroppedExp(12);
        itemInMainHand = new ItemStack(Material.BOW);
        setLootTable(new EntityLootTableBuilder()
                .addLoot(Material.BONE, 2, 4, 1.0, COMMON)
                .addLoot(Material.ARROW, 2, 5, 1.0, COMMON)
                .addLoot(Material.TIPPED_ARROW,
                        builder -> builder.setItemMeta(
                                meta -> ((PotionMeta) meta).setBasePotionType(PotionType.POISON)),
                        1,
                        3,
                        0.4,
                        UNCOMMON
                )
                .addLootWithDurabilityRange(Material.BOW, 0.10, 0.75, 0.15, RARE)
                .addGemsLoot(5, 0.04, VERY_RARE)
                .build());
    }

    @Override
    protected void applyArrowEffects(Arrow arrow, boolean isPhaseTwo) {
        int duration = isPhaseTwo ? 100 : 80;
        int amplifier = isPhaseTwo ? 1 : 0;
        arrow.addCustomEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier), true);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        Location location = entity.getLocation().add(0, 1, 0);
        entity.getWorld().playSound(location, Sound.ENTITY_BOGGED_DEATH, 1.0f, 0.5f);

        entity.getWorld().spawnParticle(Particle.SNEEZE, location, 50, 0.5, 1.0, 0.5, 0.05);
        entity.getWorld().spawnParticle(Particle.DUST, location, 30, 0.5, 1.0, 0.5, 0.0, BLIGHT_DUST);
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnRules.biome(Biome.SWAMP, Biome.MANGROVE_SWAMP).and(SpawnRules.overworldHostile()));
    }
}
