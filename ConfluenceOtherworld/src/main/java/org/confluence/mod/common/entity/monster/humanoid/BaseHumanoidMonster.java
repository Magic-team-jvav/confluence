package org.confluence.mod.common.entity.monster.humanoid;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.monster.BaseMonster;

public abstract class BaseHumanoidMonster extends BaseMonster {
    @Override
    protected boolean shouldFloatInWater() {
        return true;
    }


    public BaseHumanoidMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

}
