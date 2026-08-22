package org.confluence.mod.common.entity.npc.mood;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.Map;

/// NPC 心情系统。100 为基准值；购买价格按 100 / 心情值计算，售回价格按心情值 / 100 计算。
public final class NPCMood {
    private static final int BASE_VALUE = 100;

    private final EntityType<?> ownerType;
    private int value = BASE_VALUE;

    public NPCMood(EntityType<?> ownerType) {
        this.ownerType = ownerType;
    }

    /// 获取心情值。
    public int getValue() {
        return Math.max(value, 50);
    }

    /// 获取 NPC 向玩家出售商品时的价格系数。
    public float getBuyPriceMultiplier() {
        return (float) BASE_VALUE / getValue();
    }

    /// 获取 NPC 回收玩家物品时的价格系数。
    public float getSellPriceMultiplier() {
        return (float) getValue() / BASE_VALUE;
    }

    /// 根据附近 NPC 重新计算心情值。
    public void evaluate(Iterable<? extends LivingEntity> nearbyEntities) {
        Map<EntityType<?>, Mood> moods = MoodData.getMoodsFor(ownerType);
        int total = BASE_VALUE;
        for (LivingEntity entity : nearbyEntities) {
            if (!(entity instanceof BaseNPC npc)) continue;
            total += switch (moods.getOrDefault(npc.getType(), Mood.NEUTRAL)) {
                case LOVER -> 20;
                case LIKE -> 10;
                case NEUTRAL -> 0;
                case DISLIKE -> -10;
                case HATE -> -20;
            };
        }
        value = total;
    }
}
