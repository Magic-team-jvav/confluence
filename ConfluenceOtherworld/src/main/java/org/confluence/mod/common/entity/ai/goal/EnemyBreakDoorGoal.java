package org.confluence.mod.common.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;

import java.util.EnumSet;

/// 拆门时继续保留追击路径；破坏时间、方块校验和 mobGriefing 检查沿用原版。
public final class EnemyBreakDoorGoal extends BreakDoorGoal {
    public EnemyBreakDoorGoal(Mob mob) {
        super(mob, difficulty -> true);
        setFlags(EnumSet.noneOf(Flag.class));
    }
}
