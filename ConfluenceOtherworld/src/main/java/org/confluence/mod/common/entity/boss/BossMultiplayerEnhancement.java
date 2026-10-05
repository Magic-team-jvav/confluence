package org.confluence.mod.common.entity.boss;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;

/// 负责为 Boss 应用与难度、当前维度玩家数量有关的属性倍率。
///
/// 基础属性以专家难度单人战为基准。经典、专家和大师难度的最大生命值分别使用
/// {@code 0.66}、{@code 1.0} 和 {@code 1.5} 倍；最大生命值还会乘以
/// 当前维度中的玩家数量，最多计算八名玩家。攻击伤害只随难度变化，与
/// 玩家数量无关，并会抵消原版简单难度的额外减半，避免大师难度与原版困难
/// 难度倍率重复相乘。具有特殊生命基准或多人公式的
/// Boss 可以只覆写生命倍率，不会改变公共攻击倍率。
///
/// 属性修饰符使用固定 id 并永久保存。无论 Boss 经由自然生成、
/// 召唤物品、事件脚本还是直接加入世界创建，这套逻辑都可以安全调用；
/// 区块重新加载时也不会重复叠加或把受伤的 Boss 重新回满生命值。
public final class BossMultiplayerEnhancement {
    // 多人属性曲线最多按八名有效参战玩家计算，防止大型服务器倍率无上限膨胀。
    private static final int MAX_PLAYER_COUNT = 8;
    private static final ResourceLocation HEALTH_MODIFIER_ID = Confluence.asResource("boss_difficulty_player_count_max_health");
    private static final ResourceLocation DAMAGE_MODIFIER_ID = Confluence.asResource("boss_difficulty_attack_damage");
    private static final ResourceLocation HEALTH_CONFIG_MODIFIER_ID = Confluence.asResource("boss_server_config_max_health");

    private BossMultiplayerEnhancement() {}

    /// 按 Boss 所在位置的有效难度和当前维度玩家数量应用倍率。
    ///
    /// @param boss 需要强化的 Boss 主体
    public static void apply(LivingEntity boss) {
        if (boss.level().isClientSide) {
            return;
        }
        double healthDifficultyMultiplier = LibUtils.switchByDifficulty(boss.level(), boss.blockPosition(), 0.66D, 1.0D, 1.5D);
        double damageDifficultyMultiplier = LibUtils.switchByDifficulty(boss.level(), boss.blockPosition(), 0.66D, 1.0D, 1.0D);
        int playerCount = Math.max(1, Math.min(boss.level().players().size(), MAX_PLAYER_COUNT));
        apply(boss, healthDifficultyMultiplier, damageDifficultyMultiplier, playerCount, org.confluence.mod.common.CommonConfigs.BOSS_ATTRIBUTES_MULTIPLIER_HEALTH.get());
    }

    static void apply(LivingEntity boss, double difficultyMultiplier, int playerCount) {
        apply(boss, difficultyMultiplier, difficultyMultiplier, playerCount, 1.0D);
    }

    static void apply(LivingEntity boss, double difficultyMultiplier, int playerCount, double healthConfigMultiplier) {
        apply(boss, difficultyMultiplier, difficultyMultiplier, playerCount, healthConfigMultiplier);
    }

    private static void apply(LivingEntity boss, double healthDifficultyInput, double damageDifficultyMultiplier, int playerCount, double healthConfigMultiplier) {
        // 区块可在玩家加入前恢复；零人不能永久写入“最大生命乘零”的属性修饰符。
        int clampedPlayerCount = Math.max(1, Math.min(playerCount, MAX_PLAYER_COUNT));
        double healthDifficultyMultiplier = healthDifficultyInput;
        double healthPlayerMultiplier = clampedPlayerCount;
        if (boss instanceof BaseBoss baseBoss) {
            healthDifficultyMultiplier = baseBoss.getBossHealthDifficultyMultiplier(healthDifficultyInput);
            healthPlayerMultiplier = baseBoss.getBossHealthPlayerMultiplier(clampedPlayerCount);
        }

        AttributeInstance maxHealth = boss.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && !maxHealth.hasModifier(HEALTH_MODIFIER_ID)) {
            maxHealth.addPermanentModifier(new AttributeModifier(
                    HEALTH_MODIFIER_ID,
                    healthDifficultyMultiplier * healthPlayerMultiplier - 1.0D,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
        if (maxHealth != null && !maxHealth.hasModifier(HEALTH_CONFIG_MODIFIER_ID)) {
            maxHealth.addPermanentModifier(new AttributeModifier(HEALTH_CONFIG_MODIFIER_ID, healthConfigMultiplier - 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            boss.setHealth(boss.getMaxHealth());
        }

        AttributeInstance attackDamage = boss.getAttribute(LibAttributes.getAttackDamage());
        if (attackDamage != null && !attackDamage.hasModifier(DAMAGE_MODIFIER_ID)) {
            attackDamage.addPermanentModifier(new AttributeModifier(DAMAGE_MODIFIER_ID, damageDifficultyMultiplier - 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    /// 将同一场遭遇已经确定的难度、人数与生命配置倍率复制给运行中生成的替代主体或战斗部件。
    static void copyEncounterScaling(LivingEntity source, LivingEntity target) {
        copyModifier(source, target, Attributes.MAX_HEALTH, HEALTH_MODIFIER_ID);
        copyModifier(source, target, Attributes.MAX_HEALTH, HEALTH_CONFIG_MODIFIER_ID);
        copyModifier(source, target, LibAttributes.getAttackDamage(), DAMAGE_MODIFIER_ID);
    }

    private static void copyModifier(LivingEntity source, LivingEntity target, Holder<Attribute> attribute, ResourceLocation modifierId) {
        AttributeInstance sourceAttribute = source.getAttribute(attribute);
        AttributeInstance targetAttribute = target.getAttribute(attribute);
        if (sourceAttribute == null || targetAttribute == null || targetAttribute.hasModifier(modifierId))
            return;
        AttributeModifier modifier = sourceAttribute.getModifier(modifierId);
        if (modifier != null) {
            targetAttribute.addPermanentModifier(new AttributeModifier(
                    modifier.id(), modifier.amount(), modifier.operation()));
        }
    }
}
