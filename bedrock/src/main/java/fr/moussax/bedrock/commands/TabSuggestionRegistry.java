package fr.moussax.bedrock.commands;

import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registry for dynamic, lazily evaluated tab completion providers.
 *
 * <p>Keys are case-insensitive and typically prefixed with {@code $} (such as {@code $players}).
 */
public final class TabSuggestionRegistry {

    private final Map<String, Function<@Nullable CommandSender, List<String>>> providers = new HashMap<>();

    /**
     * Registers a sender-aware dynamic tab completion provider under a symbolic key.
     *
     * @param key      symbolic suggestion key (such as {@code $players})
     * @param provider function evaluated with the command sender whenever completion is requested
     */
    public void register(@NonNull String key, @NonNull Function<@Nullable CommandSender, List<String>> provider) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(provider, "provider");
        providers.put(normalizeKey(key), provider);
    }

    /**
     * Registers a context-free dynamic tab completion provider under a symbolic key.
     *
     * @param key      symbolic suggestion key (such as {@code $players})
     * @param provider supplier evaluated whenever completion is requested
     */
    public void register(@NonNull String key, @NonNull Supplier<List<String>> provider) {
        Objects.requireNonNull(provider, "provider");
        register(key, _ -> provider.get());
    }

    /**
     * Resolves dynamic suggestions registered under a symbolic key for a specific command sender.
     *
     * @param sender command sender requesting completion, or {@code null}
     * @param key    symbolic suggestion key
     * @return list of suggestions provided by the registered key, or {@code null} when unregistered
     */
    public @Nullable List<String> resolve(@Nullable CommandSender sender, @NonNull String key) {
        Function<@Nullable CommandSender, List<String>> provider = providers.get(normalizeKey(key));

        if (provider == null) {
            return null;
        }

        List<String> suggestions = provider.apply(sender);
        if (suggestions == null) {
            return List.of();
        }

        return suggestions.stream().filter(Objects::nonNull).toList();
    }

    /**
     * Resolves dynamic suggestions registered under a symbolic key without sender context.
     *
     * @param key symbolic suggestion key
     * @return list of suggestions provided by the registered key, or {@code null} when unregistered
     */
    public @Nullable List<String> resolve(@NonNull String key) {
        return resolve(null, key);
    }

    // ponytail: kept — centralized case-insensitive key normalization
    private static String normalizeKey(String key) {
        return key.toLowerCase(Locale.ROOT);
    }
}
