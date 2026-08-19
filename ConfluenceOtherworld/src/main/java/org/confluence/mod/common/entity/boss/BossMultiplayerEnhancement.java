package org.confluence.mod.common.entity.boss;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.UUID;

/// 负责为 Boss 应用与难度、当前维度玩家数量有关的属性倍率。
///
/// <p>基础属性以专家难度单人战为基准。经典、专家和大师难度分别使用
/// {@code 0.66}、{@code 1.0} 和 {@code 1.5} 倍属性；最大生命值还会乘以
/// 当前维度中的玩家数量，最多计算八名玩家。攻击伤害只随难度变化，与
/// 玩家数量无关。默认行为与 1.21 实现保持一致；具有原作特殊生命基准或多人公式的
/// Boss 可以只覆写生命倍率，不会改变公共攻击倍率。</p>
///
/// <p>属性修饰符使用固定 UUID 并永久保存。无论 Boss 经由自然生成、
/// 召唤物品、事件脚本还是直接加入世界创建，这套逻辑都可以安全调用；
/// 区块重新加载时也不会重复叠加或把受伤的 Boss 重新回满生命值。</p>
public final class BossMultiplayerEnhancement {
    private static final int MAX_PLAYER_COUNT = 8;
    private static final UUID HEALTH_MODIFIER_ID = PortAttributeModifier.rl2uuid(Confluence.asResource("boss_difficulty_player_count_max_health"));
    private static final UUID DAMAGE_MODIFIER_ID = PortAttributeModifier.rl2uuid(Confluence.asResource("boss_difficulty_attack_damage"));
    private static final UUID HEALTH_CONFIG_MODIFIER_ID = PortAttributeModifier.rl2uuid(Confluence.asResource("boss_server_config_max_health"));
    private static final UUID DAMAGE_CONFIG_MODIFIER_ID = PortAttributeModifier.rl2uuid(Confluence.asResource("boss_server_config_attack_damage"));

    private BossMultiplayerEnhancement() {}

    /// 按 Boss 所在位置的有效难度和当前维度玩家数量应用倍率。
    ///
    /// @param boss 需要强化的 Boss 主体
    public static void apply(LivingEntity boss) {
        if (boss.level().isClientSide) {
            return;
        }
        double difficultyMultiplier = LibUtils.switchByDifficulty(boss.level(), boss.blockPosition(), 0.66D, 1.0D, 1.5D);
        int playerCount = Math.min(boss.level().players().size(), MAX_PLAYER_COUNT);
        apply(boss, difficultyMultiplier, playerCount, org.confluence.mod.common.CommonConfigs.BOSS_ATTRIBUTES_MULTIPLIER_HEALTH.get(), org.confluence.mod.common.CommonConfigs.BOSS_ATTRIBUTES_MULTIPLIER_DAMAGE.get());
    }

    static void apply(LivingEntity boss, double difficultyMultiplier, int playerCount) {
        apply(boss, difficultyMultiplier, playerCount, 1.0D, 1.0D);
    }

    static void apply(LivingEntity boss, double difficultyMultiplier, int playerCount, double healthConfigMultiplier, double damageConfigMultiplier) {
        int clampedPlayerCount = Math.max(0, Math.min(playerCount, MAX_PLAYER_COUNT));
        double healthDifficultyMultiplier = difficultyMultiplier;
        double healthPlayerMultiplier = clampedPlayerCount;
        if (boss instanceof BaseBoss baseBoss) {
            healthDifficultyMultiplier = baseBoss.getBossHealthDifficultyMultiplier(difficultyMultiplier);
            healthPlayerMultiplier = baseBoss.getBossHealthPlayerMultiplier(clampedPlayerCount);
        }

        AttributeInstance maxHealth = boss.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && !maxHealth.hasModifier(HEALTH_MODIFIER_ID)) {
            maxHealth.addPermanentModifier(new AttributeModifier(
                    HEALTH_MODIFIER_ID,
                    "Boss difficulty and player count max health",
                    healthDifficultyMultiplier * healthPlayerMultiplier - 1.0D,
                    AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (maxHealth != null && !maxHealth.hasModifier(HEALTH_CONFIG_MODIFIER_ID)) {
            maxHealth.addPermanentModifier(new AttributeModifier(HEALTH_CONFIG_MODIFIER_ID, "Boss server config max health", healthConfigMultiplier - 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
            boss.setHealth(boss.getMaxHealth());
        }

        AttributeInstance attackDamage = boss.getAttribute(LibAttributes.getAttackDamage().get());
        if (attackDamage != null && !attackDamage.hasModifier(DAMAGE_MODIFIER_ID)) {
            attackDamage.addPermanentModifier(new AttributeModifier(DAMAGE_MODIFIER_ID, "Boss difficulty attack damage", difficultyMultiplier - 1.0D, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (attackDamage != null && !attackDamage.hasModifier(DAMAGE_CONFIG_MODIFIER_ID)) {
            attackDamage.addPermanentModifier(new AttributeModifier(DAMAGE_CONFIG_MODIFIER_ID, "Boss server config attack damage", damageConfigMultiplier - 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }
}
