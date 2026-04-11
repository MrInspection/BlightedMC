package fr.moussax.blightedMC.engine.entities.rituals;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.crafting.CraftingObject;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
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

    public static final class Builder {

        private final AncientCreature summonedCreature;
        private final List<CraftingObject> offerings = new ArrayList<>();
        private ItemStack summoningItem;
        private int gemstoneCost = 0;
        private int experienceLevelCost = 0;

        private Builder(AncientCreature summonedCreature) {
            this.summonedCreature = summonedCreature;
        }

        public static Builder of(AncientCreature creature) {
            return new Builder(creature);
        }

        public Builder addOffering(CraftingObject offering) {
            this.offerings.add(offering);
            return this;
        }

        public Builder addOffering(Material material, int amount) {
            this.offerings.add(new CraftingObject(material, amount));
            return this;
        }

        public Builder addOffering(BlightedItem item, int amount) {
            this.offerings.add(new CraftingObject(item, amount));
            return this;
        }

        public Builder addOffering(String itemId, int amount) {
            return addOffering(ItemRegistry.getItem(itemId), amount);
        }

        public Builder offerings(CraftingObject... offerings) {
            this.offerings.addAll(Arrays.asList(offerings));
            return this;
        }

        public Builder summoningItem(ItemStack item) {
            this.summoningItem = item;
            return this;
        }

        public Builder summoningItem(Material material) {
            this.summoningItem = new ItemStack(material);
            return this;
        }

        public Builder summoningItem(BlightedItem item) {
            this.summoningItem = item.toItemStack();
            return this;
        }

        public Builder summoningItem(String itemId) {
            var item = ItemRegistry.getItem(itemId);
            this.summoningItem = item.toItemStack();
            return this;
        }

        public Builder gemstoneCost(int gemstone) {
            this.gemstoneCost = gemstone;
            return this;
        }

        public Builder experienceLevelCost(int level) {
            this.experienceLevelCost = level;
            return this;
        }

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
