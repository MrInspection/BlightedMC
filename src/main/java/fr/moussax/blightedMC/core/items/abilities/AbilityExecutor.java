package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.time.Instant;

import static fr.moussax.blightedMC.utils.formatting.Formatter.warn;

/**
 * Executes player abilities, handling cooldowns, mana, and activation.
 * <p>
 * Provides static methods to trigger abilities, enforce cooldowns, and apply mana costs.
 * This class is not instantiable.
 */
public final class AbilityExecutor {
    private AbilityExecutor() {
    }

    public static <T extends Event> void execute(Ability ability, BlightedPlayer player, T event) {
        AbilityManager<T> manager = castManager(ability.manager());

        for (CooldownEntry entry : player.getCooldowns()) {
            if (entry.abilityManager().equals(manager.getClass()) && entry.abilityType() == ability.type()) {
                double remainingSeconds = entry.getRemainingCooldownTimeInSeconds();
                if (remainingSeconds <= 0) continue;

                String abilityName = ability.name();
                String timeText = String.format("%.0fs", remainingSeconds);
                warn(player.getPlayer(), "§cYour §6" + abilityName + " §cability is on cooldown for §d" + timeText + "§c!");
                if (event instanceof Cancellable c) c.setCancelled(true);
                return;
            }
        }

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
                if (manaCost > 0) {
                    player.getMana().consumeMana(manaCost);
                    player.getActionBarManager().tick();
                }
                manager.start(player);
                startCooldown(player, manager.getClass(), ability.type(), manager.getCooldownSeconds());
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
        if (className.endsWith("Ability")) className = className.substring(0, className.length() - 8);
        if (className.endsWith("Manager")) className = className.substring(0, className.length() - 7);
        if (className.length() <= 1) return className;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < className.length(); i++) {
            char c = className.charAt(i);
            if (Character.isUpperCase(c) && i > 0) result.append(' ');
            result.append(i == 0 || Character.isUpperCase(className.charAt(i - 1)) ? Character.toUpperCase(c) : Character.toLowerCase(c));
        }
        return result.toString().trim();
    }

    @SuppressWarnings("rawtypes")
    public static void startCooldown(BlightedPlayer blightedPlayer,
                                     Class<? extends AbilityManager> abilityManagerClass,
                                     AbilityType abilityType,
                                     int cooldownSeconds) {
        CooldownEntry newEntry = new CooldownEntry(abilityManagerClass, abilityType, Instant.now().plusSeconds(cooldownSeconds));

        for (CooldownEntry entry : blightedPlayer.getCooldowns()) {
            if (entry.abilityManager().equals(abilityManagerClass) && entry.abilityType() == abilityType) return;
        }

        blightedPlayer.addCooldown(newEntry);

        Bukkit.getScheduler().runTaskLater(
                BlightedMC.getInstance(),
                () -> blightedPlayer.removeCooldown(newEntry),
                cooldownSeconds * 20L
        );
    }
}
