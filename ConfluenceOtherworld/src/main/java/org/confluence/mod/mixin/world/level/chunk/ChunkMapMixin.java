package org.confluence.mod.mixin.world.level.chunk;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.worldgen.secret_seed.DrunkWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/RandomState;create(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/HolderGetter;J)Lnet/minecraft/world/level/levelgen/RandomState;", ordinal = 0))
    private RandomState modifySettings(NoiseGeneratorSettings settings, HolderGetter<NormalNoise.NoiseParameters> noiseParametersGetter, long levelSeed, Operation<RandomState> original, @Local(argsOnly = true) ServerLevel level) {
        DrunkWorld.modifyDepth(level);
        return original.call(settings, noiseParametersGetter, levelSeed);
    }

    @Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
    public abstract static class TrackedEntityMixin {
        @Shadow
        @Final
        Entity entity;

        /// 大型 Boss 按身体覆盖范围追踪；配对、解除追踪和数据发送仍使用原版追踪集合。
        @ModifyVariable(method = "updatePlayer", at = @At("STORE"))
        private boolean confluence$bossTrackingRange(boolean flag, ServerPlayer player, @Local(ordinal = 0) double d0) {
            if (entity instanceof TheDestroyer destroyer)
                return destroyer.isWithinTrackingRange(player, d0) && destroyer.broadcastToPlayer(player);
            if (!(entity instanceof WallOfFlesh wall)) return flag;
            /// 保留原视距余量，沿墙面展开追踪范围；不把中心距离误当作离墙距离。
            return wall.isWithinTrackingRange(player, (int) d0) && wall.broadcastToPlayer(player);
        }
    }
}
