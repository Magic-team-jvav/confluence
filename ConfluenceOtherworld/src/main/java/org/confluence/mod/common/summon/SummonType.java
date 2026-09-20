package org.confluence.mod.common.summon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/// 召唤物的运行类型及其唯一创建工厂。
public record SummonType(ResourceLocation id, SummonFactory factory) {
    public SummonInstance create(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose pose) {
        SummonInstance summon = factory.create(owner, slotCost, stats, pose);
        if (!summon.type().equals(id)) {
            throw new IllegalStateException("Summon factory returned " + summon.type() + " for " + id);
        }
        return summon;
    }
}
