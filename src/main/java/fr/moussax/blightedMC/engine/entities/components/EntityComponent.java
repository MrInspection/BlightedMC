package fr.moussax.blightedMC.engine.entities.components;

import org.bukkit.entity.LivingEntity;

public interface EntityComponent {
    /** Called when the entity is spawned or rehydrated. */
    default void onInit(LivingEntity entity) {}

    /** Called when the entity dies or is cleaned up. */
    default void onDestroy(LivingEntity entity) {}

    /** Unique key for retrieving the component from the entity. */
    String getId();
}
