package fr.moussax.blightedMC.gameplay.entities.bosses;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.EntityNameTag;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TheAncientKnight extends BlightedEntity {
    private BukkitRunnable abilityRunnable;
    public ArrayList<StabPlayer> swords = new ArrayList<>();

    public TheAncientKnight() {
        super("The Ancient Knight", 250, 30, EntityType.ZOMBIE);
        addAttribute(Attribute.SCALE, 4.5);
        setNameTagType(EntityNameTag.BOSS);

        armor = new ItemStack[]{
                new ItemStack(Material.NETHERITE_BOOTS),
                new ItemStack(Material.NETHERITE_LEGGINGS),
                new ItemStack(Material.NETHERITE_CHESTPLATE),
                new ItemStack(Material.NETHERITE_HELMET)
        };

        itemInMainHand = new ItemBuilder(Material.NETHERITE_SWORD).addEnchantmentGlint().toItemStack();

        addRepeatingTask(() -> {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity == null || entity.isDead()) {
                        stopAbility();
                        cancel();
                        return;
                    }

                    List<Player> nearbyPlayers = entity.getNearbyEntities(20, 20, 20).stream()
                            .filter(e -> e instanceof Player p && p.getGameMode() == GameMode.SURVIVAL)
                            .map(e -> (Player) e)
                            .toList();
                    if (nearbyPlayers.isEmpty()) return;

                    BlightedPlayer target = BlightedPlayer.getBlightedPlayer(nearbyPlayers.getFirst());
                    stabAbility(target);
                }
            };
            abilityRunnable = task;
            return task;
        }, 100L, 300L);
    }

    @Override
    public String getEntityId() {
        return "DIAMOND_GIANT";
    }

    @Override
    public TheAncientKnight clone() {
        TheAncientKnight clone = (TheAncientKnight) super.clone();
        clone.abilityRunnable = null;
        clone.swords = new ArrayList<>();

        clone.addRepeatingTask(() -> {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (clone.entity == null || clone.entity.isDead()) {
                        clone.stopAbility();
                        cancel();
                        return;
                    }

                    List<Player> nearbyPlayers = clone.entity.getNearbyEntities(20, 20, 20).stream()
                            .filter(e -> e instanceof Player p && p.getGameMode() == GameMode.SURVIVAL)
                            .map(e -> (Player) e)
                            .toList();
                    if (nearbyPlayers.isEmpty()) return;

                    BlightedPlayer target = BlightedPlayer.getBlightedPlayer(nearbyPlayers.getFirst());
                    clone.stabAbility(target);
                }
            };
            clone.abilityRunnable = task;
            return task;
        }, 100L, 300L);

        return clone;
    }

    @Override
    public LivingEntity spawn(Location location) {
        super.spawn(location);
        if (entity instanceof Zombie) {
            ((Zombie) entity).setBaby(false);
        }
        return entity;
    }

    @Override
    public void kill() {
        stopAbility();
        super.kill();
    }

    private void stabAbility(BlightedPlayer target) {
        swords.add(new StabPlayer(target, this));
    }

    private void stopAbility() {
        if (abilityRunnable != null) {
            try {
                abilityRunnable.cancel();
            } catch (IllegalStateException ignored) {
            }
            abilityRunnable = null;
        }

        for (StabPlayer sword : new ArrayList<>(swords)) {
            try {
                sword.cancel();
            } catch (IllegalStateException ignored) {
            }
        }
        swords.clear();
    }

    private static class StabPlayer extends BukkitRunnable {
        private final BlightedPlayer blightedPlayer;
        private final TheAncientKnight giant;
        private Location location;
        private Giant sword;
        private int runTime = 0;

        public StabPlayer(BlightedPlayer player, TheAncientKnight giant) {
            this.blightedPlayer = player;
            this.giant = giant;
            summonGiantSword();
            this.runTaskTimer(BlightedMC.getInstance(), 1, 1);
        }

        private void summonGiantSword() {
            Location targetLocation = blightedPlayer.getPlayer().getLocation().clone();
            targetLocation.setPitch(0);
            targetLocation.setYaw(0);

            sword = blightedPlayer.getPlayer().getWorld().spawn(targetLocation, Giant.class, g -> {
                g.setAI(false);
                g.setCustomName("Dinnerbone");
                g.setCustomNameVisible(false);
                g.setInvisible(true);
                g.setInvulnerable(true);
                g.setSilent(true);
                Objects.requireNonNull(g.getEquipment())
                        .setItemInMainHand(new ItemBuilder(Material.NETHERITE_SWORD).addEnchantmentGlint().toItemStack());
                g.setGravity(false);
            });
        }

        @Override
        public void run() {
            if (giant.entity == null || giant.entity.isDead()
                    || blightedPlayer.getPlayer() == null || !blightedPlayer.getPlayer().isOnline()) {
                cancel();
                return;
            }

            if (runTime == 0) {
                location = blightedPlayer.getPlayer().getLocation();
                Objects.requireNonNull(location.getWorld())
                        .playSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1f, 0.75f);
            }

            if (runTime < 90) {
                Location swordLocation = blightedPlayer.getPlayer().getLocation().clone();
                swordLocation.setPitch(0);
                swordLocation.setYaw(0);
                swordLocation.subtract(2, -4, 4);
                if (sword != null && !sword.isDead()) sword.teleport(swordLocation);
                location = blightedPlayer.getPlayer().getLocation();
            } else if (runTime == 101) {
                Location swordLocation = blightedPlayer.getPlayer().getLocation().clone();
                swordLocation.setPitch(0);
                swordLocation.setYaw(0);
                swordLocation.subtract(2, 1, 4);
                if (sword != null && !sword.isDead()) sword.teleport(swordLocation);

                Objects.requireNonNull(location.getWorld())
                        .spawnParticle(Particle.EXPLOSION_EMITTER, location, 1);

                List<Player> nearbyPlayers = location.getWorld()
                        .getNearbyEntities(location, 6, 6, 6).stream()
                        .filter(e -> e instanceof Player p && p.getGameMode() == GameMode.SURVIVAL)
                        .map(e -> (Player) e)
                        .toList();

                for (Player nearbyPlayer : nearbyPlayers) {
                    BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(nearbyPlayer);
                    blightedPlayer.getPlayer().damage(16, sword);
                }

                location.getWorld().playSound(location, Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);
            } else if (runTime > 200) {
                cancel();
            }

            runTime++;
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            if (sword != null && !sword.isDead()) sword.remove();
            giant.swords.remove(this);
        }
    }
}
