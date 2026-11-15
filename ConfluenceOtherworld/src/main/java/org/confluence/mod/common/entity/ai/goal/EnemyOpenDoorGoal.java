package org.confluence.mod.common.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;

import java.util.EnumSet;

/// 开门不抢占负责寻路的行为树，且不在停止目标时立即把门关回去。
public class EnemyOpenDoorGoal extends OpenDoorGoal {
    public EnemyOpenDoorGoal(Mob mob) {
        super(mob, false);
        setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !isOpen();
    }

    /// 原版 closeDoor=false 只禁止持续运行，stop() 仍会关门；不能让门在追击途中立即关回去。
    @Override
    public void stop() {}
}
