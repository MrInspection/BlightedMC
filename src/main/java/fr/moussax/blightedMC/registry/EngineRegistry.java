package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Generic engine registry providing map storage, module loading, and duplicate key checking.
 *
 * // ponytail: simplified — unified 7 static content registries into a single generic EngineRegistry<T> to eliminate duplicate map boilerplate.
 *
 * @param <T> the type of registry element stored
 */
public final class EngineRegistry<T> {

    private final String registryName;
    private final Function<T, String> idExtractor;
    private final Map<String, T> entries = new HashMap<>();

    public EngineRegistry(@NonNull String registryName, @NonNull Function<T, String> idExtractor) {
        this.registryName = Objects.requireNonNull(registryName, "registryName cannot be null");
        this.idExtractor = Objects.requireNonNull(idExtractor, "idExtractor cannot be null");
    }

    public void initialize(List<RegistryModule<Consumer<T>>> modules) {
        clear();
        if (modules != null) {
            modules.forEach(module -> module.register(this::register));
        }
        Log.success(registryName, "Registered " + entries.size() + " elements.");
    }

    public void register(T item) {
        if (item == null) return;
        String id = idExtractor.apply(item);
        if (id == null) {
            Log.warn(registryName, "Extracted null ID for element: " + item + ". Skipping.");
            return;
        }

        if (entries.containsKey(id)) {
            Log.warn(registryName, "Duplicate ID detected: " + id + ". Skipping.");
            return;
        }
        entries.put(id, item);
    }

    @Nullable
    public T get(String id) {
        if (id == null) return null;
        return entries.get(id);
    }

    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(entries.values());
    }

    public boolean contains(String id) {
        return id != null && entries.containsKey(id);
    }

    public int count() {
        return entries.size();
    }

    public void clear() {
        entries.clear();
    }
}
