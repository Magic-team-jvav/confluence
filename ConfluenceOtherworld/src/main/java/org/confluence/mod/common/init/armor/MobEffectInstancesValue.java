package org.confluence.mod.common.init.armor;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import org.confluence.lib.util.MobEffectInstanceData;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.api.primitive.CombineRule;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;

import java.util.List;

public record MobEffectInstancesValue(
        List<MobEffectInstanceData> effects
) implements PrimitiveValue<List<MobEffectInstanceData>> {
    public static final Codec<MobEffectInstancesValue> CODEC = MobEffectInstanceData.CODEC.listOf().xmap(MobEffectInstancesValue::new, MobEffectInstancesValue::effects);
    public static final CombineRule<List<MobEffectInstanceData>, MobEffectInstancesValue> MERGE = CombineRule.register((a, b) -> {
        List<MobEffectInstanceData> list = Lists.newArrayList(a);
        list.addAll(b);
        return list;
    }, Confluence.asResource("mob_effect_instances_merge"));

    @Override
    public List<MobEffectInstanceData> get() {
        return effects;
    }

    @Override
    public Codec<MobEffectInstancesValue> codec() {
        return CODEC;
    }
}
