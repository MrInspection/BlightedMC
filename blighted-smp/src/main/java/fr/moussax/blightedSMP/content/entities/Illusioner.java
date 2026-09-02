package fr.moussax.blightedSMP.content.entities;

import fr.moussax.blightedSMP.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedSMP.engine.entities.components.impl.ShieldComponent;
import fr.moussax.blightedSMP.engine.entities.rituals.AncientCreature;
import fr.moussax.blightedSMP.engine.loot.decorators.EntityLootRarity;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import java.util.List;

public class Illusioner extends AncientCreature {

    public Illusioner() {
        super("Ancient Dummy", 20, EntityType.ILLUSIONER, 30);
        setDamage(12);

        this.lootTable = new EntityLootTableBuilder()
                .addLoot(Material.SPECTRAL_ARROW, 4, 12, 0.6, EntityLootRarity.COMMON)
                .addLoot(Material.GLASS_BOTTLE, 1, 2, 0.4, EntityLootRarity.COMMON)
                .addLoot(Material.TOTEM_OF_UNDYING, 1, 0.02, EntityLootRarity.VERY_RARE)
                .addLoot(Material.OMINOUS_BOTTLE, 1, 0.15, EntityLootRarity.UNCOMMON)
                .addGemsLoot(12, 0.25, EntityLootRarity.UNCOMMON)
                .addEnchantedBookLoot(
                        List.of(Enchantment.QUICK_CHARGE, Enchantment.PIERCING, Enchantment.POWER),
                        1, 7, 0.07, EntityLootRarity.RARE
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
