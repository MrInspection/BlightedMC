package fr.moussax.blightedMC.game.entities;

import fr.moussax.blightedMC.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.core.entities.EntityAttachment;
import fr.moussax.blightedMC.core.entities.EntityNameTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Turtle;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Dummy extends AbstractBlightedEntity {
    public Dummy() {
        super("Drowned Jockey", 60, 10, EntityType.DROWNED);
        setDroppedExp(20);
        setNameTagType(EntityNameTag.SMALL_NUMBER);
    }

    @Override
    public String getEntityId() {
        return "DUMMY";
    }

    @Override
    protected void applyEquipment() {
        this.itemInMainHand = new ItemStack(Material.TRIDENT);
        this.armor = new ItemStack[]{
            new ItemStack(Material.COPPER_BOOTS),
            new ItemStack(Material.LEATHER_LEGGINGS),
            new ItemStack(Material.COPPER_CHESTPLATE),
            new ItemStack(Material.COPPER_HELMET)
        };
        super.applyEquipment();
    }

    @Override
    public LivingEntity spawn(Location location) {
        LivingEntity mob = super.spawn(location);

        Turtle guardian = (Turtle) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.TURTLE);
        guardian.setCustomName("§8Dummy's Steed");
        guardian.setCustomNameVisible(true);

        Objects.requireNonNull(guardian.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(maxHealth);
        guardian.setHealth(maxHealth);

        mob.leaveVehicle();
        guardian.addPassenger(mob);

        EntityAttachment attachment = new EntityAttachment(guardian, this);
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
