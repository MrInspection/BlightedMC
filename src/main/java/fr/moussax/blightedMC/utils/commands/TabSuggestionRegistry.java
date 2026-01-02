package fr.moussax.blightedMC.utils.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class TabSuggestionRegistry {
    private static final Map<String, Supplier<List<String>>> providers = new HashMap<>();

    private TabSuggestionRegistry() {}

    public static void register(String key, Supplier<List<String>> provider) {
        providers.put(key, provider);
    }

    public static List<String> resolve(String key) {
        Supplier<List<String>> provider = providers.get(key);
        return provider != null ? provider.get() : Collections.emptyList();
    }

    public static boolean contains(String key) {
        return providers.containsKey(key);
    }
}
