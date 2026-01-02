package fr.moussax.blightedMC.smp.features.items;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.abilities.Ability;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.features.items.abilities.WitherImpactAbility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class Hyperion implements ItemProvider {

    @Override
    public void register() {
        BlightedItem hyperion = new BlightedItem("HYPERION", ItemType.SWORD, ItemRarity.LEGENDARY, Material.IRON_SWORD);
        hyperion.setDisplayName("Hyperion");
        hyperion.addLore(
            "",
            "§5 Ability: Wither Impact  §d§lRIGHT CLICK",
            "§7 Teleport §a10 §7blocks ahead of you. Then dealing §c15,000 ",
            "§7 damage to nearby enemies. Also applies the wither",
            "§7 shield scroll reducing damage taken and granting",
            "§7 an §6absorption §7shield for §e5 §7seconds.",
            "",
            ItemRarity.LEGENDARY.getName() + " SWORD"
        );
        hyperion.setUnbreakable(true);
        hyperion.addItemFlag(ItemFlag.HIDE_UNBREAKABLE);
        hyperion.addAbility(new Ability(new WitherImpactAbility(), "Whither Impact", AbilityType.RIGHT_CLICK));

        add(hyperion);
    }
}
