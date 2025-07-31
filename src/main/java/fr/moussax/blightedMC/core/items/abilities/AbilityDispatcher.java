package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class AbilityDispatcher {

  public static AbilityType fromEvent(Event event) {
    if (event instanceof PlayerInteractEvent interact) {
      Action action = interact.getAction();
      Player player = interact.getPlayer();
      boolean sneak = player.isSneaking();

      for (AbilityType type : AbilityType.values()) {
        if (type.isOther()) continue;
        if (type.isSneak() != sneak) continue;
        if (type.toAction().contains(action)) return type;
      }
    }

    if (event instanceof EntityDamageByEntityEvent) {
      return AbilityType.ENTITY_HIT;
    }

    return null;
  }

  public static boolean matches(AbilityType abilityType, Event event) {
    AbilityType eventType = fromEvent(event);
    return eventType != null && abilityType == eventType;
  }
}
