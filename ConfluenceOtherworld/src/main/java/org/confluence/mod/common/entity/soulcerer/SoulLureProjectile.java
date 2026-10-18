package org.confluence.mod.common.entity.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import javax.annotation.ParametersAreNonnullByDefault;

/// 魂师通用弹幕：由粒子风暴特效负责外观，命中可附带范围伤害。
///
/// 引魂、强力引魂、附魂印记、引魂激荡、迷魂孢雾、灵魂枪手与灵魂引发的连锁弹幕共用同一个实体，
/// 差异全部来自构造参数（特效、速度、重力、射程、命中范围），不为每个技能再注册实体。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulLureProjectile extends Projectile {
    /// 粒子风暴定义的灵魂弹幕特效，拖尾与消失时的爆散都由该特效负责。
    public static final ResourceLocation PARTICLE = Confluence.asResource("soul_projectile");
    /// 迷魂孢雾使用的孢子特效。
    public static final ResourceLocation SPORE_PARTICLE = Confluence.asResource("spore_cloud");
    /// 魂引天星与亵渎之魂复用的星、火球特效。
    public static final ResourceLocation STAR_PARTICLE = Confluence.asResource("falling_star");
    public static final ResourceLocation FIREBALL_PARTICLE = Confluence.asResource("ball_of_fire");

    private static final String DAMAGE_TAG = "Damage";
    private static final String TRAVELLED_TAG = "TravelledDistance";
    private static final String PARTICLE_TAG = "Particle";
    private static final String GRAVITY_TAG = "Gravity";
    private static final String MAX_DISTANCE_TAG = "MaxDistance";
    private static final String MAX_AGE_TAG = "MaxAge";
    private static final String AOE_RADIUS_TAG = "AoeRadius";
    private static final String AOE_DAMAGE_TAG = "AoeDamage";

    private float damage;
    private double travelledDistance;
    private ResourceLocation particle = PARTICLE;
    private double gravity;
    private double maxDistance = SoulSkillParameters.SOUL_LURE_MAX_DISTANCE;
    private int maxAge = SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS;
    private double aoeRadius;
    private float aoeDamage;
    private @Nullable ParticleEmitter emitter;

    public SoulLureProjectile(EntityType<? extends SoulLureProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    /// 默认引魂弹幕。
    public SoulLureProjectile(LivingEntity owner, float damage) {
        this(owner, damage, PARTICLE, SoulSkillParameters.SOUL_LURE_SPEED, 0.0, SoulSkillParameters.SOUL_LURE_MAX_DISTANCE, SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS, 0.0, 0.0F);
    }

    /// 指定外观、弹道与命中范围的通用构造。
    public SoulLureProjectile(LivingEntity owner, float damage, ResourceLocation particle, float speed, double gravity, double maxDistance, int maxAge, double aoeRadius, float aoeDamage) {
        this(ModEntities.SOUL_LURE_PROJECTILE.get(), owner.level());
        setOwner(owner);
        setPos(owner.getX(), owner.getEyeY(), owner.getZ());
        this.damage = damage;
        this.particle = particle;
        this.gravity = Math.max(0.0, gravity);
        this.maxDistance = maxDistance;
        this.maxAge = maxAge;
        this.aoeRadius = Math.max(0.0, aoeRadius);
        this.aoeDamage = aoeDamage;
        shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0, speed, 0);
    }

    /// 从指定位置与方向发射，供孢子弹幕与连锁弹幕使用。
    public SoulLureProjectile(LivingEntity owner, Vec3 position, Vec3 direction, float damage, ResourceLocation particle, float speed, double gravity, double maxDistance, int maxAge, double aoeRadius, float aoeDamage) {
        this(owner, damage, particle, speed, gravity, maxDistance, maxAge, aoeRadius, aoeDamage);
        setPos(position);
        setDeltaMovement(direction.normalize().scale(speed));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    /// 每刻沿移动线段碰撞，先处理命中再检查最大距离；客户端维护粒子特效。
    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && (emitter == null || emitter.isRemoved())) {
            this.emitter = new ParticleEmitter(level(), position(), particle);
            emitter.attachEntity(this);
            emitter.hideOutline = true;
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
        }
        if (gravity > 0.0) {
            setDeltaMovement(getDeltaMovement().add(0.0, -gravity, 0.0));
        }
        Vec3 movement = getDeltaMovement();
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (!level().isClientSide && hit.getType() != HitResult.Type.MISS) {
            setPos(hit.getLocation());
            if (hit instanceof EntityHitResult entityHit) {
                entityHit.getEntity().hurt(LibDamageTypes.soul(level(), this, getOwner()), damage);
            }
            applyAoeDamage();
            discard();
            return;
        }
        setPos(position().add(movement));
        travelledDistance += movement.length();
        if (!level().isClientSide && (travelledDistance >= maxDistance || tickCount >= maxAge)) {
            applyAoeDamage();
            discard();
        }
    }

    /// 命中或消散时对周围目标补一次范围伤害，半径为零时不做任何事。
    private void applyAoeDamage() {
        if (level().isClientSide || aoeRadius <= 0.0 || aoeDamage <= 0.0F) {
            return;
        }
        Vec3 center = position();
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(aoeRadius),
                target -> target.isAlive() && !target.isSpectator() && target != getOwner() && canHitEntity(target))) {
            if (target.position().distanceToSqr(center) <= aoeRadius * aoeRadius) {
                target.hurt(LibDamageTypes.soul(level(), this, getOwner()), aoeDamage);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat(DAMAGE_TAG);
        travelledDistance = tag.getDouble(TRAVELLED_TAG);
        if (tag.contains(PARTICLE_TAG)) {
            ResourceLocation parsed = ResourceLocation.tryParse(tag.getString(PARTICLE_TAG));
            if (parsed != null) particle = parsed;
        }
        gravity = tag.getDouble(GRAVITY_TAG);
        maxDistance = tag.getDouble(MAX_DISTANCE_TAG);
        maxAge = tag.getInt(MAX_AGE_TAG);
        aoeRadius = tag.getDouble(AOE_RADIUS_TAG);
        aoeDamage = tag.getFloat(AOE_DAMAGE_TAG);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat(DAMAGE_TAG, damage);
        tag.putDouble(TRAVELLED_TAG, travelledDistance);
        tag.putString(PARTICLE_TAG, particle.toString());
        tag.putDouble(GRAVITY_TAG, gravity);
        tag.putDouble(MAX_DISTANCE_TAG, maxDistance);
        tag.putInt(MAX_AGE_TAG, maxAge);
        tag.putDouble(AOE_RADIUS_TAG, aoeRadius);
        tag.putFloat(AOE_DAMAGE_TAG, aoeDamage);
    }
}
