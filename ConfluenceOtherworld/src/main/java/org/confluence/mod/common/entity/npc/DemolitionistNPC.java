package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.npc.ai.NPCGrenadeGoal;

/// 爆破专家 —— 投掷手榴弹攻击 5 格内敌人。
public class DemolitionistNPC extends BaseNPC {

    public DemolitionistNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new NPCGrenadeGoal(this, 5));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, target -> target instanceof Enemy));
    }
}
