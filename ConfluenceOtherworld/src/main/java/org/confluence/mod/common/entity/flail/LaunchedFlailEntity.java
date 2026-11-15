package org.confluence.mod.common.entity.flail;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/// 直接发射型链锤实体。
///
/// 链刃使用无重力版本；锚通过构造参数启用投出重力。
/// 两者共享“创建后立即投出、命中后立即收回”的稳定行为。
public class LaunchedFlailEntity extends BaseFlailEntity {
    private final double thrownGravity;

    public LaunchedFlailEntity(EntityType<? extends LaunchedFlailEntity> type, Level level, double thrownGravity) {
        super(type, level);
        this.thrownGravity = thrownGravity;
    }

    @Override
    protected boolean startsLaunched() {
        return true;
    }

    @Override
    public boolean usesSpriteHead() {
        return true;
    }

    @Override
    protected double getThrownGravity() {
        return thrownGravity;
    }
}
