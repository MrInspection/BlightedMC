package fr.moussax.blightedMC.smp.features.items;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.abilities.Ability;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.features.abilities.BonemerangAbility;
import org.bukkit.Material;

public class Bonemerang implements ItemProvider {

    @Override
    public void register() {
        BlightedItem bonemerang = new BlightedItem("BONEMERANG", ItemType.BOW, ItemRarity.EPIC, Material.BONE);
        bonemerang.setDisplayName("Bonemerang");
        bonemerang.addLore(
            "",
            "§5 Ability: Swing  §d§lRIGHT CLICK",
            "§7 Throw the bone forward, slicing",
            "§7 through foes, dealing §c12 §7damage ",
            "§7 before returning to you.",
            "",
            ItemRarity.EPIC.getName() + " BOW"
        );
        bonemerang.addAbility(new Ability(new BonemerangAbility(), "Swing", AbilityType.RIGHT_CLICK));
        bonemerang.addEnchantmentGlint();
        bonemerang.isUnstackable();

        add(bonemerang);
    }
}
