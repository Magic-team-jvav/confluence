package org.confluence.mod.api.entity;

import net.minecraft.world.phys.Vec3;

/// 蠕虫需要控制飞行高度、索敌高度
public interface IHeightControlMob {
    double wrapWanderHeight(Vec3 pos);

    boolean isAttackableHeight(float originalHeight);

    /// 有时候Goal因为重写一些ai，而不需要使用高度控制，可以实现空接口
    interface Empty extends IHeightControlMob {
        @Override
        default double wrapWanderHeight(Vec3 pos) {
            return pos.y;
        }

        @Override
        default boolean isAttackableHeight(float originalHeight) {
            return true;
        }
    }
}
