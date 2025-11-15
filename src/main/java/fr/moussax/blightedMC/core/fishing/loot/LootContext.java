package fr.moussax.blightedMC.core.fishing.loot;

import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.World;
import org.bukkit.block.Biome;

/**
 * Context for evaluating loot conditions and generating loot.
 * <p>
 * Contains information about the player, the biome, and the world environment
 * at the time of the loot roll.
 *
 * @param player the player triggering the loot
 * @param biome the biome where the loot is generated
 * @param environment the world environment where the loot is generated
 */
public record LootContext(BlightedPlayer player, Biome biome, World.Environment environment) { }
