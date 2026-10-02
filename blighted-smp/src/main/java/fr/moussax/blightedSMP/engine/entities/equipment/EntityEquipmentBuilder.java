package fr.moussax.blightedSMP.engine.entities.equipment;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Fluent builder for configuring mob armor and hand equipment.
 */
@Getter
public final class EntityEquipmentBuilder {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack mainHand;
    private ItemStack offHand;

    /**
     * Sets the helmet item stack.
     *
     * @param helmet helmet item, or {@code null} to clear
     * @return this builder
     */
    public EntityEquipmentBuilder helmet(@Nullable ItemStack helmet) {
        this.helmet = helmet;
        return this;
    }

    /**
     * Sets the helmet to a new item of the given material.
     *
     * @param material helmet material
     * @return this builder
     */
    public EntityEquipmentBuilder helmet(@NonNull Material material) {
        return helmet(new ItemStack(material));
    }

    /**
     * Sets the chestplate item stack.
     *
     * @param chestplate chestplate item, or {@code null} to clear
     * @return this builder
     */
    public EntityEquipmentBuilder chestplate(@Nullable ItemStack chestplate) {
        this.chestplate = chestplate;
        return this;
    }

    /**
     * Sets the chestplate to a new item of the given material.
     *
     * @param material chestplate material
     * @return this builder
     */
    public EntityEquipmentBuilder chestplate(@NonNull Material material) {
        return chestplate(new ItemStack(material));
    }

    /**
     * Sets the leggings item stack.
     *
     * @param leggings leggings item, or {@code null} to clear
     * @return this builder
     */
    public EntityEquipmentBuilder leggings(@Nullable ItemStack leggings) {
        this.leggings = leggings;
        return this;
    }

    /**
     * Sets the leggings to a new item of the given material.
     *
     * @param material leggings material
     * @return this builder
     */
    public EntityEquipmentBuilder leggings(@NonNull Material material) {
        return leggings(new ItemStack(material));
    }

    /**
     * Sets the boots item stack.
     *
     * @param boots boots item, or {@code null} to clear
     * @return this builder
     */
    public EntityEquipmentBuilder boots(@Nullable ItemStack boots) {
        this.boots = boots;
        return this;
    }

    /**
     * Sets the boots to a new item of the given material.
     *
     * @param material boots material
     * @return this builder
     */
    public EntityEquipmentBuilder boots(@NonNull Material material) {
        return boots(new ItemStack(material));
    }

    /**
     * Sets all four armor slots in a single call.
     *
     * @param helmet     helmet item, or {@code null}
     * @param chestplate chestplate item, or {@code null}
     * @param leggings   leggings item, or {@code null}
     * @param boots      boots item, or {@code null}
     * @return this builder
     */
    public EntityEquipmentBuilder armor(
            @Nullable ItemStack helmet,
            @Nullable ItemStack chestplate,
            @Nullable ItemStack leggings,
            @Nullable ItemStack boots
    ) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        return this;
    }

    /**
     * Sets the main hand item stack.
     *
     * @param mainHand main hand item, or {@code null} to clear
     * @return this builder
     */
    public EntityEquipmentBuilder mainHand(@Nullable ItemStack mainHand) {
        this.mainHand = mainHand;
        return this;
    }

    /**
     * Sets the main hand to a new item of the given material.
     *
     * @param material main hand material
     * @return this builder
     */
    public EntityEquipmentBuilder mainHand(@NonNull Material material) {
        return mainHand(new ItemStack(material));
    }

    /**
     * Sets the off hand item stack.
     *
     * @param offHand off hand item, or {@code null} to clear
     * @return this builder
     */
    public EntityEquipmentBuilder offHand(@Nullable ItemStack offHand) {
        this.offHand = offHand;
        return this;
    }

    /**
     * Sets the off hand to a new item of the given material.
     *
     * @param material off hand material
     * @return this builder
     */
    public EntityEquipmentBuilder offHand(@NonNull Material material) {
        return offHand(new ItemStack(material));
    }

}
