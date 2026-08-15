package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshotCarrier;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.api.ITrackType;
import org.confluence.mod.common.entity.projectile.ProjectileCombatState;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.util.track.variant.BasisTrack;
import org.confluence.mod.util.track.variant.SimpleTrack;
import org.confluence.terra_curio.common.entity.BeeProjectile;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 蜜蜂枪专用弹幕。
 *
 * <p>1.21 侧只负责蜜蜂本身的运动与追踪；1.20 合并侧还需要接入 MagicLib 的发射快照，
 * 因此在 Otherworld 内补充当前格式的战斗状态。伤害、暴击、魔法倍率、穿透、击退和命中 UUID
 * 都冻结在发射瞬间，成功命中后才记录目标，避免同一只蜜蜂重复伤害同一个实体。</p>
 */
public class BeeGunBullet extends BeeProjectile implements ProjectileCombatSnapshotCarrier {
    private static final String BEE_STATE_TAG = "BeeGunState";
    private static final int BEE_STATE_VERSION = 1;
    private static final String VERSION_TAG = "Version";
    private static final String GIANT_TAG = "Giant";
    private static final String TRACKING_TAG = "SimpleTracking";

    private final ProjectileCombatState combatState = new ProjectileCombatState();
    private ITrackType trackType = new BasisTrack(90, 0.3);
    private int ownerResolutionTicks;

    public BeeGunBullet(Level level, @Nullable LivingEntity owner, boolean isGiant) {
        super(ModEntities.BEE_GUN_BULLET.get(), level, owner, isGiant);
    }

