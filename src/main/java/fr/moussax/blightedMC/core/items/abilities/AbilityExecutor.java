package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.core.players.CooldownEntry;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public final class AbilityExecutor {
  private AbilityExecutor() {}

  public static <T extends Event> void execute(Ability ability, BlightedPlayer player, T event) {
    AbilityManager<T> manager = castManager(ability.getManager());
    if (!manager.canTrigger(player)) {
      if (event instanceof Cancellable c) c.setCancelled(true);
      return;
    }
    try {
      boolean success = manager.triggerAbility(event);
      if (event instanceof Cancellable c) c.setCancelled(!success || c.isCancelled());
      if (success) {
        // Call the start method to execute the actual ability logic
        manager.start(player);
        
        startCooldown(player,
          manager.getClass(),
          ability.getType(),
          manager.getCooldownTicks());
      }
    } catch (Exception e) {
      e.printStackTrace();
      player.getPlayer().sendMessage("§cAbility execution failed: " + e.getClass().getSimpleName());
      if (event instanceof Cancellable c) c.setCancelled(true);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T extends Event> AbilityManager<T> castManager(AbilityManager<?> manager) {
    return (AbilityManager<T>) manager;
  }

  public static void startCooldown(BlightedPlayer blightedPlayer,
                                   Class<? extends AbilityManager> abilityManagerClass,
                                   AbilityType abilityType,
                                   long cooldownTicks) {
    CooldownEntry newEntry = new CooldownEntry(abilityManagerClass, abilityType);

    for (CooldownEntry entry : blightedPlayer.getCooldowns()) {
      if (entry.abilityManager().equals(abilityManagerClass) &&
        entry.abilityType() == abilityType) {
        return;
      }
    }

    blightedPlayer.addCooldown(newEntry);

    Bukkit.getScheduler().runTaskLater(
      BlightedMC.getInstance(),
      () -> blightedPlayer.removeCooldown(newEntry),
      cooldownTicks
    );
  }
}
