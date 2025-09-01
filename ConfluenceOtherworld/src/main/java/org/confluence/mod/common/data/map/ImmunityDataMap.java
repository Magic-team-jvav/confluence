package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.mixed.Immunity;

import java.util.function.ToIntFunction;

public record ImmunityDataMap(Immunity.Type type, int duration) {
    public static final Codec<ImmunityDataMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Immunity.Type.CODEC.fieldOf("type").forGetter(ImmunityDataMap::type),
            ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(ImmunityDataMap::duration)
    ).apply(instance, ImmunityDataMap::new));

    public static Immunity.Type getImmunityType(Entity entity) {
        ImmunityDataMap immunity = ModDataMaps.getEntityData(ModDataMaps.IMMUNITY, entity);
        if (immunity == null) {
            if (entity instanceof Projectile) return Immunity.Type.LOCAL;
            return Immunity.Type.STATIC;
        }
        return immunity.type();
    }

    public static int getImmunityDuration(Entity entity, DamageSource damageSource, ToIntFunction<DamageSource> defaultGetter) {
        ImmunityDataMap immunity = ModDataMaps.getEntityData(ModDataMaps.IMMUNITY, entity);
        if (immunity == null) return defaultGetter.applyAsInt(damageSource);
        return immunity.duration();
    }
}
