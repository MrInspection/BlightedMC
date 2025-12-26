package fr.moussax.blightedMC.smp.features.items;

import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemTemplate;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.abilities.Ability;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.smp.features.abilities.WitherImpactAbility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public class Hyperion implements ItemRegistry {

    @Override
    public List<ItemTemplate> defineItems() {
        ItemTemplate hyperion = new ItemTemplate("HYPERION", ItemType.SWORD, ItemRarity.LEGENDARY, Material.IRON_SWORD, "Hyperion");
        hyperion.setUnbreakable(true).addItemFlag(List.of(ItemFlag.HIDE_UNBREAKABLE));
        hyperion.addItemFlag(ItemFlag.HIDE_UNBREAKABLE);
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
        hyperion.addAbility(new Ability(new WitherImpactAbility(), "Whither Impact", AbilityType.RIGHT_CLICK));
        return ItemRegistry.add(hyperion);
    }
}
