package fr.moussax.blightedMC.commands.utils;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Stores named, lazily evaluated tab-completion providers.
 */
public final class TabSuggestionRegistry {

    private final Map<String, Supplier<List<String>>> providers = new HashMap<>();

    /**
     * Registers or replaces a suggestion provider.
     *
     * @param key      symbolic suggestion key, such as {@code $players}
     * @param provider provider evaluated whenever completion is requested
     */
    public void register(String key, Supplier<List<String>> provider) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(provider, "provider");
        providers.put(key, provider);
    }

    /**
     * Resolves a registered suggestion key.
     *
     * @param key symbolic suggestion key
     * @return suggestions for the key, or {@code null} when it is not registered
     */
    public @Nullable List<String> resolve(String key) {
        Supplier<List<String>> provider = providers.get(key);

        if (provider == null) {
            return null;
        }

        List<String> suggestions = provider.get();
        return suggestions == null ? List.of() : suggestions;
    }
}
