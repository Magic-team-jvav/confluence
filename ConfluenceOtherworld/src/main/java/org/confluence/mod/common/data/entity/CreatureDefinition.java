package org.confluence.mod.common.data.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/// 简单生物的数据包定义。
///
/// <p>该记录只保存可安全热重载的“数值配置”，不保存实体实例、行为树节点或 Forge 对象。
/// 生物实体的 Java 实现是默认值的唯一来源；数据包只保存需要改动的覆盖值。
/// KubeJS 也可以把相同结构放入 {@code kubejs/data/<实体命名空间>/entity_definition}，
/// 无需依赖本体内部 Java 类。未填写的字段统一以负数表示“沿用 Java 侧默认值”，
/// 从而允许整合包只覆盖自己关心的参数。</p>
///
/// <p>这里是稳定的数据格式边界。外部模组与脚本应写入 JSON，而不是直接持有加载器的内部映射；
/// 这样既能参与标准资源包优先级，也能在 {@code /reload} 时与其他数据包一起原子生效。</p>
public record CreatureDefinition(AttributeOverrides attributes, BehaviorOverrides behavior) {
    /// 未找到定义或定义未提供任何覆盖值时使用的不可变空对象。
    public static final CreatureDefinition EMPTY = new CreatureDefinition(AttributeOverrides.EMPTY, BehaviorOverrides.EMPTY);

    /// 数据包编解码入口。属性与行为两个区块都可省略，便于数据包只调整一个维度。
    public static final Codec<CreatureDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AttributeOverrides.CODEC.optionalFieldOf("attributes", AttributeOverrides.EMPTY)
                    .forGetter(CreatureDefinition::attributes),
            BehaviorOverrides.CODEC.optionalFieldOf("behavior", BehaviorOverrides.EMPTY)
                    .forGetter(CreatureDefinition::behavior)
    ).apply(instance, CreatureDefinition::new));

    /// 可选的原版属性基础值覆盖。
    ///
    /// <p>这些数值在实体完成属性实例初始化后写入基础值，不创建永久修饰符，避免多次加载叠加。</p>
    public record AttributeOverrides(double maxHealth, double attackDamage, double armor,
                                     double movementSpeed, double followRange,
                                     double knockbackResistance) {
        public static final AttributeOverrides EMPTY = new AttributeOverrides(-1, -1, -1, -1, -1, -1);
        public static final Codec<AttributeOverrides> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("max_health", -1.0).forGetter(AttributeOverrides::maxHealth),
                Codec.DOUBLE.optionalFieldOf("attack_damage", -1.0).forGetter(AttributeOverrides::attackDamage),
                Codec.DOUBLE.optionalFieldOf("armor", -1.0).forGetter(AttributeOverrides::armor),
                Codec.DOUBLE.optionalFieldOf("movement_speed", -1.0).forGetter(AttributeOverrides::movementSpeed),
                Codec.DOUBLE.optionalFieldOf("follow_range", -1.0).forGetter(AttributeOverrides::followRange),
                Codec.DOUBLE.optionalFieldOf("knockback_resistance", -1.0)
                        .forGetter(AttributeOverrides::knockbackResistance)
        ).apply(instance, AttributeOverrides::new));
    }

    /// 通用行为树参数覆盖。
    ///
    /// <p>字段按照行为能力而非具体生物命名：近战、冲锋、远程和飞行模板只读取自己需要的字段。
    /// 因此新增简单生物时可以复用同一格式，不必为每个实体增加独立 Codec。</p>
    public record BehaviorOverrides(double moveSpeed, double meleeRange, double wanderSpeed,
                                    int wanderRadius, int idleTicks, double chargeSpeed,
                                    int windupTicks, int shotCooldown, double shotMultiplier,
                                    double preferredRange, double orbitSpeed, double orbitRadius) {
        public static final BehaviorOverrides EMPTY = new BehaviorOverrides(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1);
        public static final Codec<BehaviorOverrides> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("move_speed", -1.0).forGetter(BehaviorOverrides::moveSpeed),
                Codec.DOUBLE.optionalFieldOf("melee_range", -1.0).forGetter(BehaviorOverrides::meleeRange),
                Codec.DOUBLE.optionalFieldOf("wander_speed", -1.0).forGetter(BehaviorOverrides::wanderSpeed),
                Codec.INT.optionalFieldOf("wander_radius", -1).forGetter(BehaviorOverrides::wanderRadius),
                Codec.INT.optionalFieldOf("idle_ticks", -1).forGetter(BehaviorOverrides::idleTicks),
                Codec.DOUBLE.optionalFieldOf("charge_speed", -1.0).forGetter(BehaviorOverrides::chargeSpeed),
                Codec.INT.optionalFieldOf("windup_ticks", -1).forGetter(BehaviorOverrides::windupTicks),
                Codec.INT.optionalFieldOf("shot_cooldown", -1).forGetter(BehaviorOverrides::shotCooldown),
                Codec.DOUBLE.optionalFieldOf("shot_multiplier", -1.0)
                        .forGetter(BehaviorOverrides::shotMultiplier),
                Codec.DOUBLE.optionalFieldOf("preferred_range", -1.0)
                        .forGetter(BehaviorOverrides::preferredRange),
                Codec.DOUBLE.optionalFieldOf("orbit_speed", -1.0).forGetter(BehaviorOverrides::orbitSpeed),
                Codec.DOUBLE.optionalFieldOf("orbit_radius", -1.0).forGetter(BehaviorOverrides::orbitRadius)
        ).apply(instance, BehaviorOverrides::new));

        public double moveSpeedOr(double fallback) {
            return positive(moveSpeed, fallback);
        }

        public double meleeRangeOr(double fallback) {
            return positive(meleeRange, fallback);
        }

        public double wanderSpeedOr(double fallback) {
            return positive(wanderSpeed, fallback);
        }

        public int wanderRadiusOr(int fallback) {
            return positive(wanderRadius, fallback);
        }

        public int idleTicksOr(int fallback) {
            return positive(idleTicks, fallback);
        }

        public double chargeSpeedOr(double fallback) {
            return positive(chargeSpeed, fallback);
        }

        public int windupTicksOr(int fallback) {
            return nonNegative(windupTicks, fallback);
        }

        public int shotCooldownOr(int fallback) {
            return positive(shotCooldown, fallback);
        }

        public double shotMultiplierOr(double fallback) {
            return nonNegative(shotMultiplier, fallback);
        }

        public double orbitSpeedOr(double fallback) {
            return positive(orbitSpeed, fallback);
        }

        public double preferredRangeOr(double fallback) {
            return positive(preferredRange, fallback);
        }

        public double orbitRadiusOr(double fallback) {
            return positive(orbitRadius, fallback);
        }

        private static double positive(double value, double fallback) {
            return Double.isFinite(value) && value > 0 ? value : fallback;
        }

        private static int positive(int value, int fallback) {
            return value > 0 ? value : fallback;
        }

        private static int nonNegative(int value, int fallback) {
            return value >= 0 ? value : fallback;
        }

        private static double nonNegative(double value, double fallback) {
            return Double.isFinite(value) && value >= 0 ? value : fallback;
        }
    }
}
