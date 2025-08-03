package fr.moussax.blightedMC.core.registry.entities.spawnable;

import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntity;
import fr.moussax.blightedMC.core.entities.spawning.condition.BiomeCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.ChanceCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.TimeCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.YLevelCondition;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Example spawnable skeleton entity that demonstrates different spawning conditions.
 * This skeleton spawns in deserts and badlands during the day with a 10% chance.
 */
public class ExampleSpawnableSkeleton extends SpawnableEntity {

  public ExampleSpawnableSkeleton() {
    super(
      "EXAMPLE_SKELETON",         // Entity ID
      "§7Desert Archer",          // Display name
      30,                         // Max health (15 hearts)
      EntityType.SKELETON,        // Vanilla entity type
      0.10                        // 10% spawn chance
    );
  }

  @Override
  protected void setupSpawnConditions() {
    // Only spawn in desert and badlands biomes
    addSpawnCondition(new BiomeCondition(Set.of(
      Biome.DESERT,
      Biome.BADLANDS,
      Biome.ERODED_BADLANDS,
      Biome.WOODED_BADLANDS
    )));

    // Only spawn during the day (not at night)
    addSpawnCondition(new TimeCondition(false));

    // Only spawn between Y levels 60-100
    addSpawnCondition(new YLevelCondition(60, 100));

    // Additional random chance (30%)
    addSpawnCondition(new ChanceCondition(0.3));
  }

  @Override
  protected void applyEquipment() {
    // Give the skeleton a bow and some armor
    this.itemInMainHand = new ItemStack(Material.BOW);
    this.armor = new ItemStack[]{
      new ItemStack(Material.LEATHER_HELMET),
      new ItemStack(Material.LEATHER_CHESTPLATE),
      new ItemStack(Material.LEATHER_LEGGINGS),
      new ItemStack(Material.LEATHER_BOOTS)
    };
    
    super.applyEquipment();
  }
} 