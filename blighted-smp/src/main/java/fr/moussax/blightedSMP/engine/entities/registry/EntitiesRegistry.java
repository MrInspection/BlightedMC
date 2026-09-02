package fr.moussax.blightedSMP.engine.entities.registry;

import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedSMP.registry.EngineRegistry;
import fr.moussax.blightedSMP.registry.RegistryModule;
import fr.moussax.bedrock.utils.debug.Log;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Central registry for all {@link BlightedEntity} prototypes.
 */
public final class EntitiesRegistry {

    private static final EngineRegistry<BlightedEntity> REGISTRY =
            new EngineRegistry<>("EntitiesRegistry", BlightedEntity::getEntityId);

    private static final List<Runnable> onRegisterCallbacks = new ArrayList<>();

    private EntitiesRegistry() {
    }

    public static void addOnRegisterCallback(Runnable callback) {
        onRegisterCallbacks.add(callback);
    }

    public static void initialize(List<RegistryModule<Consumer<BlightedEntity>>> modules) {
        clear();
        REGISTRY.initialize(modules);
    }

    public static void register(BlightedEntity entity) {
        if (entity == null) return;
        boolean isNew = !REGISTRY.contains(entity.getEntityId());
        REGISTRY.register(entity);

        if (isNew && entity instanceof SpawnableEntity spawnable) {
            SpawnableEntitiesRegistry.register(spawnable);
        }

        onRegisterCallbacks.forEach(callback -> {
            try {
                callback.run();
            } catch (Throwable throwable) {
                Log.error("EntitiesRegistry", "Failed to execute onRegister callback: " + throwable.getMessage());
            }
        });
    }

    @Nullable
    public static BlightedEntity get(String entityId) {
        BlightedEntity prototype = REGISTRY.get(entityId);
        return prototype != null ? prototype.clone() : null;
    }

    public static List<BlightedEntity> getAll() {
        return REGISTRY.getAll().stream()
                .map(BlightedEntity::clone)
                .toList();
    }

    public static void clear() {
        REGISTRY.clear();
        SpawnableEntitiesRegistry.clear();
        onRegisterCallbacks.clear();
    }
}
