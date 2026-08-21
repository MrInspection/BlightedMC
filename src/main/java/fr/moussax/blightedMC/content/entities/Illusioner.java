package fr.moussax.blightedMC.content.entities;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.components.ShieldComponent;
import fr.moussax.blightedMC.engine.entities.rituals.AncientCreature;
import fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import java.util.List;

public class Illusioner extends AncientCreature {

    public Illusioner() {
        super("Ancient Dummy", 20, EntityType.ILLUSIONER, 30);
        setDamage(12);

        this.lootTable = new EntityLootTableBuilder()
                .addLoot(Material.SPECTRAL_ARROW, 4, 12, 0.6, EntityLootFeedbackDecorator.EntityLootRarity.COMMON)
                .addLoot(Material.GLASS_BOTTLE, 1, 2, 0.4, EntityLootFeedbackDecorator.EntityLootRarity.COMMON)
                .addLoot(Material.TOTEM_OF_UNDYING, 1, 0.02, EntityLootFeedbackDecorator.EntityLootRarity.VERY_RARE)
                .addLoot(Material.OMINOUS_BOTTLE, 1, 0.15, EntityLootFeedbackDecorator.EntityLootRarity.UNCOMMON)
                .addGemsLoot(12, 0.25, EntityLootFeedbackDecorator.EntityLootRarity.UNCOMMON)
                .addEnchantedBookWithLevelRange(
                        List.of(Enchantment.QUICK_CHARGE, Enchantment.PIERCING, Enchantment.POWER),
                        1, 7, 0.07, EntityLootFeedbackDecorator.EntityLootRarity.RARE
                )
                .setMaxDrop(4)
                .build();
    }

    @Override
    protected void onDefineBehavior() {
        super.onDefineBehavior();

        if (getComponent("BLIGHTED_SHIELD") == null) {
            addComponent(new ShieldComponent(120));
        }
    }

    @Override
    public String getEntityId() {
        return "ANCIENT_ILLUSIONER";
    }
}
