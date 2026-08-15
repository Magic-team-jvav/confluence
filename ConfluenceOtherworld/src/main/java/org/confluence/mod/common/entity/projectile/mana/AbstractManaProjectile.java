package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModDamageTypes;
import org.joml.Matrix4f;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Otherworld 魔法弹幕的公共运行骨架。
 *
 * <p>本类统一处理所有者存活、粒子挂接、移动辅助、寿命与碰撞计数。年龄和已完成碰撞次数
 * 会直接决定弹幕何时销毁，属于服务端玩法状态；当前 1.20 格式将它们保存在独立版本根中，
 * 不读取旧扁平字段。缺失、类型错误或越界数据会复用战斗状态的安全失效通道。</p>
 */
public abstract class AbstractManaProjectile extends DamageSettableProjectile {
    private static final String RUNTIME_TAG = "ConfluenceManaRuntime";
    private static final int RUNTIME_VERSION = 1;
    protected boolean localVelocity = false;
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
            if (isRemoved()) {
                return;
            }
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
            combatState().recordSuccessfulHit(ProjectileHitRules.impactedEntity(target).getUUID());
            float snapshotKnockback = getProjectileCombatSnapshot() == null
                    ? 0.0F
                    : getProjectileCombatSnapshot().knockback();
            // 尚未把击退声明迁入物品动作的旧法术继续使用实体命中点数值；显式动作值优先。
            float resolvedKnockback = snapshotKnockback > 0.0F
                    ? snapshotKnockback
                    : (float) knockbackStrength;
            ProjectileHitRules.applyResolvedKnockback(this, target, resolvedKnockback, knockbackMotionY);
            return true;
        }
        return false;
    }

    /// server side only
    protected boolean doPenetrateCheck(Entity entity) {
        if (level().isClientSide) return false;
        Entity impacted = ProjectileHitRules.impactedEntity(entity);
        return combatState().canHit(impacted.getUUID(), false);
    }

    /// server side only
    protected void doDiscardInMaxPenetrate(int max) {
        if (level().isClientSide) return;
        if (combatState().successfulHitCount() >= max) {
            discard();
        }
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
        move(MoverType.SELF, gravity ? vec3.add(0, -getGravity1211(), 0) : vec3);
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
            afterBounce.run();
        }
        Vec3 apply = finalMotion.apply(motion);
        setDeltaMovement(gravity ? apply.add(0, -getGravity1211(), 0) : apply);
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
        if (!level().isClientSide && predicate.test(getInBlockState().getFluidState())) {
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
        if (tickCount < 0) {
            throw new IllegalStateException("Mana projectile age is outside the supported range");
        }
        if (collideCount < 0) {
            throw new IllegalStateException("Mana projectile collision count is outside the supported range");
        }
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putInt("Age", tickCount);
        runtime.putInt("CollideCount", collideCount);
        compound.put(RUNTIME_TAG, runtime);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (combatState().isInvalid()) {
            return;
        }
        if (!compound.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            combatState().invalidate("Missing or invalid mana projectile runtime state");
            return;
        }

        CompoundTag runtime = compound.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("Age", Tag.TAG_INT)
                || !runtime.contains("CollideCount", Tag.TAG_INT)) {
            combatState().invalidate("Malformed mana projectile runtime state");
            return;
        }
        int savedAge = runtime.getInt("Age");
        int savedCollisions = runtime.getInt("CollideCount");
        if (savedAge < 0) {
            combatState().invalidate("Mana projectile age is outside the supported range");
            return;
        }
        if (savedCollisions < 0) {
            combatState().invalidate("Mana projectile collision count is outside the supported range");
            return;
        }
        tickCount = savedAge;
        collideCount = savedCollisions;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }
}
