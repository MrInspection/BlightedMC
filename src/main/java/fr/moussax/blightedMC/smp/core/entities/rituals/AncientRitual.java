package fr.moussax.blightedMC.smp.core.entities.rituals;

import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Defines an ancient ritual for summoning creatures.
 *
 * <p>A ritual specifies the creature to be summoned, required offerings,
 * the summoning item, gemstone cost, and experience level cost. All instances
 * are automatically registered in {@link #REGISTRY} upon creation.</p>
 */
public final class AncientRitual {
    public static final Set<AncientRitual> REGISTRY = new HashSet<>();

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

        REGISTRY.add(this);
    }

    /**
     * @return the creature summoned by this ritual
     */
    public AncientCreature getSummonedCreature() {
        return summonedCreature;
    }

    /**
     * @return an unmodifiable list of offerings required for the ritual
     */
    public List<CraftingObject> getOfferings() {
        return offerings;
    }

    /**
     * @return the item used to perform the summoning
     */
    public ItemStack getSummoningItem() {
        return summoningItem;
    }

    /**
     * @return the gemstone cost required to perform the ritual
     */
    public int getGemstoneCost() {
        return gemstoneCost;
    }

    /**
     * @return the experience level cost required to perform the ritual
     */
    public int getExperienceLevelCost() {
        return experienceLevelCost;
    }

    /**
     * Builder for creating {@link AncientRitual} instances.
     *
     * <p>Allows setting the summoned creature, offerings, summoning item,
     * gemstone cost, and experience level cost before building the ritual.</p>
     */
    public static final class Builder {

        private final List<CraftingObject> offerings = new ArrayList<>();
        private AncientCreature summonedCreature;
        private ItemStack summoningItem;
        private int gemstoneCost;
        private int experienceLevelCost;

        /**
         * Sets the creature to be summoned by this ritual.
         *
         * @param creature the creature to summon
         * @return this builder instance
         */
        public Builder summonedCreature(AncientCreature creature) {
            this.summonedCreature = creature;
            return this;
        }

        /**
         * Sets multiple offerings for the ritual, replacing any existing offerings.
         *
         * @param offerings the offerings required for the ritual
         * @return this builder instance
         */
        public Builder offerings(CraftingObject... offerings) {
            this.offerings.clear();
            this.offerings.addAll(Arrays.asList(offerings));
            return this;
        }

        /**
         * Adds a single offering to the ritual.
         *
         * @param offering an offering to add
         * @return this builder instance
         */
        public Builder addOffering(CraftingObject offering) {
            this.offerings.add(offering);
            return this;
        }

        /**
         * Sets the item used to perform the summoning.
         *
         * @param item the summoning item
         * @return this builder instance
         */
        public Builder summoningItem(ItemStack item) {
            this.summoningItem = item;
            return this;
        }

        /**
         * Sets the gemstone cost required to perform the ritual.
         *
         * @param gemstone the number of gemstones required
         * @return this builder instance
         */
        public Builder gemstoneCost(int gemstone) {
            this.gemstoneCost = gemstone;
            return this;
        }

        /**
         * Sets the experience level cost required to perform the ritual.
         *
         * @param level the experience level cost
         * @return this builder instance
         */
        public Builder experienceLevelCost(int level) {
            this.experienceLevelCost = level;
            return this;
        }

        /**
         * Builds and registers the {@link AncientRitual} instance.
         *
         * @return the created ritual
         */
        public AncientRitual build() {
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
