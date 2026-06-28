package org.confluence.mod.common.init.armor.type;

import PortLib.extensions.net.minecraft.world.effect.MobEffect.PortMobEffectExtension;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.api.primitive.CombineRule;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;

public record EnhanceEffectDuration(
        Object2IntMap<MobEffect> map
) implements PrimitiveValue<Object2IntMap<MobEffect>> {
    public static final Codec<EnhanceEffectDuration> CODEC = Codec.unboundedMap(PortMobEffectExtension.directCodec(), ExtraCodecs.POSITIVE_INT)
            .<Object2IntMap<MobEffect>>xmap(Object2IntOpenHashMap::new, Object2ObjectOpenHashMap::new)
            .xmap(EnhanceEffectDuration::new, EnhanceEffectDuration::map);
    public static final CombineRule<Object2IntMap<MobEffect>, EnhanceEffectDuration> MERGE = CombineRule.register((a, b) -> {
        Object2IntOpenHashMap<MobEffect> data = new Object2IntOpenHashMap<>();
        for (Object2IntMap.Entry<MobEffect> entry : a.object2IntEntrySet()) {
            data.addTo(entry.getKey(), entry.getIntValue());
        }
        for (Object2IntMap.Entry<MobEffect> entry : b.object2IntEntrySet()) {
            data.addTo(entry.getKey(), entry.getIntValue());
        }
        return data;
    }, Confluence.asResource("enhance_effect_duration_merge"));

    @Override
    public Object2IntMap<MobEffect> get() {
        return map;
    }

    @Override
    public Codec<EnhanceEffectDuration> codec() {
        return CODEC;
    }
}
