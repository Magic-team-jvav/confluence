package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * 独立的普通黑史莱姆。
 */
public final class BlackSlime extends BaseSlime {
    private static final int SIZE = 2;

    public BlackSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, false, SIZE);
    }

    @Override
    public void setSlimeSize(int size) {
        super.setSlimeSize(SIZE);
    }

    @Override
    protected void onAttackTarget(LivingEntity target) {
        tryApplyDarkness(target);
    }
}
