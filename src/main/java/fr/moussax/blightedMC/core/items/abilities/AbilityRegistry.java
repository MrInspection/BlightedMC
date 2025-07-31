package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityRegistry {
  private static final Map<UUID, Map<AbilityManager<?>, Long>> cooldowns = new HashMap<>();

  public static <T extends Event> void triggerAbility(Player player, AbilityManager<T> ability, T event) {
    if (!ability.canTrigger(player)) return;

    long now = System.currentTimeMillis();
    long cdTicks = ability.getCooldownTicks();
    if (cdTicks > 0 && !isCooldownReady(player, ability, now, cdTicks)) {
      player.sendMessage("§cAbility is on cooldown!");
      return;
    }

    int manaCost = ability.getManaCost();
    if (!consumeMana(player, manaCost)) {
      player.sendMessage("§cNot enough mana!");
      return;
    }

    boolean success = ability.triggerAbility(event);
    if (!success) return;

    if (cdTicks > 0) {
      cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
          .put(ability, now + cdTicks * 50); // 20 ticks = 1s
    }
  }

  private static boolean isCooldownReady(Player player, AbilityManager<?> ability, long now, long cdTicks) {
    long readyAt = cooldowns
        .getOrDefault(player.getUniqueId(), Collections.emptyMap())
        .getOrDefault(ability, 0L);
    return now >= readyAt;
  }

  private static boolean consumeMana(Player player, int amount) {
    // TODO: Hook into your BlightedMC mana system
    return true;
  }

  public static void clearCooldowns(Player player) {
    cooldowns.remove(player.getUniqueId());
  }
}

