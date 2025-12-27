package fr.moussax.blightedMC.smp.core.entities.immunity;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class MaceImmunity implements EntityImmunity {
    @Override
    public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent damageByEntity)) return false;
        if (!(damageByEntity.getDamager() instanceof Player damager)) return false;

        return damager.getInventory().getItemInMainHand().getType() == Material.MACE;
    }

    @Override
    public String getImmunityMessage() {
        return "§4 ■ §cThe Blight prevents your mace from dealing damage.";
    }
}
