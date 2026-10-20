package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.SharedConstants;

/// 魂师全部可调数值的唯一入口。
///
/// 约定：每个技能一段，段内第一行注释写中文技能名。`SOUL_COST` 为灵魂消耗，`COOLDOWN_TICKS` 为冷却，`DAMAGE` 为技能自身基数；
/// 最终伤害仍要经过 `PlayerUtils.getSoulSkillDamage`（叠加武器、属性与被动）。
public class SoulSkillParameters {
    public static final float ACTIVE_DAMAGE = 5.0F;
    public static final float ACTIVE_SOUL_COST = 1.0F;
    public static final int ACTIVE_COOLDOWN_TICKS = SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_ARTIFACT_QUICK_SLOT_COUNT = 3;
    public static final int MAX_COOLING_SOUL_ARTIFACT_SLOTS = 3;
    public static final float BROKEN_GLIMMER_DAMAGE = 5.0F;
    /// 附魂系列通用的增益时长与冷却基准：15 秒。
    ///
    /// 强力附魂系列（强力附魂 / 魂引天星 / 灵魂突击 / 掠夺之魂 / 灵魂透支）各自有独立的、翻倍后的时长与冷却常量，
    /// 不要再用这两个基准值去接它们。
    public static final int SOUL_BUFF_DURATION_TICKS = 15 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_BUFF_COOLDOWN_TICKS = 15 * SharedConstants.TICKS_PER_SECOND;
    /// 强力附魂：50 秒增益，冷却 30 秒，消耗 3 魂。
    public static final int ENHANCED_SOUL_DURATION_TICKS = 50 * SharedConstants.TICKS_PER_SECOND;
    public static final int ENHANCED_SOUL_COOLDOWN_TICKS = 30 * SharedConstants.TICKS_PER_SECOND;
    public static final float ENHANCED_SOUL_SOUL_COST = 3.0F;
    /// 强力附魂：灵魂伤害 +12%。
    public static final float ENHANCED_SOUL_DAMAGE_BONUS = 0.12F;

    // ===== 仪式 =====
    /// 每完成一次仪式提升的灵魂上限。
    public static final int SOUL_PER_RITUAL = 30;
    /// 全流程仪式总次数。
    public static final int MAX_RITUAL_COUNT = 6;

    // ===== 引魂 / 强力引魂 / 附魂印记 =====
    /// 引魂弹幕：与法杖弹幕一致的直线飞行，数值集中在此便于调参。
    /// 基础速度 1.6 格/刻（比原先的 1.6×2/3 提高了 50%）。
    public static final float SOUL_LURE_SPEED = 1.6F;
    public static final double SOUL_LURE_MAX_DISTANCE = 40.0;
    public static final int SOUL_LURE_MAX_AGE_TICKS = 6 * SharedConstants.TICKS_PER_SECOND;
    /// 强力引魂：弹幕基础威力提升，冷却 0.8 秒。
    public static final float ENHANCED_LURE_DAMAGE = 8.0F;
    public static final float ENHANCED_LURE_SOUL_COST = 2.0F;
    public static final int ENHANCED_LURE_COOLDOWN_TICKS = 16;
    /// 附魂印记：命中敌人给 3 秒强力附魂，击杀敌人给 8 秒；冷却 1 秒，消耗 2 魂。
    public static final float SOUL_MARK_SOUL_COST = 2.0F;
    public static final int SOUL_MARK_COOLDOWN_TICKS = SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_MARK_HIT_BUFF_TICKS = 3 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_MARK_KILL_BUFF_TICKS = 8 * SharedConstants.TICKS_PER_SECOND;

