package org.confluence.mod.mixin.world.level.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.worldgen.secret_seed.DrunkWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/RandomState;create(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/HolderGetter;J)Lnet/minecraft/world/level/levelgen/RandomState;", ordinal = 0))
    private void modifySettings(CallbackInfo ci, @Local(argsOnly = true) ServerLevel level) {
        DrunkWorld.modifyDepth(level);
    }

    @Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
    public abstract static class TrackedEntityMixin {
        @Shadow
        @Final
        Entity entity;

        /// 只调整肉墙的距离判定，配对、解除追踪和数据发送仍使用原版同一个追踪集合。
        @ModifyVariable(method = "updatePlayer", at = @At("STORE"))
        private boolean confluence$wallTrackingRange(boolean flag, ServerPlayer player, @Local(ordinal = 0) double d0) {
            if (!(entity instanceof WallOfFlesh wall)) return flag;
            /// 保留原视距余量，沿墙面展开追踪范围；不把中心距离误当作离墙距离。
            return wall.isWithinTrackingRange(player, (int) d0) && wall.broadcastToPlayer(player);
        }
    }
}
