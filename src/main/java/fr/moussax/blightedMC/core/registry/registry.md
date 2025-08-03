# Registry Directory

The `__registry__` directory is the **centralized registry system** for all dynamically managed components of the BlightedMC plugin.  
Its purpose is to **register, initialize, and organize** all custom plugin elements in a structured and maintainable way.

```
core/
 └─ __registry__/
     ├─ items/
     │   ├─ MaterialsRegistry.java
     │   └─ ...
     ├─ blocks/
     │   └─ ...
     ├─ entities/
     │   └─ ...
     └─ recipes/
         └─ ...
```

---

### 1. `items/`
Contains all item registries.  
Each registry class is responsible for **creating and registering all related items** to `ItemsRegistry` during plugin initialization.

### 2. `blocks/`
Handles custom placeable blocks, including their **registration and initialization**.  
Intended for use with the `BlockListener` and other block-related systems.

### 3. `entities/`
Reserved for **custom entity registration**, including potential mobs, NPCs, or other interactive creatures.

### 4. `recipes/`
Contains **custom recipe registries** to register crafting, smelting, and other recipes.

---

## Guidelines

1. **One registry per category**  
   Example: `MaterialsRegistry` for all material-type items.  
   If the plugin grows, multiple registries per category are acceptable (e.g., `WeaponsRegistry`, `ArmorRegistry`).

2. **Centralized Initialization**  
   A single manager (like `ItemsRegistry` or `BlocksRegistry`) calls each registry’s `registerItems()` / `registerBlocks()` method during plugin startup.

3. **No Logic Inside Registries**  
   Registry classes **only create and register objects**.  
   Item logic, abilities, and rules are handled by their respective classes.

---

By using this structure, the plugin maintains a **clean separation between logic and registration**, making it easier to maintain, extend, and debug.