    // ===== 灵魂激荡系列 =====
    public static final double SPIRIT_SURGE_RADIUS = 5.0;
    public static final double SPIRIT_SURGE_VERTICAL_RANGE = 5.0;
    public static final int SPIRIT_SURGE_DURATION_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    public static final int SPIRIT_SURGE_PARTICLE_COUNT = 5;
    /// 强力激荡：威力 8，冷却 0.8 秒，消耗 3 魂；范围比普通激荡更大。
    public static final float EMPOWERED_SURGE_DAMAGE = 8.0F;
    public static final float EMPOWERED_SURGE_SOUL_COST = 3.0F;
    public static final int EMPOWERED_SURGE_COOLDOWN_TICKS = 16;
    public static final double EMPOWERED_SURGE_RADIUS = 8.0;
    public static final double EMPOWERED_SURGE_VERTICAL_RANGE = 6.0;
    /// 灵魂激荡冲击：连续三次激荡后的额外终极大爆炸；单发 12，终结 24，消耗 3 魂。
    public static final int SURGE_BLAST_CHARGE_COUNT = 3;
    public static final float SURGE_BLAST_DAMAGE = 12.0F;
    public static final float SURGE_BLAST_SOUL_COST = 3.0F;
    public static final float SURGE_BLAST_FINISHER_DAMAGE = 24.0F;
    public static final double SURGE_BLAST_FINISHER_RADIUS = 8.0;
    /// 引魂激荡：弹幕命中后的范围爆炸。
    public static final float LURE_SURGE_DAMAGE = 6.0F;
    public static final float LURE_SURGE_SOUL_COST = 3.0F;
    public static final int LURE_SURGE_COOLDOWN_TICKS = 30;
    public static final double LURE_SURGE_RADIUS = 3.0;
    public static final float LURE_SURGE_AOE_DAMAGE = 4.0F;
    /// 灵魂引发：激荡命中后从敌人位置射出的连锁弹幕；威力 7，冷却 1 秒，消耗 3 魂。
    public static final float SPIRIT_TRIGGER_DAMAGE = 7.0F;
    public static final float SPIRIT_TRIGGER_SOUL_COST = 3.0F;
    public static final int SPIRIT_TRIGGER_COOLDOWN_TICKS = SharedConstants.TICKS_PER_SECOND;
    public static final int SPIRIT_TRIGGER_CHAIN_COUNT = 6;
    public static final float SPIRIT_TRIGGER_CHAIN_DAMAGE = 4.0F;

    // ===== 附魂（自身增益）系列 =====
    /// 血怒：不耗魂、固定扣 10 点生命，换取 7% 近战与远程增伤；持续 8 秒，冷却 3 秒。
    public static final float BLOOD_RAGE_SOUL_COST = 0.0F;
    public static final int BLOOD_RAGE_COOLDOWN_TICKS = 3 * SharedConstants.TICKS_PER_SECOND;
    public static final int BLOOD_RAGE_DURATION_TICKS = 8 * SharedConstants.TICKS_PER_SECOND;
    public static final float BLOOD_RAGE_HEALTH_COST = 10.0F;
    public static final float BLOOD_RAGE_DAMAGE_BONUS = 0.07F;
    public static final float BLOOD_RAGE_KILL_SOUL_BONUS = 1.0F;
    /// 掠夺之魂：近战与远程伤害增益；持续 70 秒，冷却 50 秒，消耗 5 魂。
    public static final float SOUL_PLUNDER_SOUL_COST = 5.0F;
    public static final int SOUL_PLUNDER_COOLDOWN_TICKS = 50 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_PLUNDER_DURATION_TICKS = 70 * SharedConstants.TICKS_PER_SECOND;
    public static final double SOUL_PLUNDER_DAMAGE_BONUS = 0.25;
    /// 灵魂透支：40 秒内不消耗灵魂，结束后 80 秒无法使用主武器（灵魂枯竭同样翻倍）；冷却 6 秒。
    public static final int SOUL_DRAIN_COOLDOWN_TICKS = 6 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_DRAIN_DURATION_TICKS = 40 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_DRAIN_LOCKOUT_TICKS = 80 * SharedConstants.TICKS_PER_SECOND;
    /// 天穹倒转：8 秒内摔落伤害转化为回复；冷却 30 秒。
    public static final float FIRMAMENT_REVERSED_SOUL_COST = 2.0F;
    public static final int FIRMAMENT_REVERSED_COOLDOWN_TICKS = 30 * SharedConstants.TICKS_PER_SECOND;
    public static final int FIRMAMENT_REVERSED_DURATION_TICKS = 8 * SharedConstants.TICKS_PER_SECOND;
    public static final float FIRMAMENT_REVERSED_CONVERT_RATIO = 1.0F;
    /// 生命归还：记录期间失去的生命，结束时按比例转为灵魂；冷却 5 秒，消耗 5 魂。
    public static final float LIFE_RETURN_SOUL_COST = 5.0F;
    public static final int LIFE_RETURN_COOLDOWN_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    public static final int LIFE_RETURN_DURATION_TICKS = SOUL_BUFF_DURATION_TICKS;
    public static final float LIFE_RETURN_RATIO = 0.7F;
    /// 血液沸腾：下一次近战攻击的伤害倍率与消耗的灵魂比例；冷却 4 秒，命中即消耗该增益。
    public static final float BOILING_BLOOD_SOUL_RATIO = 0.8F;
    public static final int BOILING_BLOOD_COOLDOWN_TICKS = 4 * SharedConstants.TICKS_PER_SECOND;
    public static final int BOILING_BLOOD_DURATION_TICKS = SOUL_BUFF_DURATION_TICKS;
    public static final float BOILING_BLOOD_DAMAGE_MULTIPLIER = 4.0F;

