package fr.moussax.blightedMC.smp.features.entities.spawnable;

import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.entities.EntityAttachment;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

public class InfernalBlaze extends AbstractBlightedEntity {
    public InfernalBlaze() {
        super("Infernal Blaze", 60, 10, EntityType.BLAZE);
        setDroppedExp(20);
    }

    @Override
    public String getEntityId() {
        return "INFERNAL_BLAZE";
    }

    @Override
    public LivingEntity spawn(Location location) {
        LivingEntity mob = super.spawn(location);

        Blaze blaze = (Blaze) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.BLAZE);

        blaze.setCustomName("Dinnerbone");
        Objects.requireNonNull(blaze.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(maxHealth);
        blaze.setHealth(maxHealth);

        mob.leaveVehicle();
        blaze.addPassenger(mob);

        EntityAttachment attachment = new EntityAttachment(blaze, this);
        addAttachment(attachment);

        return mob;
    }

    @Override
    public void kill() {
        if (entity != null && entity.isInsideVehicle()) {
            entity.leaveVehicle();
        }
        super.kill();
    }
}
