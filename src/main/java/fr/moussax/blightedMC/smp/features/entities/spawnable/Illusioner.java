package fr.moussax.blightedMC.smp.features.entities.spawnable;

import fr.moussax.blightedMC.smp.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.smp.core.entities.loot.LootTable;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnConditionFactory;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;

public class Illusioner extends SpawnableEntity {

    public Illusioner() {
        super("ILLUSIONER", "Illusioner", 35, EntityType.ILLUSIONER, 0.05);
    }

    private LootTable createLootTable() {
        return new LootTable()
            .setMaxDrop(2)
            .addGemsLoot(15, 0.03, LootDropRarity.EXTRAORDINARY);
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnConditionFactory.insideStructure(Structure.MANSION));
    }
}
