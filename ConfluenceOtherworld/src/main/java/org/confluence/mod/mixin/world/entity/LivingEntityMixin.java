package org.confluence.mod.mixin.world.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.block.natural.ThinIceBlock;
import org.confluence.mod.common.effect.flask.FlaskEffect;
import org.confluence.mod.common.effect.neutral.ShimmerEffect;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.mod.common.util.VoidSeaHelper;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.network.s2c.FlushArmorSetBonusPacketS2C;
import org.confluence.terra_curio.common.effect.HoneyEffect;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static org.confluence.mod.common.util.VoidSeaConstants.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntity {
    @Shadow
    public abstract Map<Holder<MobEffect>, MobEffectInstance> getActiveEffectsMap();

    @Shadow
    public abstract ItemStack getLastArmorItem(EquipmentSlot slot);

    @Unique
    private final Object2IntMap<Immunity> confluence$entityImmunityTicks = new Object2IntOpenHashMap<>();
    @Unique
    private boolean confluence$breakingEasyCrashBlock;
    @Unique
    private int confluence$extraInvulnerableTicks;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void confluence$setBreakEasyCrashBlock(boolean breaking) {
        this.confluence$breakingEasyCrashBlock = breaking;
    }

    @Override
    public boolean confluence$isBreakEasyCrashBlock() {
        return confluence$breakingEasyCrashBlock;
    }

    @Override
    public Object2IntMap<Immunity> confluence$getImmunityTicks() {
        return confluence$entityImmunityTicks;
    }

    @Override
    public void confluence$setExtraInvulnerableTicks(int ticks) {
        this.confluence$extraInvulnerableTicks = ticks;
    }

    @Override
    public int confluence$getExtraInvulnerableTicks() {
        return confluence$extraInvulnerableTicks;
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"), cancellable = true)
    private void fall(CallbackInfo ci, @Local(argsOnly = true) double motionY, @Local(argsOnly = true) BlockState blockState, @Local(argsOnly = true) BlockPos blockPos) {
        LivingEntity self = confluence$self();
        if (fallDistance >= 2.5F && blockState.is(NatureBlocks.THIN_ICE_BLOCK)) {
            if (TCUtils.isIceSafe(self)) return;
            ThinIceBlock.fall(self, blockPos);
            super.checkFallDamage(motionY, false, blockState, blockPos);
            ci.cancel();
            return;
        }
        confluence$setBreakEasyCrashBlock(false);
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 waterWalk(Vec3 instance, double factorX, double factorY, double factorZ, Operation<Vec3> original) {
        LivingEntity self = confluence$self();
        FluidType fluidType = self.getBlockStateOn().getFluidState().getType().getFluidType();
        if (fluidType == ModFluids.HONEY.type().get()) {
            if (!level().isClientSide) {
                HoneyEffect.applyHoneyEffect(self);
            }
            instance = instance.scale(0.8);
        } else if (fluidType == ModFluids.SHIMMER.type().get()) {
            ShimmerEffect.applyShimmerEffect(self);
            instance = instance.add(0, -0.03, 0);
        }
        if (self.hasEffect(ModEffects.FLIPPER)) {
            return original.call(instance, 0.96, 0.96, 0.96);
        }
        if (confluence$isInVoidSea()) {
            return original.call(instance, (double) HORIZONTAL_MOVEMENT_RESISTANCE, factorY, (double) HORIZONTAL_MOVEMENT_RESISTANCE);
        }
        return original.call(instance, factorX, factorY, factorZ);
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z"))
    private boolean confluence$voidSeaWater(boolean original) {
        return original || confluence$isInVoidSea();
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"), index = 0)
    private float confluence$voidSeaMovementSpeed(float original) {
        if (confluence$isInVoidSea()) {
            return original * (confluence$self().isSwimming() ? SWIMMING_SPEED : MOVEMENT_SPEED);
        }
        return original;
    }

    @Unique
    private boolean confluence$isInVoidSea() {
        LivingEntity self = confluence$self();
        return VoidSeaHelper.isTrigger(self);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void confluence$moveWithVoidSea(CallbackInfo ci) {
        LivingEntity self = confluence$self();
        if (!VoidSeaHelper.isTrigger(self)) {
            return;
        }

        float height = VoidSeaHelper.getHeight();
        float heightO = VoidSeaHelper.getHeightO();
        if (self.getY() >= Math.min(height, heightO) - TIDE_SURFACE_RANGE
                && self.getY() <= Math.max(height, heightO)
                && height != heightO) {
            self.move(MoverType.SELF, new Vec3(0.0, height - heightO, 0.0));
        }
    }

    @ModifyExpressionValue(method = "handleOnClimbable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSuppressingSlidingDownLadder()Z"))
    private boolean checkRope(boolean original) {
        if (getInBlockState().is(ModTags.Blocks.ROPE)) {
            return !original;
        }
        return original;
    }

    @WrapWithCondition(method = "triggerOnDeathMobEffects", at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V"))
    private boolean cacheFlasks(Map<Holder<MobEffect>, MobEffectInstance> instance) {
        return FlaskEffect.saveFlaskEffects(instance);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readData(CompoundTag compound, CallbackInfo ci) {
        this.confluence$extraInvulnerableTicks = compound.getInt("ExtraInvulnerableTicks");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveData(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("ExtraInvulnerableTicks", confluence$extraInvulnerableTicks);
    }

    @Inject(method = "onAttributeUpdated", at = @At("TAIL"))
    private void updateMana(Holder<Attribute> attribute, CallbackInfo ci) {
        IronSpellHelper.updateMana(confluence$self(), attribute);
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;shouldDiscardFriction()Z"))
    private boolean discardWhenHasAnyHooked(boolean original) {
        return original || (confluence$self() instanceof Player player && !player.isCrouching() && BaseHookItem.hasAnyHooked(player));
    }

    @WrapWithCondition(method = "onEffectUpdated", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;addAttributeModifiers(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;I)V"))
    private boolean shouldAdd(MobEffect mobEffect, AttributeMap entry, int i, @Local(argsOnly = true) MobEffectInstance instance) {
        return IMobEffectInstance.of(instance).confluence$isEnabled();
    }

    @WrapMethod(method = "hasEffect")
    private boolean hasEffect(Holder<MobEffect> effect, Operation<Boolean> original) {
        return ILivingEntity.hasEffect(getActiveEffectsMap(), effect);
    }

    @WrapMethod(method = "getEffect")
    private MobEffectInstance getEffect(Holder<MobEffect> effect, Operation<MobEffectInstance> original) {
        return ILivingEntity.getEffect(getActiveEffectsMap(), effect);
    }

    @Inject(method = "handleEquipmentChanges", at = @At("HEAD"))
    private void handleArmorChanges(Map<EquipmentSlot, ItemStack> equipments, CallbackInfo ci) {
        if (confluence$self() instanceof ServerPlayer player) {
            for (Map.Entry<EquipmentSlot, ItemStack> entry : equipments.entrySet()) {
                EquipmentSlot slot = entry.getKey();
                if (EquipmentSlotGroup.ARMOR.test(slot) && !ItemStack.isSameItem(entry.getValue(), getLastArmorItem(slot))) {
                    PlayerSpecialData.of(player).flushArmorSetBonus(player);
                    FlushArmorSetBonusPacketS2C.sendToPlayersTrackingTarget(player);
                    break;
                }
            }
        }
    }

    @Inject(method = "onBelowWorld", at = @At("HEAD"), cancellable = true)
    private void confluence$onBelowWorld(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (VoidSeaHelper.isTrigger(livingEntity)
                && VoidSeaHelper.isDimensionalOverlapEffect(livingEntity)) {
            ci.cancel();
        }
    }
}
