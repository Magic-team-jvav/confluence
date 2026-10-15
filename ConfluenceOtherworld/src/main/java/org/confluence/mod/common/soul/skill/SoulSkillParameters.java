package org.confluence.mod.common.soul.skill;

import net.minecraft.SharedConstants;
import org.confluence.mod.common.init.ModSoulSkills.SkillCategory;

/// 首批魂师内容的集中测试参数；秒数统一转换为游戏刻。
public class SoulSkillParameters {
    public static final float DEFAULT_SKILL_DAMAGE = 5.0F;
    public static final float DEFAULT_SOUL_COST = 5.0F;
    public static final int DEFAULT_COOLDOWN_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    public static final float BROKEN_GLIMMER_DAMAGE = 5.0F;
    public static final int ENHANCED_SOUL_DURATION_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    public static final double ENHANCED_SOUL_DAMAGE_MULTIPLIER = 5.0D;
    public static final int ENHANCED_SOUL_COLOR = SkillCategory.SOUL.color;
    public static final double SPIRIT_SURGE_RADIUS = 5.0D;
    public static final int SPIRIT_SURGE_DURATION_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    public static final float SOUL_LURE_SPEED = 5.0F;
    public static final double SOUL_LURE_RANGE = 5.0D;
    public static final int SOUL_LURE_LIFETIME_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    public static final float SURVIVAL_INSTINCT_HEALTH_THRESHOLD = 40.0F;
    public static final float SURVIVAL_INSTINCT_SOUL_COST = 10.0F;
    public static final double SURVIVAL_INSTINCT_DURATION_RATIO = 0.5D;
    public static final float LOOT_CHANCE_PERCENT = 5.0F;
    public static final int LOOT_COUNT = 5;
    public static final int LOOT_WEIGHT = 5;
    public static final float SOUL_LURE_ENTITY_SIZE = 5.0F / 16.0F;
    public static final float SOUL_LURE_RENDER_SCALE = 5.0F / 16.0F;
    public static final int SOUL_ENTITY_TRACKING_RANGE = 5;
    public static final int SOUL_ENTITY_UPDATE_INTERVAL = 5;
    public static final double SPIRIT_SURGE_VERTICAL_REACH = 5.0D;
    public static final int SPIRIT_SURGE_RING_PARTICLE_COUNT = 5;
}
