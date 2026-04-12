package fr.moussax.blightedMC.content.entities.powerful;

import fr.moussax.blightedMC.content.entities.Watchling;
import fr.moussax.blightedMC.content.utils.ai.EndermanAI;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.BlightedType;
import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.engine.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity.*;

public class Endersent extends SpawnableEntity {

    private static final long SMASH_COOLDOWN = 5000;
    private int projectileHits = 0;
    private boolean enraged = false;
    private boolean isEscaping = false;
    private List<LivingEntity> watchlings = new ArrayList<>();
    private long lastTeleportSmash = 0;
    private int escapeTicks = 0;

    public Endersent() {
        super("ENDERSENT", "Endersent", 200, EntityType.ENDERMAN, 0.002);
        setDamage(20);
        setDroppedExp(40);

        addAttribute(Attribute.FOLLOW_RANGE, 60);
        addAttribute(Attribute.SCALE, 2);
        addAttribute(Attribute.KNOCKBACK_RESISTANCE, 1.0);
        addAttribute(Attribute.MOVEMENT_SPEED, 0.25);

        setLootTable(new EntityLootTableBuilder()
            .setMaxDrop(2)
            .addLoot(Material.ENDER_PEARL, 4, 8, 1.0, COMMON)
            .addLoot(Material.ENDER_EYE, 1, 3, 0.31, UNCOMMON)
            .addLoot("ENCHANTED_ENDER_PEARL", 1, 4, 0.11, RARE)
            .addGemsLoot(30, 0.03, VERY_RARE)
            .build()
        );

        setBlightedType(BlightedType.BOSS);
    }

    @Override
    protected void onDefineBehavior() {
        addAbility(20L, 20L, this::tickCombat);
    }

    private void tickCombat() {
        tickSmash();
        tickEscape();
    }

    private void tickSmash() {
        if (!enraged || isEscaping) return;
        if (System.currentTimeMillis() - lastTeleportSmash < SMASH_COOLDOWN) return;

        Player target = getNearestPlayer(30);
        if (target == null) return;

        if (entity.getLocation().distance(target.getLocation()) > 4) {
            performTeleportSmash(target);
        }
    }

    private void performTeleportSmash(Player target) {
        lastTeleportSmash = System.currentTimeMillis();

        entity.swingMainHand();

        Location behind = target.getLocation().add(target.getLocation().getDirection().multiply(-1.5));
        behind.setY(target.getLocation().getY());

        entity.teleport(behind);
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        Utilities.delay(() -> {
            if (isNotAlive()) return;
            entity.getWorld().spawnParticle(Particle.EXPLOSION, entity.getLocation(), 1);
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);

            for (Entity nearby : entity.getNearbyEntities(3, 2, 3)) {
                if (!(nearby instanceof LivingEntity living) || living == entity) continue;
                if (nearby instanceof Player p && p.getGameMode() == GameMode.SPECTATOR) continue;
                if (BlightedEntitiesListener.getBlightedEntity(living) instanceof Watchling) continue;
                living.damage(14, entity);
            }
        }, 9);
    }

    private void tickEscape() {
        if (!isEscaping) return;

        escapeTicks += 20;
        watchlings.removeIf(e -> e.isDead() || !e.isValid());

        if (watchlings.isEmpty() || escapeTicks >= 300) {
            endDeadlyEscape();
        }
    }

    @Override
    public void onDamageTaken(EntityDamageEvent event) {
        if (enraged || isEscaping) return;

        boolean trigger = false;
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            trigger = true;
        } else if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            projectileHits++;
            if (projectileHits >= 7) trigger = true;
        }

        if (trigger) startDeadlyEscape();
    }

    private void startDeadlyEscape() {
        enraged = true;
        isEscaping = true;
        escapeTicks = 0;

        Location loc = entity.getLocation();

        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);

        for (Entity nearby : entity.getNearbyEntities(4, 3, 4)) {
            if (!(nearby instanceof LivingEntity living) || living == entity) continue;
            if (BlightedEntitiesListener.getBlightedEntity(living) instanceof Watchling) continue;
            living.damage(18, entity);
        }

        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.setAI(false);
        entity.teleport(loc.clone().add(0, 50, 0));

        int count = 3 + new Random().nextInt(4);
        for (int i = 0; i < count; i++) {
            BlightedEntity prototype = EntitiesRegistry.get("WATCHLING");
            if (prototype == null) continue;
            LivingEntity wEntity = prototype.spawn(
                loc.clone().add((Math.random() - 0.5) * 2, 0, (Math.random() - 0.5) * 2)
            );
            watchlings.add(wEntity);
        }
    }

    private void endDeadlyEscape() {
        if (!isEscaping) return;
        isEscaping = false;

        for (LivingEntity watchling : watchlings) {
            if (!watchling.isDead()) {
                watchling.setHealth(0);
                watchling.remove();
            }
        }
        watchlings.clear();

        Player target = getNearestPlayer(60);
        Location targetLoc = (target != null) ? target.getLocation() : entity.getLocation().subtract(0, 50, 0);
        Location reappearLoc = targetLoc.clone().add((Math.random() - 0.5) * 4, 0, (Math.random() - 0.5) * 4);
        reappearLoc.setY(targetLoc.getY());

        entity.teleport(reappearLoc);
        entity.setInvisible(false);
        entity.setInvulnerable(false);
        entity.setAI(true);

        entity.getWorld().playSound(reappearLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
    }

    @Override
    protected void onConfigureAI(LivingEntity spawned) {
        EndermanAI.init(spawned);
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnRules.biome(Biome.END_MIDLANDS));
    }

    @Override
    public Endersent clone() {
        Endersent clone = (Endersent) super.clone();
        clone.watchlings = new ArrayList<>();
        clone.projectileHits = 0;
        clone.enraged = false;
        clone.isEscaping = false;
        clone.lastTeleportSmash = 0;
        clone.escapeTicks = 0;
        return clone;
    }
}
