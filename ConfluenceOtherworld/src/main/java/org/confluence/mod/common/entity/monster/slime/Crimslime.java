package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/// 猩红史莱姆：攻击致盲，统一使用与腐化史莱姆相同的中型大小。
public class Crimslime extends BaseSlime {

    public Crimslime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, false);
    }

    @Override
    public void setSlimeSize(int size) {
        super.setSlimeSize(2);
    }

    @Override
    protected void onAttackTarget(LivingEntity target) {
        tryApplyDarkness(target);
    }
}
