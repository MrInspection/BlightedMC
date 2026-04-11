package fr.moussax.blightedMC.content.entities.bosses;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.engine.entities.EntityImmunities;
import fr.moussax.blightedMC.engine.entities.BlightedType;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@EntityImmunities(EntityImmunities.ImmunityType.PROJECTILE)
public class TheAncientKnight extends AbstractBlightedEntity {

    private final List<StabPlayer> activeStabs = new CopyOnWriteArrayList<>();

    public TheAncientKnight() {
        super("The Ancient Knight", 250, 30, EntityType.ZOMBIE);
        addAttribute(Attribute.SCALE, 4);
        setBlightedType(BlightedType.BOSS);
        setPersistent(true);

        armor = new ItemStack[]{
            new ItemStack(Material.NETHERITE_BOOTS),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            new ItemStack(Material.NETHERITE_HELMET)
        };

        itemInMainHand = new ItemBuilder(Material.NETHERITE_SWORD).addEnchantmentGlint().toItemStack();
    }

    @Override
    protected void onDefineBehavior() {
        transitionToPhase(1);
    }

    @Override
    protected void onPhaseTransition(int phase) {
        if (phase == 1) {
            // Phase 1 — slow stab every 15s
            addAbility(100L, 300L, this::stabNearestPlayer);
        } else if (phase == 2) {
            // Phase 2 (≤50% HP) — faster stab + melee targeting
            addAbility(60L, 180L, this::stabNearestPlayer);
            addAbility(20L, 40L, this::meleeNearestPlayer);
        }
    }

    @Override
    public void onDamageTaken(EntityDamageEvent event) {
        double remaining = entity.getHealth() - event.getFinalDamage();
        if (remaining <= maxHealth * 0.5 && getCurrentPhase() < 2) {
            transitionToPhase(2);
        }
    }

    private void stabNearestPlayer() {
        Player target = getNearestPlayer(20);
        if (target == null) return;
        activeStabs.add(new StabPlayer(BlightedPlayer.getBlightedPlayer(target), this));
    }

    private void meleeNearestPlayer() {
        Player target = getNearestPlayer(5);
        if (target == null) return;
        meleeAttack(target);
    }

    @Override
    public String getEntityId() {
        return "DIAMOND_GIANT";
    }

    @Override
    public TheAncientKnight clone() {
        TheAncientKnight clone = (TheAncientKnight) super.clone();
        clone.activeStabs.clear();
        return clone;
    }

    @Override
    public LivingEntity spawn(Location location) {
        super.spawn(location);
        if (entity instanceof Zombie zombie) zombie.setBaby(false);
        return entity;
    }

    @Override
    public void onDeath(Location location) {
        stopAllStabs();
    }

    private void stopAllStabs() {
        for (StabPlayer stab : new ArrayList<>(activeStabs)) {
            try { stab.cancel(); } catch (IllegalStateException ignored) {}
        }
        activeStabs.clear();
    }

    // -------------------------------------------------------------------------

    private static class StabPlayer extends BukkitRunnable {
        private final BlightedPlayer target;
        private final TheAncientKnight owner;
        private Location stabledLocation;
        private Giant swordEntity;
        private int tick = 0;

        StabPlayer(BlightedPlayer target, TheAncientKnight owner) {
            this.target = target;
            this.owner = owner;
            summonSwordEntity();
            runTaskTimer(BlightedMC.getInstance(), 1L, 1L);
        }

        private void summonSwordEntity() {
            Location spawnLocation = target.getPlayer().getLocation().clone();
            spawnLocation.setPitch(0);
            spawnLocation.setYaw(0);

            swordEntity = target.getPlayer().getWorld().spawn(spawnLocation, Giant.class, g -> {
                g.setAI(false);
                g.setCustomName("Dinnerbone");
                g.setCustomNameVisible(false);
                g.setInvisible(true);
                g.setInvulnerable(true);
                g.setSilent(true);
                g.setGravity(false);
                Objects.requireNonNull(g.getEquipment())
                    .setItemInMainHand(new ItemBuilder(Material.NETHERITE_SWORD).addEnchantmentGlint().toItemStack());
            });
        }

        @Override
        public void run() {
            if (owner.isNotAlive() || !target.getPlayer().isOnline()) {
                cancel();
                return;
            }

            if (tick == 0) {
                stabledLocation = target.getPlayer().getLocation();
                Objects.requireNonNull(stabledLocation.getWorld())
                    .playSound(stabledLocation, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1f, 0.75f);
            }

            if (tick < 90) {
                trackSwordAbovePlayer();
                stabledLocation = target.getPlayer().getLocation();
            } else if (tick == 101) {
                plungeAndDamage();
            } else if (tick > 200) {
                cancel();
            }

            tick++;
        }

        private void trackSwordAbovePlayer() {
            if (swordEntity == null || swordEntity.isDead()) return;
            Location above = target.getPlayer().getLocation().clone();
            above.setPitch(0);
            above.setYaw(0);
            above.subtract(2, -4, 4);
            swordEntity.teleport(above);
        }

        private void plungeAndDamage() {
            if (swordEntity != null && !swordEntity.isDead()) {
                Location plungeLocation = target.getPlayer().getLocation().clone();
                plungeLocation.setPitch(0);
                plungeLocation.setYaw(0);
                plungeLocation.subtract(2, 1, 4);
                swordEntity.teleport(plungeLocation);
            }

            World world = Objects.requireNonNull(stabledLocation.getWorld());
            world.spawnParticle(Particle.EXPLOSION_EMITTER, stabledLocation, 1);
            world.playSound(stabledLocation, Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);

            world.getNearbyEntities(stabledLocation, 6, 6, 6).stream()
                .filter(e -> e instanceof Player p && p.getGameMode() == GameMode.SURVIVAL)
                .map(e -> (Player) e)
                .forEach(player -> player.damage(16, swordEntity));
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            if (swordEntity != null && !swordEntity.isDead()) swordEntity.remove();
            owner.activeStabs.remove(this);
        }
    }
}
