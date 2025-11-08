package fr.moussax.blightedMC.gameplay.entities;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.EntityAttachment;
import fr.moussax.blightedMC.core.entities.EntityNameTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Dummy extends BlightedEntity {
  public Dummy() {
    super("Dummy", 40, 6, EntityType.HUSK);
    setDroppedExp(15);
    setNameTagType(EntityNameTag.BLIGHTED);
  }

  @Override
  public String getEntityId() {
    return "DUMMY";
  }

  @Override
  protected void applyEquipment() {
    this.itemInMainHand = new ItemStack(Material.IRON_HOE);
    this.armor = new ItemStack[]{
      new ItemStack(Material.IRON_BOOTS),
      new ItemStack(Material.IRON_LEGGINGS),
      new ItemStack(Material.IRON_CHESTPLATE),
      new ItemStack(Material.IRON_HELMET)
    };
    super.applyEquipment();
  }

  @Override
  public LivingEntity spawn(Location location) {
    LivingEntity mob = super.spawn(location);

    ZombieHorse horse = (ZombieHorse) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.ZOMBIE_HORSE);
    horse.setCustomName("§8Dummy's Steed");
    horse.setCustomNameVisible(true);
    horse.setTamed(true);

    Objects.requireNonNull(horse.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(maxHealth);
    horse.setHealth(maxHealth);

    mob.leaveVehicle();
    horse.addPassenger(mob);

    EntityAttachment attachment = new EntityAttachment(horse, this);
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
