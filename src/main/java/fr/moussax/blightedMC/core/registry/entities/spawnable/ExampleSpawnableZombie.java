package fr.moussax.blightedMC.core.registry.entities.spawnable;

import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntity;
import fr.moussax.blightedMC.core.entities.spawning.condition.BiomeCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.ChanceCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.TimeCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.WeatherCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.YLevelCondition;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Example spawnable zombie entity that demonstrates how to use the spawning system.
 * This zombie spawns in forests at night with a 15% chance.
 */
public class ExampleSpawnableZombie extends SpawnableEntity {

  public ExampleSpawnableZombie() {
    super(
      "EXAMPLE_ZOMBIE",           // Entity ID
      "§cCorrupted Zombie",       // Display name
      40,                         // Max health (20 hearts)
      EntityType.ZOMBIE,          // Vanilla entity type
      0.15                        // 15% spawn chance
    );
  }

  @Override
  protected void setupSpawnConditions() {
    // Only spawn in forest biomes
    addSpawnCondition(new BiomeCondition(Set.of(
      Biome.FOREST,
      Biome.BIRCH_FOREST,
      Biome.DARK_FOREST,
      Biome.FLOWER_FOREST
    )));

    // Only spawn at night
    addSpawnCondition(new TimeCondition(true));

    // Only spawn during clear weather
    addSpawnCondition(new WeatherCondition(WeatherCondition.WeatherType.CLEAR));

    // Only spawn between Y levels 50-80
    addSpawnCondition(new YLevelCondition(50, 80));

    // Additional random chance (50%)
    addSpawnCondition(new ChanceCondition(0.5));
  }

  @Override
  protected void applyEquipment() {
    // Give the zombie some custom equipment
    this.itemInMainHand = new ItemStack(Material.IRON_SWORD);
    this.armor = new ItemStack[]{
      new ItemStack(Material.IRON_HELMET),
      new ItemStack(Material.IRON_CHESTPLATE),
      new ItemStack(Material.IRON_LEGGINGS),
      new ItemStack(Material.IRON_BOOTS)
    };
    
    super.applyEquipment();
  }
} 