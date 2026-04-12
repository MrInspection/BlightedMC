package fr.moussax.blightedMC.content.entities;

import fr.moussax.blightedMC.engine.entities.EntityLootTableBuilder;
import fr.moussax.blightedMC.engine.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnRules;
import fr.moussax.blightedMC.engine.entities.spawnable.engine.SpawnMode;
import fr.moussax.blightedMC.shared.loot.decorators.EntityLootFeedbackDecorator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;

import java.util.List;

public class Illusioner extends SpawnableEntity {

    public Illusioner() {
        super("ILLUSIONER", "Illusioner", 35, EntityType.ILLUSIONER, 0.3, SpawnMode.INDEPENDENT);

        this.lootTable = new EntityLootTableBuilder()
            .addLoot(Material.SPECTRAL_ARROW, 4, 12, 0.6, EntityLootFeedbackDecorator.EntityLootRarity.COMMON)
            .addLoot(Material.GLASS_BOTTLE, 1, 2, 0.4, EntityLootFeedbackDecorator.EntityLootRarity.COMMON)
            .addLoot(Material.TOTEM_OF_UNDYING, 1, 1, 0.02, EntityLootFeedbackDecorator.EntityLootRarity.VERY_RARE)
            .addLoot(Material.OMINOUS_BOTTLE, 1, 1, 0.15, EntityLootFeedbackDecorator.EntityLootRarity.UNCOMMON)
            .addGemsLoot(12, 0.25, EntityLootFeedbackDecorator.EntityLootRarity.UNCOMMON)
            .addEnchantedBookWithLevelRange(
                List.of(Enchantment.QUICK_CHARGE, Enchantment.PIERCING),
                1, 3, 0.07, EntityLootFeedbackDecorator.EntityLootRarity.RARE
            )
            .setMaxDrop(4)
            .build();
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnRules.insideStructure(Structure.MANSION));
        addCondition(SpawnRules.insideStructure(Structure.TRIAL_CHAMBERS));
    }
}
