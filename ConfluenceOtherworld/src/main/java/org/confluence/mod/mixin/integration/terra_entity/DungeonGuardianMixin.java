package org.confluence.mod.mixin.integration.terra_entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.mixed.IDamageSource;
import org.confluence.terraentity.entity.boss.DungeonGuardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DungeonGuardian.class)
public abstract class DungeonGuardianMixin {
    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lorg/confluence/terraentity/entity/boss/Skeletron;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean limitDamage(DungeonGuardian instance, DamageSource damageSource, float v, Operation<Boolean> original) {
        return original.call(instance, damageSource, ((IDamageSource) damageSource).confluence$isCritical() ? 2.0F : 1.0F);
    }
}
