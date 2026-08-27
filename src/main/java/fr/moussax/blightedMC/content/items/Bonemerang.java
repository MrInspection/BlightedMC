package fr.moussax.blightedMC.content.items;

import fr.moussax.blightedMC.content.items.abilities.BonemerangAbility;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.ItemRarity;
import fr.moussax.blightedMC.engine.items.ItemType;
import fr.moussax.blightedMC.engine.items.abilities.Ability;
import fr.moussax.blightedMC.registry.RegistryModule;
import java.util.function.Consumer;
import org.bukkit.Material;

public class Bonemerang implements RegistryModule<Consumer<BlightedItem>> {

    @Override
    public void register(Consumer<BlightedItem> registry) {
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
        bonemerang.addAbility(Ability.rightClick("Swing", new BonemerangAbility()), false);
        bonemerang.addEnchantmentGlint();
        bonemerang.unstackable();

        registry.accept(bonemerang);
    }
}
