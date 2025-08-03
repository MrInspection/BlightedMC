package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public final class AbilityExecutor {
  private AbilityExecutor() {}

  public static <T extends Event> void execute(Ability ability, BlightedPlayer player, T event) {
    AbilityManager<T> manager = castManager(ability.getManager());

    // 1. Check cooldown
    for (CooldownEntry entry : player.getCooldowns()) {
      if (entry.abilityManager().equals(manager.getClass()) && entry.abilityType() == ability.getType()) {
        double remainingSeconds = entry.getRemainingSeconds();

        if (remainingSeconds <= 0) {
          continue;
        }

        String abilityName = ability.getName();
        String timeText = String.format("%.0fs", remainingSeconds);
        MessageUtils.warnSender(player.getPlayer(), "§cYour §6" + abilityName + " §cability is on cooldown for §d" + timeText + "§c!");
        if (event instanceof Cancellable c) c.setCancelled(true);
        return;
      }
    }

    // 2. Check mana
    int manaCost = manager.getManaCost();
    if (player.getMana().getCurrentMana() < manaCost) {
      player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
      player.getActionBarManager().setInsufficientMana(true);
      if (event instanceof Cancellable c) c.setCancelled(true);
      return;
    }

    if (!manager.canTrigger(player)) {
      if (event instanceof Cancellable c) c.setCancelled(true);
      return;
    }

    try {
      boolean success = manager.triggerAbility(event);
      if (event instanceof Cancellable c) c.setCancelled(!success || c.isCancelled());
      if (success) {
        // 3. Subtract mana
        if (manaCost > 0) {
          player.getMana().consumeMana(manaCost);
          player.getActionBarManager().tick();
        }
        manager.start(player);
        startCooldown(player,
          manager.getClass(),
          ability.getType(),
          manager.getCooldownSeconds());
      }
    } catch (Exception e) {
      player.getPlayer().sendMessage("§cAbility execution failed: " + e.getClass().getSimpleName());
      if (event instanceof Cancellable c) c.setCancelled(true);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T extends Event> AbilityManager<T> castManager(AbilityManager<?> manager) {
    return (AbilityManager<T>) manager;
  }

  private static String getAbilityDisplayName(AbilityManager<?> manager) {
    String className = manager.getClass().getSimpleName();

    // Remove common suffixes
    if (className.endsWith("Ability")) {
      className = className.substring(0, className.length() - 8);
    }
    if (className.endsWith("Manager")) {
      className = className.substring(0, className.length() - 7);
    }

    // If it's a single word, just return it as-is
    if (className.length() <= 1) {
      return className;
    }

    // Convert camelCase to Title Case
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < className.length(); i++) {
      char c = className.charAt(i);

      // Add space before uppercase letters (except the first one)
      if (Character.isUpperCase(c) && i > 0) {
        result.append(' ');
      }

      // Always capitalize the first letter of each word
      if (i == 0 || Character.isUpperCase(className.charAt(i - 1))) {
        result.append(Character.toUpperCase(c));
      } else {
        result.append(Character.toLowerCase(c));
      }
    }

    return result.toString().trim();
  }

  public static void startCooldown(BlightedPlayer blightedPlayer,
                                   Class<? extends AbilityManager> abilityManagerClass,
                                   AbilityType abilityType,
                                   int cooldownSeconds) {
    CooldownEntry newEntry = new CooldownEntry(abilityManagerClass, abilityType,
      java.time.Instant.now().plusSeconds(cooldownSeconds));

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
      cooldownSeconds * 20L // Convert seconds to ticks
    );
  }
}
