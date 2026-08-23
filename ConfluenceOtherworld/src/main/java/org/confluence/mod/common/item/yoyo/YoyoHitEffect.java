package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;

@FunctionalInterface
public interface YoyoHitEffect {
    YoyoHitEffect NONE = (yoyo, owner, target) -> {};

    void apply(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target);
}
