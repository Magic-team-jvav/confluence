package org.confluence.mod.mixin.integration.opallight;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.mesdag.opallight.light.data.DynamicLightDefinition;
import org.confluence.mod.integration.opallight.OpalLightIntegration;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 手持或掉落的发光物品（火把、岩浆桶等）不进 OpalLight 的动态光：它按方块格子传播、异步计算，
/// 移动时光会一格一格地跳。这类光改由 Magic Lib 逐像素照亮，见 OpalLightIntegration.smoothItem。
@Pseudo
@Mixin(targets = "org.mesdag.opallight.light.engine.DynamicLightSources", remap = false)
public abstract class DynamicLightSourcesMixin {
    @Dynamic
    @Inject(method = "addItem", at = @At("HEAD"), cancellable = true)
    private void confluence$smoothItems(Level level, Entity entity, int slot, BlockPos pos, ItemStack stack,
                                             Long2ObjectOpenHashMap<?> sources, CallbackInfo ci) {
        if ((entity instanceof LivingEntity || entity instanceof ItemEntity) && OpalLightIntegration.smoothItem(stack)) {
            ci.cancel();
        }
    }

    /// 实体自身的光（OpalLight 的 entities 数据：发光生物、相位剑射弹）也不进 OpalLight，
    /// 由 OpalLightIntegration.entityLight 交给 Magic Lib 平滑照亮
    @Dynamic
    @Inject(method = "addDefinition", at = @At("HEAD"), cancellable = true)
    private void confluence$smoothEntities(Entity entity, int slot, BlockPos pos, DynamicLightDefinition definition,
                                           Long2ObjectOpenHashMap<?> sources, CallbackInfo ci) {
        ci.cancel();
    }
}
