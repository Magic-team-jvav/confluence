package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.common.init.ModDataMaps;

import java.util.Map;

/// 生物与 Boss 共用的数据包数值定义。
///
/// 该记录只保存可安全热重载的“数值配置”，不保存实体实例、行为树节点或 Forge 对象。
// 默认值在实体注册的 CreatureAttributeBuilder 中声明；数据包仅保存覆盖值。
/// 数据文件位于 {@code data/<命名空间>/data_maps/entity_type/creature_definition.json}；
/// KubeJS 也可以用标准实体类型 Data Map 写入相同结构，无需依赖本体内部 Java 类。
/// 未填写的字段统一以负数表示“沿用 Java 侧默认值”，
/// 从而允许整合包只覆盖自己关心的参数。
///
/// 这里是稳定的数据格式边界。外部模组与脚本应写入 JSON，而不是直接持有加载器的内部映射；
/// 这样既能参与标准资源包优先级，也能在 `/reload` 时与其他数据包一起原子生效。
public record CreatureDefinition(AttributeOverrides attributes, BehaviorOverrides behavior,
                                 BossOverrides boss, Map<String, StateOverrides> specialStates,
                                 Map<ResourceLocation, ProjectileOverrides> projectiles) {
    /// 未找到定义或定义未提供任何覆盖值时使用的不可变空对象。
    public static final CreatureDefinition EMPTY = new CreatureDefinition(AttributeOverrides.EMPTY, BehaviorOverrides.EMPTY, BossOverrides.EMPTY);

    /// 数据包编解码入口。属性、行为和 Boss 三个区块都可省略，便于数据包只调整一个维度。
    public static final Codec<CreatureDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AttributeOverrides.CODEC.optionalFieldOf("attributes", AttributeOverrides.EMPTY).forGetter(CreatureDefinition::attributes),
            BehaviorOverrides.CODEC.optionalFieldOf("behavior", BehaviorOverrides.EMPTY).forGetter(CreatureDefinition::behavior),
            BossOverrides.CODEC.optionalFieldOf("boss", BossOverrides.EMPTY).forGetter(CreatureDefinition::boss),
            Codec.unboundedMap(Codec.STRING, StateOverrides.CODEC).optionalFieldOf("special_states", Map.of()).forGetter(CreatureDefinition::specialStates),
            Codec.unboundedMap(ResourceLocation.CODEC, ProjectileOverrides.CODEC).optionalFieldOf("projectiles", Map.of()).forGetter(CreatureDefinition::projectiles)
    ).apply(instance, CreatureDefinition::new));

    public CreatureDefinition {
        specialStates = Map.copyOf(specialStates);
        projectiles = Map.copyOf(projectiles);
    }

    public CreatureDefinition(AttributeOverrides attributes, BehaviorOverrides behavior, BossOverrides boss) {
        this(attributes, behavior, boss, Map.of(), Map.of());
    }

    /// 返回实体类型对应的覆盖数据；没有定义时返回共享空对象。
    public static CreatureDefinition get(EntityType<?> type) {
        CreatureDefinition definition = ModDataMaps.getEntityData(ModDataMaps.CREATURE_DEFINITION, type);
        return definition == null ? EMPTY : definition;
    }

    public StateOverrides state(Enum<?> state) {
        return specialStates.getOrDefault(state.name().toLowerCase(java.util.Locale.ROOT), StateOverrides.EMPTY);
    }

    /// 将当前实体类型 Data Map 中的属性基础值覆盖应用到生物实例。
    public static void applyAttributes(Mob mob) {
        AttributeOverrides overrides = get(mob.getType()).attributes();
        float oldHealth = mob.getHealth();
        float oldMaxHealth = mob.getMaxHealth();
        boolean wasFullHealth = Math.abs(oldHealth - oldMaxHealth) < 0.001F;
        setBaseValue(mob, Attributes.MAX_HEALTH, overrides.maxHealth());
        setBaseValue(mob, Attributes.ATTACK_DAMAGE, overrides.attackDamage());
        setBaseValue(mob, Attributes.ARMOR, overrides.armor());
        setBaseValue(mob, Attributes.MOVEMENT_SPEED, overrides.movementSpeed());
        setBaseValue(mob, Attributes.FOLLOW_RANGE, overrides.followRange());
        setBaseValue(mob, Attributes.KNOCKBACK_RESISTANCE, overrides.knockbackResistance());
        setBaseValue(mob, Attributes.SCALE.value(), overrides.scale());
        if (wasFullHealth) {
            mob.setHealth(mob.getMaxHealth());
        } else if (oldHealth > mob.getMaxHealth()) {
            mob.setHealth(mob.getMaxHealth());
        }
    }

    private static void setBaseValue(Mob mob, Attribute attribute, double value) {
        if (!Double.isFinite(value) || value < 0.0D) return;
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) instance.setBaseValue(value);
    }

    // 状态由各实体定义；公共数据层只描述属性、行为和时序，不枚举生物技能。
    public record StateOverrides(AttributeOverrides attributes, BehaviorOverrides behavior,
                                 int duration, int attackInterval, int attackCount,
                                 int attackIntervalVariance) {
        public static final StateOverrides EMPTY = new StateOverrides(AttributeOverrides.EMPTY, BehaviorOverrides.EMPTY, -1, -1, -1, -1);
        public static final Codec<StateOverrides> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                AttributeOverrides.CODEC.optionalFieldOf("attributes", AttributeOverrides.EMPTY).forGetter(StateOverrides::attributes),
                BehaviorOverrides.CODEC.optionalFieldOf("behavior", BehaviorOverrides.EMPTY).forGetter(StateOverrides::behavior),
                Codec.intRange(-1, Integer.MAX_VALUE).optionalFieldOf("duration", -1).forGetter(StateOverrides::duration),
                Codec.intRange(-1, Integer.MAX_VALUE).optionalFieldOf("attack_interval", -1).forGetter(StateOverrides::attackInterval),
                Codec.intRange(-1, Integer.MAX_VALUE).optionalFieldOf("attack_count", -1).forGetter(StateOverrides::attackCount),
                Codec.intRange(-1, Integer.MAX_VALUE - 1).optionalFieldOf("attack_interval_variance", -1).forGetter(StateOverrides::attackIntervalVariance)
        ).apply(instance, StateOverrides::new));

        public int durationOr(int fallback) {return duration > 0 ? duration : fallback;}

        public int attackIntervalOr(int fallback) {return attackInterval > 0 ? attackInterval : fallback;}

        public int attackCountOr(int fallback) {return attackCount > 0 ? attackCount : fallback;}

        public StateOverrides withDefaults(StateOverrides defaults) {
            return new StateOverrides(attributes, behavior.withDefaults(defaults.behavior),
                    durationOr(defaults.duration), attackIntervalOr(defaults.attackInterval), attackCountOr(defaults.attackCount),
                    attackIntervalVariance >= 0 ? attackIntervalVariance : defaults.attackIntervalVariance);
        }

        public int randomAttackInterval(net.minecraft.util.RandomSource random) {
            return (int) Math.min(Integer.MAX_VALUE, (long) attackInterval +
                    (attackIntervalVariance > 0 ? random.nextInt(attackIntervalVariance + 1) : 0));
        }
    }

    // 以发射者的 Data Map 和射弹实体类型定位；数值只在发射时读取。
    public record ProjectileOverrides(double damage, double speed, double knockback,
                                      double inaccuracy, int lifetime) {
        public static final ProjectileOverrides EMPTY = new ProjectileOverrides(-1, -1, -1, -1, -1);
        public static final Codec<ProjectileOverrides> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(-1, Float.MAX_VALUE).optionalFieldOf("damage", -1.0).forGetter(ProjectileOverrides::damage),
                Codec.doubleRange(-1, Float.MAX_VALUE).optionalFieldOf("speed", -1.0).forGetter(ProjectileOverrides::speed),
                Codec.doubleRange(-1, Float.MAX_VALUE).optionalFieldOf("knockback", -1.0).forGetter(ProjectileOverrides::knockback),
                Codec.doubleRange(-1, Float.MAX_VALUE).optionalFieldOf("inaccuracy", -1.0).forGetter(ProjectileOverrides::inaccuracy),
                Codec.intRange(-1, Integer.MAX_VALUE).optionalFieldOf("lifetime", -1).forGetter(ProjectileOverrides::lifetime)
        ).apply(instance, ProjectileOverrides::new));

        public static ProjectileOverrides get(Mob owner, EntityType<?> projectile) {
            var registered = org.confluence.mod.common.init.entity.ModEntities.creatureAttributes(owner.getType());
            var declared = registered == null ? null : registered.projectiles().get(projectile);
            ProjectileOverrides defaults = declared == null ? EMPTY : declared.parameters(owner);
            ProjectileOverrides override = CreatureDefinition.get(owner.getType()).projectiles().getOrDefault(BuiltInRegistries.ENTITY_TYPE.getKey(projectile), EMPTY);
            return new ProjectileOverrides(override.damageOr((float) defaults.damage), override.speedOr((float) defaults.speed),
                    override.knockbackOr((float) defaults.knockback), override.inaccuracyOr((float) defaults.inaccuracy), override.lifetimeOr(defaults.lifetime));
        }

        public float damageOr(float fallback) {return valid(damage) ? (float) damage : fallback;}

        public float speedOr(float fallback) {return valid(speed) ? (float) speed : fallback;}

        public float knockbackOr(float fallback) {return valid(knockback) ? (float) knockback : fallback;}

        public float inaccuracyOr(float fallback) {return valid(inaccuracy) ? (float) inaccuracy : fallback;}

        public int lifetimeOr(int fallback) {return lifetime >= 0 ? lifetime : fallback;}

        private static boolean valid(double value) {return Double.isFinite(value) && value >= 0;}
    }

    /// 可选的原版属性基础值覆盖。
    ///
    /// 这些数值在实体完成属性实例初始化后写入基础值，不创建永久修饰符，避免多次加载叠加。
    public record AttributeOverrides(double maxHealth, double attackDamage, double armor,
                                     double movementSpeed, double followRange,
                                     double knockbackResistance, double scale) {
        public static final AttributeOverrides EMPTY = new AttributeOverrides(-1, -1, -1, -1, -1, -1, -1);
        public static final Codec<AttributeOverrides> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("max_health", -1.0).forGetter(AttributeOverrides::maxHealth),
                Codec.DOUBLE.optionalFieldOf("attack_damage", -1.0).forGetter(AttributeOverrides::attackDamage),
                Codec.DOUBLE.optionalFieldOf("armor", -1.0).forGetter(AttributeOverrides::armor),
                Codec.DOUBLE.optionalFieldOf("movement_speed", -1.0).forGetter(AttributeOverrides::movementSpeed),
                Codec.DOUBLE.optionalFieldOf("follow_range", -1.0).forGetter(AttributeOverrides::followRange),
                Codec.DOUBLE.optionalFieldOf("knockback_resistance", -1.0).forGetter(AttributeOverrides::knockbackResistance),
                Codec.DOUBLE.optionalFieldOf("scale", -1.0).forGetter(AttributeOverrides::scale)
        ).apply(instance, AttributeOverrides::new));
    }

    /// Boss 专属的跨攻击类型覆盖。
    ///
    /// {@code damage_multiplier} 在伤害进入受害者前统一应用，因此既覆盖普通近战属性，
    /// 也覆盖手臂、体节、冲刺和带有 Boss 所有者的弹幕。负数或未填写表示 1 倍。
    public record BossOverrides(double damageMultiplier) {
        public static final BossOverrides EMPTY = new BossOverrides(-1.0D);
        public static final Codec<BossOverrides> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("damage_multiplier", -1.0D).forGetter(BossOverrides::damageMultiplier)
        ).apply(instance, BossOverrides::new));

        public double damageMultiplierOr(double fallback) {
            return Double.isFinite(damageMultiplier) && damageMultiplier >= 0.0D ? damageMultiplier : fallback;
        }
    }

    /// 通用行为树参数覆盖。
    ///
    /// 字段按照行为能力而非具体生物命名：近战、冲锋、远程和飞行模板只读取自己需要的字段。
    /// 因此新增简单生物时可以复用同一格式，不必为每个实体增加独立 Codec。
    public record BehaviorOverrides(double moveSpeed, double meleeRange, double attackRange,
                                    double wanderSpeed,
                                    int wanderRadius, int idleTicks, double chargeSpeed,
                                    int windupTicks, int shotCooldown, double shotMultiplier,
                                    double projectileSpeed, double preferredRange,
                                    double retreatRange,
                                    double orbitSpeed, double orbitRadius,
                                    double healthRegeneration) {
        public static final BehaviorOverrides EMPTY = new BehaviorOverrides(-1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1);
        public static final Codec<BehaviorOverrides> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("move_speed", -1.0).forGetter(BehaviorOverrides::moveSpeed),
                Codec.DOUBLE.optionalFieldOf("melee_range", -1.0).forGetter(BehaviorOverrides::meleeRange),
                Codec.DOUBLE.optionalFieldOf("attack_range", -1.0).forGetter(BehaviorOverrides::attackRange),
                Codec.DOUBLE.optionalFieldOf("wander_speed", -1.0).forGetter(BehaviorOverrides::wanderSpeed),
                Codec.INT.optionalFieldOf("wander_radius", -1).forGetter(BehaviorOverrides::wanderRadius),
                Codec.INT.optionalFieldOf("idle_ticks", -1).forGetter(BehaviorOverrides::idleTicks),
                Codec.DOUBLE.optionalFieldOf("charge_speed", -1.0).forGetter(BehaviorOverrides::chargeSpeed),
                Codec.INT.optionalFieldOf("windup_ticks", -1).forGetter(BehaviorOverrides::windupTicks),
                Codec.INT.optionalFieldOf("shot_cooldown", -1).forGetter(BehaviorOverrides::shotCooldown),
                Codec.DOUBLE.optionalFieldOf("shot_multiplier", -1.0).forGetter(BehaviorOverrides::shotMultiplier),
                Codec.DOUBLE.optionalFieldOf("projectile_speed", -1.0).forGetter(BehaviorOverrides::projectileSpeed),
                Codec.DOUBLE.optionalFieldOf("preferred_range", -1.0).forGetter(BehaviorOverrides::preferredRange),
                Codec.DOUBLE.optionalFieldOf("retreat_range", -1.0).forGetter(BehaviorOverrides::retreatRange),
                Codec.DOUBLE.optionalFieldOf("orbit_speed", -1.0).forGetter(BehaviorOverrides::orbitSpeed),
                Codec.DOUBLE.optionalFieldOf("orbit_radius", -1.0).forGetter(BehaviorOverrides::orbitRadius),
                Codec.DOUBLE.optionalFieldOf("health_regeneration", -1.0).forGetter(BehaviorOverrides::healthRegeneration)
        ).apply(instance, BehaviorOverrides::new));

        public BehaviorOverrides withDefaults(BehaviorOverrides defaults) {
            return new BehaviorOverrides(moveSpeed >= 0 ? moveSpeed : defaults.moveSpeed,
                    meleeRange >= 0 ? meleeRange : defaults.meleeRange,
                    attackRange >= 0 ? attackRange : defaults.attackRange,
                    wanderSpeed >= 0 ? wanderSpeed : defaults.wanderSpeed,
                    wanderRadius >= 0 ? wanderRadius : defaults.wanderRadius,
                    idleTicks >= 0 ? idleTicks : defaults.idleTicks,
                    chargeSpeed >= 0 ? chargeSpeed : defaults.chargeSpeed,
                    windupTicks >= 0 ? windupTicks : defaults.windupTicks,
                    shotCooldown >= 0 ? shotCooldown : defaults.shotCooldown,
                    shotMultiplier >= 0 ? shotMultiplier : defaults.shotMultiplier,
                    projectileSpeed >= 0 ? projectileSpeed : defaults.projectileSpeed,
                    preferredRange >= 0 ? preferredRange : defaults.preferredRange,
                    retreatRange >= 0 ? retreatRange : defaults.retreatRange,
                    orbitSpeed >= 0 ? orbitSpeed : defaults.orbitSpeed,
                    orbitRadius >= 0 ? orbitRadius : defaults.orbitRadius,
                    healthRegeneration >= 0 ? healthRegeneration : defaults.healthRegeneration);
        }

        public double moveSpeedOr(double fallback) {
            return positive(moveSpeed, fallback);
        }

        public double meleeRangeOr(double fallback) {
            return positive(meleeRange, fallback);
        }

        public double attackRangeOr(double fallback) {
            return positive(attackRange, fallback);
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

        public double projectileSpeedOr(double fallback) {
            return positive(projectileSpeed, fallback);
        }

        public double orbitSpeedOr(double fallback) {
            return positive(orbitSpeed, fallback);
        }

        public double preferredRangeOr(double fallback) {
            return positive(preferredRange, fallback);
        }

        public double retreatRangeOr(double fallback) {
            return nonNegative(retreatRange, fallback);
        }

        public double orbitRadiusOr(double fallback) {
            return positive(orbitRadius, fallback);
        }

        public double healthRegenerationOr(double fallback) {
            return nonNegative(healthRegeneration, fallback);
        }

        private static double positive(double value, double fallback) {
            return value > 0 ? value : fallback;
        }

        private static int positive(int value, int fallback) {
            return value > 0 ? value : fallback;
        }

        private static int nonNegative(int value, int fallback) {
            return value >= 0 ? value : fallback;
        }

        private static double nonNegative(double value, double fallback) {
            return value >= 0 ? value : fallback;
        }
    }
}
