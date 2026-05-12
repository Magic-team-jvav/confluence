package org.confluence.mod.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
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
    private boolean confluence$isDisappearingOnGround;
    @Unique
    private boolean confluence$damageNotAffectedBySpeedBonus;

    @Override
    public void confluence$setData(byte dataId, Object o) {
        IAbstractArrow.super.confluence$setData(dataId, o);
        this.confluence$damageNotAffectedBySpeedBonus = (boolean) o;
    }

    @Override
    public Object confluence$getData(byte dataId) {
        return confluence$damageNotAffectedBySpeedBonus;
    }

    @Override
    public byte[] confluence$getAllDataId() {
        return confluence$dataIds;
    }

    @Override
    public boolean confluence$isDamageNotAffectedBySpeedBonus() {
        return confluence$damageNotAffectedBySpeedBonus;
    }

    @Override
    public void confluence$setDamageNotAffectedBySpeedBonus(boolean value) {
        confluence$setData(SetEntityDataPacketS2C.DATA_BOOLEAN, value);
    }

    @Override
    public boolean confluence$isDisappearingOnGround() {
        return confluence$isDisappearingOnGround;
    }

    @Override
    public void confluence$setDisappearingOnGround(boolean value) {
        this.confluence$isDisappearingOnGround = value;
    }

    @ModifyVariable(method = "shoot", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float boost(float velocity) {
        if (confluence$self().getOwner() instanceof LivingEntity living && living.hasEffect(ModEffects.ARCHERY)) {
            return velocity * 1.2F;
        }
        return velocity;
    }

    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;length()D"))
    private double notScale(Vec3 instance, Operation<Double> original) {
        return confluence$isDamageNotAffectedBySpeedBonus() ? 1.0 : original.call(instance);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void add(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean(DNABSB_KEY, confluence$isDamageNotAffectedBySpeedBonus());
        compound.putBoolean(DOG_KEY, confluence$isDisappearingOnGround());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag compound, CallbackInfo ci) {
        confluence$setDamageNotAffectedBySpeedBonus(compound.getBoolean(DNABSB_KEY));
        confluence$setDisappearingOnGround(compound.getBoolean(DOG_KEY));
    }

    @Inject(method = "onHitBlock", at = @At("TAIL"))
    private void disappear(CallbackInfo ci) {
        if (confluence$isDisappearingOnGround()) {
            confluence$self().discard();
        }
    }
}
