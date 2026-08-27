# BlightedMC

A Spigot 26.2 plugin providing a high-difficulty survival experience inspired by custom RPG mechanics. Vanilla Minecraft is no longer challenging enough. That's why we're bringing you BlightedMC!

## Features

- **Custom Item Engine:** Declarative item definitions with custom stats, abilities, full-set armor bonuses, and restriction rules.
- **Entity & Component System:** Custom mob prototypes with dynamic affixes, visual particle halos, and spawning engines.
- **Loot & Recipe Pipelines:** Weighted and probabilistic drop strategies paired with custom workbench forging.
- **Player Resource State:** Native player mana, gems currency, forge fuel, and dynamic action bar HUD rendering.

## Requirements

- Java 25+
- Spigot 26.2 server build

## Building from Source

Build the plugin JAR using Maven:

```bash
mvn clean package
```

The compiled output will be located at `target/blightedmc-1.0-SNAPSHOT.jar`. Copy the JAR file to your server's `plugins/` directory.
