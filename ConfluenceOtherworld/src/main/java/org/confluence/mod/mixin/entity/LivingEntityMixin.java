package org.confluence.mod.mixin.entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.api.event.LivingFreezeEvent;
import org.confluence.mod.common.effect.flask.FlaskEffect;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.worldgen.secret_seed.NoTraps;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.Immunity;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terra_curio.common.init.TCItems;
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

    @Unique
    private final Object2IntMap<Immunity> confluence$entityImmunityTicks = new Object2IntOpenHashMap<>();
    @Unique
    private boolean confluence$breakingEasyCrashBlock = false;
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
            if (!level().isClientSide) {
                BlockPos.betweenClosedStream(getBoundingBox().move(0.0, -0.5, 0.0)).forEach(pos -> {
                    if (pos.equals(blockPos) || level().getBlockState(pos).is(NatureBlocks.THIN_ICE_BLOCK)) {
                        level().destroyBlock(pos, true, self);
                    }
                });
            }
            confluence$setBreakEasyCrashBlock(true);
            setOnGround(false);
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
            if (!self.level().isClientSide) {
                if (self instanceof Animal || self instanceof Player) {
                    MobEffectInstance effect = self.getEffect(TCEffects.HONEY);
                    if (effect == null || effect.getDuration() < 220) {
                        self.addEffect(new MobEffectInstance(TCEffects.HONEY, 600));
                    }
                } else if (self instanceof AbstractPiglin piglin) {
                    piglin.setImmuneToZombification(true);
                }
            }
            instance = instance.scale(0.8);
        } else if (fluidType == ModFluids.SHIMMER.type().get()) {
            if (!self.level().isClientSide && self.getEyeInFluidType() == ModFluids.SHIMMER.type().get() && !self.hasEffect(ModEffects.SHIMMER)) {
                if (self.isCrouching() || !TCUtils.getAccessoriesValue(self, TCItems.EFFECT$IMMUNITIES).contains(ModEffects.SHIMMER)) {
                    self.addEffect(new MobEffectInstance(ModEffects.SHIMMER, MobEffectInstance.INFINITE_DURATION));
                }
            }
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
        if (dead != null && dead.length > 0) {
            confluence$deadO = dead[0];
        }
        return confluence$deadO;
    }

    @Inject(method = "canFreeze", at = @At(value = "HEAD"), cancellable = true)
    private void confluence$canFreeze(CallbackInfoReturnable<Boolean> cir) {
        LivingFreezeEvent.Pre post = NeoForge.EVENT_BUS.post(new LivingFreezeEvent.Pre(confluence$self()));

        if (confluence$self() instanceof Player player) {
            HookMapManager.postHooks(ModHookTypes.LIVING_FREEZE.get(), (owner, hook, original) -> {
                hook.livingFreeze(owner, confluence$self(), original);
                return original;
            }, player, post);
        }

        if (!post.canFreeze()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;freeze()Lnet/minecraft/world/damagesource/DamageSource;"))
    private void confluence$aiStep(CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new LivingFreezeEvent.Post(confluence$self()));
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
}
