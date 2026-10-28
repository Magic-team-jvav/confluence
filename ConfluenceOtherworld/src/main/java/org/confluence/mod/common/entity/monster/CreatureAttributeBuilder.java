package org.confluence.mod.common.entity.monster;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.data.map.CreatureDefinition;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyRules;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.*;

/// 本体生物注册共用的属性模板构建器。
// 基础属性、特殊状态和射弹参数共同声明；状态切换仍由实体负责。
public final class CreatureAttributeBuilder {
    private final Holder<Attribute> attackDamageAttribute;
    private final AttributeSupplier.Builder attributes;
    private final AttributeSupplier defaults;
    private final Map<Holder<Attribute>, Double> registeredValues = new LinkedHashMap<>();
    private final Map<Enum<?>, State> states = new LinkedHashMap<>();
    private final Map<EntityType<?>, Projectile> projectiles = new LinkedHashMap<>();
    private final Map<GamePhase, PhaseBuilder> phases = new LinkedHashMap<>();
    private boolean enemy, boss;
    private Double armorPenetration;

    /// 以已有属性模板创建构建器，保存所用攻击属性；后续登记不会修改传入的模板。
    private CreatureAttributeBuilder(AttributeSupplier attributes, Holder<Attribute> attackDamageAttribute) {
        this.defaults = attributes;
        this.attributes = new AttributeSupplier.Builder(attributes);
        this.attackDamageAttribute = attackDamageAttribute;
    }

    /// 登记属性覆盖值并保存到模板；同属性再次登记会替换旧值，不会累加。
    public CreatureAttributeBuilder add(Holder<Attribute> attribute, double value) {
        registeredValues.put(attribute, value);
        attributes.add(attribute, value);
        return this;
    }

    /// 将属性登记为该属性类型的默认值，并替换此前对该属性的登记。
    public CreatureAttributeBuilder add(Holder<Attribute> attribute) {
        registeredValues.put(attribute, attribute.value().getDefaultValue());
        attributes.add(attribute);
        return this;
    }

    /// 登记一个枚举状态的默认属性与行为参数。configure 立即配置构建器，动态取值留到运行时；重复的状态键会抛出 IllegalArgumentException。
    public CreatureAttributeBuilder state(Enum<?> key, Consumer<StateBuilder> configure) {
        StateBuilder builder = new StateBuilder(attackDamageAttribute);
        configure.accept(builder);
        if (states.putIfAbsent(key, builder.build()) != null)
            throw new IllegalArgumentException("Duplicate state " + key);
        return this;
    }

    /// 为指定射弹类型登记默认参数；重复类型会抛出 IllegalArgumentException。数值覆盖与难度修正在发射时解析。
    public CreatureAttributeBuilder projectile(Supplier<? extends EntityType<?>> type, Consumer<ProjectileBuilder> configure) {
        ProjectileBuilder builder = new ProjectileBuilder();
        configure.accept(builder);
        if (projectiles.putIfAbsent(type.get(), builder.build()) != null)
            throw new IllegalArgumentException("Duplicate projectile " + type.get());
        return this;
    }

    /// 生成不可变的属性、状态、射弹及阶段定义；敌怪基础属性在此规范取整并推导穿透，难度倍率留到实体应用时处理。
    public Definition build() {
        if (enemy) {
            double damage = Math.ceil(registeredValue(attackDamageAttribute));
            attributes.add(attackDamageAttribute, damage);
            attributes.add(Attributes.MAX_HEALTH, Math.ceil(registeredValue(Attributes.MAX_HEALTH)));
            attributes.add(Attributes.ARMOR, Math.ceil(registeredValue(Attributes.ARMOR)));
            attributes.add(LibAttributes.getArmorPenetration(), armorPenetration == null ? CreatureDifficultyRules.penetration(damage) : armorPenetration);
            attributes.add(Attributes.ARMOR_TOUGHNESS, Math.clamp(Math.floor(registeredValue(Attributes.ARMOR_TOUGHNESS)), 0, CreatureDifficultyRules.MAX_TOUGHNESS));
        }
        AttributeSupplier supplier = attributes.build();
        Map<GamePhase, AttributeModifiersValue> phaseModifiers = new LinkedHashMap<>();
        phases.forEach((phase, builder) -> phaseModifiers.put(phase, builder.build(supplier)));
        return new Definition(supplier, Map.copyOf(states), Map.copyOf(projectiles), enemy, boss, Map.copyOf(phaseModifiers));
    }

