package org.confluence.mod.common.summoner.attachmentEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlannedPath {
    private final String identifier;
    private final List<PathNode> nodes;
    private final Map<Integer, Runnable> plannedRunnable = new HashMap<>();
    private int currentIndex = 0;

    public PlannedPath(List<PathNode> nodes) {
        this.identifier = "default";
        this.nodes = nodes;
    }

    public PlannedPath(String identifier, List<PathNode> nodes) {
        this.identifier = identifier;
        this.nodes = nodes;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<PathNode> getNodes() {
        return nodes;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void run(int currentIndex, Runnable runnable) {
        plannedRunnable.put(currentIndex, runnable);
    }

    /**
     * 获取下一个节点并推进进度（不移除列表内数据）
     */
    public PathNode advance() {
        if (isFinished()) {
            return null;
        }
        Runnable runnable = plannedRunnable.get(currentIndex);
        if (runnable != null) {
            runnable.run();
        }
        return nodes.get(currentIndex++);
    }

    public boolean isFinished() {
        return currentIndex >= nodes.size();
    }
}
