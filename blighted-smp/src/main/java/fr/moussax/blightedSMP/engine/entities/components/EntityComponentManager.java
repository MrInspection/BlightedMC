package fr.moussax.blightedSMP.engine.entities.components;

import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Manages the registration, lifecycle, lookup, and periodic ticking of {@link EntityComponent}s.
 */
public final class EntityComponentManager {

    private final Map<String, EntityComponent> components = new HashMap<>();
    @Getter
    private boolean initialized = false;

    public EntityComponentManager() {
    }

    private EntityComponentManager(Map<String, EntityComponent> source) {
        for (Map.Entry<String, EntityComponent> entry : source.entrySet()) {
            EntityComponent fresh = AffixRegistry.getAffixById(entry.getKey());
            this.components.put(entry.getKey(), fresh != null ? fresh : entry.getValue());
        }
    }

    /**
     * Registers a component. If components have already been initialized, the component
     * is immediately initialized against the live entity.
     *
     * @param component  component to register
     * @param liveEntity current live entity, or null if unspawned
     */
    public void addComponent(@NonNull EntityComponent component, @Nullable LivingEntity liveEntity) {
        Objects.requireNonNull(component, "component cannot be null");
        components.put(component.getId(), component);
        if (initialized && liveEntity != null && liveEntity.isValid() && !liveEntity.isDead()) {
            component.onInit(liveEntity);
        }
    }

    /**
     * Retrieves a registered component by its unique string identifier.
     *
     * @param id  component identifier
     * @param <T> expected component type
     * @return component instance, or null if not registered
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends EntityComponent> T getComponent(@NonNull String id) {
        return (T) components.get(id);
    }

    /**
     * Retrieves the first registered component matching the specified class.
     *
     * @param componentClass class to match
     * @param <T>            expected component type
     * @return matching component instance, or null if none registered
     */
    @Nullable
    public <T extends EntityComponent> T getComponent(@Nullable Class<T> componentClass) {
        if (componentClass == null) {
            return null;
        }
        for (EntityComponent component : components.values()) {
            if (componentClass.isInstance(component)) {
                return componentClass.cast(component);
            }
        }
        return null;
    }

    /**
     * Returns a snapshot of all currently registered components.
     *
     * @return list of registered components
     */
    @NonNull
    public Collection<EntityComponent> getComponents() {
        return new ArrayList<>(components.values());
    }

    /**
     * Initializes all registered components against the live entity.
     *
     * @param liveEntity bound entity
     */
    public void init(@Nullable LivingEntity liveEntity) {
        if (initialized || liveEntity == null) {
            return;
        }
        initialized = true;
        for (EntityComponent component : components.values()) {
            component.onInit(liveEntity);
        }
    }

    /**
     * Destroys all registered components.
     *
     * @param liveEntity bound entity
     */
    public void destroy(@Nullable LivingEntity liveEntity) {
        for (EntityComponent component : components.values()) {
            component.onDestroy(liveEntity);
        }
        initialized = false;
    }

    /**
     * Executes periodic tick logic for all components.
     *
     * @param entity owner entity
     */
    public void tick(@NonNull BlightedEntity entity) {
        for (EntityComponent component : components.values()) {
            component.onTick(entity);
        }
    }

    /**
     * Notifies all components of the entity's death.
     *
     * @param entity   owner entity
     * @param location death location
     */
    public void onDeath(@NonNull BlightedEntity entity, @Nullable Location location) {
        if (location != null) {
            for (EntityComponent component : components.values()) {
                component.onDeath(entity, location);
            }
        }
    }

    /**
     * Creates an uninitialized detached copy of this component manager with freshly instantiated affixes.
     *
     * @return detached copy
     */
    @NonNull
    public EntityComponentManager copy() {
        return new EntityComponentManager(this.components);
    }
}
