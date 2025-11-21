package fr.moussax.blightedMC.core.entities.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.EntityAttachment;
import fr.moussax.blightedMC.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlightedEntitiesListener implements Listener {
    private static final Map<UUID, BlightedEntity> BLIGHTED_ENTITIES = new HashMap<>();
    private final ThreadLocal<Set<UUID>> processingDamageEntityIds = ThreadLocal.withInitial(HashSet::new);

    public static void registerEntity(LivingEntity entity, BlightedEntity blighted) {
        if (entity == null || blighted == null) return;
        BLIGHTED_ENTITIES.put(entity.getUniqueId(), blighted);
    }

    public static BlightedEntity getBlightedEntity(Entity entity) {
        if (entity == null) return null;
        return BLIGHTED_ENTITIES.get(entity.getUniqueId());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (processingDamageEntityIds.get().contains(entity.getUniqueId())) return;

        processingDamageEntityIds.get().add(entity.getUniqueId());

        try {
            EntityAttachment attachment = BlightedEntity.getAttachment(entity);
            if (attachment != null) {
                handleAttachmentDamage(attachment, event);
                return;
            }

            BlightedEntity blighted = BLIGHTED_ENTITIES.get(entity.getUniqueId());
            if (blighted != null) {
                handleBlightedEntityDamage(blighted, entity, event);
            }
        } finally {
            processingDamageEntityIds.get().remove(entity.getUniqueId());
            if (processingDamageEntityIds.get().isEmpty()) {
                processingDamageEntityIds.remove();
            }
        }
    }

    private void handleAttachmentDamage(EntityAttachment attachment, EntityDamageEvent event) {
        LivingEntity ownerEntity = attachment.owner().getEntity();
        if (ownerEntity == null || ownerEntity.isDead()) return;

        ownerEntity.damage(event.getFinalDamage(), getRealDamager(event));
        syncArmor((LivingEntity) attachment.entity(), ownerEntity);
    }

    private void handleBlightedEntityDamage(BlightedEntity blighted, LivingEntity entity, EntityDamageEvent event) {
        forwardDamageToAttachments(blighted, event);

        if (event instanceof EntityDamageByEntityEvent damageByEntity) {
            if (handleImmunity(blighted, entity, damageByEntity)) {
                return;
            }
        }

        double damage = event.getFinalDamage();
        double remainingHealth = entity.getHealth() + entity.getAbsorptionAmount() - damage;

        if (shouldPreventDeath(blighted, remainingHealth)) {
            event.setCancelled(true);
            entity.setHealth(1.0);
            blighted.updateNameTag();
            return;
        }

        if (remainingHealth > 0) {
            blighted.updateNameTag();
        } else {
            blighted.killAllAttachments();
        }

        scheduleNameTagUpdate(blighted, entity);
    }

    private void forwardDamageToAttachments(BlightedEntity blighted, EntityDamageEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        for (EntityAttachment attachment : new ArrayList<>(blighted.attachments)) {
            if (attachment.entity() instanceof LivingEntity living && !living.isDead()) {
                living.damage(event.getFinalDamage(), getRealDamager(event));
                syncArmor(living, entity);
            }
        }
    }

    private boolean handleImmunity(BlightedEntity blighted, LivingEntity entity, EntityDamageByEntityEvent event) {
        if (!blighted.isImmuneTo(entity, event)) return false;

        event.setCancelled(true);
        Player player = getPlayerDamager(event.getDamager());
        if (player != null) {
            player.sendMessage("§4 ■ §cThis creature is immune to this type of damage!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 0.6f);
        }
        return true;
    }

    private boolean shouldPreventDeath(BlightedEntity blighted, double remainingHealth) {
        if (remainingHealth > 0) return false;
        return blighted.attachments.stream()
                .anyMatch(att -> att.entity() instanceof LivingEntity living && !living.isDead());
    }

    private void scheduleNameTagUpdate(BlightedEntity blighted, LivingEntity entity) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!entity.isDead()) {
                    blighted.updateNameTag();
                }
            }
        }.runTaskLater(BlightedMC.getInstance(), 1L);
    }

    @EventHandler
    public void onEntityHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        EntityAttachment attachment = BlightedEntity.getAttachment(entity);
        if (attachment != null) {
            handleAttachmentHeal(attachment, entity, event);
            return;
        }

        BlightedEntity blighted = BLIGHTED_ENTITIES.get(entity.getUniqueId());
        if (blighted != null) {
            handleBlightedEntityHeal(blighted, entity, event);
        }
    }

    private void handleAttachmentHeal(EntityAttachment attachment, LivingEntity entity, EntityRegainHealthEvent event) {
        LivingEntity ownerEntity = attachment.owner().getEntity();
        if (ownerEntity == null || ownerEntity.isDead()) return;

        double newHealth = calculateNewHealth(entity, event.getAmount());
        ownerEntity.setHealth(newHealth);
        entity.setHealth(newHealth);
        syncArmor(entity, ownerEntity);
    }

    private void handleBlightedEntityHeal(BlightedEntity blighted, LivingEntity entity, EntityRegainHealthEvent event) {
        blighted.updateNameTag();
        double newHealth = calculateNewHealth(entity, event.getAmount());

        for (EntityAttachment attachment : new ArrayList<>(blighted.attachments)) {
            if (attachment.entity() instanceof LivingEntity living && !living.isDead()) {
                living.setHealth(newHealth);
                syncArmor(living, entity);
            }
        }
    }

    private double calculateNewHealth(LivingEntity entity, double healAmount) {
        double maxHealth = Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue();
        return Math.min(entity.getHealth() + healAmount, maxHealth);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity dead = event.getEntity();
        EntityAttachment attachment = BlightedEntity.getAttachment(dead);

        if (attachment != null) {
            handleAttachmentDeath(attachment, event);
            return;
        }

        BlightedEntity blighted = BLIGHTED_ENTITIES.get(dead.getUniqueId());
        if (blighted != null) {
            handleBlightedEntityDeath(blighted, dead, event);
        }
    }

    private void handleAttachmentDeath(EntityAttachment attachment, EntityDeathEvent event) {
        LivingEntity owner = attachment.owner().getEntity();
        if (owner != null && !owner.isDead()) {
            owner.setHealth(0);
            BLIGHTED_ENTITIES.remove(owner.getUniqueId());
        }

        try {
            BlightedEntity.unregisterAttachment(attachment);
        } catch (Throwable ignored) {
        }

        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    private void handleBlightedEntityDeath(BlightedEntity blighted, LivingEntity dead, EntityDeathEvent event) {
        blighted.killAllAttachments();
        blighted.removeBossBar();

        Player killer = dead.getKiller();
        BlightedPlayer player = (killer != null) ? BlightedPlayer.getBlightedPlayer(killer) : null;
        blighted.dropLoot(dead.getLocation(), player);

        BLIGHTED_ENTITIES.remove(dead.getUniqueId());

        event.getDrops().clear();
        event.setDroppedExp(blighted.getDroppedExp());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                rehydrateChunk(event.getChunk());
            }
        }.runTaskLater(BlightedMC.getInstance(), 2L);
    }

    public static void rehydrateChunk(Chunk chunk) {
        NamespacedKey key = new NamespacedKey(BlightedMC.getInstance(), "entityId");
        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (BLIGHTED_ENTITIES.containsKey(living.getUniqueId())) continue;

            PersistentDataContainer pdc = living.getPersistentDataContainer();
            String entityId = pdc.get(key, PersistentDataType.STRING);
            if (entityId == null || entityId.isEmpty()) continue;

            BlightedEntity prototype = EntitiesRegistry.getEntity(entityId);
            if (prototype == null) continue;

            BlightedEntity instance = createInstance(prototype);
            instance.attachToExisting(living);

            syncAttachmentsHealthAndArmor(instance, living);
        }
    }

    private static BlightedEntity createInstance(BlightedEntity prototype) {
        try {
            return prototype.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            return prototype.clone();
        }
    }

    private static void syncAttachmentsHealthAndArmor(BlightedEntity instance, LivingEntity owner) {
        for (EntityAttachment attachment : new ArrayList<>(instance.attachments)) {
            if (attachment.entity() instanceof LivingEntity living && !living.isDead()) {
                living.setHealth(owner.getHealth());
                syncArmor(living, owner);
            }
        }
    }

    private Player getPlayerDamager(Entity damager) {
        if (damager instanceof Player player) return player;
        if (damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player player) return player;
        }
        return null;
    }

    private Entity getRealDamager(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent damageByEntity) {
            return damageByEntity.getDamager();
        }
        return null;
    }

    private static void syncArmor(LivingEntity target, LivingEntity source) {
        EntityEquipment sourceEquipment = source.getEquipment();
        EntityEquipment targetEquipment = target.getEquipment();
        if (sourceEquipment == null || targetEquipment == null) return;

        targetEquipment.setHelmet(sourceEquipment.getHelmet());
        targetEquipment.setChestplate(sourceEquipment.getChestplate());
        targetEquipment.setLeggings(sourceEquipment.getLeggings());
        targetEquipment.setBoots(sourceEquipment.getBoots());
        targetEquipment.setItemInMainHand(sourceEquipment.getItemInMainHand());
        targetEquipment.setItemInOffHand(sourceEquipment.getItemInOffHand());
    }
}
