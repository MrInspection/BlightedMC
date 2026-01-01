package fr.moussax.blightedMC.smp.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.time.Instant;

import static fr.moussax.blightedMC.utils.formatting.Formatter.warn;

public final class AbilityExecutor {
    private AbilityExecutor() {}

    public static <T extends Event> void execute(Ability ability, BlightedPlayer player, T event) {
        AbilityManager<T> manager = castManager(ability.manager());

        for (CooldownEntry entry : player.getCooldowns()) {
            if (!entry.abilityManager().equals(manager.getClass())) continue;
            if (entry.abilityType() != ability.type()) continue;

            double remaining = entry.getRemainingCooldownTimeInSeconds();
            if (remaining <= 0) continue;

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
            startCooldown(
                player,
                manager.getClass(),
                ability.type(),
                manager.getCooldownSeconds()
            );
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

    @SuppressWarnings("rawtypes")
    public static void startCooldown(
        BlightedPlayer blightedPlayer,
        Class<? extends AbilityManager> abilityManagerClass,
        AbilityType abilityType,
        int cooldownSeconds
    ) {
        if (cooldownSeconds <= 0) return;

        CooldownEntry newEntry = new CooldownEntry(
            abilityManagerClass,
            abilityType,
            Instant.now().plusSeconds(cooldownSeconds)
        );

        blightedPlayer.getCooldowns().removeIf(cooldownEntry ->
            cooldownEntry.abilityManager().equals(abilityManagerClass)
                && cooldownEntry.abilityType() == abilityType
        );

        blightedPlayer.addCooldown(newEntry);

        Bukkit.getScheduler().runTaskLater(
            BlightedMC.getInstance(),
            () -> blightedPlayer.removeCooldown(newEntry),
            cooldownSeconds * 20L
        );
    }
}
