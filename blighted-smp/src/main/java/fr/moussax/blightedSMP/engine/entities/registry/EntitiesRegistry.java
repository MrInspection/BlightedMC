package fr.moussax.blightedSMP.engine.entities.registry;

import fr.moussax.bedrock.utils.debug.Log;
import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedSMP.registry.EngineRegistry;
import fr.moussax.blightedSMP.registry.RegistryModule;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Central registry for prototype {@link BlightedEntity} instances.
 */
public final class EntitiesRegistry {

    private static final EngineRegistry<BlightedEntity> REGISTRY =
            new EngineRegistry<>("EntitiesRegistry", BlightedEntity::getEntityId);

    private static final List<Runnable> onRegisterCallbacks = new ArrayList<>();

    private EntitiesRegistry() {
    }

    /**
     * Adds callback executed when a prototype entity is registered.
     *
     * @param callback callback runnable
     */
    public static void addOnRegisterCallback(Runnable callback) {
        onRegisterCallbacks.add(callback);
    }

    /**
     * Initializes registry with defined registration modules.
     *
     * @param modules list of registry modules
     */
    public static void initialize(List<RegistryModule<Consumer<BlightedEntity>>> modules) {
        clear();
        REGISTRY.initialize(modules);
    }

    /**
     * Registers entity prototype and executes registration callbacks.
     *
     * @param entity entity prototype to register
     */
    public static void register(BlightedEntity entity) {
        if (entity == null) return;
        REGISTRY.register(entity);

        onRegisterCallbacks.forEach(callback -> {
            try {
                callback.run();
            } catch (Throwable throwable) {
                Log.error("EntitiesRegistry", "Failed to execute onRegister callback: " + throwable.getMessage());
            }
        });
    }

    /**
     * Retrieves cloned instance of registered entity prototype by ID.
     *
     * @param entityId target entity identifier
     * @return cloned entity instance, or {@code null} if untracked
     */
    @Nullable
    public static BlightedEntity get(String entityId) {
        BlightedEntity prototype = REGISTRY.get(entityId);
        return prototype != null ? prototype.clone() : null;
    }

    /**
     * Retrieves a cloned instance of a registered spawnable entity prototype by ID.
     *
     * @param entityId target entity identifier
     * @return cloned spawnable entity instance, or {@code null} if untracked or not spawnable
     */
    @Nullable
    public static SpawnableEntity getSpawnable(String entityId) {
        BlightedEntity entity = REGISTRY.get(entityId);
        if (!(entity instanceof SpawnableEntity spawnable)) {
            return null;
        }
        return spawnable.clone();
    }

    /**
     * Returns a list of cloned instances of all registered entity prototypes.
     *
     * @return list of cloned entity prototypes
     */
    public static List<BlightedEntity> getAll() {
        return REGISTRY.getAll().stream().map(BlightedEntity::clone).toList();
    }

    /**
     * Returns a list of cloned instances of all registered spawnable entity prototypes.
     *
     * @return list of cloned spawnable entity prototypes
     */
    public static List<SpawnableEntity> getSpawnables() {
        return REGISTRY.getAll().stream()
                .filter(SpawnableEntity.class::isInstance)
                .map(entity -> ((SpawnableEntity) entity).clone())
                .toList();
    }

    /**
     * Clears all registered entity prototypes and registration callbacks.
     */
    public static void clear() {
        REGISTRY.clear();
        onRegisterCallbacks.clear();
    }
}
