package org.confluence.mod.common.entity.npc.mood;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * NPC 心情系统。心情值分别影响玩家买入与 NPC 售回价格：
 * <ul>
 *   <li>100 为基准，系数 1.0</li>
 *   <li>每 +10 心情，玩家买入价格降低 5%，NPC 回收价格提高 5%</li>
 *   <li>心情限制在 50 到 150，因此两种系数都限制在 0.75 到 1.25</li>
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
     * 获取玩家从 NPC 购买物品时的价格系数。
     */
    public float getBuyPriceMultiplier() {
        return 1.0f + (BASE_VALUE - value) * 0.005f;
    }

    /**
     * 获取 NPC 回收玩家物品时的价格系数。
     *
     * <p>该方向与买价相反：高心情既给予购买折扣，也会提高回收价。</p>
     */
    public float getSellPriceMultiplier() {
        return 1.0f + (value - BASE_VALUE) * 0.005f;
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
