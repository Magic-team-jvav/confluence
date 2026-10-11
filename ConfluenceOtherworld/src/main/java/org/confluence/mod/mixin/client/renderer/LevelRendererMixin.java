package org.confluence.mod.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.mod.common.block.natural.herbs.BaseHerbBlock;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.mixed.ILevelRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class LevelRendererMixin implements ILevelRenderer {
    @Shadow
    @Nullable
    private ViewArea viewArea;

    @Shadow
    public abstract void needsUpdate();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Override
    public void confluence$rebuildAllChunks() {
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        if (IS_SODIUM_LOADED) {
            confluence$rebuildAllChunksSodium(player);
        } else if (viewArea != null) {
            for (SectionRenderDispatcher.RenderSection chunk : viewArea.sections) {
                chunk.setDirty(true);
            }
            needsUpdate();
        }
    }

    @Unique
    private void confluence$rebuildAllChunksSodium(LocalPlayer player) {
        ChunkPos chunkPos = player.chunkPosition();
        int viewDistance = minecraft.options.renderDistance().get();
        Level level = player.level();
        int startY = level.getMinSection();
        int endY = level.getMaxSection();
        int cx, cz;
        for (int x = -viewDistance; x < viewDistance; ++x) {
            cx = chunkPos.x + x;
            for (int z = -viewDistance; z < viewDistance; ++z) {
                cz = chunkPos.z + z;
                for (int y = startY; y <= endY; ++y) {
                    ILevelRenderer.scheduleRebuildForChunk(cx, y, cz);
                }
            }
        }
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0))
    private ResourceLocation renderSun(ResourceLocation textureId) {
        return ClientPacketHandler.sunTexture == null ? textureId : ClientPacketHandler.sunTexture;
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 1))
    private ResourceLocation renderMoon(ResourceLocation textureId) {
        return ClientGameEventSystem.moonTexture == null ? textureId : ClientGameEventSystem.moonTexture;
    }

    @ModifyReturnValue(method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I", at = @At("RETURN"))
    private static int makeHerbEmissive(int original, @Local(argsOnly = true) BlockState state) {
        if (state.is(ModBlocks.SHIVERTHORN) && state.getValue(BaseHerbBlock.AGE) == 2) {
            int block = (original >> 4) & 0xf;
            if (block < 6) {
                original &= ~0xF0;
                original |= (6 << 4);
                return original;
            }
        }
        return original;
    }

    /// 雨的四边形是一根竖直的雨条，对顶点做剪切即可让所有雨条获得统一倾角
    /// （剪切后线段方向 = `(-s, 1)`，与雨条位置无关）。
    ///
    /// ⚠️ 传进 `addVertex` 的 x/y/z **已经是相机相对坐标**（`k1 - camX`、`k2 - camY`、`j1 - camZ`），
    /// 所以这里**绝不能再减一次 camY**——那会让偏移量变成 `-(camY)·s`（几十到几百格），雨会被整体甩出视野。
    /// 剪切零点只决定"在哪里保持竖直"，而雨场本来就按方块平铺，用相机高度当零点正合适。
    ///
    /// 符号：下落雨滴的位置满足 `x = 常数 - (vx/vy)·y`（y 越大越早、x 越小），
    /// 所以**下端**才顺风偏移，剪切必须取负号。
    ///
    /// 用 {@link WrapOperation} 而不是 `@Redirect`：包裹可以叠加，别的模组也包裹同一处调用时不会互相顶掉。
    @WrapOperation(method = "renderSnowAndRain",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;addVertex(FFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer confluence$slantPrecipitation(BufferBuilder instance, float x, float y, float z,
                                                        Operation<VertexConsumer> original) {
        float[] slant = WeatherHandler.precipitationSlant();
        if (slant[0] == 0.0F && slant[1] == 0.0F) return original.call(instance, x, y, z);
        return original.call(instance, x - y * slant[0], y, z - y * slant[1]);
    }

    /// 云层 **X 方向**的漂移：用风驱动的「虚拟 tick」替换原版 `ticks`。
    /// 大风天(8.94 m/s)时等于原版云速，静风时几乎停住，逆风时可以反向。
    /// 累加器在客户端 tick 里推进（见 {@code WeatherHandler}），这里只读。
    @ModifyExpressionValue(method = "renderClouds",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;ticks:I", ordinal = 1, opcode = Opcodes.GETFIELD))
    private int confluence$windDrivenCloudTicks(int original) {
        return Mth.floor(WeatherHandler.cloudTicks());
    }

    /// 云层 **Z 方向**的漂移：把漂移量叠加到方法参数 `camZ` 上。
    ///
    /// 原版只沿 X 漂（`d4 = camZ / 12 + 0.33` 没有时间项），所以刮南北风时整片云完全不动、斜风时只走 X 分量。
    /// 改的是**参数 camZ**（该方法里 camZ 只被 `d4` 用到），因此改动发生在原版 floor / frac / 网格缓存键
    /// 这条管线**之前**，UV 原点、位移动画、网格重建时机三者依然自洽，不会出现贴图跳变。
    ///
    /// @param original camZ
    @ModifyVariable(method = "renderClouds", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private double confluence$windCloudDriftZ(double original) {
        if (!WeatherHandler.cloudDriftApplies()) return original;
        float drift = WeatherHandler.cloudDriftZ();
        return drift == 0.0F ? original : original + drift;
    }
}
