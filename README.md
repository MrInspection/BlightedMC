# BlightedMC

A Spigot 26.2 plugin providing a high-difficulty survival experience inspired by custom RPG mechanics. Vanilla Minecraft is no longer challenging enough. That's why we're bringing you BlightedMC!

## Features

- **Custom Item Engine:** Declarative item definitions with custom stats, abilities, full-set armor bonuses, and restriction rules.
- **Entity & Component System:** Custom mob prototypes with dynamic affixes, visual particle halos, and spawning engines.
- **Loot & Recipe Pipelines:** Weighted and probabilistic drop strategies paired with custom workbench forging.
- **Player Resource State:** Native player mana, gems currency, forge fuel, and dynamic action bar HUD rendering.

## Architecture

- **`bedrock`**: Core framework module handling UI menus, action bars, announcements, scheduling, and utility systems.
- **`blighted-smp`**: Primary SMP plugin module containing custom items, mobs, abilities, recipes, and gameplay mechanics.
- **`blighted-mod`**: Moderation plugin attaching to the SMP server.

## Requirements

- Java 25+
- Spigot 26.2 server build

## Building from Source

Build the multi-module project using Maven:

```bash
mvn clean package
```

The compiled plugin JAR will be located at `blighted-smp/target/blighted-smp-1.0-SNAPSHOT.jar`. Copy the JAR file to your server's `plugins/` directory.
