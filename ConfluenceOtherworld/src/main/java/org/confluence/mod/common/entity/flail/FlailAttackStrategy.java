package org.confluence.mod.common.entity.flail;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.component.FlailComponent;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>连枷攻击策略接口</h1>
 * 定义连枷在五阶段状态机 {@code SPIN→THROWN→STAY→RETRACT} 中各节点的攻击行为回调。
 * <p>
 * 每种连枷可绑定不同策略实现
 * @see BaseFlailEntity
 */
public interface FlailAttackStrategy {

    /** 空策略全局单例，所有回调均为空操作，适用于无需额外攻击行为的普通连枷 */
    FlailAttackStrategy NULL = new FlailAttackStrategy() {};

    //SPIN 阶段每 tick 调用。
    default void onSpinTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    //从 SPIN 切换到 THROWN 时调用一次。
    default void onLaunch(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    //THROWN 阶段每 tick 调用。
    default void onThrownTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    //STAY 阶段每 tick 调用。
    default void onStayTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    //THROWN 切换到 RETRACT 时调用一次。
    default void onThrownToRetract(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    //RETRACT 阶段每 tick 调用。
    default void onRetractTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    //连枷碰撞命中实体时调用（在伤害计算之后）。
    default void onHitEntity(@NotNull BaseFlailEntity flail, @NotNull Player player,
                             @NotNull FlailComponent component, @NotNull LivingEntity target) {}

    //连枷被丢弃/移除时调用。
    default void onDiscard(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}
}
