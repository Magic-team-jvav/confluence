package org.confluence.mod.api.entity;

public interface ISharedFlagControllerHolder {
    SharedFlagController getSharedFlagController();

    /// 获取状态位，这是SharedFlagController的快捷方法
    ///
    /// @param flag 状态位
    /// @return 值
    default boolean getFlag(SharedFlagController.SharedFlag flag) {
        return getSharedFlagController().getFlag(flag);
    }

    /// 设置状态位，这是SharedFlagController的快捷方法
    ///
    /// @param flag  状态位
    /// @param value 值
    default void setFlag(SharedFlagController.SharedFlag flag, boolean value) {
        this.getSharedFlagController().setFlag(flag, value);
    }
}
