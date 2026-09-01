package org.confluence.mod.common.entity.boss;

import org.jetbrains.annotations.Nullable;

/// 标记由 Boss 直接召唤并维持生命周期的战斗实体。
///
/// 伤害覆盖通过该接口回溯到遭遇主体，使仆从的近战和弹幕继承同一份
/// {@code entity_definition} Boss 伤害倍率。
public interface BossOwnedEntity {
    @Nullable BaseBoss getBossOwner();
}
