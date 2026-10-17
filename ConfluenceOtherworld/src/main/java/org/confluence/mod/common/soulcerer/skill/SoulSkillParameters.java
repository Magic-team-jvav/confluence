package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.SharedConstants;

public class SoulSkillParameters {
    public static final float ACTIVE_DAMAGE = 5.0F;
    public static final float ACTIVE_SOUL_COST = 1.0F;
    public static final int ACTIVE_COOLDOWN_TICKS = SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_ARTIFACT_QUICK_SLOT_COUNT = 3;
    public static final int MAX_COOLING_SOUL_ARTIFACT_SLOTS = 3;
    public static final float BROKEN_GLIMMER_DAMAGE = 5.0F;
    public static final int ENHANCED_SOUL_DURATION_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    public static final double ENHANCED_SOUL_DAMAGE_MULTIPLIER = 5.0;
    /// 引魂弹幕：与法杖弹幕一致的直线飞行，数值集中在此便于调参。
    /// 基础速度 1.6 格/刻，再降低三分之一（×2/3）→ 约 1.0667 格/刻。
    public static final float SOUL_LURE_SPEED = 1.6F * 2.0F / 3.0F;
    public static final double SOUL_LURE_MAX_DISTANCE = 40.0;
    public static final int SOUL_LURE_MAX_AGE_TICKS = 6 * SharedConstants.TICKS_PER_SECOND;
    public static final double SPIRIT_SURGE_RADIUS = 5.0;
    public static final double SPIRIT_SURGE_VERTICAL_RANGE = 5.0;
    public static final int SPIRIT_SURGE_DURATION_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    public static final int SPIRIT_SURGE_PARTICLE_COUNT = 5;
    public static final float SOUL_ENTITY_SIZE = 5.0F / 16.0F;
    public static final float SOUL_ENTITY_RENDER_SCALE = 5.0F / 16.0F;
    public static final int SOUL_ENTITY_TRACKING_RANGE = 5;
    public static final int SOUL_ENTITY_UPDATE_INTERVAL = 5;
    public static final float LOOT_CHANCE_PERCENT = 5.0F;
    public static final int LOOT_COUNT = 5;
    public static final int LOOT_WEIGHT = 5;
    public static final float SURVIVAL_INSTINCT_HEALTH_THRESHOLD = 40.0F;
    public static final float SURVIVAL_INSTINCT_SOUL_COST = 10.0F;
    public static final float SURVIVAL_INSTINCT_DURATION_RATIO = 0.5F;
    public static final int FALLEN_SOUL_CORE_COOLDOWN_TICKS = SharedConstants.TICKS_PER_SECOND;
    /// 重复阅读已学会的技能书获得的复习时长。
    public static final int SOUL_BOOK_REVIEW_DURATION_TICKS = 10 * 60 * SharedConstants.TICKS_PER_SECOND;
    public static final float GO_OVER_SOUL_DAMAGE_BONUS = 0.05F;
    /// 引渡者：引魂标签技能的基础伤害加成。
    public static final float PSYCHOPOMP_SOUL_LURE_DAMAGE_BONUS = 1.0F;
    /// 祈灵之刃：当前灵魂高于该值时，近战伤害额外提升。
    public static final float SPIRITWARD_BLADE_SOUL_THRESHOLD = 10.0F;
    public static final float SPIRITWARD_BLADE_DAMAGE_BONUS = 3.0F;
    /// 祈灵之刃：击杀敌怪额外收取的灵魂。
    public static final float SPIRITWARD_BLADE_KILL_SOUL_BONUS = 1.0F;
    /// 祈灵之刃：向前贯穿的扁矩形范围，reach 为前端长度，(halfWidth, halfHeight) 为半宽与半高。
    public static final double SPIRITWARD_BLADE_REACH = 4.5;
    public static final double SPIRITWARD_BLADE_HALF_WIDTH = 0.45;
    public static final double SPIRITWARD_BLADE_HALF_HEIGHT = 0.45;
    /// 低于该蓄力比例时不触发贯穿，避免连点刷伤害。
    public static final float SPIRITWARD_BLADE_MIN_CHARGE = 0.9F;
    /// 蓝隐匿者全套：每累计消耗该点数的灵魂，削减一次耐药性剩余时长。
    public static final float BLUE_HERMIT_SOUL_PER_REDUCTION = 10.0F;
    public static final int BLUE_HERMIT_SICKNESS_REDUCTION_TICKS = 3 * SharedConstants.TICKS_PER_SECOND;
    /// 幻翼全套：夜间额外增加的灵魂掉落。
    public static final float PHANTOM_NIGHT_SOUL_DROP_BONUS = 2.0F;
    /// 白南瓜全套：近战暴击回复的灵魂。
    public static final float WHITE_PUMPKIN_CRIT_SOUL = 3.0F;
    /// 逐灵全套：灵魂高于该值时增伤。
    public static final float SEEKER_DAMAGE_SOUL_THRESHOLD = 20.0F;
    public static final float SEEKER_DAMAGE_BONUS = 0.07F;
    /// 魂舞全套：收集灵魂后的限时增益。
    public static final int SOULDANCER_BUFF_DURATION_TICKS = 10 * SharedConstants.TICKS_PER_SECOND;
    public static final double SOULDANCER_CRIT_BONUS = 0.08;
    public static final double SOULDANCER_SPEED_BONUS = 0.07;
    public static final float SOULDANCER_DAMAGE_REDUCTION = 0.07F;
    /// 灵魂手环：每次回复的灵魂（每 3 秒一次）。
    public static final float BAND_OF_SOUL_RECOVERY = 1.0F;
}
