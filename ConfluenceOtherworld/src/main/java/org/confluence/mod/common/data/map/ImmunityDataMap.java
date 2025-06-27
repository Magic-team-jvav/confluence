package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.confluence.mod.mixed.Immunity;

public record ImmunityDataMap(Immunity.Type type, int duration) {
    public static final Codec<ImmunityDataMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Immunity.Type.CODEC.fieldOf("type").forGetter(ImmunityDataMap::type),
            ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(ImmunityDataMap::duration)
    ).apply(instance, ImmunityDataMap::new));
}
