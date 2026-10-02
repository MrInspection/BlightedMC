package fr.moussax.blightedSMP.engine.entities.spawnable;

import fr.moussax.blightedSMP.engine.entities.spawnable.condition.SpawnCondition;
import fr.moussax.blightedSMP.engine.entities.spawnable.condition.SpawnRules;
import fr.moussax.blightedSMP.engine.entities.spawnable.engine.SpawnMode;
import lombok.Getter;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.generator.structure.Structure;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fluent builder for configuring mob natural spawn rules, probability, and mode.
 */
@Getter
public final class SpawningBuilder {

    public static final double DEFAULT_PROBABILITY = 0.01;
    public static final SpawnMode DEFAULT_MODE = SpawnMode.REPLACEMENT;

    private double probability = DEFAULT_PROBABILITY;
    private SpawnMode mode = DEFAULT_MODE;
    private double affixChance = 0.0;
    private int maxAffixes = 1;
    private final List<SpawnCondition> conditions = new ArrayList<>();

    /**
     * Sets the spawn probability in range [0.0, 1.0].
     *
     * @param probability spawn probability
     * @return this builder
     */
    public SpawningBuilder probability(double probability) {
        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("probability must be in [0.0, 1.0], got: " + probability);
        }
        this.probability = probability;
        return this;
    }

    /**
     * Sets the integration mode with Minecraft spawning mechanisms.
     *
     * @param mode spawn mode
     * @return this builder
     */
    public SpawningBuilder mode(@NonNull SpawnMode mode) {
        this.mode = Objects.requireNonNull(mode, "mode cannot be null");
        return this;
    }

    /**
     * Sets elite affix roll chance and maximum rollable affixes.
     *
     * @param chance     probability in [0.0, 1.0]
     * @param maxAffixes maximum affixes
     * @return this builder
     */
    public SpawningBuilder affixes(double chance, int maxAffixes) {
        if (chance < 0.0 || chance > 1.0) {
            throw new IllegalArgumentException("affixChance must be in [0.0, 1.0], got: " + chance);
        }
        this.affixChance = chance;
        this.maxAffixes = Math.max(1, maxAffixes);
        return this;
    }

    /**
     * Sets elite affix roll chance with up to 1 rolled affix.
     *
     * @param chance probability in [0.0, 1.0]
     * @return this builder
     */
    public SpawningBuilder affixes(double chance) {
        return affixes(chance, 1);
    }

    /**
     * Adds one or more spawn conditions evaluated with AND semantics.
     *
     * @param conditions conditions to enforce
     * @return this builder
     */
    public SpawningBuilder condition(@NonNull SpawnCondition... conditions) {
        for (SpawnCondition condition : conditions) {
            this.conditions.add(Objects.requireNonNull(condition, "condition cannot be null"));
        }
        return this;
    }

    /**
     * Convenience method adding a biome restriction rule.
     *
     * @param biomes allowed biomes
     * @return this builder
     */
    public SpawningBuilder biomes(@NonNull Biome... biomes) {
        return condition(SpawnRules.biome(biomes));
    }

    /**
     * Convenience method requiring the entity to spawn inside a structure.
     *
     * @param structure target structure
     * @return this builder
     */
    public SpawningBuilder insideStructure(@NonNull Structure structure) {
        return condition(SpawnRules.insideStructure(structure));
    }

    /**
     * Adds an OR condition group where at least one condition must pass.
     *
     * @param conditions conditions to evaluate
     * @return this builder
     */
    public SpawningBuilder anyOf(@NonNull SpawnCondition... conditions) {
        return condition(SpawnCondition.anyOf(conditions));
    }

    /**
     * Adds an AND condition group where all conditions must pass.
     *
     * @param conditions conditions to evaluate
     * @return this builder
     */
    public SpawningBuilder allOf(@NonNull SpawnCondition... conditions) {
        return condition(SpawnCondition.allOf(conditions));
    }

    /**
     * Adds a NOR condition group where none of the conditions may pass.
     *
     * @param conditions conditions that must not pass
     * @return this builder
     */
    public SpawningBuilder noneOf(@NonNull SpawnCondition... conditions) {
        return condition(SpawnCondition.noneOf(conditions));
    }

    /**
     * Adds an environment requirement.
     *
     * @param environment required world environment
     * @return this builder
     */
    public SpawningBuilder environment(@NonNull Environment environment) {
        return condition(SpawnRules.environment(environment));
    }

    /**
     * Adds standard Overworld hostile monster conditions (darkness, non-liquid).
     *
     * @return this builder
     */
    public SpawningBuilder overworldHostile() {
        return condition(SpawnRules.overworldHostile());
    }

    /**
     * Adds surface-only Overworld hostile monster conditions (sky exposed, darkness, non-liquid).
     *
     * @return this builder
     */
    public SpawningBuilder overworldSurfaceHostile() {
        return condition(SpawnRules.overworldSurfaceHostile());
    }

    /**
     * Adds Nether hostile monster conditions (non-liquid, light level <= 11).
     *
     * @return this builder
     */
    public SpawningBuilder netherHostile() {
        return condition(SpawnRules.netherHostile());
    }

    /**
     * Adds a liquid requirement (water or lava).
     *
     * @return this builder
     */
    public SpawningBuilder inLiquid() {
        return condition(SpawnRules.inLiquid());
    }

    /**
     * Adds a non-liquid requirement.
     *
     * @return this builder
     */
    public SpawningBuilder notInLiquid() {
        return condition(SpawnRules.notInLiquid());
    }

    /**
     * Adds a requirement that the spawn location is exposed to the sky.
     *
     * @return this builder
     */
    public SpawningBuilder skyExposed() {
        return condition(SpawnRules.skyExposed());
    }

    /**
     * Adds a maximum block light requirement.
     *
     * @param max maximum block light level
     * @return this builder
     */
    public SpawningBuilder maxBlockLight(int max) {
        return condition(SpawnRules.maxBlockLight(max));
    }

    /**
     * Adds a maximum total light level requirement.
     *
     * @param max maximum total light level
     * @return this builder
     */
    public SpawningBuilder maxLightLevel(int max) {
        return condition(SpawnRules.maxLightLevel(max));
    }

    /**
     * Builds an immutable {@link SpawnProfile} with the configured conditions.
     *
     * @return immutable spawn profile
     */
    @NonNull
    public SpawnProfile buildProfile() {
        return new SpawnProfile(this.conditions);
    }
}
