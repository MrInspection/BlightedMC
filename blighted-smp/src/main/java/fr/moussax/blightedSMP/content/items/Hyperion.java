package fr.moussax.blightedSMP.content.items;

import fr.moussax.blightedSMP.content.items.abilities.WitherImpactAbility;
import fr.moussax.blightedSMP.engine.items.BlightedItem;
import fr.moussax.blightedSMP.engine.items.ItemRarity;
import fr.moussax.blightedSMP.engine.items.ItemType;
import fr.moussax.blightedSMP.engine.items.abilities.Ability;
import fr.moussax.blightedSMP.registry.RegistryModule;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class Hyperion implements RegistryModule<Consumer<BlightedItem>> {

    @Override
    public void register(Consumer<BlightedItem> registry) {
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
        hyperion.addAbility(Ability.rightClick("Wither Impact", new WitherImpactAbility()), false);

        registry.accept(hyperion);
    }
}
