package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.event.Event;

import java.util.List;
import java.util.function.Predicate;

public final class AbilityTriggerer {
  private AbilityTriggerer() {}

  public static <T extends Event> void trigger(
    BlightedPlayer player,
    ItemManager itemManager,
    T event,
    Predicate<AbilityType> filter
  ) {
    if (itemManager == null) return;

    List<Ability> abilities = itemManager.getAbilities();
    if (abilities.isEmpty()) return;

    boolean isSneaking = player.getPlayer().isSneaking();
    boolean hasSneakAbility = abilities.stream().anyMatch(a -> a.getType().isSneak());

    for (Ability ability : abilities) {
      if (!filter.test(ability.getType())) continue;

      if (ability.getType().isSneak() && !isSneaking) continue;
      if (hasSneakAbility && isSneaking && !ability.getType().isSneak()) continue;

      AbilityExecutor.execute(ability, player, event);
    }
  }
}