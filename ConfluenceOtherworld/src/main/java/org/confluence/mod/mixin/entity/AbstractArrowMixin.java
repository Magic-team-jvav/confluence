package org.confluence.mod.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.confluence.lib.network.SetEntityDataPacketS2C;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.mixed.IAbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements IAbstractArrow {
    @Unique
    private static final byte[] confluence$dataIds = {SetEntityDataPacketS2C.DATA_BOOLEAN};
    @Unique
    private boolean confluence$fromShortBow = false;

    @Override
    public boolean confluence$isShootFromShortBow() {
        return confluence$fromShortBow;
    }

    @Override
    public void confluence$setShootFromShortBow(boolean is) {
        confluence$setData(SetEntityDataPacketS2C.DATA_BOOLEAN, is);
    }

    @Override
    public void confluence$setData(byte dataId, Object o) {
        IAbstractArrow.super.confluence$setData(dataId, o);
        this.confluence$fromShortBow = (boolean) o;
    }

    @Override
    public Object confluence$getData(byte dataId) {
        return confluence$fromShortBow;
    }

    @Override
    public byte[] confluence$getAllDataId() {
        return confluence$dataIds;
    }

    @ModifyVariable(method = "shoot", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float boost(float velocity) {
        if (confluence$self().getOwner() instanceof LivingEntity living && living.hasEffect(ModEffects.ARCHERY)) {
            return velocity * 1.2F;
        }
        return velocity;
    }

    @ModifyExpressionValue(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;length()D"))
    private double shortBowLength(double original) {
        return confluence$isShootFromShortBow() ? 1.0 : original;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addShortBow(CompoundTag pCompound, CallbackInfo ci) {
        pCompound.putBoolean("confluence:from_short_bow", confluence$isShootFromShortBow());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readShortBow(CompoundTag pCompound, CallbackInfo ci) {
        confluence$setShootFromShortBow(pCompound.getBoolean("confluence:from_short_bow"));
    }
}
