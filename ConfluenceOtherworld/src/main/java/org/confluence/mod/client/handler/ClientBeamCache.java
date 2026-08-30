package org.confluence.mod.client.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>客户端光束数据缓存/h1>
 */
public final class ClientBeamCache {
/** 最大tick 数，超时自动清除 */
    private static final int MAX_AGE = 10;
    /** flail 实体 ID 与光束条目 */
    private static final Map<Integer, BeamEntry> BEAMS = new HashMap<>();

    private ClientBeamCache() {}

    /**
     * 存储或更新光束目标数据
     */
    public static void put(int flailId, int[] targetIds, boolean elder) {
        BeamEntry old = BEAMS.get(flailId);
        boolean wasInactive = old == null || old.targetIds.length == 0;
        boolean nowActive = targetIds.length > 0;
        long startTimeMs = (wasInactive && nowActive) ? System.currentTimeMillis()
                : (old != null ? old.startTimeMs : System.currentTimeMillis());
        BEAMS.put(flailId, new BeamEntry(targetIds, 0, elder, startTimeMs));
    }

    /**
     * 获取指定连枷的光束目标
     */
    public static BeamEntry get(int flailId) {
        BeamEntry entry = BEAMS.get(flailId);
        if (entry != null) {}
        return entry;
    }

    /**
     * 每客户端 tick 调用，递增 age 并淘汰过期条目
     */
    public static void tick() {
        BEAMS.entrySet().removeIf(entry -> {
            entry.getValue().age++;
            if (entry.getValue().age > MAX_AGE) {return true;
            }
            return false;
        });
    }

    /**
     * 光束缓存条目
     */
    public static final class BeamEntry {
        public final int[] targetIds;
        public final boolean elder;
        public final long startTimeMs;
        public int age;

        BeamEntry(int[] targetIds, int age, boolean elder, long startTimeMs) {
            this.targetIds = targetIds;
            this.age = age;
            this.elder = elder;
            this.startTimeMs = startTimeMs;
        }
    }
}
