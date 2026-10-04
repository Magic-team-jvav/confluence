package org.confluence.mod.mixin.world.level.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/// 只调整肉墙的距离判定，配对、解除追踪和数据发送仍使用原版同一个追踪集合。
@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public abstract class WallTrackingMixin {
    @Shadow
    @Final
    private Entity entity;

    @ModifyVariable(method = "updatePlayer", at = @At("STORE"), ordinal = 0)
    private boolean confluence$wallTrackingRange(boolean flag, @Local(argsOnly = true) ServerPlayer player, @Local(name = "d0") double d0) {
        if (!(entity instanceof WallOfFlesh wall)) return flag;
        /// 保留原视距余量，沿墙面展开追踪范围；不把中心距离误当作离墙距离。
        return wall.isWithinTrackingRange(player, (int) d0) && wall.broadcastToPlayer(player);
    }
}
