package fr.moussax.blightedMC.smp.core.entities.rituals;

import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.entities.EntityNameTag;
import fr.moussax.blightedMC.utils.sound.SoundSequence;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NonNull;

public abstract class AncientCreature extends AbstractBlightedEntity {
    public AncientCreature(@NonNull String name, int maxHealth, EntityType entityType) {
        super(name, maxHealth, entityType);
        this.nameTagType = EntityNameTag.ANCIENT;
    }

    public AncientCreature(String name, int maxHealth, int damage, EntityType entityType) {
        super(name, maxHealth, damage, entityType);
        this.nameTagType = EntityNameTag.ANCIENT;
    }

    public AncientCreature(String name, int maxHealth, int damage, int defense, EntityType entityType) {
        super(name, maxHealth, damage, defense, entityType);
        this.nameTagType = EntityNameTag.ANCIENT;
    }

    @Override
    public void onDeath(Location location) {
        super.onDeath(location);
        SoundSequence.ANCIENT_MOB_DEFEAT.play(location);
    }
}
