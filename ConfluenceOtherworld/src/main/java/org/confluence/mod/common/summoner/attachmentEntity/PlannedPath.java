package org.confluence.mod.common.summoner.attachmentEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PlannedPath {
    private final String identifier;
    private final List<PathNode> nodes;
    private final Map<Integer, Runnable> plannedRunnable = new HashMap<>();
    private final Map<Integer, List<RenderKeyframe>> renderKeyframes = new HashMap<>();
    private int currentIndex = 0;

    public PlannedPath(List<PathNode> nodes) {
        this.identifier = "default";
        this.nodes = nodes;
    }

    public PlannedPath(String identifier, List<PathNode> nodes) {
        this.identifier = identifier;
        this.nodes = nodes;
    }

    public static PlannedPath sample(int ticks, int subdivisions, Function<Float, PathNode> sampler) {
        return sample("default", ticks, subdivisions, sampler);
    }

    /** ticks、subdivisions 为正数；sampler 接收整条路径的 0～1 进度。 */
    public static PlannedPath sample(String identifier, int ticks, int subdivisions, Function<Float, PathNode> sampler) {
        List<PathNode> nodes = new ArrayList<>(ticks + 1);
        for (int i = 0; i <= ticks; i++) {
            nodes.add(sampler.apply((float) i / ticks));
        }
        PlannedPath path = new PlannedPath(identifier, nodes);
        for (int i = 1; i <= ticks; i++) {
            List<RenderKeyframe> frames = new ArrayList<>(subdivisions - 1);
            for (int j = 1; j < subdivisions; j++) {
                float partialTick = (float) j / subdivisions;
                frames.add(new RenderKeyframe(partialTick, sampler.apply((i - 1 + partialTick) / ticks)));
            }
            path.render(i, frames);
        }
        return path;
    }

    /** index 为目标逻辑节点下标；帧按 partialTick 递增排列，取值在 0～1 之间，不含端点。 */
    public PlannedPath render(int index, List<RenderKeyframe> frames) {
        renderKeyframes.put(index, List.copyOf(frames));
        return this;
    }

    public List<RenderKeyframe> getRenderKeyframes(int index) {
        return renderKeyframes.getOrDefault(index, List.of());
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
