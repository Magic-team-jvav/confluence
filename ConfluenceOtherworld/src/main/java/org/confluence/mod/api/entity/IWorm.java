package org.confluence.mod.api.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.List;

/// 蠕虫头
///
/// @param <T> 体节类型
public interface IWorm<T extends PartEntity<T> & IWormSegment> {
    /// 在构造函数中初始化parts
    default List<T> initParts() {
        List<T> bodySegments = new ArrayList<>(getSegmentCount());
        int count = getSegmentCount();
        for (int i = 0; i < count; i++) {
            bodySegments.add(createPart(i + 1));
        }
        bodySegments.get(count - 1).setTail(true);
        return bodySegments;
    }

    /// 在tick中移动parts
    void tickWormMove();

    /// 获取体节数量
    int getSegmentCount();

    /// 创建体节，在initParts中自动调用
    ///
    /// @param index 当前索引
    T createPart(int index);

    /// 获取体节列表
    List<T> getBodySegments();

    default void onWormRemovedFromLevel() {
        for (T bodySegment : this.getBodySegments()) {
            var entity = (Entity) bodySegment;
            if (entity != null && !entity.isRemoved()) {
                entity.onRemovedFromWorld();
            }
        }
    }

    default void recreateWormFromPacket() {
        for (int i = 0; i < this.getBodySegments().size(); i++) {
            this.getBodySegments().get(i).setId(((Entity) this).getId() + i + 1);
        }
    }

    default PartEntity<?>[] getWormParts() {
        return getBodySegments().toArray(new PartEntity[0]);
    }

    static float wrapYRotation(float current, float target) {
        while (target - current > 180.0F) {
            current += 360.0F;
        }
        while (target - current < -180.0F) {
            current -= 360.0F;
        }
        return current;
    }
}
