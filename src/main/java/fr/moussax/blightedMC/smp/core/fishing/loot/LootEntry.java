package fr.moussax.blightedMC.smp.core.fishing.loot;

import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;

public class LootEntry {
    private final double weight;
    private final LootCondition condition;
    private final ItemStack item;
    private final EntityType entityType;
    private final AbstractBlightedEntity blightedEntity;
    private String catchMessage;

    private LootEntry(double weight, LootCondition condition, ItemStack item, EntityType entityType, AbstractBlightedEntity blightedEntity) {
        this.weight = weight;
        this.condition = condition != null ? condition : LootCondition.alwaysTrue();
        this.item = item;
        this.entityType = entityType;
        this.blightedEntity = blightedEntity;
    }

    public static LootEntry item(ItemStack item, double weight) {
        return new LootEntry(weight, null, item, null, null);
    }

    public static LootEntry item(ItemStack item, double weight, LootCondition condition) {
        return new LootEntry(weight, condition, item, null, null);
    }

    public static LootEntry entity(EntityType entityType, double weight) {
        return new LootEntry(weight, null, null, entityType, null);
    }

    public static LootEntry entity(EntityType entityType, double weight, LootCondition condition) {
        return new LootEntry(weight, condition, null, entityType, null);
    }

    public static LootEntry blightedEntity(AbstractBlightedEntity blightedEntity, double weight) {
        return new LootEntry(weight, null, null, null, blightedEntity);
    }

    public static LootEntry blightedEntity(AbstractBlightedEntity blightedEntity, double weight, LootCondition condition) {
        return new LootEntry(weight, condition, null, null, blightedEntity);
    }

    public LootEntry withCatchMessage(String message) {
        this.catchMessage = message;
        return this;
    }

    public ItemStack createItem(BlightedPlayer blightedPlayer) {
        if (catchMessage != null && blightedPlayer.getPlayer() != null) {
            blightedPlayer.getPlayer().sendMessage(catchMessage);
        }
        return item;
    }

    public LivingEntity spawnEntity(BlightedPlayer blightedPlayer, Location location, Vector velocity) {
        LivingEntity spawned = null;
        Location spawnLoc = location.clone().add(velocity);

        if (entityType != null) {
            spawned = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(spawnLoc, entityType);
        } else if (blightedEntity != null) {
            spawned = blightedEntity.clone().spawn(spawnLoc);
        }

        if (spawned != null) {
            spawned.setVelocity(velocity);
            if (catchMessage != null && blightedPlayer.getPlayer() != null) {
                blightedPlayer.getPlayer().sendMessage(catchMessage);
            }
        }

        return spawned;
    }

    public boolean meetsCondition(LootContext context) {
        return condition.test(context);
    }

    public boolean isItem() {
        return item != null;
    }

    public boolean isEntity() {
        return entityType != null || blightedEntity != null;
    }

    public double getWeight() {
        return weight;
    }
}
