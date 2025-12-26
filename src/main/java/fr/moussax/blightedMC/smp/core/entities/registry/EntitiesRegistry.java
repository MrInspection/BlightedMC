package fr.moussax.blightedMC.smp.core.entities.registry;

import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.smp.features.entities.spawnable.InfernalBlaze;
import fr.moussax.blightedMC.smp.features.entities.bosses.Goldor;
import fr.moussax.blightedMC.smp.features.entities.bosses.RevenantHorror;
import fr.moussax.blightedMC.smp.features.entities.bosses.TheAncientKnight;
import fr.moussax.blightedMC.smp.features.entities.spawnable.Illusioner;
import fr.moussax.blightedMC.smp.features.entities.spawnable.LaserEngineer;
import fr.moussax.blightedMC.smp.features.entities.spawnable.blighted.*;
import fr.moussax.blightedMC.smp.features.entities.spawnable.powerful.Endersent;
import fr.moussax.blightedMC.smp.features.entities.spawnable.Watchling;
import fr.moussax.blightedMC.smp.features.entities.spawnable.powerful.VoidlingDefender;
import fr.moussax.blightedMC.utils.debug.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntitiesRegistry {
    private static final String REGISTRY_PREFIX = "EntitiesRegistry";
    private static final Map<String, AbstractBlightedEntity> ENTITIES = new HashMap<>();

    public static void clearEntities() {
        ENTITIES.clear();
        SpawnableEntitiesRegistry.clearEntities();
    }

    public static void registerEntity(AbstractBlightedEntity entity) {
        if (ENTITIES.containsKey(entity.getEntityId())) {
            throw new IllegalArgumentException("Duplicate entity ID: " + entity.getEntityId());
        }
        ENTITIES.put(entity.getEntityId(), entity);

        if (entity instanceof SpawnableEntity spawnable) {
            SpawnableEntitiesRegistry.registerSpawnableEntity(spawnable);
        }
    }

    public static AbstractBlightedEntity getEntity(String entityId) {
        AbstractBlightedEntity prototype = ENTITIES.get(entityId);
        return prototype != null ? prototype.clone() : null;
    }

    public static List<AbstractBlightedEntity> getAllEntities() {
        return ENTITIES.values().stream()
            .map(AbstractBlightedEntity::clone)
            .toList();
    }

    private static final List<AbstractBlightedEntity> DEFAULT_ENTITIES = List.of(
        new TheAncientKnight(),
        new RevenantHorror(),
        new BlightedBogged(),
        new BlightedDrowned(),
        new BlightedHusk(),
        new BlightedParched(),
        new BlightedPiglin(),
        new BlightedSkeleton(),
        new BlightedStray(),
        new BlightedWitherSkeleton(),
        new BlightedZombie(),
        new BlightedZombifiedPiglin(),
        new LaserEngineer(),
        new InfernalBlaze(),
        new Goldor(),
        new Endersent(),
        new Watchling(),
        new Illusioner(),
        new VoidlingDefender()
    );

    public static void initializeEntities() {
        clearEntities();
        DEFAULT_ENTITIES.forEach(EntitiesRegistry::registerEntity);
        Log.success("EntitiesRegistry", "Registered " + ENTITIES.size() + " entities.");
    }
}
