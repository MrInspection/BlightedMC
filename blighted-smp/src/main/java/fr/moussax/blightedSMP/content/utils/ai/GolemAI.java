package fr.moussax.blightedSMP.content.utils.ai;

import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.LivingEntity;

public class GolemAI {

    private GolemAI() {
    }

    public static void init(LivingEntity spawned) {
        if (!(spawned instanceof CraftMob craftMob)) return;
        IronGolem golem = (IronGolem) craftMob.getHandle();

        golem.goalSelector.removeAllGoals(goal -> true);
        golem.targetSelector.removeAllGoals(goal -> true);

        golem.goalSelector.addGoal(0, new FloatGoal(golem));
        golem.goalSelector.addGoal(2, new MeleeAttackGoal(golem, 1.6D, true));
        golem.goalSelector.addGoal(3, new MoveTowardsTargetGoal(golem, 1.4D, 40.0F));
        golem.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(golem, 1.2D));
        golem.goalSelector.addGoal(8, new LookAtPlayerGoal(golem, Player.class, 12.0F));
        golem.targetSelector.addGoal(1, new HurtByTargetGoal(golem).setAlertOthers());

        golem.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                golem,
                Player.class,
                0,
                false,
                false,
                null
        ));
    }
}
