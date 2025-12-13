package fr.moussax.blightedMC.game.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.abilities.AbilityManager;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class KnightsSlamAbility implements AbilityManager<PlayerInteractEvent> {
    @Override
    public boolean triggerAbility(PlayerInteractEvent event) {
        new KnightSword(event.getPlayer().getTargetBlock(null, 6).getLocation(), BlightedPlayer.getBlightedPlayer(event.getPlayer()));
        return true;
    }

    @Override
    public int getCooldownSeconds() {
        return 30;
    }

    @Override
    public int getManaCost() {
        return 90;
    }

    @Override
    public boolean canTrigger(BlightedPlayer player) {
        return true;
    }

    @Override
    public void start(BlightedPlayer player) {
    }

    @Override
    public void stop(BlightedPlayer player) {
    }

    private static class KnightSword extends BukkitRunnable {
        private final Giant giant;

        private KnightSword(Location location, BlightedPlayer player) {
            List<Entity> entities = Objects.requireNonNull(location.getWorld())
                .getNearbyEntities(location, 4, 4, 4).stream()
                .filter(entity -> entity instanceof LivingEntity && !(entity instanceof ArmorStand))
                .filter(entity -> !entity.equals(player.getPlayer()))
                .toList();

            double abilityDamage = 50;
            int enemies = 0;

            for (Entity entity : entities) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.damage(abilityDamage);
                enemies++;
            }

            if (enemies > 0) {
                player.getPlayer().sendMessage("§5 ■ §dYour §5Knight's Slam §dability hit §c" + enemies + "§d enem" + (enemies > 1 ? "ies" : "y") + " for §c" + abilityDamage + " §ddamage!");
            }

            this.runTaskLater(BlightedMC.getInstance(), 100);

            Location targetLocation = player.getPlayer().getLocation().clone();
            var world = targetLocation.getWorld();

            targetLocation.setPitch(0);
            targetLocation.setYaw(0);
            targetLocation.subtract(2, 1, 4);
            Objects.requireNonNull(world).spawnParticle(Particle.EXPLOSION_EMITTER, targetLocation, 1);
            world.playSound(targetLocation, Sound.BLOCK_ANVIL_LAND, 1f, 0f);

            giant = player.getPlayer().getWorld().spawn(targetLocation, Giant.class, g -> {
                g.setAI(false);
                g.setCustomName("Dinnerbone");
                g.setCustomNameVisible(false);
                g.setInvisible(true);
                g.setInvulnerable(true);
                g.setSilent(true);
                Objects.requireNonNull(g.getEquipment()).setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
                g.setGravity(false);
            });
        }

        @Override
        public void run() {
            giant.remove();
        }
    }
}
