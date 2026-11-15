package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/// 使用独立夜明纹理的神圣史莱姆变体。
public class LuminousSlime extends BaseSlime {

    public LuminousSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, false);
    }

    @Override
    public boolean isFullBright() {
        return true;
    }
}
