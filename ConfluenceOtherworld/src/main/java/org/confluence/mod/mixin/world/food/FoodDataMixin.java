package org.confluence.mod.mixin.world.food;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @Shadow
    private int foodLevel;
    @Shadow
    private float saturationLevel;

    @ModifyVariable(method = "addExhaustion", at = @At("HEAD"), argsOnly = true)
    private float doubleExhaustion(float exhaustion) {
        if (ModSecretSeeds.THE_CONSTANT.match()) {
            return exhaustion * 2;
        }
        return exhaustion;
    }

    /// 关闭饥饿（泰拉）：饱食度固定为 18、饱和度 0。
    /// 这样既不会挨饿、可以一直疾跑，又保留原版的缓慢自然回复（约每 4 秒 1 点），不会出现饱和度带来的快速回血。
    @Inject(method = "tick", at = @At("HEAD"))
    private void terrariaNoHunger(Player player, CallbackInfo ci) {
        if (CommonConfigs.DISABLE_HUNGER.get()) {
            this.foodLevel = 18;
            this.saturationLevel = 0.0F;
        }
    }
}
