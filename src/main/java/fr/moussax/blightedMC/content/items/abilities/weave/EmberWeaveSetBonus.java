package fr.moussax.blightedMC.content.items.abilities.weave;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

public sealed class EmberWeaveSetBonus implements FullSetBonus permits MagmaweaveSetBonus {
    protected BlightedPlayer blightedPlayer;
    protected BukkitTask passiveTask;
    protected boolean isActive = false;

    @Override
    public String getName() {
        return "Molten Attunement";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Impervious to the inferno, your",
                "heat signature synchronizes",
                "with §6molten currents§7.",
                "",
                "Grants immunity to §cfire §7and §clava§7.",
                "Grants §b+15% §3Lava Fishing Speed§7."
        };
    }

    @Override
    public void startAbilityEffect() {
        if (isActive || blightedPlayer == null) return;

        Player player = blightedPlayer.getPlayer();
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 0.5f);

        this.isActive = true;
        this.passiveTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !isActive) {
                    this.cancel();
                    return;
                }
                player.addPotionEffect(
                        new PotionEffect(
                                PotionEffectType.FIRE_RESISTANCE,
                                70,
                                0,
                                true,
                                false,
                                true
                        )
                );

                player.getWorld().spawnParticle(
                        Particle.SMALL_FLAME,
                        player.getLocation(),
                        2,
                        0.3,
                        0.1,
                        0.3,
                        0.02
                );

                if (ThreadLocalRandom.current().nextBoolean()) {
                    player.getWorld().spawnParticle(
                            Particle.SMOKE,
                            player.getLocation().add(0, 0.2, 0),
                            2,
                            0.2, 0.3, 0.2,
                            0.01
                    );
                }
            }
        }.runTaskTimer(BlightedMC.getInstance(), 0L, 20L);
    }

    @Override
    public void stopAbilityEffect() {
        if (!isActive) return;

        this.isActive = false;

        if (passiveTask != null) {
            passiveTask.cancel();
            passiveTask = null;
        }

        if (blightedPlayer == null) return;
        Player player = blightedPlayer.getPlayer();
        player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
    }

    @Override
    public int getPieces() {
        return 4;
    }

    @Override
    public int getMaxPieces() {
        return 4;
    }

    @Override
    public void setPlayer(BlightedPlayer player) {
        this.blightedPlayer = player;
    }

    @Override
    public BlightedPlayer getAbilityOwner() {
        return this.blightedPlayer;
    }
}
