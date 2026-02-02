package org.confluence.terraentity.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    //修复肉山在地下被剔除的问题
    @ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;isSectionCompiled(Lnet/minecraft/core/BlockPos;)Z", ordinal = 0))
    public boolean renderLevel(boolean original, @Local(ordinal = 0) Entity entity) {
        return original || entity instanceof WallOfFlesh;
    }
}
