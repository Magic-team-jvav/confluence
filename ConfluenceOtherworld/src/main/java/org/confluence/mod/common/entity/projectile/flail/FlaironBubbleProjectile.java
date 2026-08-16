package org.confluence.mod.common.entity.projectile.flail;

import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.ModDamageTypes;

import java.util.Comparator;

/// 猪鲨链球发射的可追踪气泡。
public final class FlaironBubbleProjectile
        extends FlailAuxiliaryProjectile {
    private static final EntityDataAccessor<Float> SCALE =
            SynchedEntityData.defineId(
                    FlaironBubbleProjectile.class,
                    EntityDataSerializers.FLOAT);
    private static final double TARGET_RANGE = 12.0;
    private static final double ACCELERATION = 0.05;
    private static final int TARGET_LIFETIME = 160;
    private int bouncesLeft = 1;
    private boolean hasHadTarget;

    public FlaironBubbleProjectile(
            EntityType<? extends FlaironBubbleProjectile> type,
            Level level
    ) {
        super(type, level);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SCALE, 0.25F);
    }

    /// 服务端创建时随机确定尺寸，随后由实体同步数据传给客户端。
    public void randomizeScale() {
        entityData.set(SCALE, 0.25F + random.nextFloat() * 0.15F);
    }

    public float getRenderScale() {
        return entityData.get(SCALE);
    }

    @Override
    protected void afterMove() {
        if (level().isClientSide()) {
            return;
        }
        LivingEntity target = level().getEntitiesOfClass(
                        LivingEntity.class,
                        getBoundingBox().inflate(TARGET_RANGE),
                        candidate -> LibEntityUtils.canHitEntity(
                                candidate, getOwner()))
                .stream()
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElse(null);

        Vec3 velocity = getDeltaMovement();
        if (target != null) {
            if (!hasHadTarget) {
                /// 与 1.21 行为一致：首次找到目标后，从当前时刻起再保留最多
                /// 160 tick，避免默认四十 tick 在追踪途中提前消失。
                hasHadTarget = true;
                setMaximumLifetime(getLifetime() + TARGET_LIFETIME);
            }
            Vec3 direction = target.getBoundingBox().getCenter()
                    .subtract(position())
                    .normalize();
            setDeltaMovement(velocity.add(
                    direction.scale(ACCELERATION)));
        } else if (velocity.length() > 0.005) {
            setDeltaMovement(velocity.normalize().scale(
                    velocity.length() - 0.005));
        } else {
            setDeltaMovement(Vec3.ZERO);
        }
    }

    @Override
    protected boolean onHitBlockAndContinue(BlockHitResult hit) {
        if (--bouncesLeft < 0) {
            return false;
        }
        Vec3 velocity = getDeltaMovement();
        Direction direction = hit.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal());
        double dot = velocity.dot(normal);
        if (dot < 0.0) {
            velocity = velocity.subtract(normal.scale(2.0 * dot));
        }
        if (velocity.lengthSqr() < 0.01) {
            return false;
        }
        setDeltaMovement(velocity);
        setPos(hit.getLocation().add(normal.scale(0.05)));
        return true;
    }

    @Override
    protected void onHitLiving(LivingEntity target) {
        if (!(getOwner() instanceof Player player)) {
            discard();
            return;
        }
        if (target.hurt(
                ModDamageTypes.of(
                        level(),
                        ModDamageTypes.SWORD_PROJECTILE,
                        this,
                        player),
                damage)) {
            LibEntityUtils.knockBackA2B(this, target, 0.1F, 0.05F);
        }
        discard();
    }
}
