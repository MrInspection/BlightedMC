package fr.moussax.blightedMC.smp.features.entities.spawnable;

import fr.moussax.blightedMC.smp.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.smp.core.entities.loot.LootTable;
import fr.moussax.blightedMC.smp.core.entities.spawnable.condition.SpawnConditionFactory;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;

public class Illusioner extends SpawnableEntity {

    public Illusioner() {
        super("ILLUSIONER", "Illusioner", 35, EntityType.ILLUSIONER, 0.05);
        setLootTable(createLootTable());
    }

    private LootTable createLootTable() {
        return new LootTable()
            .setMaxDrop(2)
            .addLoot(new ItemBuilder(Material.ENCHANTED_BOOK).addEnchantment(Enchantment.POWER, 8).asEnchantedBook().toItemStack(), 1,1,0.98, LootDropRarity.COMMON)
            .addGemsLoot(15, 0.03, LootDropRarity.VERY_RARE);
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnConditionFactory.insideStructure(Structure.MANSION));
    }
}
