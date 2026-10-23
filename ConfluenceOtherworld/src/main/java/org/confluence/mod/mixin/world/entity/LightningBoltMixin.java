package org.confluence.mod.mixin.world.entity;

import net.minecraft.world.entity.LightningBolt;
import org.confluence.mod.common.CommonConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {
    /// 启用泰拉闪电束时，闪电不再点燃方块。
    ///
    /// 只拦截点火来源本身，闪电的伤害、避雷针、铜块除锈与成就统计都照旧执行。
    @Inject(method = "spawnFire", at = @At("HEAD"), cancellable = true)
    private void skipFire(int extraIgnitions, CallbackInfo ci) {
        if (CommonConfigs.TERRA_STYLE_LIGHTNING_BOLT.get() && CommonConfigs.TERRA_STYLE_LIGHTNING_BOLT_NO_FIRE.get()) {
            ci.cancel();
        }
    }
}
