package org.confluence.mod.common.entity.projectile.Flail;

import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.init.ModDamageTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * <h1>猪鲨链球气泡投射物</h1>
 * 由 {@code FlailStrategy.FlaironAttackStrategy} 在连枷 SPIN/THROWN/RETRACT 阶段生成。
 * <p>
 * 具有追踪能力——每 tick 向最近的敌怪微调方向，
 * 可在地面弹射最多 1 次，命中敌怪后消失。
 * 造成武器面板 50% 的伤害。
 */
public class FlaironBubbleProjectile extends BaseFlailProjectile {

    /** 剩余方块弹射次数 */
    private int bounceLeft = 1;
    /** 随机渲染缩放（半边长），范围 [0.25, 0.4]，即边长 [0.5, 0.8] 格 */
    private final float bubbleScale;
    private static final double ACCELERATION = 0.005;
    /** 索敌半径 */
    private static final double TARGET_RANGE = 12.0;
    /** 默认存活时间上限 */
    private static final int DEFAULT_LIFETIME = 40;
    /** 有目标时的存活时间上限 */
    private static final int TARGET_LIFETIME = 160;
    /** 是否曾锁定过目标（用于一次性延长存活时间） */
    private boolean hasHadTarget;

    public FlaironBubbleProjectile(@NotNull EntityType<? extends BaseFlailProjectile> entityType,
                                   @NotNull Level level,
                                   @Nullable BaseFlailEntity parentFlail,
                                   @Nullable Player owner,
                                   @NotNull Vec3 velocity) {
        super(entityType, level, parentFlail, owner);
        setDeltaMovement(velocity);
        setNoGravity(true);
        faceVelocity();
        setMaxLifetime(DEFAULT_LIFETIME);
        this.hasHadTarget = false;
        // 随机边长 [0.5, 0.8] 格 → 半边长 [0.25, 0.40]
        this.bubbleScale = 0.25F + level.random.nextFloat() * 0.15F;
    }

    /** 供 {@link EntityType.Builder} 反射使用的无参构造器 */
    public FlaironBubbleProjectile(@NotNull EntityType<? extends BaseFlailProjectile> entityType,
                                   @NotNull Level level) {
        super(entityType, level);
        this.bubbleScale = 0.3F; // 反射构造默认值
    }

    @Override
    public float getBillboardScale() {
        return bubbleScale;
    }

    @Override
    protected void subTick() {
        if (level().isClientSide()) return;

        // ── 服务端：索敌与加减速 ──
        Level level = level();
        AABB searchBox = getBoundingBox().inflate(TARGET_RANGE);
        Entity owner = getOwner();
        LivingEntity target = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                        e -> e != owner
                                && e.isAlive()
                                && LibUtils.canHitEntity(this, e))
                .stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                .orElse(null);

        Vec3 velocity = getDeltaMovement();
        double speed = velocity.length();

        if (target != null) {
            // 首次锁定目标：重置存活时间（从当前 tick 起延长 160 tick）
            if (!hasHadTarget) {
                hasHadTarget = true;
                setMaxLifetime(getTickCount() + TARGET_LIFETIME);
            }

            // 朝向目标加速
            Vec3 toTarget = target.getBoundingBox().getCenter()
                    .subtract(position()).normalize();
            velocity = velocity.add(toTarget.scale(ACCELERATION*10));
        } else {
            // 无目标：减速
            if (speed > ACCELERATION) {
                Vec3 dir = velocity.normalize();
                velocity = dir.scale(speed - ACCELERATION);
            } else {
                velocity = Vec3.ZERO;
            }
        }

        setDeltaMovement(velocity);
        if (velocity.lengthSqr() > 1e-8) {
            faceVelocity();
        }
    }

    @Override
    public boolean canHitEntity(@NotNull Entity target) {
        Entity owner = getOwner();
        if (target == owner || target == getParentFlail()) return false;
        return super.canHitEntity(target);
    }

    @Override
    protected void onHitLiving(@NotNull LivingEntity target) {
        Player player = getOwnerAsPlayer();
        if (player == null) return;

        DamageSource source = ModDamageTypes.of(level(), ModDamageTypes.SWORD_PROJECTILE, this, player);
        if (target.hurt(source, baseDamage)) {
            VectorUtils.knockBackA2B(this, target, 0.1f, 0.05f);
        }
        // 气泡命中即消失
        discard();
    }

    @Override
    protected boolean onProjectileBlockHit(@NotNull BlockHitResult hitResult) {
        Vec3 motion = getDeltaMovement();
        if (motion.lengthSqr() < 1e-8) {
            return false;
        }

        bounceLeft--;
        if (bounceLeft < 0) {
            return false;
        }

        // 根据击中面的法线反射速度
        Direction face = hitResult.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(face.getNormal());
        double dot = motion.dot(normal);
        if (dot < 0) {
            motion = motion.subtract(normal.scale(2.0 * dot));
        }

        if (motion.lengthSqr() < 0.01) {
            return false;
        }

        setDeltaMovement(motion);
        faceVelocity();
        return true;
    }
}
