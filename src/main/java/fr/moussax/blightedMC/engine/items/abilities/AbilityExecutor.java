package fr.moussax.blightedMC.engine.items.abilities;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.engine.player.hud.PlayerHudManager;
import fr.moussax.blightedMC.shared.ui.actionbar.ActionbarService;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.Sound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.time.Duration;

import static fr.moussax.blightedMC.shared.text.Messenger.warn;

/**
 * Validates player resources, checks active cooldowns, and executes custom item abilities.
 */
public final class AbilityExecutor {

    private AbilityExecutor() {
    }

    /**
     * Evaluates cooldowns, verifies mana sufficiency, and executes the specified ability.
     *
     * @param <T>     event type
     * @param ability ability to execute
     * @param player  player context triggering the ability
     * @param event   triggering Bukkit event
     */
    public static <T extends Event> void execute(Ability ability, BlightedPlayer player, T event) {
        AbilityManager<T> manager = castManager(ability.manager());

        double remaining = player.getRemainingCooldown(manager.getClass(), ability.type());
        if (remaining > 0) {
            warn(player.getPlayer(), "§c⌚ Your §f" + ability.name() + " §cability is on cooldown for §d" + (int) Math.ceil(remaining) + "s§c!");
            cancel(event);
            return;
        }

        if (!manager.canTrigger(player)) {
            cancel(event);
            return;
        }

        int manaCost = manager.getManaCost();
        if (!player.hasMana(manaCost)) {
            player.getPlayer().playSound(
                    player.getPlayer().getLocation(),
                    Sound.ENTITY_ENDERMAN_TELEPORT,
                    100f,
                    0.5f
            );
            ActionbarService.ifPresent(service -> service.sendSlotAlert(
                    player.getPlayer(), PlayerHudManager.SECTION_MANA, "§c§lNOT ENOUGH MANA", Duration.ofSeconds(2))
            );
            cancel(event);
            return;
        }

        try {
            boolean success = manager.triggerAbility(event);
            if (!success) {
                if (manager.cancelEvent(false)) cancel(event);
                return;
            }

            if (manager.cancelEvent(true)) cancel(event);

            if (manaCost > 0) {
                player.consumeMana(manaCost);
                ActionbarService.ifPresent(service -> service.renderPlayer(player.getPlayer()));
            }

            manager.start(player);

            if (manager.getCooldownSeconds() > 0) {
                player.setCooldown(manager.getClass(), ability.type(), manager.getCooldownSeconds());
            }
        } catch (Exception exception) {
            Log.error("AbilityExecutor", "Ability execution failed: " + exception.getClass().getSimpleName());
            cancel(event);
        }
    }

    private static void cancel(Event event) {
        if (event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> AbilityManager<T> castManager(AbilityManager<?> manager) {
        return (AbilityManager<T>) manager;
    }
}
