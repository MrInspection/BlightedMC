package fr.moussax.blightedSMP.engine.entities.registry;

import fr.moussax.bedrock.utils.debug.Log;
import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedSMP.registry.EngineRegistry;
import fr.moussax.blightedSMP.registry.RegistryModule;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Central registry for {@link BlightedEntity} definitions and instance factories.
 * Instantiates pristine entities on demand using registered factories or zero-argument constructors.
 */
public final class EntitiesRegistry {

    private static final EngineRegistry<BlightedEntity> REGISTRY =
            new EngineRegistry<>("EntitiesRegistry", BlightedEntity::getEntityId);

    private static final Map<String, Supplier<? extends BlightedEntity>> FACTORIES = new ConcurrentHashMap<>();
    private static final List<Runnable> onRegisterCallbacks = new ArrayList<>();

    private EntitiesRegistry() {
    }

    /**
     * Adds callback executed when an entity is registered.
     *
     * @param callback callback runnable
     */
    public static void addOnRegisterCallback(@NonNull Runnable callback) {
        onRegisterCallbacks.add(Objects.requireNonNull(callback, "callback cannot be null"));
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
     * Registers an entity definition with a supplier factory.
     * All subsequent creations will invoke this factory to produce pristine, unshared instances.
     *
     * @param factory entity factory
     * @param <T>     entity type
     */
    public static <T extends BlightedEntity> void register(@NonNull Supplier<T> factory) {
        Objects.requireNonNull(factory, "factory cannot be null");
        T sample = factory.get();
        if (sample == null) {
            return;
        }
        REGISTRY.register(sample);
        FACTORIES.put(sample.getEntityId(), factory);
        notifyCallbacks();
    }

    /**
     * Registers an entity prototype and binds an automatic zero-argument constructor factory.
     *
     * @param entity entity prototype to register
     */
    public static void register(@Nullable BlightedEntity entity) {
        if (entity == null) {
            return;
        }
        REGISTRY.register(entity);
        registerDefaultFactory(entity);
        notifyCallbacks();
    }

    /**
     * Binds an explicit factory supplier for a specific entity ID.
     *
     * @param entityId target entity ID
     * @param factory  supplier factory
     * @param <T>      entity type
     */
    public static <T extends BlightedEntity> void registerFactory(@NonNull String entityId, @NonNull Supplier<T> factory) {
        FACTORIES.put(
                Objects.requireNonNull(entityId, "entityId cannot be null"),
                Objects.requireNonNull(factory, "factory cannot be null")
        );
    }

    /**
     * Creates and returns a fresh, independent instance of the registered entity
     * ready to be spawned in the world. Prefers the registered factory supplier,
     * falling back to zero-argument constructor reflection.
     *
     * @param entityId target entity identifier
     * @param <T>      expected entity type
     * @return fresh entity instance, or {@code null} if untracked
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends BlightedEntity> T create(@Nullable String entityId) {
        if (entityId == null) {
            return null;
        }
        Supplier<? extends BlightedEntity> factory = FACTORIES.get(entityId);
        if (factory != null) {
            try {
                return (T) factory.get();
            } catch (Throwable throwable) {
                Log.error("EntitiesRegistry", "Failed to create entity from factory '" + entityId + "': " + throwable.getMessage());
            }
        }
        return (T) instantiate(REGISTRY.get(entityId));
    }

    /**
     * Creates and returns a fresh, independent instance of the registered spawnable entity
     * ready to be spawned in the world.
     *
     * @param entityId target entity identifier
     * @return fresh spawnable instance, or {@code null} if untracked or not spawnable
     */
    @Nullable
    public static SpawnableEntity createSpawnable(@Nullable String entityId) {
        BlightedEntity entity = create(entityId);
        if (entity instanceof SpawnableEntity spawnable) {
            return spawnable;
        }
        return null;
    }

    /**
     * Retrieves the registered definition template for read-only inspection.
     * Must not be spawned directly into the world.
     *
     * @param entityId target entity identifier
     * @return registered definition template, or {@code null} if untracked
     */
    @Nullable
    public static BlightedEntity getPrototype(@Nullable String entityId) {
        return entityId != null ? REGISTRY.get(entityId) : null;
    }

    /**
     * Returns a list of fresh instances of all registered entities.
     *
     * @return list of fresh entity instances
     */
    @NonNull
    public static List<BlightedEntity> getAll() {
        return REGISTRY.getAll().stream()
                .map(entity -> {
                    BlightedEntity fresh = create(entity.getEntityId());
                    return fresh != null ? fresh : entity;
                })
                .toList();
    }

    /**
     * Returns a list of fresh instances of all registered spawnable entities.
     *
     * @return list of fresh spawnable entity instances
     */
    @NonNull
    public static List<SpawnableEntity> getSpawnables() {
        return REGISTRY.getAll().stream()
                .filter(SpawnableEntity.class::isInstance)
                .map(entity -> {
                    SpawnableEntity fresh = createSpawnable(entity.getEntityId());
                    return fresh != null ? fresh : (SpawnableEntity) entity;
                })
                .toList();
    }

    /**
     * Clears all registered entity prototypes, factories, and registration callbacks.
     */
    public static void clear() {
        REGISTRY.clear();
        FACTORIES.clear();
        onRegisterCallbacks.clear();
    }

    private static BlightedEntity instantiate(@Nullable BlightedEntity prototype) {
        if (prototype == null) {
            return null;
        }
        Class<? extends BlightedEntity> clazz = prototype.getClass();
        try {
            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Throwable throwable) {
            Log.error("EntitiesRegistry", "Failed to instantiate entity '" + prototype.getEntityId()
                    + "' (" + clazz.getName() + "). Ensure a public zero-arg constructor exists.");
            return null;
        }
    }

    private static void registerDefaultFactory(@NonNull BlightedEntity entity) {
        Class<? extends BlightedEntity> clazz = entity.getClass();
        FACTORIES.put(entity.getEntityId(), () -> {
            try {
                var constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Throwable throwable) {
                throw new IllegalStateException("Failed to instantiate entity '" + entity.getEntityId()
                        + "' (" + clazz.getName() + "). Ensure a public zero-arg constructor exists or register a factory.", throwable);
            }
        });
    }

    private static void notifyCallbacks() {
        onRegisterCallbacks.forEach(callback -> {
            try {
                callback.run();
            } catch (Throwable throwable) {
                Log.error("EntitiesRegistry", "Failed to execute onRegister callback: " + throwable.getMessage());
            }
        });
    }
}
