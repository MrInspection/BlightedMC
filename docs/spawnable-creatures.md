# Spawnable Entities System

This system allows custom BlightedEntity instances to spawn naturally in the world alongside vanilla mobs, following vanilla-like spawning logic.

## Overview

The spawning system consists of several components:

- **SpawnableEntity**: Abstract class that extends BlightedEntity and includes spawn profile functionality
- **SpawnableEntitiesRegistry**: Registry for managing spawnable entities
- **SpawnableEntitiesListener**: Handles the spawning logic during vanilla mob spawns
- **SpawnCondition**: Interface for defining spawn criteria
- **SpawnEntitiesProfile**: Combines multiple spawn conditions

## How It Works

1. When vanilla mobs spawn naturally (NATURAL, CHUNK_GEN, REINFORCEMENTS), the SpawnableEntitiesListener is triggered
2. The listener checks all registered spawnable entities
3. For each entity, it verifies spawn conditions and chance
4. If conditions are met, the custom entity spawns at the same location

## Creating a Spawnable Entity

### 1. Extend SpawnableEntity

```java
public class MyCustomZombie extends SpawnableEntity {
    
    public MyCustomZombie() {
        super(
            "MY_CUSTOM_ZOMBIE",    // Unique entity ID
            "§cCorrupted Zombie",   // Display name
            40,                     // Max health (20 hearts)
            EntityType.ZOMBIE,      // Vanilla entity type
            0.15                    // 15% spawn chance
        );
    }
    
    @Override
    protected void setupSpawnConditions() {
        // Add spawn conditions here
        addSpawnCondition(new BiomeCondition(Set.of(Biome.FOREST)));
        addSpawnCondition(new TimeCondition(true)); // Night only
        addSpawnCondition(new ChanceCondition(0.5)); // 50% chance
    }
    
    @Override
    protected void applyEquipment() {
        // Customize equipment
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
```

### 2. Register the Entity

Add your entity to the `SpawnableEntitiesRegistry.initializeEntities()` method:

```java
public static void initializeEntities() {
    clearEntities();
    
    addEntity(new MyCustomZombie());
    // Add more entities...
}
```

## Available Spawn Conditions

### BiomeCondition
Spawns only in specific biomes:
```java
addSpawnCondition(new BiomeCondition(Set.of(
    Biome.FOREST,
    Biome.DARK_FOREST,
    Biome.BIRCH_FOREST
)));
```

### TimeCondition
Spawns only at night or day:
```java
addSpawnCondition(new TimeCondition(true));  // Night only
addSpawnCondition(new TimeCondition(false)); // Day only
```

### WeatherCondition
Spawns only during specific weather:
```java
addSpawnCondition(new WeatherCondition(WeatherCondition.WeatherType.CLEAR));
addSpawnCondition(new WeatherCondition(WeatherCondition.WeatherType.RAIN));
addSpawnCondition(new WeatherCondition(WeatherCondition.WeatherType.THUNDERSTORM));
```

### YLevelCondition
Spawns only within specific Y-level ranges:
```java
addSpawnCondition(new YLevelCondition(50, 80)); // Y levels 50-80
```

### ChanceCondition
Additional random chance factor:
```java
addSpawnCondition(new ChanceCondition(0.3)); // 30% chance
```

## Spawn Logic

The system follows these steps:

1. **Vanilla Mob Spawn**: A vanilla mob spawns naturally
2. **Event Trigger**: CreatureSpawnEvent is fired
3. **Condition Check**: For each registered spawnable entity:
   - Check if all spawn conditions are met
   - Check if spawn chance roll succeeds
4. **Entity Spawn**: If all checks pass, spawn the custom entity

## Important Notes

- Custom entities spawn **alongside** vanilla mobs, not instead of them
- The system only handles NATURAL, CHUNK_GEN, and REINFORCEMENTS spawn reasons
- Spawn conditions are AND-based (all must be true)
- Spawn chance is applied after condition checks
- The system is designed to be lightweight and not interfere with vanilla spawning

## Example Entities

See the `impl` package for example implementations:
- `ExampleSpawnableZombie`: Forest zombie that spawns at night
- `ExampleSpawnableSkeleton`: Desert archer that spawns during the day

## Troubleshooting

- **Entity not spawning**: Check spawn conditions and chance values
- **Performance issues**: Reduce the number of registered entities or simplify conditions
- **Conflicts**: Ensure entity IDs are unique across the registry 