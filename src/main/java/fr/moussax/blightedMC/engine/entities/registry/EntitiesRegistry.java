package fr.moussax.blightedMC.engine.entities.registry;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class EntitiesRegistry {
    private static final Map<String, BlightedEntity> ENTITIES = new HashMap<>();

    private static final List<Runnable> onRegisterCallbacks = new ArrayList<>();

    private EntitiesRegistry() {
    }

    public static void addOnRegisterCallback(Runnable callback) {
        onRegisterCallbacks.add(callback);
    }

    public static void initialize(List<RegistryModule<Consumer<BlightedEntity>>> modules) {
        clear();
        modules.forEach(module -> module.register(EntitiesRegistry::register));
        Log.success("EntitiesRegistry", "Registered " + ENTITIES.size() + " entities (spawnable: " + SpawnableEntitiesRegistry.count() + ").");
    }

    public static void register(BlightedEntity entity) {
        if (ENTITIES.containsKey(entity.getEntityId())) {
            Log.warn("EntitiesRegistry", "Duplicate entity ID detected: " + entity.getEntityId() + ". Skipping.");
            return;
        }

        ENTITIES.put(entity.getEntityId(), entity);

        if (entity instanceof SpawnableEntity spawnable) {
            SpawnableEntitiesRegistry.register(spawnable);
        }

        onRegisterCallbacks.forEach(callback -> {
            try {
                callback.run();
            } catch (Throwable t) {
                Log.error("EntitiesRegistry", "Failed to execute onRegister callback: " + t.getMessage());
            }
        });
    }

    @Nullable
    public static BlightedEntity get(String entityId) {
        BlightedEntity prototype = ENTITIES.get(entityId);
        return prototype != null ? prototype.clone() : null;
    }

    public static List<BlightedEntity> getAll() {
        return ENTITIES.values().stream()
                .map(BlightedEntity::clone)
                .toList();
    }

    public static void clear() {
        ENTITIES.clear();
        SpawnableEntitiesRegistry.clear();
        onRegisterCallbacks.clear();
    }
}
