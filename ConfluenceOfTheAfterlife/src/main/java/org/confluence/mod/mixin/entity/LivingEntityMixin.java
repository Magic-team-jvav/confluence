package org.confluence.mod.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.worldgen.secret_seed.NoTraps;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.Immunity;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.mixed.SelfGetter;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntity, SelfGetter<LivingEntity> {
    @Shadow
    public abstract boolean isSuppressingSlidingDownLadder();

    @Unique
    private final Object2IntMap<Immunity> confluence$entityImmunityTicks = new Object2IntOpenHashMap<>();

    @Unique
    private boolean confluence$breakingEasyCrashBlock = false;

    @Unique
    private boolean confluence$deadO;

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

    @Inject(method = "checkFallDamage", at = @At("HEAD"), cancellable = true)
    private void fall(double motionY, boolean onGround, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
        LivingEntity self = self();
        if (fallDistance >= 2.5F && blockState.is(NatureBlocks.THIN_ICE_BLOCK) && !TCUtils.hasAccessoriesType(self, AccessoryItems.ICE$SAFE)) {
            if (!level().isClientSide) {
                BlockPos.betweenClosedStream(getBoundingBox().move(0.0, -0.5, 0.0)).forEach(pos -> {
                    if (pos.equals(blockPos) || level().getBlockState(pos).is(NatureBlocks.THIN_ICE_BLOCK)) {
                        level().destroyBlock(pos, true, self);
                    }
                });
            }
            this.confluence$breakingEasyCrashBlock = true;
            setOnGround(false);
            super.checkFallDamage(motionY, false, blockState, blockPos);
            ci.cancel();
        } else {
            this.confluence$breakingEasyCrashBlock = false;
        }
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private Vec3 waterWalk(Vec3 par1) {
        LivingEntity self = self();
        FluidType fluidType = self.getBlockStateOn().getFluidState().getType().getFluidType();
        if (fluidType == ModFluids.HONEY.type().get()) {
            if ((!self.level().isClientSide && self instanceof Animal) || self instanceof ServerPlayer) {
                self.addEffect(new MobEffectInstance(TCEffects.HONEY, 600));
            }
            par1 = par1.scale(0.6);
        } else if (fluidType == ModFluids.SHIMMER.type().get()) {
            if (!self.level().isClientSide && self.getEyeInFluidType() == ModFluids.SHIMMER.type().get() && !self.hasEffect(ModEffects.SHIMMER)) {
                if (self.isCrouching() || !TCUtils.getAccessoriesValue(self, TCItems.EFFECT$IMMUNITIES).contains(ModEffects.SHIMMER)) {
                    self.addEffect(new MobEffectInstance(ModEffects.SHIMMER, MobEffectInstance.INFINITE_DURATION));
                }
            }
            par1 = par1.add(0.0, -0.003, 0.0);
        }
        if (self.hasEffect(ModEffects.FLIPPER)) {
            return new Vec3(0.96, par1.y, 0.96);
        }
        return par1;
    }

    @ModifyVariable(method = "handleOnClimbable", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/world/phys/Vec3;"), ordinal = 2)
    private double checkRope(double d2, @Local(argsOnly = true) Vec3 deltaMovement) {
        NoTraps.breakClimbable(self());
        if (deltaMovement.y < 0.0 && !isSuppressingSlidingDownLadder() && self() instanceof Player && getInBlockState().is(ModTags.Blocks.ROPE)) {
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
}
