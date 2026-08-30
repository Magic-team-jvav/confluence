package org.confluence.mod.mixin.integration.terraentity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.lib.mixed.ILibDamageSource;
import org.confluence.terraentity.entity.boss.Skeletron;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Skeletron.class, remap = false)
public abstract class SkeletronMixin {
    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lorg/confluence/terraentity/entity/boss/AbstractTerraBossBase;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean limitDamage(Skeletron instance, DamageSource damageSource, float amount, Operation<Boolean> original) {
        if (!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            int size = instance.hands.size();
            if (size > 2) {
                ILibDamageSource lds = ILibDamageSource.of(damageSource);
                amount = lds != null && lds.confluence$isCritical() ? 2.0F : 1.0F;
            } else if (size != 0) {
                amount = amount * (1 - size * 0.45F);
            }
        }
        return original.call(instance, damageSource, amount);
    }
}
