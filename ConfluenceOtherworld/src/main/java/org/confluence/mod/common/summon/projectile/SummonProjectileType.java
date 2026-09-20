package org.confluence.mod.common.summon.projectile;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.summon.SummonInstance;

import java.util.function.BiFunction;

/// 召唤附件弹幕的运行类型。
public record SummonProjectileType<T extends SummonProjectileInstance>(ResourceLocation id, BiFunction<SummonInstance, LivingEntity, T> factory) {
    public T create(SummonInstance source, LivingEntity target) {
        return factory.apply(source, target);
    }
}
