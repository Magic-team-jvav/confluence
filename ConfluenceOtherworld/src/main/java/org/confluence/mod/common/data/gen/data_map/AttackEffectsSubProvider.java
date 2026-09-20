package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import org.confluence.lib.common.LibEffects;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.AttackEffects;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.mesdag.portlib.datamap.PortDataMapProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AttackEffectsSubProvider {
    public static void gather(ModDataMapProvider.Appender<Builder> appender) {
        appender.create()
                .add(MonsterEntities.ICE_SLIME, effects()
                        .contact(MobEffects.MOVEMENT_SLOWDOWN, effect()
                                .chance(1.0 / 12)
                                .duration(200)
                                .expert(value -> value.duration(400))
                                .master(value -> value.duration(500))
                                .condition(AttackEffects.Condition.CAN_BE_CHILLED)))
                .add(MonsterEntities.BLACK_SLIME, effects()
                        .contact(MobEffects.DARKNESS, effect()
                                .chance(0.25)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))))
                .add(MonsterEntities.CORRUPT_SLIME, effects()
                        .contact(MobEffects.DARKNESS, effect()
                                .chance(0.25)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))))
                .add(MonsterEntities.CRIMSLIME, effects()
                        .contact(MobEffects.DARKNESS, effect()
                                .chance(0.25)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))))
                .add(MonsterEntities.SLIMELING, effects()
                        .contact(MobEffects.DARKNESS, effect()
                                .chance(0.25)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))))
                .add(MonsterEntities.WINGLESS_SLIMER, effects()
                        .contact(MobEffects.DARKNESS, effect()
                                .chance(0.25)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))))
                .add(MonsterEntities.SPIKED_ICE_SLIME, effects()
                        .contact(MobEffects.MOVEMENT_SLOWDOWN, effect()
                                .chance(1)
                                .duration(400)
                                .expert(value -> value.duration(800))
                                .master(value -> value.duration(1000))
                                .condition(AttackEffects.Condition.NOT_FROZEN_IMMUNE))
                        .contact(ModEffects.FROZEN.get(), effect()
                                .chance(1.0 / 15)
                                .duration(20)
                                .expert(value -> value.chance(13.0 / 125).duration(40))
                                .master(value -> value.duration(50))
                                .condition(AttackEffects.Condition.NOT_FROZEN_IMMUNE)))
                .add(MonsterEntities.ANGLER_FISH, effects()
                        .melee(ModEffects.BLEEDING.get(), effect()
                                .chance(1.0 / 8)
                                .duration(900)
                                .expert(value -> value.duration(1800))
                                .master(value -> value.duration(2250))))
                .add(MonsterEntities.WEREWOLF, effects()
                        .melee(ModEffects.BLEEDING.get(), effect()
                                .chance(1.0 / 8)
                                .duration(900)
                                .expert(value -> value.duration(1800))
                                .master(value -> value.duration(2250))))
                .add(MonsterEntities.BLACK_RECLUSE, effects()
                        .melee(ModEffects.ACID_VENOM.get(), effect()
                                .chance(0.1)
                                .duration(80)
                                .expert(value -> value.duration(160))
                                .master(value -> value.duration(200))))
                .add(MonsterEntities.JUNGLE_CREEPER, effects()
                        .melee(ModEffects.ACID_VENOM.get(), effect()
                                .chance(0.1)
                                .duration(80)
                                .expert(value -> value.duration(160))
                                .master(value -> value.duration(200))))
                .add(MonsterEntities.SAND_POACHER, effects()
                        .melee(ModEffects.ACID_VENOM.get(), effect()
                                .chance(1)
                                .duration(80)
                                .expert(value -> value.duration(160))
                                .master(value -> value.duration(200))))
                .add(MonsterEntities.CURSED_SKULL, effects()
                        .contact(ModEffects.CURSED.get(), effect()
                                .chance(1.0 / 3)
                                .duration(80)
                                .expert(value -> value.duration(160))
                                .master(value -> value.duration(200))))
                .add(MonsterEntities.ENCHANTED_SWORD, effects()
                        .contact(ModEffects.CURSED.get(), effect()
                                .chance(1.0 / 3)
                                .duration(80)
                                .expert(value -> value.duration(160))
                                .master(value -> value.duration(200))))
                .add(MonsterEntities.GREEN_JELLYFISH, effects()
                        .contact(ModEffects.SILENCED.get(), effect()
                                .chance(0.2)
                                .duration(140)
                                .expert(value -> value.duration(280))
                                .master(value -> value.duration(350))))
                .add(MonsterEntities.WRAITH, effects()
                        .contact(MobEffects.MOVEMENT_SLOWDOWN, effect()
                                .chance(1.0 / 8)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))))
                .add(MonsterEntities.ARMORED_SKELETON, effects()
                        .melee(ModEffects.BROKEN_ARMOR.get(), effect()
                                .chance(1.0 / 6)
                                .duration(2400)
                                .expert(value -> value.duration(4800))
                                .master(value -> value.duration(6000))))
                .add(MonsterEntities.GIANT_BAT, effects()
                        .contact(LibEffects.CONFUSED.get(), effect()
                                .chance(1.0 / 14)
                                .duration(100)
                                .expert(value -> value.duration(200))
                                .master(value -> value.duration(250))))
                .add(MonsterEntities.PIXIE, effects()
                        .contact(ModEffects.SILENCED.get(), effect()
                                .chance(0.1)
                                .duration(140)
                                .expert(value -> value.duration(280))
                                .master(value -> value.duration(350)))
                        .contact(MobEffects.MOVEMENT_SLOWDOWN, effect()
                                .chance(1.0 / 8)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))))
                .add(MonsterEntities.ICE_BAT, effects()
                        .contact(MobEffects.MOVEMENT_SLOWDOWN, effect()
                                .chance(1.0 / 15)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))
                                .condition(AttackEffects.Condition.NOT_FROZEN_IMMUNE))
                        .contact(ModEffects.FROZEN.get(), effect()
                                .chance(1.0 / 35)
                                .duration(20)
                                .expert(value -> value
                                        .chance(69.0 / 1225)
                                        .duration(40))
                                .master(value -> value.duration(50))
                                .condition(AttackEffects.Condition.NOT_FROZEN_IMMUNE)))
                .add(MonsterEntities.MUMMY, effects()
                        .melee(MobEffects.MOVEMENT_SLOWDOWN, effect()
                                .chance(1.0 / 8)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))))
                .add(MonsterEntities.LIGHT_MUMMY, effects()
                        .melee(LibEffects.CONFUSED.get(), effect()
                                .chance(1.0 / 14)
                                .duration(100)
                                .expert(value -> value.duration(200))
                                .master(value -> value.duration(250))))
                .add(MonsterEntities.VILE_GHOUL, effects()
                        .melee(ModEffects.CURSED_INFERNO.get(), effect()
                                .chance(1)
                                .duration(140)
                                .expert(value -> value.duration(280))
                                .master(value -> value.duration(350))))
                .add(MonsterEntities.TAINTED_GHOUL, effects()
                        .melee(ModEffects.ICHOR.get(), effect()
                                .chance(1)
                                .duration(140)
                                .expert(value -> value.duration(280))
                                .master(value -> value.duration(350))))
                .add(MonsterEntities.DREAMER_GHOUL, effects()
                        .melee(LibEffects.CONFUSED.get(), effect()
                                .chance(1)
                                .duration(280)
                                .expert(value -> value.duration(560))
                                .master(value -> value.duration(700))))
                .add(MonsterEntities.DARK_MUMMY, effects()
                        .melee(ModEffects.SILENCED.get(), effect()
                                .chance(0.2)
                                .duration(140)
                                .expert(value -> value.duration(280))
                                .master(value -> value.duration(350)))
                        .melee(MobEffects.DARKNESS, effect()
                                .chance(0.25)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))))
                .add(MonsterEntities.BLOOD_MUMMY, effects()
                        .melee(ModEffects.SILENCED.get(), effect()
                                .chance(0.2)
                                .duration(140)
                                .expert(value -> value.duration(280))
                                .master(value -> value.duration(350)))
                        .melee(MobEffects.DARKNESS, effect()
                                .chance(0.25)
                                .duration(300)
                                .expert(value -> value.duration(600))
                                .master(value -> value.duration(750))));
    }

    /// 创建一个实体的效果集合；接触、近战和射弹列表相互独立，多次添加会依次保留。
    public static EffectsBuilder effects() {
        return new EffectsBuilder();
    }

    /// 创建单个效果的基础参数。chance、duration 必填，amplifier 默认为 0（一级）。
    public static EffectBuilder effect() {
        return new EffectBuilder();
    }

    public static class Builder extends PortDataMapProvider.Builder<AttackEffects, EntityType<?>> {
        /// 为攻击效果 Data Map 创建数据生成器。
        public Builder() {
            super(ModDataMaps.ATTACK_EFFECTS);
        }

        /// 将效果集合绑定到实体注册项；射弹效果应绑定到具体射弹的 EntityType。
        public Builder add(Supplier<? extends EntityType<?>> type, EffectsBuilder effects) {
            super.add(Objects.requireNonNull(type.get().builtInRegistryHolder().getKey()), effects.build(), false);
            return this;
        }
    }

    public static final class EffectsBuilder {
        private final List<AttackEffects.Effect> contact = new ArrayList<>();
        private final List<AttackEffects.Effect> melee = new ArrayList<>();
        private final List<AttackEffects.Effect> projectile = new ArrayList<>();

        /// 添加身体接触攻击的效果，仅在明确标记为接触攻击且实际造成伤害后触发。
        public EffectsBuilder contact(MobEffect effect, EffectBuilder parameters) {
            contact.add(parameters.build(effect));
            return this;
        }

        /// 添加主动近战攻击的效果，不用于接触扫描或射弹命中。
        public EffectsBuilder melee(MobEffect effect, EffectBuilder parameters) {
            melee.add(parameters.build(effect));
            return this;
        }

        /// 添加射弹命中效果；查射弹自身的配置，难度使用发射者的位置，未找到发射者时使用射弹位置。
        public EffectsBuilder projectile(MobEffect effect, EffectBuilder parameters) {
            projectile.add(parameters.build(effect));
            return this;
        }

        /// 构建不可变效果集合。各效果独立抽取触发概率，允许一次命中施加多个效果。
        public AttackEffects build() {
            return new AttackEffects(contact, melee, projectile);
        }
    }

    public static class ParametersBuilder {
        private Optional<Double> chance = Optional.empty();
        private Optional<AttackEffects.Duration> duration = Optional.empty();
        private Optional<Integer> amplifier = Optional.empty();

        /// 每次有效命中的触发概率，范围 [0, 1]：0 不触发，1 必定触发，0.25 表示 25%。
        /// 写分数时使用浮点数，例如 1.0 / 12，避免整数除法得到 0。
        public ParametersBuilder chance(double chance) {
            if (chance < 0 || chance > 1)
                throw new IllegalArgumentException("chance must be between 0 and 1");
            this.chance = Optional.of(chance);
            return this;
        }

        /// 固定持续时间，单位为游戏 tick（20 tick = 1 秒），必须大于 0。
        public ParametersBuilder duration(int ticks) {
            return duration(ticks, ticks);
        }

        /// 随机持续时间，单位为 tick；触发后在 \[min, max\] 闭区间内均匀抽取整数。
        /// min 必须大于 0，max 不小于 min；两者相等时等同于固定持续时间。
        public ParametersBuilder duration(int min, int max) {
            if (min <= 0 || max < min) throw new IllegalArgumentException("Invalid duration range");
            duration = Optional.of(new AttackEffects.Duration(min, max));
            return this;
        }

        /// 效果等级使用从 0 开始的数值：0 为一级，1 为二级，允许范围 [0, 255]。
        public ParametersBuilder amplifier(int amplifier) {
            if (amplifier < 0 || amplifier > 255)
                throw new IllegalArgumentException("amplifier must be between 0 and 255");
            this.amplifier = Optional.of(amplifier);
            return this;
        }

        /// 构建参数覆盖值。未填写的字段保持为空，运行时继承上一难度的对应值。
        public AttackEffects.Parameters build() {
            return new AttackEffects.Parameters(chance, duration, amplifier);
        }
    }

    public static final class EffectBuilder extends ParametersBuilder {
        private AttackEffects.Parameters expert = AttackEffects.Parameters.EMPTY;
        private AttackEffects.Parameters master = AttackEffects.Parameters.EMPTY;
        private Optional<AttackEffects.Condition> condition = Optional.empty();

        /// 设置触发概率 [0, 1]，例如 0.25 为 25%。
        @Override
        public EffectBuilder chance(double chance) {
            super.chance(chance);
            return this;
        }

        /// 设置固定持续时间，单位 tick，20 tick = 1 秒；必须大于 0。
        @Override
        public EffectBuilder duration(int ticks) {
            super.duration(ticks);
            return this;
        }

        /// 设置持续时间闭区间 \[min, max\]，触发时均匀取整数 tick。
        @Override
        public EffectBuilder duration(int min, int max) {
            super.duration(min, max);
            return this;
        }

        /// 设置从 0 开始的效果等级，0 为一级，1 为二级。
        @Override
        public EffectBuilder amplifier(int amplifier) {
            super.amplifier(amplifier);
            return this;
        }

        /// 配置专家模式覆盖项：未填写的字段继承基础参数，大师模式也会继承这些覆盖。
        /// 如需同时修改概率和时长，在同一个回调中配置；重复调用会替换此前的专家覆盖。
        public EffectBuilder expert(Consumer<ParametersBuilder> configure) {
            ParametersBuilder builder = new ParametersBuilder();
            configure.accept(builder);
            expert = builder.build();
            return this;
        }

        /// 配置大师模式覆盖项：先应用基础与专家参数，再覆盖这里填写的字段。
        /// 重复调用会替换此前的大师覆盖；不会影响普通和专家模式。
        public EffectBuilder master(Consumer<ParametersBuilder> configure) {
            ParametersBuilder builder = new ParametersBuilder();
            configure.accept(builder);
            master = builder.build();
            return this;
        }

        /// 设置施加效果前的额外条件；未设置时不附加条件，目标自身的效果免疫判定仍然生效。
        /// 当前只保留一个条件，重复调用会替换此前的条件。
        public EffectBuilder condition(AttackEffects.Condition condition) {
            this.condition = Optional.of(condition);
            return this;
        }

        /// 绑定效果并构建配置；缺少基础概率或持续时间时立即报错，避免生成不完整数据。
        public AttackEffects.Effect build(MobEffect effect) {
            AttackEffects.Parameters base = build();
            return new AttackEffects.Effect(effect,
                    base.chance().orElseThrow(() -> new IllegalStateException("Missing effect chance")),
                    base.duration().orElseThrow(() -> new IllegalStateException("Missing effect duration")),
                    base.amplifier().orElse(0), expert, master, condition);
        }
    }
}
