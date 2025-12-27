package fr.moussax.blightedMC.smp.core.entities.rituals;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an ancient summoning ritual that produces a specific {@link AncientCreature}.
 * <p>
 * Defines the requirements to perform the ritual, including the summoned creature,
 * offerings, summoning item, gemstone cost, and experience level cost.
 * <p>
 * Instances are immutable once built and can be created using the {@link Builder}.
 */
public final class AncientRitual {

    private final AncientCreature summonedCreature;
    private final List<CraftingObject> offerings;
    private final ItemStack summoningItem;
    private final int gemstoneCost;
    private final int experienceLevelCost;

    private AncientRitual(
        AncientCreature summonedCreature,
        List<CraftingObject> offerings,
        ItemStack summoningItem,
        int gemstoneCost,
        int experienceLevelCost
    ) {
        this.summonedCreature = summonedCreature;
        this.offerings = List.copyOf(offerings);
        this.summoningItem = summoningItem;
        this.gemstoneCost = gemstoneCost;
        this.experienceLevelCost = experienceLevelCost;
    }

    /** @return the creature summoned by this ritual */
    public AncientCreature getSummonedCreature() {
        return summonedCreature;
    }

    /** @return an unmodifiable list of offerings required for the ritual */
    public List<CraftingObject> getOfferings() {
        return offerings;
    }

    /** @return the item used to perform the summoning */
    public ItemStack getSummoningItem() {
        return summoningItem;
    }

    /** @return the gemstone cost required to perform the ritual */
    public int getGemstoneCost() {
        return gemstoneCost;
    }

    /** @return the experience level cost required to perform the ritual */
    public int getExperienceLevelCost() {
        return experienceLevelCost;
    }

    /**
     * Builder for creating {@link AncientRitual} instances.
     * <p>
     * Provides a fluent API to define the summoned creature, offerings,
     * summoning item, gemstone cost, and experience level cost.
     * <p>
     * Ensures immutability of the resulting ritual.
     */
    public static final class Builder {

        private final AncientCreature summonedCreature;
        private final List<CraftingObject> offerings = new ArrayList<>();
        private ItemStack summoningItem;
        private int gemstoneCost = 0;
        private int experienceLevelCost = 0;

        private Builder(AncientCreature summonedCreature) {
            this.summonedCreature = summonedCreature;
        }

        /** Starts building a ritual for the specified creature */
        public static Builder of(AncientCreature creature) {
            return new Builder(creature);
        }

        /** Adds a single offering to the ritual */
        public Builder addOffering(CraftingObject offering) {
            this.offerings.add(offering);
            return this;
        }

        /** Adds a material as an offering with the specified amount */
        public Builder addOffering(Material material, int amount) {
            this.offerings.add(new CraftingObject(material, amount));
            return this;
        }

        /** Adds a custom item as an offering with the specified amount */
        public Builder addOffering(BlightedItem item, int amount) {
            this.offerings.add(new CraftingObject(item, amount));
            return this;
        }

        /** Adds a custom item by ID as an offering with the specified amount */
        public Builder addOffering(String itemId, int amount) {
            return addOffering(ItemRegistry.getItem(itemId), amount);
        }

        /** Adds multiple offerings at once */
        public Builder offerings(CraftingObject... offerings) {
            this.offerings.addAll(Arrays.asList(offerings));
            return this;
        }

        /** Sets the item used for summoning */
        public Builder summoningItem(ItemStack item) {
            this.summoningItem = item;
            return this;
        }

        /** Sets the summoning item using a vanilla material */
        public Builder summoningItem(Material material) {
            this.summoningItem = new ItemStack(material);
            return this;
        }

        /** Sets the summoning item using a custom item */
        public Builder summoningItem(BlightedItem item) {
            this.summoningItem = item.toItemStack();
            return this;
        }

        /** Sets the summoning item using a custom item ID */
        public Builder summoningItem(String itemId) {
            var item = ItemRegistry.getItem(itemId);
            if (item != null) {
                this.summoningItem = item.toItemStack();
            }
            return this;
        }

        /** Sets the gemstone cost for the ritual */
        public Builder gemstoneCost(int gemstone) {
            this.gemstoneCost = gemstone;
            return this;
        }

        /** Sets the experience level cost for the ritual */
        public Builder experienceLevelCost(int level) {
            this.experienceLevelCost = level;
            return this;
        }

        /**
         * Builds the ritual.
         *
         * @return a new {@link AncientRitual} instance
         * @throws IllegalStateException if the summoning item was not set
         */
        public AncientRitual build() {
            if (summoningItem == null) {
                throw new IllegalStateException("Summoning item cannot be null");
            }
            return new AncientRitual(
                summonedCreature,
                offerings,
                summoningItem,
                gemstoneCost,
                experienceLevelCost
            );
        }
    }
}
