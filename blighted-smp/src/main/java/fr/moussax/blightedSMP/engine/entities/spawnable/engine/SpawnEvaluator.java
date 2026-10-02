package fr.moussax.blightedSMP.engine.entities.spawnable.engine;

import fr.moussax.blightedSMP.engine.entities.spawnable.SpawnableEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility for evaluating spawn conditions and selecting spawnable entity candidates weighted by probability.
 */
public final class SpawnEvaluator {

    private SpawnEvaluator() {
    }

    /**
     * Evaluates spawn conditions for candidate entities and selects an eligible entity weighted by probability.
     *
     * @param candidates candidate spawnable entity prototypes
     * @param location   target spawn location
     * @param world      target world
     * @param random     random number generator
     * @return selected entity prototype, or {@code null} if no spawn condition was met or probability roll failed
     */
    @Nullable
    public static SpawnableEntity selectCandidate(List<SpawnableEntity> candidates, Location location, World world, ThreadLocalRandom random) {
        if (candidates == null || candidates.isEmpty() || location == null || world == null) {
            return null;
        }

        List<SpawnableEntity> eligible = null;
        for (SpawnableEntity entity : candidates) {
            if (!entity.canSpawnAt(location, world)) continue;
            if (eligible == null) eligible = new ArrayList<>(candidates.size());
            eligible.add(entity);
        }

        if (eligible == null) return null;

        double totalChance = 0.0;
        for (SpawnableEntity entity : eligible) {
            totalChance += entity.getSpawnProbability();
        }

        if (random.nextDouble() >= Math.min(totalChance, 1.0)) return null;

        double selectionRoll = random.nextDouble() * totalChance;
        double cumulative = 0.0;
        for (SpawnableEntity entity : eligible) {
            cumulative += entity.getSpawnProbability();
            if (selectionRoll < cumulative) {
                return entity;
            }
        }

        return eligible.getLast();
    }
}
