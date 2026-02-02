package org.confluence.terra_curio.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.terra_curio.client.handler.GravitationHandler;
import org.confluence.terra_curio.mixed.IEntity;
import org.confluence.terra_curio.util.TCUtils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity {
    @Shadow
    public abstract DamageSources damageSources();

    @Shadow
    protected abstract BlockPos getOnPos(float yOffset);

    @Shadow
    public abstract EntityDimensions getDimensions(Pose pose);

    @Shadow
    public abstract Pose getPose();

    @Shadow
    public float fallDistance;
    @Shadow
    private Level level;
    @Shadow
    public boolean verticalCollisionBelow;

    @Shadow
    public boolean verticalCollision;
    @Unique
    private int terra_curio$cthulhuSprintingTime = 0;
    @Unique
    private boolean terra_curio$isShouldRot = false;
    @Unique
    private float terra_curio$dimensionHeight = 0.0F;
    @Unique
    private final boolean terra_curio$isPlayer = confluence$self() instanceof Player;

    @Override
    public int terra_curio$getCthulhuSprintingTime() {
        return terra_curio$cthulhuSprintingTime;
    }

    @Override
    public void terra_curio$setCthulhuSprintingTime(int amount) {
        this.terra_curio$cthulhuSprintingTime = amount;
    }

    @Override
    public void terra_curio$setShouldRot(boolean bool) {
        this.terra_curio$isShouldRot = bool;
    }

    @Override
    public boolean terra_curio$isShouldRot() {
        return terra_curio$isShouldRot;
    }

    @Override
    public float terra_curio$getDimensionHeight() {
        return terra_curio$dimensionHeight;
    }

    @Override
    public boolean terra_curio$isPlayer() {
        return terra_curio$isPlayer;
    }

    @ModifyExpressionValue(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInLava()Z", ordinal = 1))
    private boolean resetLavaImmune(boolean original) {
        Entity self = confluence$self();
        if (self instanceof LivingEntity) {
            original = TCUtils.applyLavaImmune(original, self);
            this.terra_curio$dimensionHeight = terra_curio$isShouldRot ? getDimensions(getPose()).height() : 0.0F;
        }
        return original;
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    private void tickProfiler(CallbackInfo ci) {
        if (terra_curio$cthulhuSprintingTime > 0) this.terra_curio$cthulhuSprintingTime--;
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    private void collidingCheck(Entity entity, CallbackInfo ci) {
        if (terra_curio$isPlayer) {
            TCUtils.applyCthulhuTouch((Player) confluence$self(), entity);
        } else if (IEntity.of(entity).terra_curio$isPlayer()) {
            TCUtils.applyCthulhuTouch((Player) entity, confluence$self());
        }
    }

    @Inject(method = "getOnPosLegacy", at = @At("RETURN"), cancellable = true)
    private void getOnPosAbove(CallbackInfoReturnable<BlockPos> cir) {
        if (terra_curio$isShouldRot) {
            cir.setReturnValue(getOnPos(-(terra_curio$dimensionHeight + 0.2F)));
        }
    }

    @WrapOperation(method = "checkSupportingBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBoundingBox()Lnet/minecraft/world/phys/AABB;"))
    private AABB getBoundingBox(Entity instance, Operation<AABB> original) {
        AABB aabb = original.call(instance);
        if (terra_curio$isShouldRot) {
            return new AABB(aabb.minX, aabb.maxY + Mth.EPSILON, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
        }
        return aabb;
    }

    @Inject(method = "checkFallDamage", at = @At("TAIL"))
    private void updateFallDIstance(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
        if (terra_curio$isShouldRot && !level.isClientSide && y > 0.0) {
            this.fallDistance += (float) y;
        }
    }

    @ModifyArg(method = "spawnSprintParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), index = 2)
    private double modifyParticlePosY(double y) {
        if (terra_curio$isShouldRot) {
            return y - 0.2 + terra_curio$dimensionHeight;
        }
        return y;
    }

    @ModifyArg(method = "spawnSprintParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), index = 5)
    private double modifyParticleSpeedY(double y) {
        return terra_curio$isShouldRot ? -y : y;
    }

    @Inject(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;verticalCollisionBelow:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void flip(MoverType type, Vec3 pos, CallbackInfo ci) {
        if (GravitationHandler.isShouldRot(confluence$self())) {
            this.verticalCollisionBelow = verticalCollision && pos.y > 0.0;
        }
    }
}
