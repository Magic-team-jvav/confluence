package org.confluence.mod.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
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
public abstract class AbstractArrowMixin extends Projectile implements IAbstractArrow {
    @Unique
    private static final byte[] confluence$dataIds = {SetEntityDataPacketS2C.DATA_BOOLEAN};
    @Unique
    private boolean confluence$fromShortBow = false;
    /// 落地是否立即消失
    @Unique
    private boolean confluence$isDisappearingOnGround;
    @Unique
    private boolean confluence$damageNotAffectedBySpeedBonus;

    private AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

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

    /// 伤害不受速度影响
    @Override
    public boolean confluence$isDamageNotAffectedBySpeedBonus() {
        return confluence$damageNotAffectedBySpeedBonus || confluence$isShootFromShortBow();
    }

    @Override
    public void confluence$setDamageNotAffectedBySpeedBonus(boolean is) {
        confluence$damageNotAffectedBySpeedBonus = is;
    }

    @Override
    public boolean confluence$isDisappearingOnGround() {
        return confluence$isDisappearingOnGround;
    }

    @Override
    public void confluence$setDisappearingOnGround(boolean confluence$isDisappearingOnGround) {
        this.confluence$isDisappearingOnGround = confluence$isDisappearingOnGround;
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
        return confluence$isDamageNotAffectedBySpeedBonus() ? 1.0 : original;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addShortBow(CompoundTag pCompound, CallbackInfo ci) {
        pCompound.putBoolean("confluence:from_short_bow", confluence$isShootFromShortBow());
        pCompound.putBoolean("confluence:damage_not_affected_by_speed_bonus", confluence$damageNotAffectedBySpeedBonus);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readShortBow(CompoundTag pCompound, CallbackInfo ci) {
        confluence$setShootFromShortBow(pCompound.getBoolean("confluence:from_short_bow"));
        confluence$damageNotAffectedBySpeedBonus = pCompound.getBoolean("confluence:damage_not_affected_by_speed_bonus");
    }

    @WrapMethod(method = "onHitBlock")
    private void onHitBlock(BlockHitResult result, Operation<Void> original) {
        original.call(result);
        if (confluence$isDisappearingOnGround()) {
            discard();
        }
    }
}
