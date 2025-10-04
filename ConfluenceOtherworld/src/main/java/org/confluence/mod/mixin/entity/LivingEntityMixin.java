package org.confluence.mod.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.api.event.LivingFreezeEvent;
import org.confluence.mod.common.block.natural.ThinIceBlock;
import org.confluence.mod.common.effect.flask.FlaskEffect;
import org.confluence.mod.common.effect.neutral.ShimmerEffect;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.mod.common.worldgen.secret_seed.NoTraps;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.common.effect.HoneyEffect;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntity, SelfGetter<LivingEntity> {
    @Shadow
    public abstract boolean isSuppressingSlidingDownLadder();

    @Shadow
    public abstract boolean canFreeze();

    @Shadow
    public abstract Map<Holder<MobEffect>, MobEffectInstance> getActiveEffectsMap();

    @Unique
    private final Object2IntMap<Immunity> confluence$entityImmunityTicks = new Object2IntOpenHashMap<>();
    @Unique
    private boolean confluence$breakingEasyCrashBlock;
    @Unique
    private boolean confluence$deadO;
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
    private void fall(double motionY, boolean onGround, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
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
        return original.call(instance, factorX, factorY, factorZ);
    }

    @ModifyVariable(method = "handleOnClimbable", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/world/phys/Vec3;"), ordinal = 2)
    private double checkRope(double d2, @Local(argsOnly = true) Vec3 deltaMovement) {
        NoTraps.breakClimbable(confluence$self());
        if (deltaMovement.y < 0.0 && !isSuppressingSlidingDownLadder() && confluence$self() instanceof Player && getInBlockState().is(ModTags.Blocks.ROPE)) {
            return 0.0;
        }
        return Math.max(deltaMovement.y, -0.15F);
    }

    @Override
    public boolean confluence$deadO(boolean... dead) {
        if (dead != null && dead.length != 0) {
            this.confluence$deadO = dead[0];
        }
        return confluence$deadO;
    }

    @Inject(method = "canFreeze", at = @At(value = "HEAD"), cancellable = true)
    private void confluence$canFreeze(CallbackInfoReturnable<Boolean> cir) {
        LivingFreezeEvent event = NeoForge.EVENT_BUS.post(new LivingFreezeEvent(confluence$self()));

        if (confluence$self() instanceof Player player) {
            HookMapManager.postHooks(ModHookTypes.LIVING_FREEZE.get(), (owner, hook, original) -> {
                hook.livingFreeze(owner, confluence$self(), original);
                return original;
            }, player, event);
        }

        if (event.isCanceled()) {
            cir.setReturnValue(false);
        }
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

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V",at=@At(value = "NEW",target = "(Lnet/minecraft/world/entity/Entity;I)Lnet/minecraft/network/protocol/game/ClientboundAnimatePacket;"))
    private void handleSwordProjectile(InteractionHand hand, boolean updateSelf, CallbackInfo ci) {
        if (hand == InteractionHand.MAIN_HAND && confluence$self() instanceof Player player) {
            PlayerUtils.swordProjectile(player);
        }
    }
}