    // ===== 灵魂 / 自然系列 =====
    /// 魂引天星：持续 40 秒，期间按间隔消耗灵魂召唤坠星；启动消耗 12 魂，冷却 30 秒。
    public static final float STAR_CALL_SOUL_COST = 12.0F;
    public static final int STAR_CALL_COOLDOWN_TICKS = 30 * SharedConstants.TICKS_PER_SECOND;
    public static final int STAR_CALL_DURATION_TICKS = 40 * SharedConstants.TICKS_PER_SECOND;
    public static final int STAR_CALL_INTERVAL_TICKS = 10;
    public static final float STAR_CALL_DAMAGE = 6.0F;
    public static final float STAR_CALL_SOUL_PER_STAR = 1.0F;
    public static final float STAR_CALL_REFUND_CHANCE = 0.2F;
    /// 迷魂孢雾：更小更快更多、从天飘落的孢子弹幕；威力 4，冷却 0.5 秒，消耗 1 魂。
    public static final float CONFUSE_SPORES_SOUL_COST = 1.0F;
    public static final int CONFUSE_SPORES_COOLDOWN_TICKS = 10;
    public static final int CONFUSE_SPORES_COUNT = 8;
    public static final float CONFUSE_SPORES_DAMAGE = 4.0F;
    public static final float CONFUSE_SPORES_SPEED = 1.2F;
    public static final double CONFUSE_SPORES_SPREAD = 0.35;
    public static final double CONFUSE_SPORES_SPAWN_HEIGHT = 3.0;
    /// 自然之惩戒：毒刺弹幕；威力 5，冷却 1.5 秒，消耗 2 魂。
    public static final float NATURES_WRATH_SOUL_COST = 2.0F;
    public static final int NATURES_WRATH_COOLDOWN_TICKS = 30;
    public static final int NATURES_WRATH_COUNT = 5;
    public static final float NATURES_WRATH_DAMAGE = 5.0F;
    public static final float NATURES_WRATH_SPEED = 2.0F;
    public static final int NATURES_WRATH_POISON_DURATION_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    /// 亵渎之魂：地面钻出的火球弹幕，命中小范围爆炸；威力 7，冷却 1 秒，消耗 4 魂。
    public static final float PROFANE_SOUL_SOUL_COST = 4.0F;
    public static final int PROFANE_SOUL_COOLDOWN_TICKS = SharedConstants.TICKS_PER_SECOND;
    public static final float PROFANE_SOUL_DAMAGE = 7.0F;
    public static final double PROFANE_SOUL_AOE_RADIUS = 2.5;
    public static final float PROFANE_SOUL_AOE_DAMAGE = 4.0F;
    public static final double PROFANE_SOUL_SPAWN_DISTANCE = 6.0;
    /// 灭却业火：消耗全部灵魂，直接击杀生命值低于消耗数的目标。
    public static final int KARMA_FLAME_COOLDOWN_TICKS = 10 * SharedConstants.TICKS_PER_SECOND;
    public static final double KARMA_FLAME_RADIUS = 6.0;
    /// 灵魂突击：改为增益技能——50 秒内每 {@link #SOUL_ASSAULT_INTERVAL_TICKS} 刻把灵魂转成子弹；冷却 30 秒，消耗 10 魂。
    public static final int SOUL_ASSAULT_MAX_BULLETS = 10;
    public static final float SOUL_ASSAULT_BULLET_DAMAGE = 4.0F;
    public static final float SOUL_ASSAULT_SOUL_COST = 10.0F;
    public static final int SOUL_ASSAULT_COOLDOWN_TICKS = 30 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_ASSAULT_DURATION_TICKS = 50 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_ASSAULT_INTERVAL_TICKS = 10;
    public static final float SOUL_ASSAULT_REFUND_CHANCE = 0.2F;
    /// 附魂激荡：主动释放的限时增益——消耗 10 魂，冷却 60 秒，增益 120 秒；
    /// 增益期间每 {@link #SOUL_SURGE_INTERVAL_TICKS} 刻自动释放一次灵魂激荡，且这些自动释放不消耗灵魂。
    public static final float SOUL_SURGE_SOUL_COST = 10.0F;
    public static final int SOUL_SURGE_COOLDOWN_TICKS = 60 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_SURGE_DURATION_TICKS = 120 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_SURGE_INTERVAL_TICKS = ACTIVE_COOLDOWN_TICKS * 3;

