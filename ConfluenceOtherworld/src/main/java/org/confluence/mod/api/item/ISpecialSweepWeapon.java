package org.confluence.mod.api.item;

import net.minecraft.server.level.ServerPlayer;

/// 蓄力条满时可以打出「扩大范围的特殊横扫」的武器。
///
/// 左键空挥时由 `EmptyTargetSweepPacketC2S` 按特殊横扫区域结算一次范围攻击，
/// 结算完成后回调 [onSpecialSweep]，供武器附带自己的效果（例如刃弓补一箭）。
public interface ISpecialSweepWeapon {
    /// 是否启用特殊横扫
    default boolean hasSpecialSweep() {
        return true;
    }

    /// 特殊横扫结算完成后的回调（仅服务端）
    default void onSpecialSweep(ServerPlayer player) {}
}