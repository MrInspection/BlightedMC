package fr.moussax.blightedMC.shared.loot;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Contextual state for a single loot roll and drop execution.
 *
 * @param blightedPlayer player triggering the roll, or {@code null} if un-owned
 * @param world          world where loot occurs
 * @param biome          biome at loot origin
 * @param origin         location where loot is spawned
 * @param random         random generator for this roll
 * @param velocity       velocity applied to dropped items, or {@code null}
 */
public record LootContext(
        BlightedPlayer blightedPlayer,
        World world,
        Biome biome,
        Location origin,
        ThreadLocalRandom random,
        Vector velocity
) {
}