    // ===== 被动 =====
    /// 自然法则：概率不消耗灵魂。
    public static final float LAW_OF_NATURE_FREE_CHANCE = 0.2F;
    /// 星魂联结：灵魂伤害与魔法伤害互相共用的加成比例。
    public static final float STAR_LINK_SHARE_RATIO = 0.5F;
    /// 灵魂收割：召唤物命中时概率回魂。
    public static final float SOUL_HARVEST_CHANCE = 0.05F;
    public static final float SOUL_HARVEST_SOUL = 1.0F;
    /// 噬魂借力：召唤物攻击消耗灵魂换取伤害。
    public static final float SOUL_DEVOUR_SOUL_COST = 1.0F;
    public static final float SOUL_DEVOUR_DAMAGE_BONUS = 0.5F;
    /// 反噬抵抗：生命代偿伤害减免。
    public static final float BACKLASH_RESISTANCE_REDUCTION = 0.6F;
    /// 分魂驾驭：技能伤害提升与冷却延长。
    public static final float SOUL_CONTROL_DAMAGE_MULTIPLIER = 2.0F;
    public static final float SOUL_CONTROL_COOLDOWN_MULTIPLIER = 1.5F;

    // ===== 既有内容 =====
    public static final float SOUL_ENTITY_SIZE = 5.0F / 16.0F;
    /// 引魂弹幕的碰撞箱：为通用灵魂实体的四倍（5/16 → 20/16），命中判定明显更宽松。
    public static final float SOUL_LURE_ENTITY_SIZE = 20.0F / 16.0F;
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
    /// 魂瓶：灵魂条状态下用玻璃瓶装填需要消耗的灵魂。
    public static final float SOUL_BOTTLE_SOUL_COST = 20.0F;
    /// 灵魂短弓 / 灵魂弓：当前灵魂高于该值时，箭矢伤害额外提升。
    public static final float SOUL_BOW_SOUL_THRESHOLD = 10.0F;
    public static final float SOUL_BOW_DAMAGE_BONUS = 3.0F;
    /// 灵魂短弓 / 灵魂弓：用它击杀的敌怪额外收取的灵魂。
    public static final float SOUL_BOW_KILL_SOUL_BONUS = 1.0F;
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
    /// 灵魂再生手环：额外缓慢回复的生命值（每秒一次，与泰拉瑞亚再生手环同档）。
    public static final float BAND_OF_SOUL_REGENERATION_HEAL_PER_SECOND = 0.2F;
    /// 冰冷魂戒：玩家技能冷却缩短的比例。
    public static final float FROSTBITE_SOUL_RING_COOLDOWN_REDUCTION = 0.2F;
}
