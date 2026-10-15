package org.confluence.mod.common.entity.projectile.soul;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.soul.skill.SoulSkillCastContext;
import org.confluence.mod.common.soul.skill.SoulSkillParameters;
import org.joml.Vector3f;

public class SoulLureProjectile extends DamageSettableProjectile {
    private static final EntityDataAccessor<Vector3f> DATA_DIRECTION = SynchedEntityData.defineId(SoulLureProjectile.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Float> DATA_TRAVELLED_DISTANCE = SynchedEntityData.defineId(SoulLureProjectile.class, EntityDataSerializers.FLOAT);
    private double distanceTravelled;

    public SoulLureProjectile(EntityType<? extends SoulLureProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public SoulLureProjectile(SoulSkillCastContext context) {
        this(ModEntities.SOUL_LURE_PROJECTILE.get(), context.player().level());
        setOwner(context.player());
        setDamage(context.damage());
        setPos(context.player().getEyePosition());
        Vec3 direction = context.player().getLookAngle();
        entityData.set(DATA_DIRECTION, direction.toVector3f());
        setDeltaMovement(direction.scale(SoulSkillParameters.SOUL_LURE_SPEED));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DIRECTION, new Vector3f());
        builder.define(DATA_TRAVELLED_DISTANCE, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && (getOwner() == null || tickCount > SoulSkillParameters.SOUL_LURE_LIFETIME_TICKS)) {
            discard();
            return;
        }
        if (level().isClientSide) {
            distanceTravelled = Math.max(distanceTravelled, entityData.get(DATA_TRAVELLED_DISTANCE));
        }
        double remaining = SoulSkillParameters.SOUL_LURE_RANGE - distanceTravelled;
        if (remaining <= 0.0) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }
        if (level().isClientSide) {
            setDeltaMovement(new Vec3(entityData.get(DATA_DIRECTION)).scale(SoulSkillParameters.SOUL_LURE_SPEED));
        }
        Vec3 movement = getDeltaMovement();
        double distance = movement.length();
        if (distance > remaining) {
            movement = movement.scale(remaining / distance);
            distance = remaining;
            setDeltaMovement(movement);
        }
        if (!level().isClientSide) {
            HitResult result = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (result.getType() != HitResult.Type.MISS && !NeoForge.EVENT_BUS.post(new ProjectileImpactEvent(this, result)).isCanceled()) {
                onHit(result);
            }
            if (isRemoved()) {
                return;
            }
        }
        checkInsideBlocks();
        setPos(position().add(movement));
        distanceTravelled += distance;
        if (!level().isClientSide) {
            entityData.set(DATA_TRAVELLED_DISTANCE, (float) distanceTravelled);
        }
        updateRotation();
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return target.canBeHitByProjectile() && ProjectileHitRules.canHit(getOwner(), target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide) {
            Entity target = result.getEntity();
            if (getOwner() != null && ProjectileHitRules.canHit(getOwner(), target)) {
                ProjectileHitRules.damageRecipient(target).hurt(getDamageSource(), getDamage());
            }
            discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public float getCalculatedDamage() {
        return getDamage();
    }

    @Override
    public DamageSource getDamageSource() {
        return LibDamageTypes.of(level(), LibDamageTypes.SOUL, this, getOwner());
    }
}
