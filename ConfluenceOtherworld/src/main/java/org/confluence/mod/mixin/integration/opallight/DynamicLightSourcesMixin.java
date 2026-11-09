package org.confluence.mod.mixin.integration.opallight;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.integration.opallight.OpalLightIntegration;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 手持或掉落的发光方块物品（火把等）不进 OpalLight 的动态光：它按方块格子传播、异步计算，
/// 移动时光会一格一格地跳。这类光改由 Magic Lib 逐像素照亮，见 OpalLightIntegration.smoothBlockItem。
@Pseudo
@Mixin(targets = "org.mesdag.opallight.light.engine.DynamicLightSources", remap = false)
public abstract class DynamicLightSourcesMixin {
    @Dynamic
    @Inject(method = "addItem", at = @At("HEAD"), cancellable = true)
    private void confluence$smoothBlockItems(Level level, Entity entity, int slot, BlockPos pos, ItemStack stack,
                                             Long2ObjectOpenHashMap<?> sources, CallbackInfo ci) {
        if ((entity instanceof LivingEntity || entity instanceof ItemEntity) && OpalLightIntegration.smoothBlockItem(stack)) {
            ci.cancel();
        }
    }
}
