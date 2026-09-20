package org.confluence.mod.common.summon;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/// 保存召唤物尚待执行的离散轨迹。
public final class SummonPath {
    private final String identifier;
    private List<SummonPose> nodes;
    private int currentIndex;

    public SummonPath(String identifier, List<SummonPose> nodes) {
        this.identifier = identifier;
        this.nodes = nodes;
    }

    public String identifier() {
        return identifier;
    }

    public List<SummonPose> nodes() {
        return nodes;
    }

    public int currentIndex() {
        return currentIndex;
    }

    public void updateRemainingNodes(List<SummonPose> nodes) {
        if (nodes.size() < currentIndex) {
            throw new IllegalArgumentException("Updated summon path must retain visited nodes");
        }
        this.nodes = nodes;
    }

    public @Nullable SummonPose advance() {
        return isFinished() ? null : nodes.get(currentIndex++);
    }

    public boolean isFinished() {
        return currentIndex >= nodes.size();
    }
}
