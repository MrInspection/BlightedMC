package fr.moussax.bedrock.commands;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Registry for dynamic, lazily evaluated tab completion providers.
 *
 * <p>Keys are case-insensitive and typically prefixed with {@code $} (such as {@code $players}).
 */
public final class TabSuggestionRegistry {

    private final Map<String, Supplier<List<String>>> providers = new HashMap<>();

    /**
     * Registers a dynamic tab completion provider under a symbolic key.
     *
     * @param key      symbolic suggestion key (such as {@code $players})
     * @param provider supplier evaluated whenever completion is requested
     */
    public void register(String key, Supplier<List<String>> provider) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(provider, "provider");
        providers.put(normalizeKey(key), provider);
    }

    /**
     * Resolves dynamic suggestions registered under a symbolic key.
     *
     * @param key symbolic suggestion key
     * @return list of suggestions provided by the registered key, or {@code null} when unregistered
     */
    public @Nullable List<String> resolve(String key) {
        Supplier<List<String>> provider = providers.get(normalizeKey(key));

        if (provider == null) {
            return null;
        }

        List<String> suggestions = provider.get();
        if (suggestions == null) {
            return List.of();
        }

        return suggestions.stream().filter(Objects::nonNull).toList();
    }

    // ponytail: kept — centralized case-insensitive key normalization
    private static String normalizeKey(String key) {
        return key.toLowerCase(Locale.ROOT);
    }
}
