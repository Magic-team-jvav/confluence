package org.confluence.mod.mixin.world.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.gui.hud.HitMarkerHud;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LocalLivingEntityMixin implements IClientLivingEntity {
    @Unique
    private boolean confluence$deadO;
    @Unique
    private Vec3 confluence$deathMotion;

    @Inject(method = "handleDamageEvent", at = @At("TAIL"))
    private void confluence$hitMarker(DamageSource source, CallbackInfo ci) {
        HitMarkerHud.onDamage((LivingEntity) (Object) this, source);
    }

    @Override
    public boolean confluence$deadO(boolean... dead) {
        if (dead != null && dead.length != 0) {
            this.confluence$deadO = dead[0];
        }
        return confluence$deadO;
    }

    @Override
    public Vec3 confluence$deathMotion(Vec3... motion) {
        if (motion != null && motion.length > 0) {
            confluence$deathMotion = motion[0];
        }
        return confluence$deathMotion;
    }
}
