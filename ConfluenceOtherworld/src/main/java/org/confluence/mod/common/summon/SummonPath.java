package org.confluence.mod.common.summon;

import java.util.List;
import java.util.Objects;

/// 保存召唤物尚待执行的离散轨迹。
///
/// <p>重新规划只替换后续节点，不重置已经完成的进度，因此攻击动作可以持续追踪移动目标。</p>
public final class SummonPath {
    private final String identifier;
    private List<SummonPose> nodes;
    private int currentIndex;

    public SummonPath(String identifier, List<SummonPose> nodes) {
        this.identifier = Objects.requireNonNull(identifier, "Summon path identifier must not be null");
        this.nodes = List.copyOf(nodes);
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
        List<SummonPose> updated = List.copyOf(nodes);
        if (updated.size() < currentIndex) {
            throw new IllegalArgumentException("Updated summon path must retain visited nodes");
        }
        this.nodes = updated;
    }

    public SummonPose advance() {
        return isFinished() ? null : nodes.get(currentIndex++);
    }

    public boolean isFinished() {
        return currentIndex >= nodes.size();
    }
}
