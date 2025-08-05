package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Executes abilities for players, handling cooldowns, mana consumption, and activation logic.
 * <p>
 * This class provides static methods to:
 * <ul>
 *   <li>Validate if an ability can be executed (cooldown and mana check).</li>
 *   <li>Trigger the ability through its {@link AbilityManager}.</li>
 *   <li>Manage cooldown entries for abilities.</li>
 * </ul>
 * This class is not instantiable.
 */
public final class AbilityExecutor {
  private AbilityExecutor() {}

  /**
   * Executes an ability for a given player when an event occurs.
   * <p>
   * Steps performed:
   * <ol>
   *   <li>Checks if the ability is on cooldown; cancels the event if so.</li>
   *   <li>Checks if the player has enough mana; cancels the event if not.</li>
   *   <li>Validates custom conditions using {@link AbilityManager#canTrigger(BlightedPlayer)}.</li>
   *   <li>Attempts to trigger the ability; if successful, subtracts mana and starts cooldown.</li>
   * </ol>
   *
   * @param ability the ability to execute
   * @param player the {@link BlightedPlayer} attempting to use the ability
   * @param event the triggering Bukkit event
   * @param <T> the event type
   */
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

  /**
   * Casts a generic {@link AbilityManager} to a typed instance for the event.
   *
   * @param manager the raw manager instance
   * @param <T> the Bukkit event type handled by the manager
   * @return the typed {@link AbilityManager} instance
   */
  @SuppressWarnings("unchecked")
  private static <T extends Event> AbilityManager<T> castManager(AbilityManager<?> manager) {
    return (AbilityManager<T>) manager;
  }

  /**
   * Generates a human-readable display name for an ability manager class.
   * <p>
   * Removes the suffixes "Ability" and "Manager" and converts camel case to
   * a space-separated title case string.
   *
   * @param manager the ability manager
   * @return the formatted display name
   */
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

  /**
   * Starts a cooldown timer for a specific ability and registers it in the player's cooldown list.
   * <p>
   * If the player already has a cooldown entry for the same ability manager and type,
   * this method does nothing.
   *
   * @param blightedPlayer the player to apply the cooldown to
   * @param abilityManagerClass the ability manager class
   * @param abilityType the ability to type
   * @param cooldownSeconds the duration of the cooldown in seconds
   */
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
