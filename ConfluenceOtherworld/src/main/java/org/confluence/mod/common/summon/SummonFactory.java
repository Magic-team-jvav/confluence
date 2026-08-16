package org.confluence.mod.common.summon;

import net.minecraft.server.level.ServerPlayer;

/// 根据物品提供的基础伤害，创建一个不进入世界实体列表的服务端召唤物实例。
@FunctionalInterface
public interface SummonFactory {
    SummonInstance create(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose);
}
