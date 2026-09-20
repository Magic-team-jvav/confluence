package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.data.map.CreatureDefinition;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.*;

/// 本体生物注册共用的属性模板构建器。
// 基础属性、特殊状态和射弹参数共同声明；状态切换仍由实体负责。
public final class CreatureAttributeBuilder {
    private final Attribute attackDamageAttribute;
    private final AttributeSupplier.Builder attributes;
    private final Map<Enum<?>, State> states = new LinkedHashMap<>();
    private final Map<EntityType<?>, Projectile> projectiles = new LinkedHashMap<>();

    private CreatureAttributeBuilder(AttributeSupplier attributes, Attribute attackDamageAttribute) {
        this.attributes = new AttributeSupplier.Builder(attributes);
        this.attackDamageAttribute = attackDamageAttribute;
    }

    public CreatureAttributeBuilder add(Attribute attribute, double value) {
        attributes.add(attribute, value);
        return this;
    }

    public CreatureAttributeBuilder add(Attribute attribute) {
        attributes.add(attribute);
        return this;
    }

    public CreatureAttributeBuilder state(Enum<?> key, Consumer<StateBuilder> configure) {
        StateBuilder builder = new StateBuilder(attackDamageAttribute);
        configure.accept(builder);
        if (states.putIfAbsent(key, builder.build()) != null)
            throw new IllegalArgumentException("Duplicate state " + key);
        return this;
    }

    public CreatureAttributeBuilder projectile(Supplier<? extends EntityType<?>> type, Consumer<ProjectileBuilder> configure) {
        ProjectileBuilder builder = new ProjectileBuilder();
        configure.accept(builder);
        if (projectiles.putIfAbsent(type.get(), builder.build()) != null)
            throw new IllegalArgumentException("Duplicate projectile " + type.get());
        return this;
    }

    public Definition build() {
        return new Definition(attributes.build(), Map.copyOf(states), Map.copyOf(projectiles));
    }

