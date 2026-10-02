package fr.moussax.blightedSMP.engine.entities.equipment;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Manages equipment state for custom entities, holding template items and synchronizing with live Bukkit entities.
 */
public final class EntityEquipmentHolder {

    private ItemStack[] armor;
    private ItemStack itemInMainHand;
    private ItemStack itemInOffHand;

    public EntityEquipmentHolder() {
    }

    private void ensureArmorArray() {
        if (armor == null) {
            armor = new ItemStack[4];
        }
    }

    /**
     * Sets the helmet item and synchronizes it with the live entity if active.
     *
     * @param helmet     helmet item, or {@code null} to clear
     * @param liveEntity live entity to update, or {@code null} if unspawned
     */
    public void setHelmet(@Nullable ItemStack helmet, @Nullable LivingEntity liveEntity) {
        ensureArmorArray();
        this.armor[3] = helmet;
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            liveEntity.getEquipment().setHelmet(helmet);
        }
    }

    /**
     * Sets the chestplate item and synchronizes it with the live entity if active.
     *
     * @param chestplate chestplate item, or {@code null} to clear
     * @param liveEntity live entity to update, or {@code null} if unspawned
     */
    public void setChestplate(@Nullable ItemStack chestplate, @Nullable LivingEntity liveEntity) {
        ensureArmorArray();
        this.armor[2] = chestplate;
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            liveEntity.getEquipment().setChestplate(chestplate);
        }
    }

    /**
     * Sets the leggings item and synchronizes it with the live entity if active.
     *
     * @param leggings   leggings item, or {@code null} to clear
     * @param liveEntity live entity to update, or {@code null} if unspawned
     */
    public void setLeggings(@Nullable ItemStack leggings, @Nullable LivingEntity liveEntity) {
        ensureArmorArray();
        this.armor[1] = leggings;
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            liveEntity.getEquipment().setLeggings(leggings);
        }
    }

    /**
     * Sets the boots item and synchronizes it with the live entity if active.
     *
     * @param boots      boots item, or {@code null} to clear
     * @param liveEntity live entity to update, or {@code null} if unspawned
     */
    public void setBoots(@Nullable ItemStack boots, @Nullable LivingEntity liveEntity) {
        ensureArmorArray();
        this.armor[0] = boots;
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            liveEntity.getEquipment().setBoots(boots);
        }
    }

    /**
     * Sets all four armor slots and synchronizes them with the live entity if active.
     *
     * @param helmet     helmet item, or {@code null}
     * @param chestplate chestplate item, or {@code null}
     * @param leggings   leggings item, or {@code null}
     * @param boots      boots item, or {@code null}
     * @param liveEntity live entity to update, or {@code null} if unspawned
     */
    public void setArmor(
            @Nullable ItemStack helmet,
            @Nullable ItemStack chestplate,
            @Nullable ItemStack leggings,
            @Nullable ItemStack boots,
            @Nullable LivingEntity liveEntity
    ) {
        setHelmet(helmet, liveEntity);
        setChestplate(chestplate, liveEntity);
        setLeggings(leggings, liveEntity);
        setBoots(boots, liveEntity);
    }

    /**
     * Clears all armor slots and strips armor from the live entity if active.
     *
     * @param liveEntity live entity to update, or {@code null} if unspawned
     */
    public void clearArmor(@Nullable LivingEntity liveEntity) {
        this.armor = null;
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            liveEntity.getEquipment().setArmorContents(new ItemStack[4]);
        }
    }

    /**
     * Clears all armor and hand items and empties equipment on the live entity if active.
     *
     * @param liveEntity live entity to update, or {@code null} if unspawned
     */
    public void clearEquipment(@Nullable LivingEntity liveEntity) {
        this.armor = null;
        this.itemInMainHand = null;
        this.itemInOffHand = null;
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            liveEntity.getEquipment().clear();
        }
    }

    /**
     * Sets the main hand item and synchronizes it with the live entity if active.
     *
     * @param item       main hand item, or {@code null} to clear
     * @param liveEntity live entity to update, or {@code null} if unspawned
     */
    public void setMainHand(@Nullable ItemStack item, @Nullable LivingEntity liveEntity) {
        this.itemInMainHand = item;
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            liveEntity.getEquipment().setItemInMainHand(item);
        }
    }

    /**
     * Sets the off hand item and synchronizes it with the live entity if active.
     *
     * @param item       off hand item, or {@code null} to clear
     * @param liveEntity live entity to update, or {@code null} if unspawned
     */
    public void setOffHand(@Nullable ItemStack item, @Nullable LivingEntity liveEntity) {
        this.itemInOffHand = item;
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            liveEntity.getEquipment().setItemInOffHand(item);
        }
    }

    /**
     * Toggles whether the configured main hand item is currently equipped on the live entity.
     *
     * @param liveEntity live entity to update, or {@code null}
     * @param equipped   {@code true} to equip the item, {@code false} to temporarily unequip
     */
    public void setMainHandEquipped(@Nullable LivingEntity liveEntity, boolean equipped) {
        if (!isEntityActive(liveEntity) || liveEntity.getEquipment() == null) {
            return;
        }
        liveEntity.getEquipment().setItemInMainHand(equipped ? itemInMainHand : null);
    }

    /**
     * Toggles whether the configured off hand item is currently equipped on the live entity.
     *
     * @param liveEntity live entity to update, or {@code null}
     * @param equipped   {@code true} to equip the item, {@code false} to temporarily unequip
     */
    public void setOffHandEquipped(@Nullable LivingEntity liveEntity, boolean equipped) {
        if (!isEntityActive(liveEntity) || liveEntity.getEquipment() == null) {
            return;
        }
        liveEntity.getEquipment().setItemInOffHand(equipped ? itemInOffHand : null);
    }

    /**
     * Returns the template item held in the main hand.
     *
     * @return main hand template item, or {@code null} if empty
     */
    @Nullable
    public ItemStack getItemInMainHand() {
        return itemInMainHand;
    }

    /**
     * Returns the template item held in the off hand.
     *
     * @return off hand template item, or {@code null} if empty
     */
    @Nullable
    public ItemStack getItemInOffHand() {
        return itemInOffHand;
    }

    /**
     * Retrieves the current helmet from the live entity if active, or from stored template.
     *
     * @param liveEntity active live entity, or {@code null}
     * @return helmet item, or {@code null} if empty
     */
    @Nullable
    public ItemStack getHelmet(@Nullable LivingEntity liveEntity) {
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            return liveEntity.getEquipment().getHelmet();
        }
        return armor != null && armor.length > 3 ? armor[3] : null;
    }

    /**
     * Retrieves the current chestplate from the live entity if active, or from stored template.
     *
     * @param liveEntity active live entity, or {@code null}
     * @return chestplate item, or {@code null} if empty
     */
    @Nullable
    public ItemStack getChestplate(@Nullable LivingEntity liveEntity) {
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            return liveEntity.getEquipment().getChestplate();
        }
        return armor != null && armor.length > 2 ? armor[2] : null;
    }

    /**
     * Retrieves the current leggings from the live entity if active, or from stored template.
     *
     * @param liveEntity active live entity, or {@code null}
     * @return leggings item, or {@code null} if empty
     */
    @Nullable
    public ItemStack getLeggings(@Nullable LivingEntity liveEntity) {
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            return liveEntity.getEquipment().getLeggings();
        }
        return armor != null && armor.length > 1 ? armor[1] : null;
    }

    /**
     * Retrieves the current boots from the live entity if active, or from stored template.
     *
     * @param liveEntity active live entity, or {@code null}
     * @return boots item, or {@code null} if empty
     */
    @Nullable
    public ItemStack getBoots(@Nullable LivingEntity liveEntity) {
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            return liveEntity.getEquipment().getBoots();
        }
        return armor != null && armor.length > 0 ? armor[0] : null;
    }

    /**
     * Retrieves the current main hand item from the live entity if active, or from stored template.
     *
     * @param liveEntity active live entity, or {@code null}
     * @return main hand item, or {@code null} if empty
     */
    @Nullable
    public ItemStack getMainHand(@Nullable LivingEntity liveEntity) {
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            return liveEntity.getEquipment().getItemInMainHand();
        }
        return itemInMainHand;
    }

    /**
     * Retrieves the current off hand item from the live entity if active, or from stored template.
     *
     * @param liveEntity active live entity, or {@code null}
     * @return off hand item, or {@code null} if empty
     */
    @Nullable
    public ItemStack getOffHand(@Nullable LivingEntity liveEntity) {
        if (isEntityActive(liveEntity) && liveEntity.getEquipment() != null) {
            return liveEntity.getEquipment().getItemInOffHand();
        }
        return itemInOffHand;
    }

    /**
     * Applies configuration from an {@link EntityEquipmentBuilder}.
     *
     * @param builder    configured builder
     * @param liveEntity optional active entity to update immediately
     */
    public void applyBuilder(@NonNull EntityEquipmentBuilder builder, @Nullable LivingEntity liveEntity) {
        if (builder.getHelmet() != null) setHelmet(builder.getHelmet(), liveEntity);
        if (builder.getChestplate() != null) setChestplate(builder.getChestplate(), liveEntity);
        if (builder.getLeggings() != null) setLeggings(builder.getLeggings(), liveEntity);
        if (builder.getBoots() != null) setBoots(builder.getBoots(), liveEntity);
        if (builder.getMainHand() != null) setMainHand(builder.getMainHand(), liveEntity);
        if (builder.getOffHand() != null) setOffHand(builder.getOffHand(), liveEntity);
    }

    /**
     * Applies all configured equipment to the newly spawned or rehydrated entity and zeroes drop chances.
     *
     * @param entity target living entity
     */
    public void applyTo(@NonNull LivingEntity entity) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) {
            return;
        }

        if (armor != null) {
            equipment.setArmorContents(armor);
        }
        if (itemInMainHand != null) {
            equipment.setItemInMainHand(itemInMainHand);
        }
        if (itemInOffHand != null) {
            equipment.setItemInOffHand(itemInOffHand);
        }

        zeroEquipmentDropChances(equipment);
    }

    /**
     * Ensures equipment items do not drop naturally when the entity dies.
     * Custom drops are controlled through the loot system.
     *
     * @param equipment entity equipment to zero
     */
    public static void zeroEquipmentDropChances(@NonNull EntityEquipment equipment) {
        equipment.setHelmetDropChance(0f);
        equipment.setChestplateDropChance(0f);
        equipment.setLeggingsDropChance(0f);
        equipment.setBootsDropChance(0f);
        equipment.setItemInMainHandDropChance(0f);
        equipment.setItemInOffHandDropChance(0f);
    }

    /**
     * Creates a deep copy of this equipment holder with cloned item stacks.
     *
     * @return cloned equipment holder
     */
    public EntityEquipmentHolder copy() {
        EntityEquipmentHolder copy = new EntityEquipmentHolder();
        if (this.armor != null) {
            copy.armor = new ItemStack[4];
            for (int i = 0; i < 4; i++) {
                copy.armor[i] = cloneItem(this.armor[i]);
            }
        }
        copy.itemInMainHand = cloneItem(this.itemInMainHand);
        copy.itemInOffHand = cloneItem(this.itemInOffHand);
        return copy;
    }

    private static ItemStack cloneItem(ItemStack item) {
        return item != null ? item.clone() : null;
    }

    private static boolean isEntityActive(LivingEntity entity) {
        return entity != null && entity.isValid() && !entity.isDead();
    }
}
