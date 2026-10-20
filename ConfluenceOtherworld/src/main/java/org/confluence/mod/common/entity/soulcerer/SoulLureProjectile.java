package org.confluence.mod.common.entity.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
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
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.sword.SwordProjectileAppearance;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import javax.annotation.ParametersAreNonnullByDefault;

/// 魂师通用弹幕：默认由粒子风暴特效负责外观，命中可附带范围伤害。
///
/// 引魂、强力引魂、附魂印记、引魂激荡、迷魂孢雾、灵魂突击与灵魂引发的连锁弹幕共用同一个实体，
/// 差异全部来自构造参数（特效、速度、重力、射程、命中范围），不为每个技能再注册实体。
/// 魂引天星是唯一的例外：外观换成星怒的坠星面片（见 {@link #STAR_APPEARANCE}），因此不再带粒子。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulLureProjectile extends Projectile {
    /// 粒子风暴定义的灵魂弹幕特效，拖尾与消失时的爆散都由该特效负责。
    public static final ResourceLocation PARTICLE = Confluence.asResource("soul_projectile");
    /// 迷魂孢雾使用的孢子特效。
    public static final ResourceLocation SPORE_PARTICLE = Confluence.asResource("spore_cloud");
    /// 亵渎之魂使用的火球特效。
    public static final ResourceLocation FIREBALL_PARTICLE = Confluence.asResource("ball_of_fire");
    /// 魂引天星的外观：直接复刻星怒坠星弹射物的交叉面片，取代原来的 `falling_star` 粒子。
    ///
    /// 参数与 `SwordItems#STARFURY` 的 `SwordProjectileAppearance.Cross` 完全一致，
    /// 换成这里只是为了让魂师技能不必依赖一把武器。
    public static final SwordProjectileAppearance.Cross STAR_APPEARANCE = new SwordProjectileAppearance.Cross(
            Confluence.asResource("textures/entity/star_fury_projectile.png"), 0xFFFF9696, 2.0F, 18.0F, 10);

    private static final EntityDataAccessor<Boolean> DATA_STAR_APPEARANCE = SynchedEntityData.defineId(SoulLureProjectile.class, EntityDataSerializers.BOOLEAN);

    private static final String DAMAGE_TAG = "Damage";
    private static final String TRAVELLED_TAG = "TravelledDistance";
    private static final String PARTICLE_TAG = "Particle";
    private static final String GRAVITY_TAG = "Gravity";
    private static final String MAX_DISTANCE_TAG = "MaxDistance";
    private static final String MAX_AGE_TAG = "MaxAge";
    private static final String AOE_RADIUS_TAG = "AoeRadius";
    private static final String AOE_DAMAGE_TAG = "AoeDamage";
    private static final String ENHANCED_SOUL_ON_HIT_TAG = "EnhancedSoulOnHit";
    private static final String STAR_APPEARANCE_TAG = "StarAppearance";

    private float damage;
    private double travelledDistance;
    private ResourceLocation particle = PARTICLE;
    private double gravity;
    private double maxDistance = SoulSkillParameters.SOUL_LURE_MAX_DISTANCE;
    private int maxAge = SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS;
    private double aoeRadius;
    private float aoeDamage;
    /// 附魂印记专用：命中时给自己挂几 tick 强力附魂；0 表示没有该效果。
    private int enhancedSoulOnHitTicks;
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

    /// 附魂印记：设置命中时给自己挂的强力附魂时长（tick），0 表示关闭。
    public void setEnhancedSoulOnHit(int ticks) {
        this.enhancedSoulOnHitTicks = Math.max(0, ticks);
    }

    /// 魂引天星的坠星：从指定位置向下坠落，外观使用星怒的坠星面片、不带粒子。
    public static SoulLureProjectile star(LivingEntity owner, Vec3 position, Vec3 direction, float damage, float speed, double gravity) {
        SoulLureProjectile projectile = new SoulLureProjectile(owner, position, direction, damage, PARTICLE, speed, gravity,
                SoulSkillParameters.SOUL_LURE_MAX_DISTANCE, SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS, 0.0, 0.0F);
        projectile.setStarAppearance(true);
        return projectile;
    }

    /// 切换成星怒坠星的外观；打开后客户端不再生成任何粒子特效。
    public void setStarAppearance(boolean value) {
        entityData.set(DATA_STAR_APPEARANCE, value);
    }

    /// 是否使用星怒坠星的外观（由客户端渲染器读取）。
    public boolean isStarAppearance() {
        return entityData.get(DATA_STAR_APPEARANCE);
    }

    /// 附魂印记：命中给短增益，若这一下直接击杀则换成更长的增益。
    private void grantEnhancedSoul(Entity victim) {
        if (enhancedSoulOnHitTicks <= 0 || !(getOwner() instanceof ServerPlayer owner)) {
            return;
        }
        boolean killed = victim instanceof LivingEntity living && living.getHealth() <= 0.0F;
        int ticks = killed ? SoulSkillParameters.SOUL_MARK_KILL_BUFF_TICKS : enhancedSoulOnHitTicks;
        owner.addEffect(new MobEffectInstance(ModEffects.ENHANCED_SOUL.get(), ticks, 0, false, true, true));
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_STAR_APPEARANCE, false);
    }

    /// 每刻沿移动线段碰撞，先处理命中再检查最大距离；客户端维护粒子特效。
    @Override
    public void tick() {
        super.tick();
        /// 星怒外观由实体渲染器负责，这里不再叠一层粒子。
        if (level().isClientSide && !isStarAppearance() && (emitter == null || emitter.isRemoved())) {
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
                Entity victim = entityHit.getEntity();
                victim.hurt(LibDamageTypes.soul(level(), this, getOwner()), damage);
                grantEnhancedSoul(victim);
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
        enhancedSoulOnHitTicks = tag.getInt(ENHANCED_SOUL_ON_HIT_TAG);
        setStarAppearance(tag.getBoolean(STAR_APPEARANCE_TAG));
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
        tag.putInt(ENHANCED_SOUL_ON_HIT_TAG, enhancedSoulOnHitTicks);
        tag.putBoolean(STAR_APPEARANCE_TAG, isStarAppearance());
    }
}
