package org.confluence.mod.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.confluence.mod.common.CommonConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin {
    @ModifyArg(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"), index = 1)
    private double enhance(double y, @Local(ordinal = 1) double d1) {
        if (y == d1 * 0.01 && CommonConfigs.isDragonChargePlayer()) {
            return d1 * 0.1;
        }
        return y;
    }
}
