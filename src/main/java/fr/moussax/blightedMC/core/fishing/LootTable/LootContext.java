package fr.moussax.blightedMC.core.fishing.LootTable;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.World;
import org.bukkit.block.Biome;

public record LootContext(BlightedPlayer player, Biome biome, World.Environment environment) {
}
