package org.confluence.mod.common.entity.npc.mood;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * NPC 心情系统。心情值影响交易价格系数：
 * <ul>
 *   <li>100 为基准，系数 1.0</li>
 *   <li>每 +10 心情，买入价格 -5%、卖出价格 +5%（系数 0.75 ~ 1.5）</li>
 * </ul>
 */
public class NPCMood {
    private static final int BASE_VALUE = 100;
    private static final int MIN_VALUE = 50;
    private static final int MAX_VALUE = 150;

    private final Map<EntityType<?>, Mood> moodMap = new IdentityHashMap<>();
    private int value = BASE_VALUE;

    public NPCMood() {
        moodMap.putAll(MoodData.DEFAULT_MOODS);
    }

    public NPCMood(Map<EntityType<?>, Mood> customMoods) {
        moodMap.putAll(MoodData.DEFAULT_MOODS);
        moodMap.putAll(customMoods);
    }

    /**
     * 获取心情值。
     */
    public int getValue() {
        return value;
    }

    /**
     * 获取交易价格系数（0.75 ~ 1.5）。
     * 100 心情 = 1.0，每 +10 心情 → 0.05 偏移。
     */
    public float getTradePriceMultiplier() {
        return 1.0f + (BASE_VALUE - value) * 0.005f;
    }

    /**
     * 根据附近 NPC 重新计算心情值。
     */
    public void evaluate(Iterable<BaseNPC> nearbyNPCs) {
        MoodCount counts = new MoodCount();
        for (BaseNPC npc : nearbyNPCs) {
            Mood mood = moodMap.getOrDefault(npc.getType(), Mood.NEUTRAL);
            counts.add(mood);
        }
        value = Mth.clamp(BASE_VALUE + counts.sum(), MIN_VALUE, MAX_VALUE);
    }

    private static class MoodCount {
        private final EnumMap<Mood, Integer> counts = new EnumMap<>(Mood.class);

        void add(Mood mood) {
            counts.merge(mood, 1, Integer::sum);
        }

        int sum() {
            return counts.getOrDefault(Mood.LOVER, 0) * 20
                    + counts.getOrDefault(Mood.LIKE, 0) * 10
                    + counts.getOrDefault(Mood.NEUTRAL, 0) * 0
                    + counts.getOrDefault(Mood.DISLIKE, 0) * -10
                    + counts.getOrDefault(Mood.HATE, 0) * -20;
        }
    }
}
