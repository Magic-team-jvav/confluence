package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 火星人型敌怪的尺寸倍率，由碰撞箱、模型和武器几何共同读取。 */
public abstract class MartianHumanoidMonster extends BaseWarriorMonster {
    public static final float SIZE_SCALE = 0.66F;

    protected MartianHumanoidMonster(EntityType<? extends MartianHumanoidMonster> type, Level level) {
        this(type, level, 0.0D);
    }

    protected MartianHumanoidMonster(EntityType<? extends MartianHumanoidMonster> type, Level level, double pursuitSpeedBonus) {
        super(type, level, pursuitSpeedBonus);
        refreshDimensions();
    }

    @Override
    public float getScale() {
        // 与属性倍率相乘，旧存档中的 scale 属性和数据包覆盖也保持相同比例。
        return super.getScale() * SIZE_SCALE;
    }
}
