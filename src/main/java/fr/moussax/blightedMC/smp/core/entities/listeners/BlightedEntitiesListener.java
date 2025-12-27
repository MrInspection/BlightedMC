package fr.moussax.blightedMC.smp.core.entities.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.entities.EntityAttachment;
import fr.moussax.blightedMC.smp.core.entities.immunity.EntityImmunity;
import fr.moussax.blightedMC.smp.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

import java.util.*;

import static fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity.ENTITY_ID_KEY;

public class BlightedEntitiesListener implements Listener {
    private static final Map<UUID, AbstractBlightedEntity> BLIGHTED_ENTITIES = new HashMap<>();
    private final ThreadLocal<Set<UUID>> processingDamageEntityIds = ThreadLocal.withInitial(HashSet::new);

    public static void registerEntity(LivingEntity entity, AbstractBlightedEntity blighted) {
        if (entity == null || blighted == null) return;
        BLIGHTED_ENTITIES.put(entity.getUniqueId(), blighted);
    }

    public static AbstractBlightedEntity getBlightedEntity(Entity entity) {
        if (entity == null) return null;
        return BLIGHTED_ENTITIES.get(entity.getUniqueId());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (processingDamageEntityIds.get().contains(entity.getUniqueId())) return;

        processingDamageEntityIds.get().add(entity.getUniqueId());

        try {
            EntityAttachment attachment = AbstractBlightedEntity.getAttachment(entity);
            if (attachment != null) {
                handleAttachmentDamage(attachment, event);
                return;
            }

            AbstractBlightedEntity blighted = BLIGHTED_ENTITIES.get(entity.getUniqueId());
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

    private void handleBlightedEntityDamage(AbstractBlightedEntity blighted, LivingEntity entity, EntityDamageEvent event) {
        forwardDamageToAttachments(blighted, event);

        if (event instanceof EntityDamageByEntityEvent damageByEntity) {
            if (handleImmunity(blighted, entity, damageByEntity)) {
                return;
            }
        }

        blighted.onDamageTaken(event);

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

    private void forwardDamageToAttachments(AbstractBlightedEntity blighted, EntityDamageEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        for (EntityAttachment attachment : new ArrayList<>(blighted.attachments)) {
            if (attachment.entity() instanceof LivingEntity living && !living.isDead()) {
                living.damage(event.getFinalDamage(), getRealDamager(event));
                syncArmor(living, entity);
            }
        }
    }

    private boolean handleImmunity(AbstractBlightedEntity blighted, LivingEntity entity, EntityDamageByEntityEvent event) {
        EntityImmunity triggeredRule = blighted.getTriggeredImmunity(entity,event);
        if(triggeredRule == null) return false;

        event.setCancelled(true);
        Player player = getPlayerDamager(event.getDamager());
        if (player != null) {
            player.sendMessage(triggeredRule.getImmunityMessage());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 0.6f);
        }
        return true;
    }

    private boolean shouldPreventDeath(AbstractBlightedEntity blighted, double remainingHealth) {
        if (remainingHealth > 0) return false;
        return blighted.attachments.stream()
            .anyMatch(att -> att.entity() instanceof LivingEntity living && !living.isDead());
    }

    private void scheduleNameTagUpdate(AbstractBlightedEntity blighted, LivingEntity entity) {
        Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> {
            if (entity != null && !entity.isDead()) {
                blighted.updateNameTag();
            }
        }, 1L);
    }

    @EventHandler
    public void onEntityHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        EntityAttachment attachment = AbstractBlightedEntity.getAttachment(entity);
        if (attachment != null) {
            handleAttachmentHeal(attachment, entity, event);
            return;
        }

        AbstractBlightedEntity blighted = BLIGHTED_ENTITIES.get(entity.getUniqueId());
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

    private void handleBlightedEntityHeal(AbstractBlightedEntity blighted, LivingEntity entity, EntityRegainHealthEvent event) {
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
        EntityAttachment attachment = AbstractBlightedEntity.getAttachment(dead);

        if (attachment != null) {
            handleAttachmentDeath(attachment, event);
            return;
        }

        AbstractBlightedEntity blighted = BLIGHTED_ENTITIES.get(dead.getUniqueId());
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
            AbstractBlightedEntity.unregisterAttachment(attachment);
        } catch (Throwable ignored) {
        }

        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    private void handleBlightedEntityDeath(AbstractBlightedEntity blighted, LivingEntity dead, EntityDeathEvent event) {
        blighted.killAllAttachments();
        blighted.removeBossBar();

        Player killer = dead.getKiller();
        BlightedPlayer player = (killer != null) ? BlightedPlayer.getBlightedPlayer(killer) : null;
        blighted.dropLoot(dead.getLocation(), player);
        blighted.onDeath(dead.getLocation());
        BLIGHTED_ENTITIES.remove(dead.getUniqueId());

        event.getDrops().clear();
        event.setDroppedExp(blighted.getDroppedExp());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> rehydrateChunk(event.getChunk()), 2L);
    }

    public static void rehydrateChunk(Chunk chunk) {

        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (BLIGHTED_ENTITIES.containsKey(living.getUniqueId())) continue;

            PersistentDataContainer pdc = living.getPersistentDataContainer();
            String entityId = pdc.get(ENTITY_ID_KEY, PersistentDataType.STRING);
            if (entityId == null || entityId.isEmpty()) continue;

            AbstractBlightedEntity prototype = EntitiesRegistry.get(entityId);
            if (prototype == null) continue;

            AbstractBlightedEntity instance = createInstance(prototype);
            instance.attachToExisting(living);

            syncAttachmentsHealthAndArmor(instance, living);
        }
    }

    private static AbstractBlightedEntity createInstance(AbstractBlightedEntity prototype) {
        try {
            return prototype.clone();
        } catch (Exception exception) {
            Log.error("ENTITIES_LISTENER", "Failed to create instance of " + prototype.getEntityId() + " entity with following error: " + exception.getMessage());
            throw new RuntimeException("Entity instantiation failed", exception);
        }
    }

    private static void syncAttachmentsHealthAndArmor(AbstractBlightedEntity instance, LivingEntity owner) {
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
