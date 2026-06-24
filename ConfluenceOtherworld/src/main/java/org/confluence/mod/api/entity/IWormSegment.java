package org.confluence.mod.api.entity;

/// 蠕虫体节
public interface IWormSegment {
    /// 用在渲染器区分尾部模型
    boolean isTail();

    void setTail(boolean tail);

    /// 第index个体节，某些渲染器可能用得上，如飞龙
    int getIndex();

    /// 当受到攻击时渲染受伤红色Overlay
    boolean isHurtOverlay();
}