    /// 创建普通敌怪属性模板，默认移动速度为 0.25、跟随范围为 32 格、攻击击退为 1、击退抗性为 0.28。
    public static CreatureAttributeBuilder creature() {
        Attribute attackDamage = LibAttributes.getAttackDamage().value();
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(attackDamage)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.28)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.SCALE.value(), 1.0D)
                .add(Attributes.SAFE_FALL_DISTANCE.value(), 8.0)
                .build(), attackDamage);
    }

    /// 创建仅含原版生物基础属性的模板，供动物、展示实体和其他非敌怪实体使用。
    public static CreatureAttributeBuilder critter() {
        return new CreatureAttributeBuilder(Mob.createMobAttributes().build(), Attributes.ATTACK_DAMAGE);
    }

    public static CreatureAttributeBuilder from(AttributeSupplier.Builder attributes) {
        return new CreatureAttributeBuilder(attributes.build(), Attributes.ATTACK_DAMAGE);
    }

    /// 创建地面昆虫属性模板。
    public static CreatureAttributeBuilder insect() {
        return critter().maxHealth(3).movementSpeed(0.18)
                .stepHeight(0.3).fallDamageMultiplier(0);
    }

    /// 创建飞行小动物属性模板。
    public static CreatureAttributeBuilder flyingCritter() {
        return insect().flyingSpeed(0.25).magicLibAttackDamage(3);
    }

    /// 创建原版兔子属性模板。
    public static CreatureAttributeBuilder rabbit() {
        return new CreatureAttributeBuilder(Rabbit.createAttributes().build(), Attributes.ATTACK_DAMAGE);
    }

    /// 创建仅含生命实体基础属性的模板。
    public static CreatureAttributeBuilder living() {
        return new CreatureAttributeBuilder(LivingEntity.createLivingAttributes().build(), Attributes.ATTACK_DAMAGE);
    }

    /// 创建史莱姆属性模板，默认移动速度为 0.2、跟随范围为 16 格、水中移动效率为 0.2。
    public static CreatureAttributeBuilder slime() {
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY.value(), 0.2)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .build(), Attributes.ATTACK_DAMAGE);
    }

    /// 创建水生敌怪属性模板；水下导航、呼吸和游动行为仍由实体实现。
    public static CreatureAttributeBuilder aquatic() {
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE)
                .add(Attributes.KNOCKBACK_RESISTANCE)
                .build(), Attributes.ATTACK_DAMAGE);
    }

    /// 创建 Boss 属性模板，默认移动速度为 1、跟随范围为 300 格、击退抗性为 1。
    public static CreatureAttributeBuilder boss() {
        Attribute attackDamage = Attributes.ATTACK_DAMAGE;
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(attackDamage)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.SCALE.value(), 1.0D)
                .add(Attributes.SAFE_FALL_DISTANCE.value(), 8.0)
                .build(), attackDamage);
    }

    /// 创建城镇 NPC 属性模板。
    public static CreatureAttributeBuilder npc() {
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.MAX_HEALTH, 250.0)
                .add(Attributes.ARMOR, 15.0)
                .add(Attributes.MOVEMENT_SPEED, 0.15)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .build(), Attributes.ATTACK_DAMAGE)
                .attackDamage(10.0);
    }

    /// 设置最大生命值，2 点生命值对应一颗心。
    public CreatureAttributeBuilder maxHealth(double value) {
        add(Attributes.MAX_HEALTH, value);
        return this;
    }

    /// 设置基础护甲值。
    public CreatureAttributeBuilder armor(double value) {
        add(Attributes.ARMOR, value);
        return this;
    }

    /// 设置当前模板使用的基础攻击伤害属性。
    public CreatureAttributeBuilder attackDamage(double value) {
        add(attackDamageAttribute, value);
        return this;
    }

    /// 设置 MagicLib 攻击伤害属性，供明确使用该属性的实体使用。
    public CreatureAttributeBuilder magicLibAttackDamage(double value) {
        add(LibAttributes.getAttackDamage().value(), value);
        return this;
    }

    /// 设置跟随范围，单位为格；实际索敌仍受 AI 条件限制。
    public CreatureAttributeBuilder followRange(double value) {
        add(Attributes.FOLLOW_RANGE, value);
        return this;
    }

    /// 设置攻击造成的额外击退强度，不是生物自身的击退抗性。
    public CreatureAttributeBuilder attackKnockback(double value) {
        add(Attributes.ATTACK_KNOCKBACK, value);
        return this;
    }

    /// 设置基础移动速度属性，实际速度由移动控制器、AI 速度倍率等共同决定。
    ///
    /// @param value 移动速度属性值，不是每秒移动的格数
    public CreatureAttributeBuilder movementSpeed(double value) {
        add(Attributes.MOVEMENT_SPEED, value);
        return this;
    }

    /// 设置基础飞行速度属性，仅影响读取该属性的移动逻辑，不会使生物获得飞行能力。
    ///
    /// @param value 飞行速度属性值，不是每秒移动的格数
    public CreatureAttributeBuilder flyingSpeed(double value) {
        add(Attributes.FLYING_SPEED, value);
        return this;
    }

    /// 设置水中移动效率属性。
    public CreatureAttributeBuilder waterMovementEfficiency(double value) {
        add(Attributes.WATER_MOVEMENT_EFFICIENCY.value(), value);
        return this;
    }

    /// 设置摔落伤害倍率；0 表示不承受普通摔落伤害。
    public CreatureAttributeBuilder fallDamageMultiplier(double value) {
        add(Attributes.FALL_DAMAGE_MULTIPLIER.value(), value);
        return this;
    }

    /// 设置普通摔落伤害计算中的安全距离，不等同于免疫摔落伤害。
    ///
    /// @param value 不计入摔落伤害的基础距离，单位为格
    public CreatureAttributeBuilder safeFallDistance(double value) {
        add(Attributes.SAFE_FALL_DISTANCE.value(), value);
        return this;
    }

    /// 设置基础重力属性；无重力状态或自定义移动逻辑可能跳过该属性。
    ///
    /// @param value 重力加速度属性值，常规运动中对应每 tick 的竖直速度减量
    public CreatureAttributeBuilder gravity(double value) {
        add(Attributes.GRAVITY.value(), value);
        return this;
    }

    /// 设置基础跳跃力度，不直接指定跳跃高度，也不主动触发跳跃。
    ///
    /// @param value 跳跃初始向上速度的属性值，实际高度还受重力和跳跃逻辑影响
    public CreatureAttributeBuilder jumpStrength(double value) {
        add(Attributes.JUMP_STRENGTH_1211.value(), value);
        return this;
    }

    /// 设置移动时无需跳跃即可跨上的最大台阶高度。
    ///
    /// @param value 台阶高度，单位为格
    public CreatureAttributeBuilder stepHeight(double value) {
        add(Attributes.STEP_HEIGHT.value(), value);
        return this;
    }

    /// 设置生物的基础击退抗性，减弱受到攻击时的击退，不影响自身攻击的击退力度。
    ///
    /// @param value 击退抗性，范围为 0～1；0 表示不减免，1 表示完全抵抗受该属性控制的击退
    public CreatureAttributeBuilder knockbackResistance(double value) {
        add(Attributes.KNOCKBACK_RESISTANCE, value);
        return this;
    }

    /// 设置召唤增援概率，仅影响读取该属性的增援逻辑，不会自动给生物添加召唤行为。
    ///
    /// @param value 增援概率，范围为 0～1；0 表示不触发，1 表示其他条件满足时必定触发
    public CreatureAttributeBuilder spawnReinforcementsChance(double value) {
        add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, value);
        return this;
    }

    public record Definition(AttributeSupplier attributes, Map<Enum<?>, State> states,
                             Map<EntityType<?>, Projectile> projectiles) {}

    public record Projectile(ToDoubleFunction<Mob> damage, double speed, double knockback,
                             double inaccuracy, int lifetime) {
        public CreatureDefinition.ProjectileOverrides parameters(Mob owner) {
            return new CreatureDefinition.ProjectileOverrides(damage.applyAsDouble(owner), speed, knockback, inaccuracy, lifetime);
        }
    }

    public static final class ProjectileBuilder {
        private ToDoubleFunction<Mob> damage = mob -> -1;
        private double speed = -1, knockback = -1, inaccuracy = -1;
        private int lifetime = -1;

        public ProjectileBuilder damage(double value) {
            damage = mob -> value;
            return this;
        }

        public ProjectileBuilder damage(ToDoubleFunction<Mob> value) {
            damage = value;
            return this;
        }

        public ProjectileBuilder speed(double value) {
            speed = value;
            return this;
        }

        public ProjectileBuilder knockback(double value) {
            knockback = value;
            return this;
        }

        public ProjectileBuilder inaccuracy(double value) {
            inaccuracy = value;
            return this;
        }

        public ProjectileBuilder lifetime(int value) {
            lifetime = value;
            return this;
        }

        private Projectile build() {return new Projectile(damage, speed, knockback, inaccuracy, lifetime);}
    }

    public record State(Map<Attribute, Value> attributes,
                        Function<Mob, CreatureDefinition.StateOverrides> parameters) {}

    public record Value(ToDoubleFunction<Mob> amount, AttributeModifier.Operation operation,
                        boolean absolute) {}

    public static final class StateBuilder {
        private final Attribute attackDamage;
        private final Map<Attribute, Value> attributes = new LinkedHashMap<>();
        private ToIntFunction<Mob> duration = mob -> -1, attackInterval = mob -> -1, attackCount = mob -> -1;
        private int windupTicks = -1, attackIntervalVariance;
        private ToDoubleFunction<Mob> moveSpeed = mob -> -1, chargeSpeed = mob -> -1;

        private StateBuilder(Attribute attackDamage) {this.attackDamage = attackDamage;}

        public StateBuilder attribute(Attribute attribute, double value) {
            if (value < 0)
                throw new IllegalArgumentException("Invalid state attribute " + value);
            attributes.put(attribute, new Value(mob -> value, AttributeModifier.Operation.ADDITION, true));
            return this;
        }

        public StateBuilder multiply(Attribute attribute, double multiplier) {
            if (multiplier < 0)
                throw new IllegalArgumentException("Invalid state multiplier " + multiplier);
            attributes.put(attribute, new Value(mob -> multiplier - 1, AttributeModifier.Operation.MULTIPLY_TOTAL, false));
            return this;
        }

        public StateBuilder bonus(Attribute attribute, double value) {
            attributes.put(attribute, new Value(mob -> value, AttributeModifier.Operation.ADDITION, false));
            return this;
        }

        public StateBuilder multiplyBase(Attribute attribute, double multiplier) {
            if (multiplier < 0)
                throw new IllegalArgumentException("Invalid state multiplier " + multiplier);
            attributes.put(attribute, new Value(mob -> multiplier - 1, AttributeModifier.Operation.MULTIPLY_BASE, false));
            return this;
        }

        public StateBuilder attribute(Attribute attribute, ToDoubleFunction<Mob> value) {
            attributes.put(attribute, new Value(value, AttributeModifier.Operation.ADDITION, true));
            return this;
        }

        public StateBuilder multiply(Attribute attribute, ToDoubleFunction<Mob> value) {
            attributes.put(attribute, new Value(mob -> value.applyAsDouble(mob) - 1, AttributeModifier.Operation.MULTIPLY_TOTAL, false));
            return this;
        }

        public StateBuilder bonus(Attribute attribute, ToDoubleFunction<Mob> value) {
            attributes.put(attribute, new Value(value, AttributeModifier.Operation.ADDITION, false));
            return this;
        }

        public StateBuilder attackDamage(double value) {return attribute(attackDamage, value);}

        public StateBuilder armor(double value) {return attribute(Attributes.ARMOR, value);}

        public StateBuilder movementSpeed(double value) {return attribute(Attributes.MOVEMENT_SPEED, value);}

        public StateBuilder knockbackResistance(double value) {return attribute(Attributes.KNOCKBACK_RESISTANCE, value);}

        public StateBuilder duration(int value) {
            duration = mob -> value;
            return this;
        }

        public StateBuilder duration(ToIntFunction<Mob> value) {
            duration = value;
            return this;
        }

        public StateBuilder attackInterval(int value) {
            attackInterval = mob -> value;
            return this;
        }

        public StateBuilder attackInterval(ToIntFunction<Mob> value) {
            attackInterval = value;
            return this;
        }

        public StateBuilder attackInterval(int minimum, int maximum) {
            if (minimum <= 0 || maximum < minimum)
                throw new IllegalArgumentException("Invalid attack interval range");
            attackInterval = mob -> minimum;
            attackIntervalVariance = maximum - minimum;
            return this;
        }

        public StateBuilder attackCount(int value) {
            attackCount = mob -> value;
            return this;
        }

        public StateBuilder attackCount(ToIntFunction<Mob> value) {
            attackCount = value;
            return this;
        }

        public StateBuilder windupTicks(int value) {
            windupTicks = value;
            return this;
        }

        public StateBuilder moveSpeed(double value) {
            moveSpeed = mob -> value;
            return this;
        }

        public StateBuilder moveSpeed(ToDoubleFunction<Mob> value) {
            moveSpeed = value;
            return this;
        }

        public StateBuilder chargeSpeed(double value) {
            chargeSpeed = mob -> value;
            return this;
        }

        public StateBuilder chargeSpeed(ToDoubleFunction<Mob> value) {
            chargeSpeed = value;
            return this;
        }

        private State build() {
            var move = moveSpeed;
            var charge = chargeSpeed;
            var ticks = duration;
            var interval = attackInterval;
            var count = attackCount;
            int windup = windupTicks, variance = attackIntervalVariance;
            return new State(Map.copyOf(attributes), mob -> new CreatureDefinition.StateOverrides(
                    CreatureDefinition.AttributeOverrides.EMPTY,
                    new CreatureDefinition.BehaviorOverrides(move.applyAsDouble(mob), -1, -1, -1, -1, -1,
                            charge.applyAsDouble(mob), windup, -1, -1, -1, -1, -1, -1, -1, -1),
                    ticks.applyAsInt(mob), interval.applyAsInt(mob), count.applyAsInt(mob), variance));
        }
    }
}
