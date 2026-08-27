package fr.moussax.blightedMC.engine.entities.components;

import fr.moussax.blightedMC.engine.entities.components.impl.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Registry for rollable elite mob components and affixes.
 *
 * <p>Provides functional factory registration and random selection methods for assigning
 * modular {@link EntityComponent} instances to custom entities.</p>
 */
public final class AffixRegistry {

    private static final Map<String, Supplier<EntityComponent>> REGISTRY = new HashMap<>();

    static {
        register("AFFIX_BURNING", BurningComponent::new);
        register("AFFIX_CHILLING", ChillingComponent::new);
        register("AFFIX_VOID_STRIKE", VoidStrikeComponent::new);
        register("AFFIX_VAMPIRIC", VampiricComponent::new);
        register("AFFIX_FRENZIED", FrenziedComponent::new);
        register("AFFIX_SHIELD", () -> new ShieldComponent(120.0));
    }

    private AffixRegistry() {
    }

    /**
     * Registers a component factory for the specified identifier.
     *
     * @param id      unique component identifier
     * @param factory factory supplier producing fresh component instances
     */
    public static void register(String id, Supplier<EntityComponent> factory) {
        REGISTRY.put(id, factory);
    }

    /**
     * Instantiates a single random registered component.
     *
     * @return fresh component instance, or {@code null} if no components are registered
     */
    public static EntityComponent getRandomAffix() {
        if (REGISTRY.isEmpty()) return null;
        List<Supplier<EntityComponent>> suppliers = new ArrayList<>(REGISTRY.values());
        Supplier<EntityComponent> selected = suppliers.get(ThreadLocalRandom.current().nextInt(suppliers.size()));
        return selected.get();
    }

    /**
     * Rolls up to a specified number of distinct registered components.
     *
     * @param count maximum number of distinct components to roll
     * @return unmodifiable list of fresh component instances
     */
    public static List<EntityComponent> getRandomAffixes(int count) {
        if (REGISTRY.isEmpty() || count <= 0) return List.of();

        List<String> keys = new ArrayList<>(REGISTRY.keySet());
        Collections.shuffle(keys, ThreadLocalRandom.current());

        List<EntityComponent> result = new ArrayList<>();
        int limit = Math.min(count, keys.size());
        for (int i = 0; i < limit; i++) {
            Supplier<EntityComponent> supplier = REGISTRY.get(keys.get(i));
            if (supplier != null) {
                result.add(supplier.get());
            }
        }
        return result;
    }

    /**
     * Instantiates a registered component by its unique identifier.
     *
     * @param id component identifier
     * @return fresh component instance, or {@code null} if unmapped
     */
    public static EntityComponent getAffixById(String id) {
        Supplier<EntityComponent> supplier = REGISTRY.get(id);
        return supplier != null ? supplier.get() : null;
    }
}
