package org.confluence.mod.common.summon.projectile;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summon.SummonInstance;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/// 对应 Servantry 附件类型注册层的召唤弹幕类型表。
public final class SummonProjectileTypes {
    private static final Map<ResourceLocation, SummonProjectileType<?>> TYPES = new LinkedHashMap<>();

    public static final SummonProjectileType<HornetStingerAttachment> HORNET_STINGER =
            register("summon_bee_stick_proj", HornetStingerAttachment::new);
    public static final SummonProjectileType<ImpFireballAttachment> IMP_FIREBALL =
            register("fire_imp_proj", ImpFireballAttachment::new);

    private SummonProjectileTypes() {}

    private static <T extends SummonProjectileInstance> SummonProjectileType<T> register(String path, BiFunction<SummonInstance, LivingEntity, T> factory) {
        SummonProjectileType<T> type = new SummonProjectileType<>(Confluence.asResource(path), factory);
        if (TYPES.putIfAbsent(type.id(), type) != null) {
            throw new IllegalStateException("Duplicate summon projectile type: " + type.id());
        }
        return type;
    }
}
