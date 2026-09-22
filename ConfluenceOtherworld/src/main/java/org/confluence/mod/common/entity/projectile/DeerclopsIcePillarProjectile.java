package org.confluence.mod.common.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.data.map.CreatureDefinition.ProjectileOverrides;
import org.confluence.mod.common.init.ModEffects;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/// 独眼巨鹿近距离攻击形成的短时冰柱。
///
/// 冰柱本身不移动，也不会因为接触方块而提前消失。实体只会被同一根冰柱
/// 命中一次；碰撞高度随可见冰块逐步增长，避免画面尚未升起时提前伤到玩家。
public final class DeerclopsIcePillarProjectile extends Projectile implements GeoEntity {
    private static final EntityDataAccessor<Vector3f> DATA_AXIS = SynchedEntityData.defineId(DeerclopsIcePillarProjectile.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Boolean> DATA_RENDER_WAVE = SynchedEntityData.defineId(DeerclopsIcePillarProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Vector3f> DATA_WAVE_DIRECTION = SynchedEntityData.defineId(DeerclopsIcePillarProjectile.class, EntityDataSerializers.VECTOR3);
    private static final int LIFETIME = 40;
    private final Set<UUID> hitEntities = new HashSet<>();
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private float damage;

    public DeerclopsIcePillarProjectile(EntityType<? extends DeerclopsIcePillarProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
        Vector3f axis = new Vector3f(random.nextFloat() - 0.5F, 2.0F, random.nextFloat() - 0.5F).normalize();
        entityData.set(DATA_AXIS, axis);
    }

    public void configure(Mob owner, Vec3 origin, float damage) {
        setOwner(owner);
        setPos(origin);
        this.damage = ProjectileOverrides.get(owner, getType()).damageOr(damage);
    }

    public void configureWaveModel(Mob owner, Vec3 origin, Vec3 direction) {
        Vec3 horizontal = direction.multiply(1.0, 0.0, 1.0).normalize();
        setOwner(owner);
        setPos(origin);
        entityData.set(DATA_RENDER_WAVE, true);
        entityData.set(DATA_WAVE_DIRECTION, horizontal.toVector3f());
        float yaw = (float) Math.toDegrees(Math.atan2(-horizontal.x, horizontal.z));
        setYRot(yaw);
        yRotO = yaw;
    }

    public boolean rendersWaveModel() {
        return entityData.get(DATA_RENDER_WAVE);
    }

    public Vec3 getWaveDirection() {
        return new Vec3(entityData.get(DATA_WAVE_DIRECTION));
    }

    public Vector3f getAxis() {
        return entityData.get(DATA_AXIS);
    }

    public int getVisibleBlockCount() {
        return Math.min(4, tickCount * Math.max(0, LIFETIME - tickCount) / 75);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_AXIS, new Vector3f(0.0F, 1.0F, 0.0F));
        entityData.define(DATA_RENDER_WAVE, false);
        entityData.define(DATA_WAVE_DIRECTION, new Vector3f(0.0F, 0.0F, 1.0F));
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > LIFETIME) {
            discard();
            return;
        }
        if (rendersWaveModel() || level().isClientSide || !(getOwner() instanceof Mob owner)) {
            return;
        }

        int visibleBlocks = getVisibleBlockCount();
        if (visibleBlocks <= 0) {
            return;
        }
        Vector3f axis = getAxis();
        Vec3 direction = new Vec3(axis.x(), axis.y(), axis.z()).normalize();
        for (int blockIndex = 0; blockIndex < visibleBlocks; blockIndex++) {
            Vec3 center = position().add(direction.scale(blockIndex + 0.5));
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().move(center.subtract(position())).inflate(0.35),
                    target -> target != owner && owner.canAttack(target))) {
                if (hitEntities.add(target.getUUID()) && target.hurt(damageSources().mobProjectile(this, owner), damage)) {
                    target.addEffect(new MobEffectInstance(ModEffects.FROST_BURN.get(), 100));
                }
            }
        }
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return target instanceof LivingEntity living && getOwner() instanceof Mob owner && living != owner && owner.canAttack(living) && !hitEntities.contains(target.getUUID());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