    /// 阶段成长也在实体注册处声明；各阶段均相对初始模板，不逐阶段累乘。
    public CreatureAttributeBuilder phase(GamePhase phase, Consumer<PhaseBuilder> configure) {
        PhaseBuilder builder = new PhaseBuilder(attackDamageAttribute);
        configure.accept(builder);
        if (phases.putIfAbsent(phase, builder) != null)
            throw new IllegalArgumentException("Duplicate phase " + phase);
        return this;
    }

    public static final class PhaseBuilder {
        private final Map<Holder<Attribute>, Double> values = new LinkedHashMap<>();
        private final Map<Holder<Attribute>, Double> multipliers = new LinkedHashMap<>();

        private final Holder<Attribute> attackDamage;

        /// 保存当前实体模板的攻击属性，确保阶段成长写入正确的属性类型。
        private PhaseBuilder(Holder<Attribute> attackDamage) {this.attackDamage = attackDamage;}

        /// 阶段填写换算后的专家基础值，与实体基础注册使用相同单位。
        public PhaseBuilder maxHealth(double value) {return add(Attributes.MAX_HEALTH, value);}

        /// 穿透由换算后的基础攻击推导；阶段值不预乘难度倍率。
        public PhaseBuilder attackDamage(double value) {
            return add(attackDamage, value).add(LibAttributes.getArmorPenetration(), CreatureDifficultyRules.penetration(value));
        }

        /// 覆盖该世界阶段的基础护甲，填写换算后的专家基础值；不叠加到上一阶段，也不预乘难度倍率。
        public PhaseBuilder armor(double value) {return add(Attributes.ARMOR, value);}

        /// 覆盖该世界阶段的基础韧性，填写换算后的专家基础值；不叠加到上一阶段，也不预乘难度倍率。
        public PhaseBuilder armorToughness(double value) {return add(Attributes.ARMOR_TOUGHNESS, value);}

        /// 覆盖阶段基础值；同属性同时设置倍率时，先覆盖再乘算。
        ///
        /// @param attribute 要覆盖的属性
        /// @param value 换算后的有限基础数值，不包含游戏难度倍率
        /// @return 当前阶段构建器
        /// @throws IllegalArgumentException 数值为 NaN 或无穷大
        public PhaseBuilder add(Holder<Attribute> attribute, double value) {
            if (!Double.isFinite(value))
                throw new IllegalArgumentException("Invalid phase value " + value);
            values.put(attribute, value);
            return this;
        }

        /// 相对初始模板的倍率，例如 1.5 表示基础值的 150%；若同属性有阶段覆盖，则乘在覆盖值上。
        ///
        /// @param attribute  要乘算的属性
        /// @param multiplier 有限且非负的倍率，1 表示不变
        /// @return 当前阶段构建器
        /// @throws IllegalArgumentException 倍率为负数、NaN 或无穷大
        public PhaseBuilder multiply(Holder<Attribute> attribute, double multiplier) {
            if (!Double.isFinite(multiplier) || multiplier < 0)
                throw new IllegalArgumentException("Invalid phase multiplier " + multiplier);
            multipliers.put(attribute, multiplier);
            return this;
        }

