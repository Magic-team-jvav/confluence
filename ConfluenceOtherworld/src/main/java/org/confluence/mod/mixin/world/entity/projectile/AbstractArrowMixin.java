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
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshotCarrier;
import org.confluence.lib.network.SetEntityDataPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.mixed.IAbstractArrow;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements IAbstractArrow, ProjectileCombatSnapshotCarrier {
    @Unique
    private static final String confluence$SNAPSHOT_KEY = "ConfluenceProjectileSnapshot";
    @Unique
    private static final byte[] confluence$dataIds = {SetEntityDataPacketS2C.DATA_BOOLEAN};
    @Unique
    private boolean confluence$isDisappearingOnGround;
    @Unique
    private boolean confluence$damageNotAffectedBySpeedBonus;
    @Unique
    private @Nullable ProjectileCombatSnapshot confluence$combatSnapshot;
    @Unique
    private boolean confluence$invalidCombatSnapshot;

    @Override
    public @Nullable ProjectileCombatSnapshot getProjectileCombatSnapshot() {
        return confluence$combatSnapshot;
    }

    @Override
    public void setProjectileCombatSnapshot(ProjectileCombatSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Arrow projectile combat snapshot must not be null");
        }
        confluence$combatSnapshot = snapshot;
        confluence$invalidCombatSnapshot = false;
    }

    @Override
    public void confluence$setData(byte dataId, Object o) {
        AbstractArrow self = confluence$self();
        if (!self.level().isClientSide) {
            // Mixin 合并到普通类后不能保留接口 default-super 的 invokespecial，否则 JVM 会把
            // AbstractArrow 类常量误当成 InterfaceMethodref 并在首次调用时报错。
            ConfluenceMagicLib.NETWORK_HANDLER.sendToPlayersTrackingEntity(
                    self,
                    new SetEntityDataPacketS2C(
                            self.getId(), new SetEntityDataPacketS2C.Entry(dataId, o)));
        }
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
        if (confluence$combatSnapshot != null) {
            compound.put(confluence$SNAPSHOT_KEY, confluence$combatSnapshot.toTag());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag compound, CallbackInfo ci) {
        confluence$setDamageNotAffectedBySpeedBonus(compound.getBoolean(DNABSB_KEY));
        confluence$setDisappearingOnGround(compound.getBoolean(DOG_KEY));
        confluence$combatSnapshot = null;
        confluence$invalidCombatSnapshot = false;
        if (compound.contains(confluence$SNAPSHOT_KEY, CompoundTag.TAG_COMPOUND)) {
            try {
                confluence$combatSnapshot = ProjectileCombatSnapshot.fromTag(
                        compound.getCompound(confluence$SNAPSHOT_KEY));
            } catch (RuntimeException exception) {
                confluence$invalidCombatSnapshot = true;
                Confluence.LOGGER.error("Discarding arrow with invalid projectile combat snapshot", exception);
            }
        }
    }

    /**
     * 损坏的当前格式快照必须在任何移动或伤害逻辑前失效关闭。
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void discardInvalidSnapshot(CallbackInfo ci) {
        if (confluence$invalidCombatSnapshot && !confluence$self().level().isClientSide) {
            confluence$self().discard();
            ci.cancel();
        }
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
