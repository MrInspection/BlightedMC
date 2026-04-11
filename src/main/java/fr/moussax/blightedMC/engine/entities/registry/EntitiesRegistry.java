package fr.moussax.blightedMC.engine.entities.registry;

import fr.moussax.blightedMC.engine.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.engine.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.content.entities.frenzied.*;
import fr.moussax.blightedMC.content.entities.bosses.TheAncientKnight;
import fr.moussax.blightedMC.content.entities.Illusioner;
import fr.moussax.blightedMC.content.entities.Watchling;
import fr.moussax.blightedMC.content.entities.powerful.Endersent;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntitiesRegistry {
    private static final Map<String, AbstractBlightedEntity> ENTITIES = new HashMap<>();

    private static final List<AbstractBlightedEntity> DEFAULT_ENTITIES = List.of(
        new TheAncientKnight(),
        new FrenziedBogged(),
        new FrenziedDrowned(),
        new FrenziedHusk(),
        new FrenziedParched(),
        new FrenziedPiglin(),
        new FrenziedSkeleton(),
        new FrenziedStray(),
        new FrenziedWitherSkeleton(),
        new FrenziedZombie(),
        new FrenziedZombifiedPiglin(),
        new Endersent(),
        new Watchling(),
        new Illusioner()
    );

    private EntitiesRegistry() {
    }

    public static void initialize() {
        clear();
        DEFAULT_ENTITIES.forEach(EntitiesRegistry::register);
        Log.success("EntitiesRegistry", "Registered " + ENTITIES.size() + " entities (spawnable: " + SpawnableEntitiesRegistry.count() + ").");
    }

    public static void register(AbstractBlightedEntity entity) {
        if (ENTITIES.containsKey(entity.getEntityId())) {
            Log.warn("EntitiesRegistry", "Duplicate entity ID detected: " + entity.getEntityId() + ". Skipping.");
            return;
        }

        ENTITIES.put(entity.getEntityId(), entity);

        if (entity instanceof SpawnableEntity spawnable) {
            SpawnableEntitiesRegistry.register(spawnable);
        }
    }

    @Nullable
    public static AbstractBlightedEntity get(String entityId) {
        AbstractBlightedEntity prototype = ENTITIES.get(entityId);
        return prototype != null ? prototype.clone() : null;
    }

    public static List<AbstractBlightedEntity> getAll() {
        return ENTITIES.values().stream()
            .map(AbstractBlightedEntity::clone)
            .toList();
    }

    public static void clear() {
        ENTITIES.clear();
        SpawnableEntitiesRegistry.clear();
    }
}
