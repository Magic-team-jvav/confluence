package org.confluence.mod.common.summoner.attachmentEntity;

import java.util.List;

public record RenderKeyframe(float partialTick, PathNode node) {

    public static PathNode interpolate(List<RenderKeyframe> frames, float partialTick) {
        RenderKeyframe from = frames.get(0);
        for (int i = 1; i < frames.size(); i++) {
            RenderKeyframe to = frames.get(i);
            if (partialTick <= to.partialTick) {
                return from.node.lerp(to.node, (partialTick - from.partialTick) / (to.partialTick - from.partialTick));
            }
            from = to;
        }
        return from.node;
    }
}
