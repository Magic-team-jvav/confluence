package org.confluence.mod.common.init.armor;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.api.primitive.CombineRule;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;

import java.util.List;

public record MobEffectInstancesValue(List<Effect> effects) implements PrimitiveValue<List<MobEffectInstancesValue.Effect>> {
    public static final Codec<MobEffectInstancesValue> CODEC = Effect.CODEC.listOf().xmap(MobEffectInstancesValue::new, MobEffectInstancesValue::effects);
    public static final CombineRule<List<Effect>, MobEffectInstancesValue> MERGE = CombineRule.register((a, b) -> {
        List<Effect> list = Lists.newArrayList(a);
        list.addAll(b);
        return list;
    }, Confluence.asResource("mob_effect_instances_merge"));

    @Override
    public List<Effect> get() {
        return effects;
    }

    @Override
    public Codec<MobEffectInstancesValue> codec() {
        return CODEC;
    }

    public record Effect(Holder<MobEffect> holder, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
        public static final Codec<Effect> CODEC = MobEffectInstance.CODEC.xmap(Effect::instance2Entry, Effect::entry2Instance);

        public Effect(Holder<MobEffect> effect, int duration) {
            this(effect, duration, 0);
        }

        public Effect(Holder<MobEffect> effect, int duration, int amplifier) {
            this(effect, duration, amplifier, false, true);
        }

        public Effect(Holder<MobEffect> effect, int duration, int amplifier, boolean ambient, boolean visible) {
            this(effect, duration, amplifier, ambient, visible, visible);
        }

        public MobEffectInstance get() {
            return entry2Instance(this);
        }

        public static Effect instance2Entry(MobEffectInstance instance) {
            return new Effect(
                    instance.getEffect(),
                    instance.getDuration(),
                    instance.getAmplifier(),
                    instance.isAmbient(),
                    instance.isVisible(),
                    instance.showIcon()
            );
        }

        public static MobEffectInstance entry2Instance(Effect effect) {
            return new MobEffectInstance(
                    effect.holder,
                    effect.duration,
                    effect.amplifier,
                    effect.ambient,
                    effect.visible,
                    effect.showIcon
            );
        }
    }
}
