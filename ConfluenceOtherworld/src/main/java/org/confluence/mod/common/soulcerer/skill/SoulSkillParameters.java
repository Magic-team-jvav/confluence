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
    /// 强力附魂：100 秒增益，冷却 60 秒，消耗 6 魂（强度、时长与冷却各再翻倍，耗魂 +3）。
    public static final int ENHANCED_SOUL_DURATION_TICKS = 100 * SharedConstants.TICKS_PER_SECOND;
    public static final int ENHANCED_SOUL_COOLDOWN_TICKS = 60 * SharedConstants.TICKS_PER_SECOND;
    public static final float ENHANCED_SOUL_SOUL_COST = 6.0F;
    /// 附魂系列统一追加的灵魂消耗（已并入各技能自己的常量，此值仅作文档留痕）。
    public static final float SOUL_ATTACH_EXTRA_SOUL_COST = 3.0F;
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
    /// 引魂弹幕的弱追踪：索敌半径内、且目标在弹道**前方**时，每刻把速度朝目标插值该比例。
    ///
    /// 只做「微调弹道」，不做「拐弯追击」：强度远低于带路的老前辈（0.15 对 0.5），
    /// 也不会回头追身后的目标，因此改善手感但不改变引魂「大致向前」的打法。
    public static final double SOUL_LURE_WEAK_HOMING_STRENGTH = 0.15;
    public static final double SOUL_LURE_WEAK_HOMING_RADIUS = 20.0;
    /// 引魂：冷却 0.8 秒（本轮从通用的 1 秒下调）。
    public static final int SOUL_LURE_COOLDOWN_TICKS = 16;
    /// 引魂弹幕命中敌怪后的额外弹射次数：从被击中的敌怪朝另一个敌怪再射一发；
    /// 周围没有第二个敌怪时，朝随机偏移方向射出（`SOUL_LURE_BOUNCE_RADIUS` 内索敌）。
    public static final int SOUL_LURE_BOUNCE_COUNT = 1;
    public static final double SOUL_LURE_BOUNCE_RADIUS = 16.0;
    /// 强力引魂：弹幕基础威力提升，冷却 0.8 秒；额外弹射次数在基础 1 次之上再 +1。
    public static final float ENHANCED_LURE_DAMAGE = 8.0F;
    public static final float ENHANCED_LURE_SOUL_COST = 2.0F;
    public static final int ENHANCED_LURE_COOLDOWN_TICKS = 16;
    /// 强力引魂：在本体自带 1 次弹射之上额外增加的弹射次数。
    public static final int ENHANCED_LURE_EXTRA_BOUNCE = 1;
    /// 附魂印记：命中给**敌人**挂 10 秒「附魂印记」debuff；之后所有技能命中它都回 {@link #SOUL_MARK_HIT_SOUL} 魂，
    /// 击杀带印记的敌人回 {@link #SOUL_MARK_KILL_SOUL} 魂。冷却 0.8 秒，消耗 2 魂。
    public static final float SOUL_MARK_SOUL_COST = 2.0F;
    public static final int SOUL_MARK_COOLDOWN_TICKS = 16;
    public static final int SOUL_MARK_DURATION_TICKS = 10 * SharedConstants.TICKS_PER_SECOND;
    /// 技能命中带印记敌人的回魂数（见 `LivingEntityEvents#soulWeaponTraitsOnSkillHit`）。
    public static final float SOUL_MARK_HIT_SOUL = 1.0F;
    /// 击杀带印记敌人的回魂数（见 `LivingEntityEvents#deathSoul`）。
    public static final float SOUL_MARK_KILL_SOUL = 2.0F;

    // ===== 灵魂激荡系列 =====
    /// 灵魂激荡的判定框：以玩家为中心，0.5 秒内从 0 扩散到 {@link #SPIRIT_SURGE_RADIUS} 格。
    /// 本轮伤害范围再 +20%：6.0 → 7.2（强力激荡与终结爆炸同步 8.0 → 9.6）。
    public static final double SPIRIT_SURGE_RADIUS = 7.2;
    public static final double SPIRIT_SURGE_VERTICAL_RANGE = 5.0;
    public static final int SPIRIT_SURGE_DURATION_TICKS = SharedConstants.TICKS_PER_SECOND / 2;
    /// 灵魂回响（技能书）：灵魂激荡每次释放后，有该概率在原地**再触发一次灵魂激荡**，
    /// 这一发是书籍给的额外触发，**不消耗灵魂**（见 `SpiritSurgeSkill#rollSoulEcho`）。
    public static final float SOUL_ECHO_CHANCE = 0.2F;
    /// 强力激荡：威力 8，冷却 0.8 秒，消耗 3 魂；范围比普通激荡更大（8.0 → 9.6）。
    public static final float EMPOWERED_SURGE_DAMAGE = 8.0F;
    public static final float EMPOWERED_SURGE_SOUL_COST = 3.0F;
    public static final int EMPOWERED_SURGE_COOLDOWN_TICKS = 16;
    public static final double EMPOWERED_SURGE_RADIUS = 9.6;
    public static final double EMPOWERED_SURGE_VERTICAL_RANGE = 6.0;
    /// 灵魂激荡冲击：连续三次激荡后的额外终极大爆炸；单发 12，终结 24，消耗 3 魂。
    public static final int SURGE_BLAST_CHARGE_COUNT = 3;
    public static final float SURGE_BLAST_DAMAGE = 12.0F;
    public static final float SURGE_BLAST_SOUL_COST = 3.0F;
    public static final float SURGE_BLAST_FINISHER_DAMAGE = 24.0F;
    public static final double SURGE_BLAST_FINISHER_RADIUS = 9.6;
    /// 终结爆炸的击退强度：只推范围内的生物，施法者自己不会被推走。
    public static final double SURGE_BLAST_FINISHER_KNOCKBACK = 1.2;
    /// 引魂激荡：弹幕命中后，**在敌人位置**再放一次灵魂激荡（半径同下）；这发激荡是衍生攻击，不消耗灵魂。
    public static final float LURE_SURGE_DAMAGE = 6.0F;
    public static final float LURE_SURGE_SOUL_COST = 3.0F;
    public static final int LURE_SURGE_COOLDOWN_TICKS = 30;
    public static final double LURE_SURGE_RADIUS = 3.0;
    /// 灵魂引发：激荡命中后从敌人位置射出的连锁弹幕；威力 7，冷却 1 秒，消耗 3 魂。
    public static final float SPIRIT_TRIGGER_DAMAGE = 7.0F;
    public static final float SPIRIT_TRIGGER_SOUL_COST = 3.0F;
    public static final int SPIRIT_TRIGGER_COOLDOWN_TICKS = SharedConstants.TICKS_PER_SECOND;
    public static final int SPIRIT_TRIGGER_CHAIN_COUNT = 6;
    public static final float SPIRIT_TRIGGER_CHAIN_DAMAGE = 4.0F;

    // ===== 附魂（自身增益）系列 =====
    /// 血怒：不耗魂、固定扣 5 点生命，换取 +200% 近战与远程增伤；持续 16 秒，冷却 3 秒。
    public static final float BLOOD_RAGE_SOUL_COST = 0.0F;
    public static final int BLOOD_RAGE_COOLDOWN_TICKS = 3 * SharedConstants.TICKS_PER_SECOND;
    public static final int BLOOD_RAGE_DURATION_TICKS = 16 * SharedConstants.TICKS_PER_SECOND;
    public static final float BLOOD_RAGE_HEALTH_COST = 5.0F;
    public static final float BLOOD_RAGE_DAMAGE_BONUS = 2.0F;
    public static final float BLOOD_RAGE_KILL_SOUL_BONUS = 1.0F;
    /// 掠夺之魂：近战与远程伤害增益；持续 140 秒，冷却 100 秒，消耗 8 魂（原 5，强力附魂系列 +3）。
    public static final float SOUL_PLUNDER_SOUL_COST = 8.0F;
    public static final int SOUL_PLUNDER_COOLDOWN_TICKS = 100 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_PLUNDER_DURATION_TICKS = 140 * SharedConstants.TICKS_PER_SECOND;
    public static final double SOUL_PLUNDER_DAMAGE_BONUS = 0.25;
    /// 灵魂透支：80 秒内不消耗灵魂，结束后 160 秒无法使用主武器；冷却 12 秒，消耗 3 魂。
    public static final float SOUL_DRAIN_SOUL_COST = 3.0F;
    public static final int SOUL_DRAIN_COOLDOWN_TICKS = 12 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_DRAIN_DURATION_TICKS = 80 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_DRAIN_LOCKOUT_TICKS = 160 * SharedConstants.TICKS_PER_SECOND;
    /// 灵魂缓冲（原「天穹倒转」）：8 秒内摔落伤害转化为回复；冷却 30 秒。
    public static final float SOUL_BUFFER_SOUL_COST = 2.0F;
    public static final int SOUL_BUFFER_COOLDOWN_TICKS = 30 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_BUFFER_DURATION_TICKS = 8 * SharedConstants.TICKS_PER_SECOND;
    public static final float SOUL_BUFFER_CONVERT_RATIO = 1.0F;
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
    /// 魂引天星：持续 80 秒，期间每 {@link #STAR_CALL_INTERVAL_TICKS} 刻（本轮 50 刻 = 2.5 秒）
    /// 给半径内最近的多只敌怪各落一颗坠星；启动消耗 15 魂（原 12，强力附魂系列 +3），冷却 60 秒，
    /// 增益期间的额外齐射不耗魂。
    ///
    /// 节奏口径：4 轮 / 8 秒 ~ 4 轮 / 12 秒（即一轮 2~3 秒），取中 2.5 秒。
    public static final float STAR_CALL_SOUL_COST = 15.0F;
    public static final int STAR_CALL_COOLDOWN_TICKS = 60 * SharedConstants.TICKS_PER_SECOND;
    public static final int STAR_CALL_DURATION_TICKS = 80 * SharedConstants.TICKS_PER_SECOND;
    public static final int STAR_CALL_INTERVAL_TICKS = 50;
    /// 坠星伤害：原 6 点，本轮 +5。
    public static final float STAR_CALL_DAMAGE = 11.0F;
    /// 每轮齐射的灵魂消耗：**0 = 增益期间的额外齐射完全不耗魂**。
    ///
    /// 灵魂只在施放时按 {@link #STAR_CALL_SOUL_COST} 扣一次，之后每轮坠星都是免费的；
    /// 调成非 0 就恢复「每轮按轮计费 + 概率返还」的旧口径。
    public static final float STAR_CALL_VOLLEY_SOUL_COST = 0.0F;
    /// 有轮次成本时才结算的返魂概率；免费齐射不再返还（否则会变成白嫖灵魂）。
    public static final float STAR_CALL_REFUND_CHANCE = 0.2F;
    /// 索敌半径：只落在半径内**敌怪**头顶，范围内没有敌怪时整轮不落星。
    public static final double STAR_CALL_TARGET_RADIUS = 48.0;
    /// 每轮最多砸几只敌怪（按距离从近到远取前 N 只）——本轮从 4 降到 2，每轮只落两颗。
    public static final int STAR_CALL_STARS_PER_VOLLEY = 2;
    /// 同一轮内相邻两只敌怪的坠星生成时间差（刻）：让坠星错开落地，而不是同一刻齐落。
    public static final int STAR_CALL_STAGGER_TICKS = 1;
    /// 坠星的生成高度：目标位置上方该格数（取星怒的 20 格）；若那里是实心方块，会向下找第一个空位。
    public static final double STAR_CALL_SPAWN_HEIGHT = 20.0;
    /// 迷魂孢雾：一轮 8 枚孢子弹幕，**每轮独立掷骰**决定弹道：
    /// 50% 斜上方抛射（生成在头顶上方、带重力，从上飘落），50% 直线平射（从眼睛射出、不吃重力）。
    /// 威力 1.5，冷却 0.5 秒，消耗 1 魂。
    public static final float CONFUSE_SPORES_SOUL_COST = 1.0F;
    public static final int CONFUSE_SPORES_COOLDOWN_TICKS = 10;
    public static final int CONFUSE_SPORES_COUNT = 8;
    public static final float CONFUSE_SPORES_DAMAGE = 1.5F;
    public static final float CONFUSE_SPORES_SPEED = 1.2F;
    public static final double CONFUSE_SPORES_SPREAD = 0.35;
    public static final double CONFUSE_SPORES_SPAWN_HEIGHT = 3.0;
    /// 一轮齐射走「斜上方抛射」的概率，其余概率改走直线弹道。
    public static final float CONFUSE_SPORES_UPWARD_CHANCE = 0.5F;
    /// 斜上方抛射的仰角（弧度，约 20°）：配合重力让孢子从高处斜着飘下来。
    public static final double CONFUSE_SPORES_UPWARD_PITCH = 0.35;
    /// 迷魂孢雾的最终伤害公式：(武器伤害 × 20% + 技能自身伤害) × (1 + 灵魂伤害增伤百分比)。
    ///
    /// 灵魂增伤那一段由统一伤害流程乘一次，这里只声明“武器伤害只吃 20%”。
    public static final float CONFUSE_SPORES_WEAPON_SHARE = 0.2F;
    /// 自然之惩戒：毒刺弹幕；威力 5，冷却 1 秒，消耗 2 魂。
    public static final float NATURES_WRATH_SOUL_COST = 2.0F;
    public static final int NATURES_WRATH_COOLDOWN_TICKS = 20;
    public static final int NATURES_WRATH_COUNT = 5;
    public static final float NATURES_WRATH_DAMAGE = 5.0F;
    public static final float NATURES_WRATH_SPEED = 2.0F;
    public static final int NATURES_WRATH_POISON_DURATION_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    /// 亵渎之魂：地面钻出的火球弹幕，命中小范围爆炸；威力 7，冷却 0.8 秒，消耗 4 魂。
    public static final float PROFANE_SOUL_SOUL_COST = 4.0F;
    public static final int PROFANE_SOUL_COOLDOWN_TICKS = 16;
    public static final float PROFANE_SOUL_DAMAGE = 7.0F;
    public static final double PROFANE_SOUL_AOE_RADIUS = 2.5;
    public static final float PROFANE_SOUL_AOE_DAMAGE = 4.0F;
    public static final double PROFANE_SOUL_SPAWN_DISTANCE = 6.0;
    /// 火球从敌怪**脚下**多少格处钻出（负数方向），随后朝敌怪中心向上飞。
    public static final double PROFANE_SOUL_SPAWN_OFFSET = 0.8;
    /// 灭却业火：消耗全部灵魂，直接击杀半径 16 格内生命值低于消耗数的目标；被它击杀的敌怪额外掉 2 魂。
    public static final int KARMA_FLAME_COOLDOWN_TICKS = 10 * SharedConstants.TICKS_PER_SECOND;
    public static final double KARMA_FLAME_RADIUS = 16.0;
    public static final float KARMA_FLAME_KILL_SOUL_BONUS = 2.0F;
    /// 灵魂突击：改为增益技能——100 秒内每 {@link #SOUL_ASSAULT_INTERVAL_TICKS} 刻把灵魂转成子弹；冷却 60 秒，消耗 13 魂。
    public static final int SOUL_ASSAULT_MAX_BULLETS = 10;
    public static final float SOUL_ASSAULT_BULLET_DAMAGE = 4.0F;
    public static final float SOUL_ASSAULT_SOUL_COST = 13.0F;
    public static final int SOUL_ASSAULT_COOLDOWN_TICKS = 60 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_ASSAULT_DURATION_TICKS = 100 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_ASSAULT_INTERVAL_TICKS = 10;
    public static final float SOUL_ASSAULT_REFUND_CHANCE = 0.2F;
    /// 灵魂突击：增益期间用枪械开火的强化——消耗 1 魂，这一发子弹伤害 +5，并有 50% 概率返还 1 魂。
    ///
    /// 灵魂不足时不触发强化（不走生命代偿），普通子弹照常打出。
    public static final float SOUL_ASSAULT_GUN_DAMAGE_BONUS = 5.0F;
    public static final float SOUL_ASSAULT_GUN_SOUL_COST = 1.0F;
    public static final float SOUL_ASSAULT_GUN_REFUND_CHANCE = 0.5F;
    public static final float SOUL_ASSAULT_GUN_REFUND_SOUL = 1.0F;
    /// 附魂激荡：主动释放的限时增益——消耗 10 魂，冷却 60 秒，增益 120 秒；
    /// 增益期间每 {@link #SOUL_SURGE_INTERVAL_TICKS} 刻自动释放一次灵魂激荡，且这些自动释放不消耗灵魂。
    public static final float SOUL_SURGE_SOUL_COST = 10.0F;
    public static final int SOUL_SURGE_COOLDOWN_TICKS = 60 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_SURGE_DURATION_TICKS = 120 * SharedConstants.TICKS_PER_SECOND;
    public static final int SOUL_SURGE_INTERVAL_TICKS = ACTIVE_COOLDOWN_TICKS * 3;

    // ===== 被动 =====
    /// 自然法则：概率不消耗灵魂。
    public static final float LAW_OF_NATURE_FREE_CHANCE = 0.2F;
    /// 星魂连结：灵魂增伤增益结束后，继承的魔法增伤增益按来源的该比例折半。
    public static final float STAR_LINK_MAGIC_BONUS_RATIO = 0.5F;
    public static final float STAR_LINK_MAGIC_DURATION_RATIO = 0.5F;
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
    public static final float SOUL_BOW_KILL_SOUL_BONUS = 2.0F;
    /// 重复阅读已学会的技能书获得的复习时长。
    public static final int SOUL_BOOK_REVIEW_DURATION_TICKS = 10 * 60 * SharedConstants.TICKS_PER_SECOND;
    public static final float GO_OVER_SOUL_DAMAGE_BONUS = 0.05F;
    /// 引渡者：引魂标签技能的基础伤害加成（原 +1，本轮 +2）。
    public static final float PSYCHOPOMP_SOUL_LURE_DAMAGE_BONUS = 2.0F;
    /// 引渡者：引魂系列弹幕额外弹射的次数。
    public static final int PSYCHOPOMP_SOUL_LURE_EXTRA_BOUNCE = 1;
    /// 祈灵之刃：当前灵魂高于该值时，近战伤害额外提升。
    public static final float SPIRITWARD_BLADE_SOUL_THRESHOLD = 10.0F;
    public static final float SPIRITWARD_BLADE_DAMAGE_BONUS = 3.0F;
    /// 祈灵之刃：击杀敌怪额外收取的灵魂。
    public static final float SPIRITWARD_BLADE_KILL_SOUL_BONUS = 2.0F;
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
    /// 灵魂长袍套（灵魂头套 + 灵魂长袍）：套装效果，灵魂伤害加成；与单件自带的 +5% 叠加。
    public static final float SOUL_ROBE_SET_SOUL_DAMAGE_BONUS = 0.05F;

    // ===== 主武器自带效果 =====
    /// 残破微光：手持时获得灵魂，有概率回复生命。
    public static final float BROKEN_GLIMMER_HEAL_CHANCE = 0.2F;
    public static final float BROKEN_GLIMMER_HEAL_AMOUNT = 4.0F;
    /// 星魄：技能命中敌人时，按概率额外落下一颗坠星砸向该敌人。
    public static final float STAR_SPIRIT_STAR_CHANCE = 0.3F;
    /// 颂魂指引：引魂弹幕额外穿透的敌怪数量。
    public static final int SOUL_CHANT_GUIDE_EXTRA_PIERCE = 2;
    /// 丛林的灯（生机灿烂）：技能命中敌人时附带的中毒时长。
    public static final int LIFE_SPLENDOR_POISON_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    /// 无分昼夜：技能命中敌人时附带的暗影焰时长。
    public static final int DAY_AND_NIGHT_ALIKE_SHADOWFLAME_TICKS = 5 * SharedConstants.TICKS_PER_SECOND;
    /// 松露灯：手持时技能冷却缩短的比例。
    public static final float TRUFFLE_LANTERN_COOLDOWN_REDUCTION = 0.3F;

    // ===== 收魂与饰品 =====
    /// 束魂锁链 / 手环系列（灵魂手环、灵魂再生手环、通灵手环）/ 刻骨的记忆：
    /// 每佩戴一件，每次收魂额外 +1 灵魂。
    public static final float SOUL_COLLECTION_ACCESSORY_BONUS = 1.0F;
    /// 祈灵之刃 / 灵魂弓 / 灵魂短弓：命中敌怪时获得灵魂的概率与数量。
    public static final float SOUL_WEAPON_HIT_SOUL_CHANCE = 0.5F;
    public static final float SOUL_WEAPON_HIT_SOUL = 1.0F;
}