    public BeeGunBullet(EntityType<? extends BeeProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            if (combatState.discardIfInvalid(this)) {
                return;
            }
            if (combatState.snapshot() == null) {
                combatState.invalidate("Bee gun projectile is missing its combat snapshot");
                combatState.discardIfInvalid(this);
                return;
            }
            if (combatState.wasLoadedFromTag() && getOwner() == null) {
                if (ownerResolutionTicks++ == 0) {
                    return;
                }
                combatState.invalidate("Bee gun projectile owner could not be resolved after loading");
                combatState.discardIfInvalid(this);
                return;
            }
        }
        super.tick();
    }

    @Override
    protected void trackTarget() {
        LivingEntity target = LibEntityUtils.getAABBAngleTarget(
                position(), position().add(getDeltaMovement().normalize()), level(), getOwner(),
                10, 180, this::canHitEntity);
        if (target == null) {
            setDeltaMovement(getDeltaMovement().normalize().scale(isGiant() ? 0.5 : 0.25));
        } else {
            Vec3 motion = getDeltaMovement();
            Vec3 direction = target.position().add(0.0, target.getEyeHeight() * 0.5, 0.0).subtract(position());
            double angle = LibMathUtils.angleBetween(motion, direction);
            if (angle < 90.0 && !(trackType instanceof SimpleTrack)) {
                trackType = new SimpleTrack(
                        90, 0.5, isGiant() ? 0.5 : 0.25, Optional.of(0.5), 0.5);
            }
            setDeltaMovement(trackType.calDeltaMovement(getDeltaMovement(), direction, angle));
        }
    }

    /**
     * 所有者、队伍、PvP 与成功命中 UUID 均在追踪和碰撞阶段统一过滤。
     */
    @Override
    protected boolean canHitEntity(Entity target) {
        if (!ProjectileHitRules.canHit(getOwner(), target)) {
            return false;
        }
        Entity impacted = ProjectileHitRules.impactedEntity(target);
        return combatState.canHit(impacted.getUUID(), false);
    }

    /**
     * 使用冻结基础伤害命中；巨蜂原有的一至三点随机加伤和额外推动保持不变。
     *
     * <p>MagicLib 会在伤害事件中根据本实体携带的 MAGIC 快照应用一次通道倍率和暴击。</p>
     */
    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide || isRemoved()) {
            return;
        }
        ProjectileCombatSnapshot snapshot = combatState.snapshot();
        if (snapshot == null) {
            return;
        }
        Entity impacted = ProjectileHitRules.impactedEntity(result.getEntity());
        if (!ProjectileHitRules.canHit(getOwner(), impacted)
                || !combatState.canHit(impacted.getUUID(), false)) {
            return;
        }

        float damage = snapshot.baseDamage()
                + (isGiant() ? random.nextInt(1, 4) : random.nextBoolean() ? 1.0F : 0.0F);
        if (!impacted.hurt(getDamageSource(), damage)) {
            return;
        }
        combatState.recordSuccessfulHit(impacted.getUUID());
        if (snapshot.knockback() > 0.0F) {
            ProjectileHitRules.applyResolvedKnockback(this, impacted, snapshot.knockback(), 0.0);
        }
        if (isGiant()) {
            Vec3 motion = impacted.position().subtract(position()).normalize().scale(0.5);
            impacted.push(motion.x, motion.y, motion.z);
        }
    }

    @Override
    protected DamageSource getDamageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
    }

    /**
     * 只写当前战斗格式；TerraCurio 父类字段继续负责蜜蜂运动与反弹状态。
     */
    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        combatState.writeTo(compound, Math.max(0, maximumLifetime() - tickCount), -1);
        CompoundTag beeState = new CompoundTag();
        beeState.putInt(VERSION_TAG, BEE_STATE_VERSION);
        beeState.putBoolean(GIANT_TAG, isGiant());
        beeState.putBoolean(TRACKING_TAG, trackType instanceof SimpleTrack);
        compound.put(BEE_STATE_TAG, beeState);
    }

    /**
     * 当前格式战斗状态是伤害权威来源，父类保存的基础伤害不会参与恢复后的计算。
     */
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        ProjectileCombatState.RestoredBudgets budgets = combatState.readFrom(compound);
        if (combatState.isInvalid()) {
            return;
        }
        try {
            if (!compound.contains(BEE_STATE_TAG, CompoundTag.TAG_COMPOUND)) {
                throw new IllegalArgumentException("Missing bee gun projectile state");
            }
            CompoundTag beeState = compound.getCompound(BEE_STATE_TAG);
            if (!beeState.contains(VERSION_TAG, CompoundTag.TAG_INT)
                    || beeState.getInt(VERSION_TAG) != BEE_STATE_VERSION) {
                throw new IllegalArgumentException("Unsupported bee gun projectile state version");
            }
            if (!beeState.contains(GIANT_TAG, CompoundTag.TAG_BYTE)
                    || !beeState.contains(TRACKING_TAG, CompoundTag.TAG_BYTE)) {
                throw new IllegalArgumentException("Missing bee gun projectile state field");
            }
            entityData.set(DATA_IS_GIANT, beeState.getBoolean(GIANT_TAG));
            trackType = beeState.getBoolean(TRACKING_TAG)
                    ? new SimpleTrack(90, 0.5, isGiant() ? 0.5 : 0.25, Optional.of(0.5), 0.5)
                    : new BasisTrack(90, 0.3);
            if (budgets.remainingLifetime() < 0 || budgets.remainingLifetime() > maximumLifetime()) {
                throw new IllegalArgumentException("Bee gun projectile lifetime is out of range");
            }
            tickCount = maximumLifetime() - budgets.remainingLifetime();
            baseDamage = combatState.snapshot().baseDamage();
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            combatState.invalidate(message == null || message.isBlank()
                    ? "Malformed bee gun projectile state"
                    : message);
        }
    }

    @Override
    public @Nullable ProjectileCombatSnapshot getProjectileCombatSnapshot() {
        return combatState.snapshot();
    }

    @Override
    public void setProjectileCombatSnapshot(ProjectileCombatSnapshot snapshot) {
        combatState.installSnapshot(snapshot);
        baseDamage = snapshot.baseDamage();
    }

    private int maximumLifetime() {
        return isGiant() ? 220 : 200;
    }
}
