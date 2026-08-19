package org.confluence.mod.common.summon.projectile;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.api.summon.OwnedSummon;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.api.whip.WhipTagTracker;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.summon.SummonInstance;
import org.confluence.mod.common.summon.SummonPose;
import org.confluence.mod.common.summon.SummonRenderPart;
import org.confluence.mod.common.summon.SummonVisualState;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// 由玩家召唤附件容器维护的非实体弹幕。
public abstract class SummonProjectileInstance implements OwnedSummon, Immunity {
    private static final int MAX_LIFETIME = 100;
    private final UUID uuid = UUID.randomUUID();
    private final ResourceLocation type;
    private final ServerPlayer owner;
    private final UUID intendedTargetId;
    private final float baseDamage;
    private Vec3 position;
    private final Vec3 velocity;
    private boolean removed;
    private int tickCount;

    protected SummonProjectileInstance(ResourceLocation type, SummonInstance source, LivingEntity target,
                                       float velocity, float inaccuracy) {
        this.type = type;
        this.owner = source.owner();
        intendedTargetId = target.getUUID();
        baseDamage = source.stats().baseDamage();
        position = source.position();
        Vec3 aimPoint = source.actualTarget() != null && source.actualTarget() != source.target()
                ? source.targetPosition()
                : new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
        Vec3 direction = aimPoint.subtract(position).normalize();
        double spread = 0.0075 * inaccuracy;
        direction = direction.add(owner.getRandom().triangle(0.0, spread), owner.getRandom().triangle(0.0, spread),
                owner.getRandom().triangle(0.0, spread)).normalize();
        this.velocity = direction.scale(velocity);
    }

    public final void tick() {
        if (removed || !owner.isAlive() || owner.isRemoved() || ++tickCount > MAX_LIFETIME) {
            removed = true;
            return;
        }
        Vec3 end = position.add(velocity);
        BlockHitResult blockHit = owner.level().clip(new ClipContext(position, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, owner));
        EntityHitResult entityHit = findEntityHit(end);
        double blockDistance = blockHit.getType() == HitResult.Type.BLOCK
                ? position.distanceToSqr(blockHit.getLocation()) : Double.MAX_VALUE;
        double entityDistance = entityHit == null ? Double.MAX_VALUE : position.distanceToSqr(entityHit.getLocation());
        if (entityDistance <= blockDistance) {
            position = entityHit.getLocation();
            hit(entityHit.getEntity());
        } else if (blockDistance < Double.MAX_VALUE) {
            position = blockHit.getLocation();
            removed = true;
        } else {
            position = end;
        }
    }

    private @Nullable EntityHitResult findEntityHit(Vec3 end) {
        Entity nearest = null;
        Vec3 nearestHit = null;
        double nearestDistance = Double.MAX_VALUE;
        Entity intended = owner.serverLevel().getEntity(intendedTargetId);
        if (intended != null && canHit(intended)) {
            Vec3 hit = intersection(intended, end);
            if (hit != null) {
                nearest = intended;
                nearestHit = hit;
                nearestDistance = position.distanceToSqr(hit);
            }
        }
        AABB search = AABB.ofSize(position, 0.5, 0.5, 0.5).expandTowards(velocity).inflate(0.35);
        for (Entity candidate : owner.level().getEntities((Entity) null, search, this::canHit)) {
            Vec3 hit = intersection(candidate, end);
            if (hit == null) continue;
            double distance = position.distanceToSqr(hit);
            if (distance < nearestDistance) {
                nearest = candidate;
                nearestHit = hit;
                nearestDistance = distance;
            }
        }
        return nearest == null ? null : new EntityHitResult(nearest, nearestHit);
    }

    private boolean canHit(Entity candidate) {
        Entity impacted = ProjectileHitRules.impactedEntity(candidate);
        return impacted instanceof LivingEntity target
                && SummonTargetCache.isValidTarget(owner, target, Double.MAX_VALUE, true)
                && ProjectileHitRules.canHit(owner, candidate);
    }

    private @Nullable Vec3 intersection(Entity target, Vec3 end) {
        AABB box = target.getBoundingBox().inflate(0.35);
        return box.clip(position, end).orElse(box.contains(position) ? position : null);
    }

    private void hit(Entity rawTarget) {
        Entity impacted = ProjectileHitRules.impactedEntity(rawTarget);
        if (!(impacted instanceof LivingEntity target) || Immunity.isActive(this, target)) {
            removed = true;
            return;
        }
        float damage = baseDamage * (float) owner.getAttributeValue(LibAttributes.getSummonDamage());
        damage = WhipTagTracker.modifyDamage(owner, this, target, damage);
        DamageSource damageSource = LibDamageTypes.of(owner.level(), LibDamageTypes.SUMMONER, owner);
        float finalDamage = damage;
        if (Immunity.withCause(this, () -> target.hurt(damageSource, finalDamage)))
            onSuccessfulHit(target);
        removed = true;
    }

    protected abstract void onSuccessfulHit(LivingEntity target);

    protected final ServerPlayer owner() {
        return owner;
    }

    public final SummonRenderPart renderPart() {
        Vec3 direction = velocity.normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float pitch = (float) Math.toDegrees(Math.asin(-direction.y));
        return new SummonRenderPart(uuid, type, new SummonPose(position, yaw, pitch, 0.0F),
                SummonVisualState.DEFAULT, 0);
    }

    public final boolean isRemoved() {
        return removed;
    }

    @Override
    public final UUID getSummonOwnerId() {
        return owner.getUUID();
    }

    @Override
    public final Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    @Override
    public final int confluence$getImmunityDuration(DamageSource damageSource) {
        return 1;
    }
}
