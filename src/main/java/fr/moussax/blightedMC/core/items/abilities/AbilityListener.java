package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class AbilityListener implements Listener {

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    triggerPlayerAbility(event.getPlayer(), event);
  }

  @EventHandler
  public void onHit(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player player) {
      triggerPlayerAbility(player, event);
    }
  }

  private <T extends Event> void triggerPlayerAbility(Player player, T event) {
    AbilityManager<T> ability = getAbilityFromItem(player);

    if (ability != null && AbilityDispatcher.matches(ability.getType(), event)) {
      AbilityRegistry.triggerAbility(player, ability, event);
    }
  }

  private <T extends Event> AbilityManager<T> getAbilityFromItem(Player player) {
    // TODO: Map item in hand to its AbilityManager
    return null;
  }
}
