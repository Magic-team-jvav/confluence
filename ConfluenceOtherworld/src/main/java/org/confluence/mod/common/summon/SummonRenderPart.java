package org.confluence.mod.common.summon;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * 描述一个逻辑召唤物需要同步到客户端的单个可视部件。
 */
public record SummonRenderPart(UUID id, ResourceLocation type, SummonPose pose,
                               SummonVisualState visualState, int order) {}
