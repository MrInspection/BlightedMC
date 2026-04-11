package fr.moussax.blightedMC.content.entities.frenzied;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Objects;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public final class FrenziedDrowned extends FrenziedCreature {

    private static final double PULL_RADIUS = 12.0;
    private static final double PULL_STRENGTH = 0.35;
    private static final long PULL_PERIOD_TICKS = 40L;

    public FrenziedDrowned() {
        super("FRENZIED_DROWNED", "Frenzied Drowned", EntityType.DROWNED);
        setLootTable(new EntityLootTableBuilder()
            .addLoot(Material.ROTTEN_FLESH, 2, 5, 1.0, COMMON)
            .addLoot(Material.COPPER_INGOT, 1, 3, 0.4, UNCOMMON)
            .addLoot(Material.NAUTILUS_SHELL, 1, 1, 0.08, RARE)
            .addLootWithDurabilityRange(Material.TRIDENT, 0.05, 0.80, 0.02, VERY_RARE)
            .addGemsLoot(5, 0.04, VERY_RARE)
            .build()
        );
        setDamage(6);
        setDroppedExp(12);
        itemInMainHand = new ItemStack(Material.TRIDENT);
        addAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY, 2);
    }

    @Override
    protected void onDefineCombatBehavior() {
        addAbility(10L, PULL_PERIOD_TICKS, this::performWaterPull);
    }

    private void performWaterPull() {
        // Pull only works when the Drowned is in water.
        if (!entity.isInWater()) return;

        Player target = getNearestPlayer(PULL_RADIUS);
        if (target == null) return;

        Location drownedLocation = entity.getLocation();
        Vector pullDirection = drownedLocation.toVector()
            .subtract(target.getLocation().toVector())
            .normalize()
            .multiply(PULL_STRENGTH);

        target.setVelocity(target.getVelocity().add(pullDirection));

        entity.getWorld().spawnParticle(
            Particle.BUBBLE_COLUMN_UP,
            target.getLocation(),
            8, 0.3, 0.5, 0.3, 0.05
        );
        entity.getWorld().playSound(
            drownedLocation,
            Sound.ENTITY_ELDER_GUARDIAN_CURSE,
            0.6f, 1.8f
        );
    }

    @Override
    protected void onEnrage(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
        Objects.requireNonNull(entity.getEquipment()).setItemInMainHand(
            new ItemBuilder(Material.TRIDENT).addEnchantment(Enchantment.IMPALING, 2).toItemStack()
        );
    }

    @Override
    protected void onEnrageBehavior() {
        // Pull becomes continuous regardless of water on enrage.
        addAbility(0L, 20L, this::performAggressivePull);
    }

    private void performAggressivePull() {
        Player target = getNearestPlayer(PULL_RADIUS);
        if (target == null) return;

        Location drownedLocation = entity.getLocation();
        Vector pullDirection = drownedLocation.toVector()
            .subtract(target.getLocation().toVector())
            .normalize()
            .multiply(PULL_STRENGTH * 1.5);

        target.setVelocity(target.getVelocity().add(pullDirection));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, false, true));

        entity.getWorld().spawnParticle(
            Particle.BUBBLE_COLUMN_UP,
            target.getLocation(),
            12, 0.4, 0.6, 0.4, 0.08
        );
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(
            SpawnRules.biome(
                    Biome.RIVER, Biome.FROZEN_RIVER,
                    Biome.OCEAN, Biome.COLD_OCEAN, Biome.FROZEN_OCEAN,
                    Biome.LUKEWARM_OCEAN, Biome.WARM_OCEAN, Biome.DEEP_OCEAN,
                    Biome.DEEP_COLD_OCEAN, Biome.DEEP_FROZEN_OCEAN,
                    Biome.DEEP_LUKEWARM_OCEAN, Biome.DRIPSTONE_CAVES
                )
                .and(SpawnRules.maxBlockLight(0))
                .and(SpawnRules.maxLightLevel(7))
                .and(SpawnRules.notInLiquid().negate())
        );
    }
}
