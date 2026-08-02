package fr.moussax.blightedMC.engine.entities.rituals;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.utils.sound.SoundSequence;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NonNull;

public abstract class AncientCreature extends BlightedEntity {
    public AncientCreature(@NonNull String name, int maxHealth, EntityType entityType) {
        super(name, maxHealth, entityType);
        setBoss(true);
    }

    public AncientCreature(String name, int maxHealth, int damage, EntityType entityType) {
        super(name, maxHealth, damage, entityType);
        setBoss(true);
    }

    public AncientCreature(String name, int maxHealth, int damage, int defense, EntityType entityType) {
        super(name, maxHealth, damage, defense, entityType);
        setBoss(true);
    }

    @Override
    public void onDeath(Location location) {
        super.onDeath(location);
        SoundSequence.ANCIENT_MOB_DEFEAT.play(location);
    }
}
