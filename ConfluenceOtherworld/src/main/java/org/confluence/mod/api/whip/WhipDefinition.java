package org.confluence.mod.api.whip;

import org.confluence.mod.api.whip.curve.WhipCurve;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/// 一种鞭子的不可变战斗定义。
///
/// <p>普通鞭子只需声明这份定义。直接命中、友方召唤物命中和召唤标记分别使用独立入口，
/// 避免不同生命周期的效果相互混用。</p>
///
/// @param durationTicks           挥动动画和攻击实体的基础持续时间
/// @param hitCooldownTicks        同一目标在一次挥动中再次接受判定前的间隔
/// @param baseDamage              第一名敌人受到的直接伤害
/// @param rangeMultiplier         基础轨迹的射程倍率
/// @param damageFalloff           后续敌人的直接伤害倍率
/// @param minimumDamageMultiplier 连续命中时允许的最低伤害倍率
/// @param penetratesBlocks        是否忽略方块遮挡
/// @param curve                   挥动轨迹
/// @param directHitEffects        直接命中敌人时执行的效果
/// @param friendlyHitEffects      命中己方召唤物时执行的效果
/// @param tagEffect               施加到敌人身上的独立召唤标记
public record WhipDefinition(int durationTicks, int hitCooldownTicks, float baseDamage,
                             float rangeMultiplier, float damageFalloff,
                             float minimumDamageMultiplier, boolean penetratesBlocks,
                             WhipCurve curve, List<WhipDirectHitEffect> directHitEffects,
                             List<WhipFriendlyHitEffect> friendlyHitEffects,
                             Supplier<? extends WhipTagEffect> tagEffect) {
    public WhipDefinition {
        if (durationTicks <= 0) {
            throw new IllegalArgumentException("Whip duration must be positive");
        }
        if (hitCooldownTicks <= 0) {
            throw new IllegalArgumentException("Whip hit cooldown must be positive");
        }
        if (!Float.isFinite(baseDamage) || baseDamage < 0.0F) {
            throw new IllegalArgumentException("Whip damage must be finite and non-negative");
        }
        if (!Float.isFinite(rangeMultiplier) || rangeMultiplier <= 0.0F) {
            throw new IllegalArgumentException("Whip range multiplier must be finite and positive");
        }
        if (!Float.isFinite(damageFalloff) || damageFalloff <= 0.0F || damageFalloff > 1.0F) {
            throw new IllegalArgumentException("Whip damage falloff must be in (0, 1]");
        }
        if (!Float.isFinite(minimumDamageMultiplier) || minimumDamageMultiplier < 0.0F || minimumDamageMultiplier > 1.0F) {
            throw new IllegalArgumentException("Whip minimum damage multiplier must be in [0, 1]");
        }
        curve = Objects.requireNonNull(curve, "Whip curve must not be null");
        directHitEffects = List.copyOf(Objects.requireNonNull(directHitEffects, "directHitEffects"));
        friendlyHitEffects = List.copyOf(Objects.requireNonNull(friendlyHitEffects, "friendlyHitEffects"));
        tagEffect = Objects.requireNonNull(tagEffect, "tagEffect");
    }
}
