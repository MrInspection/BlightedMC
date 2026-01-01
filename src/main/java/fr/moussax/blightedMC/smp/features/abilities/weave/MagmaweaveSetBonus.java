package fr.moussax.blightedMC.smp.features.abilities.weave;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public final class MagmaweaveSetBonus extends EmberWeaveSetBonus {
    private int taskCounter = 0;

    @Override
    public void startAbilityEffect() {
        if (isActive) return;

        Player player = blightedPlayer.getPlayer();
        if (player == null) return;

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 2.0f, 0.5f);

        this.isActive = true;
        this.taskCounter = 0;
        this.passiveTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !isActive) {
                    this.cancel();
                    taskCounter = 0;
                    if (player.isOnline()) {
                        player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                        player.removePotionEffect(PotionEffectType.RESISTANCE);
                    }
                    return;
                }
                taskCounter++;

                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 70, 0, true, false, true));

                if (player.getLocation().getBlock().isLiquid() && player.getLocation().getBlock().getType() == Material.LAVA) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0, true, false, true));
                    if (taskCounter % 10 == 0) {
                        player.getWorld().spawnParticle(
                            Particle.FALLING_OBSIDIAN_TEAR,
                            player.getLocation().add(0, 1.2, 0),
                            1, 0.2, 0.1, 0.2, 0
                        );
                    }
                }

                player.getWorld().spawnParticle(
                    Particle.FLAME,
                    player.getLocation().add(0, 0.4, 0),
                    2, 0.3, 0.1, 0.3, 0.02
                );
            }
        }.runTaskTimer(BlightedMC.getInstance(), 0L, 5L);
    }
}
