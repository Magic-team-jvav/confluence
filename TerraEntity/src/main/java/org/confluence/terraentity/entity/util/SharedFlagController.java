package org.confluence.terraentity.entity.util;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.jetbrains.annotations.NotNull;

/**
 * 实体共享状态
 */
public class SharedFlagController {

    final SynchedEntityData entityData;
    final EntityDataAccessor<Integer> DATA_SHARE_FLAG;

    int shareIndex = -1;
    public SharedFlagController(SynchedEntityData entityData, EntityDataAccessor<Integer> DATA_SHARE_FLAG) {
        this.entityData = entityData;
        this.DATA_SHARE_FLAG = DATA_SHARE_FLAG;
    }

    /**
     * 计算状态
     * @param index 状态位
     * @param value 值
     * @return 计算后的状态
     */
    protected int calShareFlag(int data, int index, boolean value) {
        return value? (data | (index)) : (data & ~(index));
    }

    /**
     * 反转状态
     * @param index 状态位
     * @return 反转后的状态
     */
    protected int reverseShareFlag(int data, int index) {
        return data ^ (index);
    }

    /**
     * 设置状态位
     * @param flag 状态位
     * @param value 值
     */
    public void setFlag(SharedFlag flag, boolean value) {
        this.entityData.set(this.DATA_SHARE_FLAG, this.calShareFlag(this.entityData.get(this.DATA_SHARE_FLAG), flag.index, value));
    }

    /**
     * 获取状态位
     * @param flag 状态位
     * @return 值
     */
    public boolean getFlag(SharedFlag flag) {
        return (this.entityData.get(this.DATA_SHARE_FLAG) & (flag.index)) != 0;
    }

    /**
     * 注册共享状态
     * @return 共享状态
     */
    public SharedFlag registerFlag() {
        if(shareIndex >= 31) {
            throw new RuntimeException("Shared flag index overflow");
        }
        ++shareIndex;
        return new SharedFlag(1 << shareIndex);
    }

    /**
     * 状态标志位
     * @param index 移位后的flag位
     */
    public record SharedFlag(int index) {
        @Override
        public @NotNull String toString() {
            return "SharedFlag : " + Integer.numberOfTrailingZeros(index);
        }
    }
}
