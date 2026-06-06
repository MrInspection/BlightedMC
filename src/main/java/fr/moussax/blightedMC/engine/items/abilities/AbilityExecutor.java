package fr.moussax.blightedMC.engine.items.abilities;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.Sound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import static fr.moussax.blightedMC.shared.formatting.Formatter.warn;

public final class AbilityExecutor {
    private AbilityExecutor() {}

    public static <T extends Event> void execute(Ability ability, BlightedPlayer player, T event) {
        AbilityManager<T> manager = castManager(ability.manager());

        double remaining = player.getRemainingCooldown(manager.getClass(), ability.type());
        if (remaining > 0) {
            warn(
                    player.getPlayer(),
                    "§cYour §f" + ability.name()
                            + " §cability is on cooldown for §d"
                            + (int) Math.ceil(remaining) + "s§c!"
            );
            cancel(event);
            return;
        }

        if (!manager.canTrigger(player)) {
            cancel(event);
            return;
        }

        int manaCost = manager.getManaCost();
        if (player.getMana().getCurrentMana() < manaCost) {
            player.getPlayer().playSound(
                    player.getPlayer().getLocation(),
                    Sound.ENTITY_ENDERMAN_TELEPORT,
                    100f,
                    0.5f
            );
            player.getActionBarManager().setInsufficientMana(true);
            cancel(event);
            return;
        }

        try {
            boolean success = manager.triggerAbility(event);
            if (!success) {
                cancel(event);
                return;
            }

            cancel(event);

            if (manaCost > 0) {
                player.getMana().consumeMana(manaCost);
                player.getActionBarManager().tick();
            }

            manager.start(player);

            if (manager.getCooldownSeconds() > 0) {
                player.setCooldown(manager.getClass(), ability.type(), manager.getCooldownSeconds());
            }
        } catch (Exception e) {
            Log.error("§cAbility execution failed: " + e.getClass().getSimpleName());
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
