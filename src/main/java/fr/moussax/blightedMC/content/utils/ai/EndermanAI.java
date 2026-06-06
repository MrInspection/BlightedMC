package fr.moussax.blightedMC.content.utils.ai;

import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.LivingEntity;

public class EndermanAI {

    private EndermanAI() {
    }

    public static void init(LivingEntity spawned) {
        if (!(spawned instanceof CraftMob craftMob)) return;
        EnderMan nmsEnderman = (EnderMan) craftMob.getHandle();

        nmsEnderman.goalSelector.removeAllGoals(goal -> true);
        nmsEnderman.targetSelector.removeAllGoals(goal -> true);

        nmsEnderman.goalSelector.addGoal(1, new MeleeAttackGoal(nmsEnderman, 1.0D, false));
        nmsEnderman.goalSelector.addGoal(7, new RandomStrollGoal(nmsEnderman, 1.0D));
        nmsEnderman.goalSelector.addGoal(8, new LookAtPlayerGoal(nmsEnderman, Player.class, 8.0F));
        nmsEnderman.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(nmsEnderman, Player.class, true));
    }
}
