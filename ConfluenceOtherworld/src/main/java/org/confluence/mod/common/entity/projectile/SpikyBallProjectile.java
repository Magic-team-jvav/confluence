package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.common.entitiy.IAxisZRotate;
import org.confluence.lib.common.entitiy.IBouncy;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshotCarrier;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;

/**
 * 普通尖刺球弹幕。
 *
 * <p>弹幕保留原有的重力、弹性、静态无敌帧和 3.2 点伤害；每个实体 UUID 只会计入一次穿透预算，
 * 第七个唯一目标会使弹幕销毁。年龄与已命中 UUID 由独立运行状态持久化，区块卸载或存档重载后
 * 不会重置寿命和穿透次数。</p>
 */
public class SpikyBallProjectile extends Projectile
        implements Immunity, IAxisZRotate, IBouncy, ProjectileCombatSnapshotCarrier {
    private static final int MAXIMUM_SAVED_AGE = 1597;
    private static final int MAXIMUM_TRACKED_HIT_TARGETS = 6;
    private static final int DISCARD_AT_UNIQUE_TARGET_COUNT = 7;

    public final Rotate rotate = new Rotate();
    private final SpikyBallRuntime runtime = new SpikyBallRuntime();
    private final ProjectileCombatState combatState = new ProjectileCombatState();
    private int ownerResolutionTicks;

    public SpikyBallProjectile(EntityType<SpikyBallProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SpikyBallProjectile(LivingEntity shooter) {
        super(ModEntities.SPIKY_BALL.get(), shooter.level());
        setPos(shooter.getX(), shooter.getEyeY() - 0.1F, shooter.getZ());
        setOwner(shooter);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        if (runtime.discardIfInvalid(this)) {
            return;
        }
        if (!level().isClientSide && combatState.discardIfInvalid(this)) {
            return;
        }
        if (waitForLoadedOwner()) {
            return;
        }
        if (tickCount > 1596) {
            discard();
            return;
        }
        super.tick();
        updateRotation();

        bounce(this::move, this::getDeltaMovement, this::setDeltaMovement, getDefaultGravity(), 0.96);

        if (level().isClientSide) {
            rotateZ(rotate, this, 0.125F);
        } else {
            AABB boundingBox = getBoundingBox().inflate(1.0);
            EntityHitResult result = ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F);
            if (result != null) {
                Entity entity = result.getEntity();
                ProjectileCombatSnapshot snapshot = combatState.snapshot();
                float baseDamage = snapshot == null ? 3.2F : snapshot.baseDamage();
                if (entity.hurt(damageSources().mobProjectile(
                        this, getOwner() instanceof LivingEntity living ? living : null), baseDamage)) {
                    LibEntityUtils.knockBackA2B(this, entity, 0.1, 0.02);
                }
                if (runtime.recordHitTarget(entity.getUUID())
                        && runtime.hitTargetCount() >= DISCARD_AT_UNIQUE_TARGET_COUNT) {
                    discard();
                }
            }
        }
    }

    @Override
    protected void updateRotation() {
        if (rotate.different()) {
            super.updateRotation();
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return LibEntityUtils.canHitEntity(target, getOwner());
    }

    @Override
    public double getDefaultGravity() {
        return 0.05;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.tickCount = runtime.readFrom(compound, MAXIMUM_SAVED_AGE, MAXIMUM_TRACKED_HIT_TARGETS);
        combatState.readFrom(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        combatState.writeTo(compound, -1, -1);
        runtime.writeTo(compound, tickCount, MAXIMUM_SAVED_AGE, MAXIMUM_TRACKED_HIT_TARGETS);
    }

    /**
     * 存档恢复后等待所有者 UUID 解析一 tick；仍为空或被篡改成非玩家时安全失效。
     * 新生成弹幕不走该分支，超级尖刺球也完全不复用这套玩家所有者规则。
     */
    private boolean waitForLoadedOwner() {
        if (level().isClientSide || !combatState.wasLoadedFromTag()
                || combatState.snapshot() == null) {
            return false;
        }
        Entity restoredOwner = getOwner();
        if (restoredOwner == null) {
            if (ownerResolutionTicks++ == 0) {
                return true;
            }
            combatState.invalidate("Spiky ball owner could not be resolved after loading");
            combatState.discardIfInvalid(this);
            return true;
        }
        if (!(restoredOwner instanceof ServerPlayer)) {
            combatState.invalidate("Loaded player spiky ball owner is not a server player");
            combatState.discardIfInvalid(this);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable ProjectileCombatSnapshot getProjectileCombatSnapshot() {
        return combatState.snapshot();
    }

    @Override
    public void setProjectileCombatSnapshot(ProjectileCombatSnapshot snapshot) {
        combatState.installSnapshot(snapshot);
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.STATIC;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 15;
    }
}
