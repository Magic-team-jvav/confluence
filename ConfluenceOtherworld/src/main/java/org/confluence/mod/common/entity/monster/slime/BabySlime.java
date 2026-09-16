package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 由史莱姆之母分裂产生的独立幼体。
 */
public final class BabySlime extends BaseSlime {
    private static final int SIZE = 1;

    public BabySlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, false, SIZE);
    }

    @Override
    public void setSlimeSize(int size) {
        super.setSlimeSize(SIZE);
    }
}
