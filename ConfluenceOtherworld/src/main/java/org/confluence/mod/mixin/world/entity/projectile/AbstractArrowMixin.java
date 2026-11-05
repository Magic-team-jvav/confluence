package org.confluence.mod.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.mixed.ILibExtraSyncedData;
import org.confluence.lib.network.s2c.SetEntityDataPacketS2C;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.mixed.IAbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements IAbstractArrow {
    @Unique
    private static final byte[] confluence$dataIds = {SetEntityDataPacketS2C.DATA_BOOLEAN, SetEntityDataPacketS2C.DATA_VEC3};
    @Unique
    private boolean confluence$isDisappearingOnGround;
    @Unique
    private boolean confluence$damageNotAffectedBySpeedBonus;

    @Override
    public void confluence$setData(byte dataId, Object o) {
        ILibExtraSyncedData.defaultSetData(confluence$self(), dataId, o);
        if (dataId == SetEntityDataPacketS2C.DATA_BOOLEAN) {
            this.confluence$damageNotAffectedBySpeedBonus = (boolean) o;
        } else if (dataId == SetEntityDataPacketS2C.DATA_VEC3 && confluence$self().level().isClientSide) {
            confluence$self().setDeltaMovement((Vec3) o);
        }
    }

    @Override
    public Object confluence$getData(byte dataId) {
        return dataId == SetEntityDataPacketS2C.DATA_VEC3 ? confluence$self().getDeltaMovement() : confluence$damageNotAffectedBySpeedBonus;
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

    @Shadow
    protected abstract boolean canHitEntity(Entity entity);

    // 箭矢起点位于实体扩大的碰撞箱内时，原版射线检测会漏掉该实体。
    @Inject(method = "findHitEntity", at = @At("HEAD"), cancellable = true)
    private void confluence$hitOverlappingTarget(Vec3 start, Vec3 end, CallbackInfoReturnable<EntityHitResult> cir) {
        AbstractArrow arrow = confluence$self();
        if (arrow.tickCount > 1) return;
        for (Entity target : arrow.level().getEntities(arrow, arrow.getBoundingBox().inflate(1.0), this::canHitEntity)) {
            if (target.getBoundingBox().inflate(0.3).contains(start)
                    && (target.getBoundingBox().contains(start)
                    || target.getBoundingBox().getCenter().subtract(start).dot(end.subtract(start)) > 0.0)) {
                cir.setReturnValue(new EntityHitResult(target, start));
                return;
            }
        }
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

    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean reduceDamageIfHasSweater(Entity entity, DamageSource damageSource, float damage, Operation<Boolean> original) {
        AbstractArrow self = confluence$self();
        Component name = self.getCustomName();
        if (name != null && name.equals(DartTrapBlock.NAME)) {
            if (entity instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.CHEST).is(VanityArmorItems.DEAD_MANS_SWEATER.get())) {
                damage /= 2.0F;
            }
        }
        return original.call(entity, damageSource, damage);
    }
}
