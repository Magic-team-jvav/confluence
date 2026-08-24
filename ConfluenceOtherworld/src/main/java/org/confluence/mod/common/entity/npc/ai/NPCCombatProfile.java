package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import org.confluence.mod.common.data.entity.CreatureDefinition;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.function.Function;
import java.util.function.Supplier;

/// 实体注册完成后交给 NPC 使用的不可变战斗定义。
///
/// 注册过程中的具名配置由 NpcEntities 负责；运行时这里只保存武器、攻击策略和两组默认值，
/// 避免把注册 DSL、可变构建状态与实体 AI 混在同一个类型中。
public record NPCCombatProfile(Function<BaseNPC, Item> weapon, Attack attack,
                               AttributesDefaults attributes, BehaviorDefaults behavior) {
    /// 使用固定武器提供器开始构建 NPC 战斗定义。
    public static Builder builder(Supplier<? extends Item> weapon, Attack attack) {
        return builder(ignored -> weapon.get(), attack);
    }

    /// 使用可根据 NPC 状态切换武器的函数开始构建战斗定义。
    public static Builder builder(Function<BaseNPC, Item> weapon, Attack attack) {
        return new Builder(weapon, attack);
    }

    /// 判断数据覆盖后的攻击或后撤距离是否启用；两者均关闭时不扫描敌人。
    public boolean reactsToEnemies(BaseNPC npc) {
        Values values = values(npc);
        return values.attackRange() > 0 || values.retreatRange() > 0;
    }

    /// 合并实体当前属性、注册默认值和数据包覆盖，生成一次 AI 决策使用的数值快照。
    public Values values(BaseNPC npc) {
        CreatureDefinition.BehaviorOverrides overrides = npc.creatureDefinition().behavior();
        float damage = (float) (npc.getAttributeValue(Attributes.ATTACK_DAMAGE)
                * NPCCombatProgression.damageMultiplier(npc));
        int attackInterval = overrides.shotCooldownOr(behavior.attackInterval());
        return new Values(damage,
                overrides.attackRangeOr(behavior.attackRange()),
                overrides.retreatRangeOr(behavior.retreatRange()),
                overrides.windupTicksOr(behavior.prepareTime()),
                NPCCombatProgression.attackInterval(attackInterval),
                overrides.projectileSpeedOr(behavior.projectileSpeed()));
    }

    /// 返回数据包覆盖后的每秒自然恢复量；0 表示关闭自然恢复。
    public double healthRegeneration(BaseNPC npc) {
        return npc.creatureDefinition().behavior().healthRegenerationOr(behavior.healthRegeneration());
    }

    /// 执行已经通过目标、距离、视线和冷却校验的具体攻击。
    @FunctionalInterface
    public interface Attack {
        /// 以本次决策的稳定数值对目标发动一次攻击。
        void perform(BaseNPC npc, LivingEntity target, Values values);
    }

    /// 注册时写入实体 Attribute 的默认值；同名 entity_definition 字段可在运行时覆盖。
    public record AttributesDefaults(
            /// 最大生命值。
            double maxHealth,
            /// 基础攻击伤害。
            double attackDamage,
            /// 基础护甲值。
            double armor,
            /// 地面移动速度。
            double movementSpeed,
            /// 属性层面的最大索敌距离。
            double followRange,
            /// 取值范围为 0 到 1 的击退抗性。
            double knockbackResistance) {}

    /// 公共自卫目标使用的默认行为参数；同名 entity_definition 字段可在运行时覆盖。
    public record BehaviorDefaults(
            /// 能够执行攻击策略的最大距离。
            double attackRange,
            /// 敌人进入后开始后撤的距离，0 表示不后撤。
            double retreatRange,
            /// 锁定目标后发动攻击前持续瞄准的 tick 数。
            int prepareTime,
            /// 两次攻击之间的 tick 数。
            int attackInterval,
            /// 远程攻击创建弹体时使用的初速度。
            double projectileSpeed,
            /// 每秒自然恢复的生命值，0 表示不恢复。
            double healthRegeneration) {}

    /// 单次自卫决策使用的数值快照，避免一次攻击读取到两个数据重载版本。
    public record Values(
            /// 当前 Attribute 计算后的实际攻击伤害。
            float damage,
            /// 本轮数据定义生效后的攻击距离。
            double attackRange,
            /// 本轮数据定义生效后的后撤距离。
            double retreatRange,
            /// 本轮数据定义生效后的攻击准备时间。
            int prepareTime,
            /// 本轮数据定义生效后的攻击间隔。
            int attackInterval,
            /// 本轮数据定义生效后的弹体初速度。
            double projectileSpeed) {}

    /// 仅在实体注册期间使用的可变构建器，运行中的 NPC 只持有构建后的不可变定义。
    public static final class Builder {
        private final Function<BaseNPC, Item> weapon;
        private final Attack attack;
        private double maxHealth = 250;
        private double damage = 10;
        private double defense = 15;
        private double movementSpeed = 0.3;
        private double followRange = 24;
        private double knockbackResistance = 0.5;
        private double attackRange = 10;
        private double retreatRange = 4;
        private int prepareTime = 10;
        private int attackInterval = 30;
        private double projectileSpeed = 1;
        private double healthRegeneration = 1.0 / 3.0;

        /// 由 NPCCombatProfile.builder 创建并固定两个必填策略。
        private Builder(Function<BaseNPC, Item> weapon, Attack attack) {
            this.weapon = weapon;
            this.attack = attack;
        }

        /// 设置默认最大生命；entity_definition 的 max_health 可覆盖该值。
        public Builder maxHealth(double value) {
            maxHealth = value;
            return this;
        }

        /// 设置默认攻击伤害；entity_definition 的 attack_damage 可覆盖该值。
        public Builder damage(double value) {
            damage = value;
            return this;
        }

        /// 设置默认防御；entity_definition 的 armor 可覆盖该值。
        public Builder defense(double value) {
            defense = value;
            return this;
        }

        /// 设置默认移动速度；entity_definition 的 movement_speed 可覆盖该值。
        public Builder movementSpeed(double value) {
            movementSpeed = value;
            return this;
        }

        /// 设置默认索敌上限；entity_definition 的 follow_range 可覆盖该值。
        public Builder followRange(double value) {
            followRange = value;
            return this;
        }

        /// 设置默认击退抗性；entity_definition 的 knockback_resistance 可覆盖该值。
        public Builder knockbackResistance(double value) {
            knockbackResistance = value;
            return this;
        }

        /// 设置默认攻击距离；entity_definition 的 attack_range 可覆盖该值。
        public Builder attackRange(double value) {
            attackRange = value;
            return this;
        }

        /// 设置默认后撤距离；0 可关闭后撤，entity_definition 的 retreat_range 可覆盖该值。
        public Builder retreatRange(double value) {
            retreatRange = value;
            return this;
        }

        /// 设置默认攻击准备 tick 数；entity_definition 的 windup_ticks 可覆盖该值。
        public Builder prepareTime(int value) {
            prepareTime = value;
            return this;
        }

        /// 设置默认攻击间隔 tick 数；entity_definition 的 shot_cooldown 可覆盖该值。
        public Builder attackInterval(int value) {
            attackInterval = value;
            return this;
        }

        /// 设置默认弹体初速度；entity_definition 的 projectile_speed 可覆盖该值。
        public Builder projectileSpeed(double value) {
            projectileSpeed = value;
            return this;
        }

        /// 设置默认每秒自然恢复量；entity_definition 的 health_regeneration 可覆盖该值。
        public Builder healthRegeneration(double value) {
            healthRegeneration = value;
            return this;
        }

        /// 校验全部默认值并生成实体可安全共享的不可变战斗定义。
        public NPCCombatProfile build() {
            validate();
            return new NPCCombatProfile(weapon, attack,
                    new AttributesDefaults(maxHealth, damage, defense, movementSpeed, followRange,
                            knockbackResistance),
                    new BehaviorDefaults(attackRange, retreatRange, prepareTime, attackInterval, projectileSpeed,
                            healthRegeneration));
        }

        /// 在注册阶段拒绝非有限值和越界值，避免无效参数进入实体 tick。
        private void validate() {
            if (!Double.isFinite(maxHealth) || !Double.isFinite(damage) || !Double.isFinite(defense)
                    || !Double.isFinite(movementSpeed) || !Double.isFinite(followRange)
                    || !Double.isFinite(knockbackResistance) || !Double.isFinite(attackRange)
                    || !Double.isFinite(retreatRange) || !Double.isFinite(projectileSpeed)
                    || !Double.isFinite(healthRegeneration)
                    || maxHealth <= 0 || damage < 0 || defense < 0 || movementSpeed < 0 || followRange < 0
                    || knockbackResistance < 0 || attackRange < 0 || retreatRange < 0
                    || prepareTime < 0 || attackInterval <= 0 || projectileSpeed <= 0 || healthRegeneration < 0) {
                throw new IllegalArgumentException("NPC combat profile values are out of range");
            }
        }
    }
}
