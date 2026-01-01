package fr.moussax.blightedMC.smp.features.entities.spawnable;

import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.smp.core.entities.spawnable.condition.SpawnConditionFactory;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;

public class Illusioner extends SpawnableEntity {

    public Illusioner() {
        super("ILLUSIONER", "Illusioner", 35, EntityType.ILLUSIONER, 0.05);
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnConditionFactory.insideStructure(Structure.MANSION));
    }
}
