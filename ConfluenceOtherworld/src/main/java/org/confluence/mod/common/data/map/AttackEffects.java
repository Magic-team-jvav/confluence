package org.confluence.mod.common.data.map;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/// 按攻击入口配置效果。普通参数为基础，专家覆盖基础，大师再覆盖专家；每个效果独立判定。
public record AttackEffects(List<Effect> contact, List<Effect> melee, List<Effect> projectile) {
    public static final Codec<AttackEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Fields.optional(Effect.CODEC.listOf(), "contact", List.of()).forGetter(AttackEffects::contact),
            Fields.optional(Effect.CODEC.listOf(), "melee", List.of()).forGetter(AttackEffects::melee),
            Fields.optional(Effect.CODEC.listOf(), "projectile", List.of()).forGetter(AttackEffects::projectile)
    ).apply(instance, AttackEffects::new));

    /// 只由服务端的伤害完成事件调用；射弹查自身配置，难度使用发射者所在位置。
    public static void afterDamage(LivingEntity target, DamageSource source) {
        Entity direct = source.getDirectEntity();
        if (target.level().isClientSide || direct == null) return;
        boolean ranged = direct instanceof Projectile;
        if (!ranged && !(source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO) || source.is(DamageTypes.PLAYER_ATTACK)))
            return;
        AttackEffects data = ModDataMaps.getEntityData(ModDataMaps.ATTACK_EFFECTS, direct);
        if (data == null) return;
        List<Effect> effects = ranged ? data.projectile : direct instanceof BaseMonster monster && monster.isPerformingContactAttack() ? data.contact : data.melee;
        Entity attacker = source.getEntity() == null ? direct : source.getEntity();
        boolean master = LibUtils.isMaster(attacker.level(), attacker.blockPosition());
        boolean expert = master || LibUtils.isAtLeastExpert(attacker.level(), attacker.blockPosition());
        RandomSource random = target.getRandom();
        for (Effect effect : effects) {
            if (effect.condition.isPresent() && !effect.condition.get().matches(target)) continue;
            Parameters parameters = effect.parameters();
            if (expert) parameters = parameters.overlay(effect.expert);
            if (master) parameters = parameters.overlay(effect.master);
            if (parameters.chance.orElseThrow() <= random.nextDouble()) continue;
            Duration duration = parameters.duration.orElseThrow();
            int ticks = duration.min + random.nextInt(duration.max - duration.min + 1);
            target.addEffect(new MobEffectInstance(effect.effect, ticks, parameters.amplifier.orElseThrow()), attacker);
        }
    }

    public record Duration(int min, int max) {
        private static final Codec<Duration> RANGE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("min").forGetter(Duration::min),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("max").forGetter(Duration::max)
        ).apply(instance, Duration::new));
        public static final Codec<Duration> CODEC = Codec.either(Codec.intRange(1, Integer.MAX_VALUE), RANGE_CODEC)
                .flatXmap(value -> {
                    Duration duration = value.map(ticks -> new Duration(ticks, ticks), range -> range);
                    return duration.min <= duration.max ? DataResult.success(duration) : DataResult.error(() -> "duration.min must not exceed duration.max");
                }, duration -> duration.min > 0 && duration.min <= duration.max
                        ? DataResult.success(duration.min == duration.max ? Either.left(duration.min) : Either.right(duration))
                        : DataResult.error(() -> "Invalid duration range"));
    }

    /// 可选字段用于逐级覆盖，未填写的值保留上一难度配置。
    public record Parameters(Optional<Double> chance, Optional<Duration> duration,
                             Optional<Integer> amplifier) {
        public static final Parameters EMPTY = new Parameters(Optional.empty(), Optional.empty(), Optional.empty());
        public static final Codec<Parameters> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Fields.optional(Codec.doubleRange(0, 1), "chance").forGetter(Parameters::chance),
                Fields.optional(Duration.CODEC, "duration").forGetter(Parameters::duration),
                Fields.optional(Codec.intRange(0, 255), "amplifier").forGetter(Parameters::amplifier)
        ).apply(instance, Parameters::new));

        public Parameters overlay(Parameters overrides) {
            return new Parameters(overrides.chance.or(() -> chance), overrides.duration.or(() -> duration), overrides.amplifier.or(() -> amplifier));
        }
    }

    public record Effect(MobEffect effect, double chance, Duration duration, int amplifier,
                         Parameters expert, Parameters master, Optional<Condition> condition) {
        public static final Codec<Effect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("effect").forGetter(Effect::effect),
                Codec.doubleRange(0, 1).fieldOf("chance").forGetter(Effect::chance),
                Duration.CODEC.fieldOf("duration").forGetter(Effect::duration),
                Fields.optional(Codec.intRange(0, 255), "amplifier", 0).forGetter(Effect::amplifier),
                Fields.optional(Parameters.CODEC, "expert", Parameters.EMPTY).forGetter(Effect::expert),
                Fields.optional(Parameters.CODEC, "master", Parameters.EMPTY).forGetter(Effect::master),
                Fields.optional(Condition.CODEC, "condition").forGetter(Effect::condition)
        ).apply(instance, Effect::new));

        public Parameters parameters() {
            return new Parameters(Optional.of(chance), Optional.of(duration), Optional.of(amplifier));
        }
    }

    /// 1.20 的 optionalFieldOf 会吞掉非法值；配置存在但无法解析时必须报告错误。
    private static final class Fields {
        private static <A> MapCodec<Optional<A>> optional(Codec<A> codec, String name) {
            return new MapCodec<>() {
                @Override
                public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
                    T value = input.get(name);
                    return value == null ? DataResult.success(Optional.empty()) : codec.parse(ops, value).map(Optional::of);
                }

                @Override
                public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                    input.ifPresent(value -> prefix.add(name, codec.encodeStart(ops, value)));
                    return prefix;
                }

                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.of(ops.createString(name));
                }
            };
        }

        private static <A> MapCodec<A> optional(Codec<A> codec, String name, A defaultValue) {
            return optional(codec, name).xmap(value -> value.orElse(defaultValue),
                    value -> Objects.equals(value, defaultValue) ? Optional.empty() : Optional.of(value));
        }
    }

    public enum Condition implements StringRepresentable {
        NOT_FROZEN_IMMUNE,
        CAN_BE_CHILLED;

        public static final Codec<Condition> CODEC = StringRepresentable.fromEnum(Condition::values);

        public boolean matches(LivingEntity target) {
            return !TCUtils.hasType(target, TCItems.FROZEN$IMMUNE)
                    && (this != CAN_BE_CHILLED || !target.hasEffect(ModEffects.FROZEN.get()));
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
