package fr.moussax.blightedMC.game.rituals;

import fr.moussax.blightedMC.core.entities.rituals.AncientRitual;
import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import fr.moussax.blightedMC.game.entities.bosses.Goldor;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;

public class RitualsRegistry {
    public static void registerRituals() {
        new AncientRitual.Builder()
            .summonedCreature(new Goldor())
            .summoningItem(new ItemBuilder(Material.WITHER_SKELETON_SKULL, "§6Goldor").toItemStack())
            .addOffering(new CraftingObject(ItemDirectory.getItem("PLASMA_BUCKET"), 1))
            .addOffering(new CraftingObject(Material.WITHER_SKELETON_SKULL, 64))
            .gemstoneCost(120)
            .experienceLevelCost(20)
            .build();
    }
}
