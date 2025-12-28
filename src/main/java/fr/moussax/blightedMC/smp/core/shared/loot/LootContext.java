package fr.moussax.blightedMC.smp.core.shared.loot;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Context for loot selection and execution.
 *
 * <p>Encapsulates the player, world, biome, loot origin, and random generator
 * for a single loot roll. Supports both player-driven and non-player-driven loot.</p>
 *
 * @param player the player responsible for the loot, or {@code null}
 * @param world the world where the loot occurs
 * @param biome the biome at the loot origin
 * @param origin the location where loot is dropped or spawned
 * @param random RNG for the current loot roll
 */
public record LootContext(BlightedPlayer player, World world, Biome biome, Location origin,
                          ThreadLocalRandom random) {
}
