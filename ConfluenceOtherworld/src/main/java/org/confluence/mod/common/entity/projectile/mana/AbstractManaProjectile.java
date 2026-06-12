package org.confluence.mod.common.entity.projectile.mana;

import PortLib.extensions.net.minecraft.world.entity.Entity.PortEntityExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.init.ModDamageTypes;
import org.joml.Matrix4f;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class AbstractManaProjectile extends DamageSettableProjectile {
    protected boolean localVelocity = false;
    private Set<UUID> penetrateSet; // use getter
    protected int collideCount;
    protected ParticleEmitter emitter;
    private Runnable particleChecker = this::doNothing;

    public AbstractManaProjectile(EntityType<? extends AbstractManaProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected final void doNothing() {}

    @Override
    public final void tick() {
        if (getOwner() == null) {
            discard();
        } else {
            super.tick();
            particleChecker.run();
            doHitCheck();
        }
    }

    /// common
    protected void doHitCheck() {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitResult.getType();
        if (hitresult$type == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitResult);
        } else if (hitresult$type == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitResult);
        }
    }

    /// common
    protected boolean doHurtAndKnockback(Entity target, double knockbackStrength, double knockbackMotionY) {
        if (target.hurt(getDamageSource(), getCalculatedDamage())) {
            if (knockbackStrength > 0 || knockbackMotionY > 0) {
                LibEntityUtils.knockBackA2B(this, target, knockbackStrength, knockbackMotionY);
            }
            return true;
        }
        return false;
    }

    /// server side only
    protected boolean doPenetrateCheck(Entity entity) {
        if (level().isClientSide) return false;
        return getPenetrateSet().add(entity.getUUID());
    }

    /// server side only
    protected void doDiscardInMaxPenetrate(int max) {
        if (level().isClientSide) return;
        if (penetrateSet == null) return;
        if (getPenetrateSet().size() >= max) {
            discard();
        }
    }

    /// server side only
    protected Set<UUID> getPenetrateSet() {
        if (level().isClientSide) return Set.of();
        if (penetrateSet == null) {
            this.penetrateSet = new HashSet<>();
        }
        return penetrateSet;
    }

    /// common
    protected Vec3 doSimpleMove() {
        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setPos(offX, offY, offZ);
        return vec3;
    }

    /// for afterBounce
    ///
    /// server side only
    ///
    /// @param maxCollide Inclusive
    protected void doCollisionCheck(int maxCollide) {
        if (this.collideCount++ >= maxCollide && !level().isClientSide) {
            discard();
        }
    }

    /// common
    protected void doBouncyMove(boolean gravity, Runnable afterBounce, UnaryOperator<Vec3> finalMotion) {
        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, gravity ? vec3.add(0, -getGravity(), 0) : vec3);
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
            afterBounce.run();
        }
        Vec3 apply = finalMotion.apply(motion);
        setDeltaMovement(gravity ? apply.add(0, -getGravity(), 0) : apply);
    }

    /// client side only
    protected void withParticle(ResourceLocation particleId, Runnable afterCreate) {
        this.particleChecker = () -> {
            if (level().isClientSide && (emitter == null || emitter.isRemoved())) {
                this.emitter = new ParticleEmitter(level(), position(), particleId);
                emitter.attachEntity(this);
                emitter.hideOutline = true;
                afterCreate.run();
                MolangParticleEngine.INSTANCE.addEmitter(emitter);
            }
        };
    }

    /// client side only
    protected void withParticle(ResourceLocation particleId) {
        withParticle(particleId, () -> emitter.parentSpace = new Matrix4f().setTranslation(0, getBbHeight() * 0.5F, 0));
    }

    /// server side only
    ///
    /// @param maxAge Exclusive
    protected void doAgeCheck(int maxAge) {
        if (tickCount > maxAge && !level().isClientSide) {
            discard();
        }
    }

    /// server side only
    protected void doFluidCheck(Predicate<FluidState> predicate) {
        if (!level().isClientSide && predicate.test(PortEntityExtension.getInBlockState(this).getFluidState())) {
            discard();
        }
    }

    @Override
    public DamageSource getDamageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float cos = Mth.cos(x * Mth.DEG_TO_RAD);
        float f = -Mth.sin(y * Mth.DEG_TO_RAD) * cos;
        float f1 = -Mth.sin((x + z) * Mth.DEG_TO_RAD);
        float f2 = Mth.cos(y * Mth.DEG_TO_RAD) * cos;
        shoot(f, f1, f2, velocity, inaccuracy);
        if (localVelocity) {
            Vec3 vec3 = shooter.getKnownMovement();
            setDeltaMovement(getDeltaMovement().add(vec3.x, shooter.onGround() ? 0.0 : vec3.y, vec3.z));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Age", tickCount);
        compound.putInt("CollideCount", collideCount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.tickCount = compound.getInt("Age");
        this.collideCount = compound.getInt("CollideCount");
    }

    @Override
    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        return false;
    }
}
