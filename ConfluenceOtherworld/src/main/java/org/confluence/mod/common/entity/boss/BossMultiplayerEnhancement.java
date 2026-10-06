package org.confluence.mod.common.entity.boss;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.UUID;

/// 仅负责遭遇人数与生命配置，难度全部交给 CreatureDifficultyScaling。
public final class BossMultiplayerEnhancement {
    private static final int MAX_PLAYER_COUNT = 8;
    private static final UUID LEGACY_HEALTH_ID = PortAttributeModifier.rl2uuid(Confluence.asResource("boss_difficulty_player_count_max_health"));
    private static final UUID LEGACY_DAMAGE_ID = PortAttributeModifier.rl2uuid(Confluence.asResource("boss_difficulty_attack_damage"));
    private static final UUID HEALTH_CONFIG_ID = PortAttributeModifier.rl2uuid(Confluence.asResource("boss_server_config_max_health"));
    private static final UUID PLAYER_COUNT_ID = PortAttributeModifier.rl2uuid(Confluence.asResource("boss_encounter_player_count_health"));

    private BossMultiplayerEnhancement() {}

    /// 遭遇人数只确定一次；已有存档移除旧难度项，不叠加新的难度倍率。
    public static void apply(LivingEntity boss) {
        if (boss.level().isClientSide) return;
        float health = boss.getHealth(), maximum = boss.getMaxHealth();
        boolean full = health > 0 && Math.abs(health - maximum) < 0.001F;
        AttributeInstance maxHealth = boss.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance damage = boss.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) damage.removeModifier(LEGACY_DAMAGE_ID);
        if (maxHealth != null) {
            maxHealth.removeModifier(LEGACY_HEALTH_ID);
            if (!maxHealth.hasModifier(PLAYER_COUNT_ID)) {
                int count = Math.max(1, Math.min(boss.level().players().size(), MAX_PLAYER_COUNT));
                double multiplier = boss instanceof BaseBoss base ? base.getBossHealthPlayerMultiplier(count) : count;
                maxHealth.addPermanentModifier(new AttributeModifier(PLAYER_COUNT_ID, "Boss encounter player count", multiplier - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            replace(maxHealth, HEALTH_CONFIG_ID, CommonConfigs.BOSS_ATTRIBUTES_MULTIPLIER_HEALTH.get());
        }
        CreatureDifficultyScaling.apply(boss, false);
        boss.setHealth(full ? boss.getMaxHealth() : Math.min(health, boss.getMaxHealth()));
    }

    /// 配置项覆盖自己固定 ID 的乘算，不随调用次数累乘。
    private static void replace(AttributeInstance attribute, UUID id, double multiplier) {
        attribute.removeModifier(id);
        if (multiplier != 1)
            attribute.addPermanentModifier(new AttributeModifier(id, "Boss server config max health", multiplier - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    /// 替代主体继承遭遇人数，难度仍由其登记的实体类别统一应用。
    static void copyEncounterScaling(LivingEntity source, LivingEntity target) {
        for (UUID id : new UUID[]{PLAYER_COUNT_ID, HEALTH_CONFIG_ID}) {
            AttributeInstance from = source.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance to = target.getAttribute(Attributes.MAX_HEALTH);
            if (from == null || to == null) continue;
            AttributeModifier modifier = from.getModifier(id);
            if (modifier != null) to.addOrReplacePermanentModifier(modifier);
        }
        CreatureDifficultyScaling.apply(target, false);
    }
}
