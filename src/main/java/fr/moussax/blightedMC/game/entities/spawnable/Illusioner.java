package fr.moussax.blightedMC.game.entities.spawnable;

import fr.moussax.blightedMC.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.core.entities.loot.LootTable;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnConditionFactory;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnableEntity;
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