        /// 转为既有阶段数据映射；覆盖用加法差值，倍率用独立总倍率。
        private AttributeModifiersValue build(AttributeSupplier attributes) {
            AttributeModifiersValue.Builder builder = AttributeModifiersValue.builder();
            var id = Confluence.asResource("game_phase_modifier");
            values.forEach((attribute, value) -> {
                double base = attributes.hasAttribute(attribute) ? attributes.getBaseValue(attribute) : attribute.value().getDefaultValue();
                builder.add(attribute, id, value - base, AttributeModifier.Operation.ADD_VALUE);
            });
            multipliers.forEach((attribute, value) -> builder.add(attribute,
                    values.containsKey(attribute) ? Confluence.asResource("game_phase_multiplier") : id,
                    value - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            return builder.build();
        }
    }

    /// 在模板已有默认值上乘算；覆盖用 add，乘算用 multiply，不另存第二份基础值。
    public CreatureAttributeBuilder multiply(Holder<Attribute> attribute, double multiplier) {
        if (!Double.isFinite(multiplier) || multiplier < 0)
            throw new IllegalArgumentException("Invalid attribute multiplier " + multiplier);
        return add(attribute, registeredValue(attribute) * multiplier);
    }

    /// 读取登记值或初始模板，不提前 build，避免原版构建器被冻结。
    private double registeredValue(Holder<Attribute> attribute) {
        Double value = registeredValues.get(attribute);
        return value != null ? value : defaults.hasAttribute(attribute) ? defaults.getBaseValue(attribute) : attribute.value().getDefaultValue();
    }

    /// 专家穿透默认由接触伤害推导；只有需要微调的物种才显式覆盖。
    public CreatureAttributeBuilder armorPenetration(double value) {
        armorPenetration = Math.max(1, Math.min(CreatureDifficultyRules.MAX_PENETRATION, value));
        return this;
    }

    /// 登记已转换的专家基础韧性，不传入 Wiki 防御原值。
    public CreatureAttributeBuilder armorToughness(double value) {return add(Attributes.ARMOR_TOUGHNESS, value);}

    /// 标记模板参与本体敌怪难度系统；boss 为 true 时使用 Boss 的生命及攻击倍率。
    private CreatureAttributeBuilder enemy(boolean boss) {
        this.enemy = true;
        this.boss = boss;
        return this;
    }

    /// 创建普通敌怪属性模板，默认移动速度为 0.25、跟随范围为 32 格、攻击击退为 1、击退抗性为 0.28。
    public static CreatureAttributeBuilder creature() {
        Holder<Attribute> attackDamage = LibAttributes.getAttackDamage();
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(attackDamage)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.28)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.SCALE, 1.0D)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0)
                .build(), attackDamage).enemy(false);
    }

    /// 创建仅含原版生物基础属性的模板，供动物、展示实体和其他非敌怪实体使用。
    public static CreatureAttributeBuilder critter() {
        return new CreatureAttributeBuilder(Mob.createMobAttributes().build(), Attributes.ATTACK_DAMAGE);
    }

    /// 从给定属性构建器生成初始模板，默认使用原版攻击属性；不会自动标记为敌怪或应用难度规则。
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
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.2)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .build(), Attributes.ATTACK_DAMAGE).enemy(false);
    }

    /// 创建水生敌怪属性模板；水下导航、呼吸和游动行为仍由实体实现。
    public static CreatureAttributeBuilder aquatic() {
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE)
                .add(Attributes.KNOCKBACK_RESISTANCE)
                .build(), Attributes.ATTACK_DAMAGE).enemy(false);
    }

    /// 创建 Boss 属性模板，默认移动速度为 0.23、跟随范围为 64 格、击退抗性为 1。
    public static CreatureAttributeBuilder boss() {
        Holder<Attribute> attackDamage = Attributes.ATTACK_DAMAGE;
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(attackDamage)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.SCALE, 1.0D)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0)
                .build(), attackDamage).enemy(true);
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
        add(LibAttributes.getAttackDamage(), value);
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
        add(Attributes.WATER_MOVEMENT_EFFICIENCY, value);
        return this;
    }

    /// 设置摔落伤害倍率；0 表示不承受普通摔落伤害。
    public CreatureAttributeBuilder fallDamageMultiplier(double value) {
        add(Attributes.FALL_DAMAGE_MULTIPLIER, value);
        return this;
    }

    /// 设置普通摔落伤害计算中的安全距离，不等同于免疫摔落伤害。
    ///
    /// @param value 不计入摔落伤害的基础距离，单位为格
    public CreatureAttributeBuilder safeFallDistance(double value) {
        add(Attributes.SAFE_FALL_DISTANCE, value);
        return this;
    }

    /// 设置基础重力属性；无重力状态或自定义移动逻辑可能跳过该属性。
    ///
    /// @param value 重力加速度属性值，常规运动中对应每 tick 的竖直速度减量
    public CreatureAttributeBuilder gravity(double value) {
        add(Attributes.GRAVITY, value);
        return this;
    }

    /// 设置基础跳跃力度，不直接指定跳跃高度，也不主动触发跳跃。
    ///
    /// @param value 跳跃初始向上速度的属性值，实际高度还受重力和跳跃逻辑影响
    public CreatureAttributeBuilder jumpStrength(double value) {
        add(Attributes.JUMP_STRENGTH, value);
        return this;
    }

    /// 设置移动时无需跳跃即可跨上的最大台阶高度。
    ///
    /// @param value 台阶高度，单位为格
    public CreatureAttributeBuilder stepHeight(double value) {
        add(Attributes.STEP_HEIGHT, value);
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
                             Map<EntityType<?>, Projectile> projectiles, boolean enemy,
                             boolean boss,
                             Map<GamePhase, AttributeModifiersValue> phases) {}

    public record Projectile(ToDoubleFunction<Mob> damage, double speed, double knockback,
                             double inaccuracy, int lifetime, boolean fromAttackAttribute) {
        /// 对 owner 求出射弹基础参数，通过 CreatureDifficultyScaling 处理伤害难度，返回供发射逻辑使用的参数；负值保留为未指定。
        public CreatureDefinition.ProjectileOverrides parameters(Mob owner) {
            return new CreatureDefinition.ProjectileOverrides(CreatureDifficultyScaling.projectileDamage(owner,
                    damage.applyAsDouble(owner), fromAttackAttribute), speed, knockback, inaccuracy, lifetime);
        }
    }

    public static final class ProjectileBuilder {
        private ToDoubleFunction<Mob> damage = mob -> -1;
        private double speed = -1, knockback = -1, inaccuracy = -1;
        private int lifetime = -1;
        private boolean fromAttackAttribute;

        /// 登记独立射弹的专家基础伤害，固定值向上取整；发射时由公共入口应用一次难度倍率。
        public ProjectileBuilder damage(double value) {
            damage = mob -> Math.ceil(value);
            fromAttackAttribute = false;
            return this;
        }

        /// 登记独立射弹的动态专家基础伤害；函数应返回未乘难度倍率的值，本重载不自动取整。
        public ProjectileBuilder damage(ToDoubleFunction<Mob> value) {
            damage = value;
            fromAttackAttribute = false;
            return this;
        }

        /// 使用当前攻击属性的指定倍率，不再额外叠加难度倍率。
        public ProjectileBuilder attackDamage(double multiplier) {
            return attackDamage(mob -> multiplier);
        }

        /// 独立射弹登记伤害的微调倍率；仍使用公共特殊攻击难度倍率。
        public ProjectileBuilder damageMultiplier(double multiplier) {
            if (!Double.isFinite(multiplier) || multiplier < 0)
                throw new IllegalArgumentException("Invalid projectile multiplier " + multiplier);
            ToDoubleFunction<Mob> previous = damage;
            damage = mob -> previous.applyAsDouble(mob) * multiplier;
            return this;
        }

        /// 比例可读取特殊状态或行为覆盖，难度只由公共入口结算一次。
        public ProjectileBuilder attackDamage(ToDoubleFunction<Mob> multiplier) {
            damage = mob -> mob.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier.applyAsDouble(mob);
            fromAttackAttribute = true;
            return this;
        }

        /// 对已声明的弹幕伤害按攻击属性缩放；基准值显式登记，避免调接触伤害时暗中改变分母。
        public ProjectileBuilder scaleWithAttack(double referenceAttack) {
            if (referenceAttack <= 0)
                throw new IllegalArgumentException("Reference attack must be positive");
            ToDoubleFunction<Mob> baseDamage = damage;
            damage = mob -> baseDamage.applyAsDouble(mob) * mob.getAttributeValue(Attributes.ATTACK_DAMAGE) / referenceAttack;
            fromAttackAttribute = true;
            return this;
        }

        /// 设置射弹初速度，单位格/tick；负值表示未指定，由发射逻辑采用默认值。
        public ProjectileBuilder speed(double value) {
            speed = value;
            return this;
        }

        /// 设置射弹击退强度；负值表示未指定，不是生物自身的击退抗性。
        public ProjectileBuilder knockback(double value) {
            knockback = value;
            return this;
        }

        /// 设置射弹发射散布参数，0 表示无额外散布；负值表示沿用发射逻辑默认值。
        public ProjectileBuilder inaccuracy(double value) {
            inaccuracy = value;
            return this;
        }

        /// 设置射弹存活时间，单位 tick；负值表示未指定。
        public ProjectileBuilder lifetime(int value) {
            lifetime = value;
            return this;
        }

        /// 冻结当前射弹参数及伤害来源标记；动态伤害函数保留到实际发射时求值。
        private Projectile build() {return new Projectile(damage, speed, knockback, inaccuracy, lifetime, fromAttackAttribute);}
    }

    /// 注册默认值的公共动态取值器；只组合基础数值，不应用难度倍率，也不修改实体属性。
    /// 实体回调只提供当前状态，运算结果仍由 CreatureDefinition 覆盖及原有状态系统应用。
    public record DynamicValue(ToDoubleFunction<Mob> source) implements ToDoubleFunction<Mob> {
        /// 对 mob 重新计算表达式，不缓存结果，也不修改实体属性。
        ///
        /// @param mob 当前取值的实体
        /// @return 当前表达式的计算结果
        @Override
        public double applyAsDouble(Mob mob) {return source.applyAsDouble(mob);}

        /// 创建固定数值表达式；求值时始终返回传入的 value。
        ///
        /// @param value 与使用位置采用相同单位的固定数值
        /// @return 固定值表达式
        public static DynamicValue value(double value) {return value(mob -> value);}

        /// 包装实体状态读取函数，创建时不求值，每次读取时重新执行 source。
        ///
        /// @param source 接收当前实体的取值函数
        /// @return 延迟调用 source 的动态表达式
        public static DynamicValue value(ToDoubleFunction<Mob> source) {return new DynamicValue(source);}

        /// 肉前、肉后、花后的基础值选择；只选当前阶段，不累计叠加。
        public static DynamicValue byWorldPhase(double early, double hardmode, double postPlantera) {
            return value(mob -> {
                GamePhase phase = KillBoard.INSTANCE.getGamePhase();
                return phase.isAtLeast(GamePhase.PLANTERA) ? postPlantera : phase.isHardmode() ? hardmode : early;
            });
        }

        /// 供移动速度等非属性倍率参数选择难度值；攻击与生命仍使用统一属性缩放。
        public static DynamicValue byDifficulty(double classic, double expert, double master, double legendary) {
            return value(mob -> switch (CreatureDifficultyScaling.difficulty(mob)) {
                case CLASSIC -> classic;
                case EXPERT -> expert;
                case MASTER -> master;
                case LEGENDARY -> legendary;
            });
        }

        /// 读取已损失生命比例：1 - 当前生命 / 最大生命。正常生命范围内满血为 0、零血为 1；最大生命非正时返回 0，不额外钳制结果。
        ///
        /// @return 求值时读取生命比例的表达式
        public static DynamicValue healthLoss() {
            return value(mob -> mob.getMaxHealth() > 0 ? 1.0 - mob.getHealth() / mob.getMaxHealth() : 0);
        }

        /// 阈值为剩余血量比例；低于阈值时选择第一个值，否则选择第二个值。
        public static DynamicValue belowHealth(float threshold, double below, double otherwise) {
            return when(mob -> mob.getMaxHealth() > 0 && mob.getHealth() / mob.getMaxHealth() < threshold, below, otherwise);
        }

        /// 按当前实体的条件选择固定值，每次求值时重新判断，相当于 condition ? yes : no。
        ///
        /// @param condition 接收当前实体的条件
        /// @param yes       条件为 true 时返回的值
        /// @param no        条件为 false 时返回的值
        /// @return 延迟判断条件的动态表达式
        public static DynamicValue when(Predicate<Mob> condition, double yes, double no) {
            return when(condition, value(yes), value(no));
        }

        /// 按条件选择动态分支，只计算选中的分支，未选中的分支不会执行；条件与分支使用同一个实体。
        ///
        /// @param condition 接收当前实体的条件
        /// @param yes       条件为 true 时求值的表达式
        /// @param no        条件为 false 时求值的表达式
        /// @return 延迟选择并求值的表达式
        public static DynamicValue when(Predicate<Mob> condition, DynamicValue yes, DynamicValue no) {
            return value(mob -> (condition.test(mob) ? yes : no).applyAsDouble(mob));
        }

        /// 仅筛选专家机制是否启用，不提前乘难度倍率。
        public DynamicValue expertOnly(double classic) {
            return when(mob -> CreatureDifficultyScaling.difficulty(mob) != CreatureDifficultyRules.Difficulty.CLASSIC, this, value(classic));
        }

        /// 将当前结果乘以 multiplier，不取整、不应用额外难度修正。
        ///
        /// @param multiplier 乘数，例如 1.5 表示原值的 150%
        /// @return 乘算后的新表达式，原表达式保持不变
        public DynamicValue scale(double multiplier) {return value(mob -> applyAsDouble(mob) * multiplier);}

        /// 将当前结果除以 divisor，保留浮点结果。
        ///
        /// @param divisor 有限且非零的除数
        /// @return 除算后的新表达式
        /// @throws IllegalArgumentException 除数为零、NaN 或无穷大
        public DynamicValue divide(double divisor) {
            if (!Double.isFinite(divisor) || divisor == 0)
                throw new IllegalArgumentException("Invalid divisor " + divisor);
            return value(mob -> applyAsDouble(mob) / divisor);
        }

        /// 将当前结果增加固定 amount；负数表示减少。
        ///
        /// @param amount 与当前结果同单位的增量
        /// @return 加法计算的新表达式
        public DynamicValue add(double amount) {return value(mob -> applyAsDouble(mob) + amount);}

        /// 减去 amount 的动态结果，两边使用同一个实体求值。
        ///
        /// @param amount 与当前结果同单位的被减表达式
        /// @return 当前结果减去 amount 的新表达式
        public DynamicValue subtract(DynamicValue amount) {return value(mob -> applyAsDouble(mob) - amount.applyAsDouble(mob));}

        /// 向下取整，仍返回 double；负数向负无穷方向取整，例如 -1.2 得到 -2。
        ///
        /// @return 向下取整的新表达式
        public DynamicValue floor() {return value(mob -> Math.floor(applyAsDouble(mob)));}

        /// 向上取整，仍返回 double；例如 1.2 得到 2，-1.2 得到 -1。
        ///
        /// @return 向上取整的新表达式
        public DynamicValue ceil() {return value(mob -> Math.ceil(applyAsDouble(mob)));}

        /// 限制结果的下限，不限制上限，也不进行取整。
        ///
        /// @param minimum 与当前结果同单位的最小值
        /// @return 当前结果与 minimum 中较大值的表达式
        public DynamicValue atLeast(double minimum) {return value(mob -> Math.max(minimum, applyAsDouble(mob)));}

        /// 转为整数取值函数，求值时执行 Java 的 double 到 int 强制转换，小数向零截断。需要向上或向下取整时应先调用 ceil 或 floor。
        ///
        /// @return 每次对当前实体求值后转成 int 的函数
        public ToIntFunction<Mob> asInt() {return mob -> (int) applyAsDouble(mob);}
    }

    public record State(Map<Holder<Attribute>, Value> attributes,
                        Function<Mob, CreatureDefinition.StateOverrides> parameters) {}

    public record Value(ToDoubleFunction<Mob> amount, AttributeModifier.Operation operation,
                        boolean absolute) {}

    public static final class StateBuilder {
        private final Holder<Attribute> attackDamage;
        private final Map<Holder<Attribute>, Value> attributes = new LinkedHashMap<>();
        private ToIntFunction<Mob> duration = mob -> -1, attackInterval = mob -> -1, attackCount = mob -> -1;
        private int windupTicks = -1, attackIntervalVariance;
        private ToDoubleFunction<Mob> moveSpeed = mob -> -1, chargeSpeed = mob -> -1;

        /// 创建空状态定义，沿用实体模板的攻击属性；同属性重复登记时以后一次为准。
        private StateBuilder(Holder<Attribute> attackDamage) {this.attackDamage = attackDamage;}

        /// 设置状态的绝对属性值，以相对实体基础值的修饰符实现；不会直接改写基础属性，也不预乘难度倍率。
        public StateBuilder attribute(Holder<Attribute> attribute, double value) {
            if (value < 0)
                throw new IllegalArgumentException("Invalid state attribute " + value);
            attributes.put(attribute, new Value(mob -> value, AttributeModifier.Operation.ADD_VALUE, true));
            return this;
        }

        /// 设置状态的总乘算倍率，以 multiplier - 1 作为修饰符；1 表示不变，不会额外预乘游戏难度。
        public StateBuilder multiply(Holder<Attribute> attribute, double multiplier) {
            if (multiplier < 0)
                throw new IllegalArgumentException("Invalid state multiplier " + multiplier);
            attributes.put(attribute, new Value(mob -> multiplier - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false));
            return this;
        }

        /// 设置状态的加法增量，不是覆盖值；负数可用于削弱属性。
        public StateBuilder bonus(Holder<Attribute> attribute, double value) {
            attributes.put(attribute, new Value(mob -> value, AttributeModifier.Operation.ADD_VALUE, false));
            return this;
        }

        /// 设置状态的基础乘算倍率，使用 MULTIPLY_BASE 语义；1 表示不变，0 表示该项减去一份基础值。
        public StateBuilder multiplyBase(Holder<Attribute> attribute, double multiplier) {
            if (multiplier < 0)
                throw new IllegalArgumentException("Invalid state multiplier " + multiplier);
            attributes.put(attribute, new Value(mob -> multiplier - 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, false));
            return this;
        }

        /// 设置状态的绝对属性值，以相对实体基础值的修饰符实现；不会直接改写基础属性，也不预乘难度倍率。 动态函数在读取或刷新状态时求值，不在注册时执行。
        public StateBuilder attribute(Holder<Attribute> attribute, ToDoubleFunction<Mob> value) {
            attributes.put(attribute, new Value(value, AttributeModifier.Operation.ADD_VALUE, true));
            return this;
        }

        /// 设置状态的总乘算倍率，以 multiplier - 1 作为修饰符；1 表示不变，不会额外预乘游戏难度。 动态函数在读取或刷新状态时求值，不在注册时执行。
        public StateBuilder multiply(Holder<Attribute> attribute, ToDoubleFunction<Mob> value) {
            attributes.put(attribute, new Value(mob -> value.applyAsDouble(mob) - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false));
            return this;
        }

        /// 设置状态的加法增量，不是覆盖值；负数可用于削弱属性。 动态函数在读取或刷新状态时求值，不在注册时执行。
        public StateBuilder bonus(Holder<Attribute> attribute, ToDoubleFunction<Mob> value) {
            attributes.put(attribute, new Value(value, AttributeModifier.Operation.ADD_VALUE, false));
            return this;
        }

        /// 设置当前状态的基础攻击伤害，使用实体模板的攻击属性；不提前应用难度倍率。
        public StateBuilder attackDamage(double value) {return attribute(attackDamage, value);}

        /// 设置当前状态的基础护甲覆盖值，不是护甲增量。
        public StateBuilder armor(double value) {return attribute(Attributes.ARMOR, value);}

        /// 注册基础护甲、单位加成及状态来源；按 ceil(base + perUnit * units) 求值，不做泰拉原值转换。
        ///
        /// @param base    换算后的基础护甲
        /// @param perUnit 每单位状态量提供的护甲增量
        /// @param units   动态状态量，例如失血比例或剩余手臂数
        /// @return 当前状态构建器
        public StateBuilder armor(double base, double perUnit, DynamicValue units) {
            return attribute(Attributes.ARMOR, units.scale(perUnit).add(base).ceil());
        }

        /// 按 base + floor(perUnit * units) 生成基础韧性；难度偏移和上限留给 CreatureDifficultyScaling。
        ///
        /// @param base    换算后的基础韧性
        /// @param perUnit 每单位状态量提供的韧性增量
        /// @param units   动态状态量，例如失血比例或剩余手臂数
        /// @return 当前状态构建器
        public StateBuilder armorToughness(double base, double perUnit, DynamicValue units) {
            return attribute(Attributes.ARMOR_TOUGHNESS, units.scale(perUnit).floor().add(base));
        }

        /// 设置状态的 MOVEMENT_SPEED 属性覆盖值，与行为参数 moveSpeed 分开。
        public StateBuilder movementSpeed(double value) {return attribute(Attributes.MOVEMENT_SPEED, value);}

        /// 设置状态的击退抗性属性覆盖值，通常为 0～1。
        public StateBuilder knockbackResistance(double value) {return attribute(Attributes.KNOCKBACK_RESISTANCE, value);}

        /// 设置状态持续时间，单位 tick；非正值在参数合并时视为未指定。
        public StateBuilder duration(int value) {
            duration = mob -> value;
            return this;
        }

        /// 设置状态持续时间，单位 tick；非正值在参数合并时视为未指定。 动态函数在读取或刷新状态时求值，不在注册时执行。
        public StateBuilder duration(ToIntFunction<Mob> value) {
            duration = value;
            return this;
        }

        /// 设置攻击间隔，单位 tick；非正值在参数合并时视为未指定。
        public StateBuilder attackInterval(int value) {
            attackInterval = mob -> value;
            return this;
        }

        /// 设置攻击间隔，单位 tick；非正值在参数合并时视为未指定。 动态函数在读取或刷新状态时求值，不在注册时执行。
        public StateBuilder attackInterval(ToIntFunction<Mob> value) {
            attackInterval = value;
            return this;
        }

        /// 设置攻击间隔的闭区间 [minimum, maximum]，单位 tick；随机参数在运行时读取，不在注册时抽取。
        ///
        /// @param minimum 最小间隔，必须大于 0
        /// @param maximum 最大间隔，不得小于 minimum
        /// @return 当前状态构建器
        /// @throws IllegalArgumentException 区间无效时抛出
        public StateBuilder attackInterval(int minimum, int maximum) {
            if (minimum <= 0 || maximum < minimum)
                throw new IllegalArgumentException("Invalid attack interval range");
            attackInterval = mob -> minimum;
            attackIntervalVariance = maximum - minimum;
            return this;
        }

        /// 设置该状态的攻击次数；非正值在参数合并时视为未指定。
        public StateBuilder attackCount(int value) {
            attackCount = mob -> value;
            return this;
        }

        /// 设置该状态的攻击次数；非正值在参数合并时视为未指定。 动态函数在读取或刷新状态时求值，不在注册时执行。
        public StateBuilder attackCount(ToIntFunction<Mob> value) {
            attackCount = value;
            return this;
        }

        /// 设置攻击前摇时间，单位 tick；保存到状态行为参数，由具体行为读取。
        public StateBuilder windupTicks(int value) {
            windupTicks = value;
            return this;
        }

        /// 设置状态的行为移动速度参数，不直接修改 MOVEMENT_SPEED 属性；单位与倍率含义由调用该状态的移动行为决定。
        public StateBuilder moveSpeed(double value) {
            moveSpeed = mob -> value;
            return this;
        }

        /// 设置状态的行为移动速度参数，不直接修改 MOVEMENT_SPEED 属性；单位与倍率含义由调用该状态的移动行为决定。 动态函数在读取或刷新状态时求值，不在注册时执行。
        public StateBuilder moveSpeed(ToDoubleFunction<Mob> value) {
            moveSpeed = value;
            return this;
        }

        /// 设置状态的冲刺速度参数，由具体冲刺行为读取，不直接修改移动速度属性。
        public StateBuilder chargeSpeed(double value) {
            chargeSpeed = mob -> value;
            return this;
        }

        /// 设置状态的冲刺速度参数，由具体冲刺行为读取，不直接修改移动速度属性。 动态函数在读取或刷新状态时求值，不在注册时执行。
        public StateBuilder chargeSpeed(ToDoubleFunction<Mob> value) {
            chargeSpeed = value;
            return this;
        }

        /// 生成不可变的状态属性及行为参数读取函数；创建定义时不执行动态取值。
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
