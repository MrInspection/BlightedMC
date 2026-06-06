package fr.moussax.blightedMC.engine.entities.affixes;

import fr.moussax.blightedMC.engine.entities.components.EntityComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public final class AffixRegistry {

    private static final Map<String, Supplier<EntityComponent>> AFFIXES = new HashMap<>();

    static {
        register(new BurningAffix());
        register(new VoidStrikeAffix());
        register(new ChillingAffix());
    }

    private static void register(EntityComponent componentTemplate) {
        AFFIXES.put(componentTemplate.getId(), () -> {
            try {
                return componentTemplate.getClass().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate affix: " + componentTemplate.getId(), e);
            }
        });
    }

    public static EntityComponent getRandomAffix() {
        if (AFFIXES.isEmpty()) return null;
        Object[] values = AFFIXES.values().toArray();
        @SuppressWarnings("unchecked")
        Supplier<EntityComponent> supplier = (Supplier<EntityComponent>) values[ThreadLocalRandom.current().nextInt(values.length)];
        return supplier.get();
    }

    public static EntityComponent getAffixById(String id) {
        Supplier<EntityComponent> supplier = AFFIXES.get(id);
        return supplier != null ? supplier.get() : null;
    }
}
