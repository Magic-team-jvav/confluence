package org.confluence.mod.common.summon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

/// 召唤物的运行类型及其唯一创建工厂。
public record SummonType(ResourceLocation id, SummonFactory factory) {
    public SummonType {
        Objects.requireNonNull(id, "Summon type id must not be null");
        Objects.requireNonNull(factory, "Summon factory must not be null");
    }

    public SummonInstance create(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose pose) {
        SummonInstance summon = Objects.requireNonNull(
                factory.create(owner, slotCost, stats, pose),
                "Summon factory must not return null");
        if (!summon.type().equals(id)) {
            throw new IllegalStateException("Summon factory returned " + summon.type() + " for " + id);
        }
        return summon;
    }
}
