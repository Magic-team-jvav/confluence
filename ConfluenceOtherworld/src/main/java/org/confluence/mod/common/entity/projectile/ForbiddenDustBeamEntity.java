package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

/// 尘土射弹：禁戒套装奖励的“尘土束”。
///
/// 直线飞行，**不能穿方块**：撞到方块就在方块表面召唤远古风暴；命中生物则在生物处召唤。
public class ForbiddenDustBeamEntity extends Projectile {
    private static final int MAX_LIFE = 80;
    private boolean resolved;
    /// 客户端尾迹：单个持续发射的粒子发射器，跟着射弹走。
    @Nullable
    private ParticleEmitter trail;

    public ForbiddenDustBeamEntity(EntityType<? extends ForbiddenDustBeamEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
        setInvulnerable(true);
    }

    public ForbiddenDustBeamEntity(Player owner, Vec3 direction) {
        this(ModEntities.FORBIDDEN_DUST_BEAM.get(), owner.level());
        setOwner(owner);
        setPos(owner.getEyePosition());
        setDeltaMovement(direction.normalize().scale(1.6));
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > MAX_LIFE) {
            discard();
            return;
        }
        Vec3 from = position();
        Vec3 movement = getDeltaMovement();
        Vec3 to = from.add(movement);
        if (level().isClientSide) {
            if (trail == null) {
                trail = new ParticleEmitter(level(), from, Confluence.asResource("forbidden_dust"));
                trail.attachEntity(this);
                /// ParticleStorm 的发射器必须显式加进引擎才会渲染。
                MolangParticleEngine.INSTANCE.addEmitter(trail);
            }
            setPos(to);
            return;
        }
        if (movement.lengthSqr() < 1.0E-6) {
            discard();
            return;
        }

        BlockHitResult blockHit = level().clip(new ClipContext(
                from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        double blockDistanceSqr = blockHit.getType() == HitResult.Type.MISS ? Double.MAX_VALUE : blockHit.getLocation().distanceToSqr(from);

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level(), this, from, to,
                getBoundingBox().expandTowards(movement).inflate(1.0),
                candidate -> !resolved && candidate != getOwner() && LibEntityUtils.allowsAttack(getOwner(), candidate));
        double entityDistanceSqr = entityHit == null ? Double.MAX_VALUE : entityHit.getLocation().distanceToSqr(from);

        if (entityDistanceSqr < blockDistanceSqr && entityHit != null) {
            resolve(entityHit.getLocation(), entityHit.getEntity());
        } else if (blockHit.getType() != HitResult.Type.MISS) {
            resolve(blockHit.getLocation(), null);
        } else {
            setPos(to);
        }
    }

    /// 在命中点召唤远古风暴；命中生物时以生物中心为准。
    private void resolve(Vec3 hitPoint, @Nullable Entity target) {
        if (resolved) return;
        resolved = true;
        Vec3 spawn = target != null ? target.position() : hitPoint;
        if (getOwner() instanceof Player player && level() instanceof ServerLevel serverLevel) {
            AncientStormEntity.summon(serverLevel, spawn, player);
        }
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD, hitPoint.x, hitPoint.y, hitPoint.z, 12, 0.2, 0.2, 0.2, 0.02);
        }
        discard();
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 16384.0;
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (trail != null) {
            trail.remove();
            trail = null;
        }
    }
}
