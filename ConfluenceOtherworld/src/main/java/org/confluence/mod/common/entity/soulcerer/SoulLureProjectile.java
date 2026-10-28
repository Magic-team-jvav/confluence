package org.confluence.mod.common.entity.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.soulcerer.SoulWeaponItem;
import org.confluence.mod.common.item.sword.SwordProjectileAppearance;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/// 魂师通用弹幕：默认由粒子风暴特效负责外观，命中可附带范围伤害。
///
/// 引魂、强力引魂、附魂印记、引魂激荡、迷魂孢雾、灵魂突击与灵魂引发的连锁弹幕共用同一个实体，
/// 差异全部来自构造参数（特效、速度、重力、射程、命中范围），不为每个技能再注册实体。
/// 魂引天星是唯一的例外：外观换成星怒的坠星面片（见 {@link #STAR_APPEARANCE}），因此不再带粒子。
///
/// 命中行为按优先级结算：先扣穿透（颂魂指引），穿透用完再弹射一次（引魂弹幕自带一次，引渡者书再 +1），
/// 两者都耗尽才消散。弹射时从被打中的敌怪朝最近的另一个敌怪射出一发，没有第二个敌怪就朝随机偏移方向射出。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulLureProjectile extends Projectile {
    /// 粒子风暴定义的灵魂弹幕特效，拖尾与消失时的爆散都由该特效负责。
    public static final ResourceLocation PARTICLE = Confluence.asResource("soul_projectile");
    /// 带路的老前辈（主武器词条）用的满强度追踪插值比例；弱追踪用
    /// {@link SoulSkillParameters#SOUL_LURE_WEAK_HOMING_STRENGTH}，两者共用同一套转向逻辑。
    private static final double FULL_HOMING_STRENGTH = 0.5;
    /// 迷魂孢雾使用的孢子特效。
    /// 迷魂孢雾使用的孢子特效（专用粒子风暴定义，7 帧序列帧贴图）。
    ///
    /// 注意 id **不能**再叫 `spore_cloud`：那是 `ModParticleTypes` 里注册的原版粒子类型，
    /// 同名会让发射器走上另一条解析路径，也是旧版迷魂孢雾崩溃的诱因之一。
    public static final ResourceLocation CONFUSE_SPORE_PARTICLE = Confluence.asResource("confuse_spore_projectile");
    /// 亵渎之魂使用的火球特效。
    public static final ResourceLocation FIREBALL_PARTICLE = Confluence.asResource("ball_of_fire");
    /// 「不画粒子」的标记：灵魂突击的子弹不需要任何粒子特效。
    public static final ResourceLocation NO_PARTICLE = Confluence.asResource("none");
    /// 魂引天星的外观：直接复刻星怒坠星弹射物的交叉面片，取代原来的 `falling_star` 粒子。
    ///
    /// 参数与 `SwordItems#STARFURY` 的 `SwordProjectileAppearance.Cross` 完全一致，
    /// 换成这里只是为了让魂师技能不必依赖一把武器。
    public static final SwordProjectileAppearance.Cross STAR_APPEARANCE = new SwordProjectileAppearance.Cross(
            Confluence.asResource("textures/entity/star_fury_projectile.png"), 0xFFFFFFFF, 2.0F, 18.0F, 10);

    private static final EntityDataAccessor<Boolean> DATA_STAR_APPEARANCE = SynchedEntityData.defineId(SoulLureProjectile.class, EntityDataSerializers.BOOLEAN);
    /// 粒子特效 id 的同步通道。
    ///
    /// 弹幕是在服务端构造的，`particle` 字段不会自己过到客户端；不同步的话客户端永远按默认的
    /// `confluence:soul_projectile` 画，迷魂孢雾的孢子特效就永远不会生效。
    private static final EntityDataAccessor<String> DATA_PARTICLE = SynchedEntityData.defineId(SoulLureProjectile.class, EntityDataSerializers.STRING);

    private static final String DAMAGE_TAG = "Damage";
    private static final String TRAVELLED_TAG = "TravelledDistance";
    private static final String PARTICLE_TAG = "Particle";
    private static final String GRAVITY_TAG = "Gravity";
    private static final String MAX_DISTANCE_TAG = "MaxDistance";
    private static final String MAX_AGE_TAG = "MaxAge";
    private static final String AOE_RADIUS_TAG = "AoeRadius";
    private static final String AOE_DAMAGE_TAG = "AoeDamage";
    private static final String SOUL_MARK_ON_HIT_TAG = "SoulMarkOnHit";
    private static final String STAR_APPEARANCE_TAG = "StarAppearance";
    private static final String SPAWN_DELAY_TAG = "SpawnDelay";
    private static final String BOUNCES_TAG = "Bounces";
    private static final String PIERCE_TAG = "Pierce";
    private static final String HOMING_TAG = "Homing";
    private static final String WEAK_HOMING_TAG = "WeakHoming";
    private static final String HIT_ENTITIES_TAG = "HitEntities";

    private float damage;
    private double travelledDistance;
    private ResourceLocation particle = PARTICLE;
    private double gravity;
    private double maxDistance = SoulSkillParameters.SOUL_LURE_MAX_DISTANCE;
    private int maxAge = SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS;
    private double aoeRadius;
    private float aoeDamage;
    /// 附魂印记专用：命中时给**被打中的敌人**挂几 tick 附魂印记；0 表示没有该效果。
    private int soulMarkOnHitTicks;
    /// 生成延迟：魂引天星齐射用它与前一颗错开落地时间；延迟期间不移动、不判定。
    private int spawnDelayTicks;
    /// 命中后的剩余弹射次数。
    private int bouncesRemaining = SoulSkillParameters.SOUL_LURE_BOUNCE_COUNT;
    /// 命中后的剩余穿透次数（颂魂指引在生成后补入）。
    private int pierceRemaining;
    /// 是否追踪最近敌怪（带路的老前辈，转向强度 0.5）。
    private boolean homing;
    /// 弱追踪开关（引魂系列弹幕默认开启，转向强度只有 0.15，且不追身后的目标）。
    private boolean weakHoming;
    /// 命中敌怪后在该位置释放一次灵魂激荡（引魂激荡用）；0 表示不释放。
    private double surgeOnHitRadius;
    private boolean weaponTraitsResolved;
    private final Set<UUID> hitEntities = new HashSet<>();
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
        /// 粒子 id 同步给客户端，否则客户端只会按默认的引魂粒子画（迷魂孢雾的孢子特效就不生效）。
        entityData.set(DATA_PARTICLE, particle.toString());
        shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0, speed, 0);
    }

    /// 从指定位置与方向发射，供孢子弹幕、连锁弹幕与弹射弹幕使用。
    public SoulLureProjectile(LivingEntity owner, Vec3 position, Vec3 direction, float damage, ResourceLocation particle, float speed, double gravity, double maxDistance, int maxAge, double aoeRadius, float aoeDamage) {
        this(owner, damage, particle, speed, gravity, maxDistance, maxAge, aoeRadius, aoeDamage);
        setPos(position);
        setDeltaMovement(direction.normalize().scale(speed));
    }

    /// 客户端画特效用的粒子 id：读同步数据（服务端构造时写入），解析失败才回落到默认引魂粒子。
    private ResourceLocation syncedParticle() {
        ResourceLocation parsed = ResourceLocation.tryParse(entityData.get(DATA_PARTICLE));
        return parsed == null ? PARTICLE : parsed;
    }

    /// 附魂印记：设置命中时给敌人挂的附魂印记时长（tick），0 表示关闭。
    public void setSoulMarkOnHit(int ticks) {
        this.soulMarkOnHitTicks = Math.max(0, ticks);
    }

    /// 开关弱追踪：引魂系列弹幕默认打开，孢子这类需要散开的弹幕关掉。
    public void setWeakHoming(boolean value) {
        this.weakHoming = value;
    }

    /// 覆盖本发弹幕的剩余弹射次数（魂引天星的坠星不弹射）。
    public void setBounces(int count) {
        this.bouncesRemaining = Math.max(0, count);
    }

    /// 追加弹射次数（引渡者的技能书）。
    public void addBounces(int extra) {
        this.bouncesRemaining = Math.max(0, this.bouncesRemaining + extra);
    }

    /// 设置生成延迟：延迟期间弹幕停在原地，不移动也不结算命中。
    public void setSpawnDelay(int ticks) {
        this.spawnDelayTicks = Math.max(0, ticks);
    }

    /// 命中敌怪时在该位置补一发灵魂激荡（引魂激荡用）；半径 0 表示关闭。
    public void setSurgeOnHit(double radius) {
        this.surgeOnHitRadius = Math.max(0.0, radius);
    }

    /// 魂引天星的坠星：从指定位置向下坠落，外观使用星怒的坠星面片、不带粒子、不弹射。
    public static SoulLureProjectile star(LivingEntity owner, Vec3 position, Vec3 direction, float damage, float speed, double gravity) {
        SoulLureProjectile projectile = new SoulLureProjectile(owner, position, direction, damage, PARTICLE, speed, gravity,
                SoulSkillParameters.SOUL_LURE_MAX_DISTANCE, SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS, 0.0, 0.0F);
        projectile.setStarAppearance(true);
        projectile.setBounces(0);
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

    /// 附魂印记：给被打中的敌人挂 10 秒印记（时长由技能传入）。
    ///
    /// 印记本身不改伤害：之后**所有技能**命中它都回 1 魂、击杀它回 2 魂，
    /// 结算点在 `LivingEntityEvents#soulWeaponTraitsOnSkillHit` 与 `#deathSoul`。
    private void applySoulMark(Entity victim) {
        if (soulMarkOnHitTicks <= 0 || !(victim instanceof LivingEntity living) || living.level().isClientSide) {
            return;
        }
        living.addEffect(new MobEffectInstance(ModEffects.SOUL_MARK.get(), soulMarkOnHitTicks, 0, false, true, true));
    }

    /// 主武器自带的两条弹幕词条：颂魂指引的穿透、带路的老前辈的追踪。
    ///
    /// 只在首个服务端 tick 结算一次，之后的换手不影响这一发已经射出去的弹幕。
    private void resolveWeaponTraits() {
        if (weaponTraitsResolved) {
            return;
        }
        weaponTraitsResolved = true;
        if (!(getOwner() instanceof Player player)) {
            return;
        }
        if (PlayerUtils.holdsSoulWeaponTrait(player, SoulWeaponItem.Trait.LURE_PIERCE)) {
            pierceRemaining += SoulSkillParameters.SOUL_CHANT_GUIDE_EXTRA_PIERCE;
        }
        homing = PlayerUtils.holdsSoulWeaponTrait(player, SoulWeaponItem.Trait.LURE_HOMING);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_STAR_APPEARANCE, false);
        entityData.define(DATA_PARTICLE, PARTICLE.toString());
    }

    /// 每刻沿移动线段碰撞，先处理命中再检查最大距离；客户端维护粒子特效。
    @Override
    public void tick() {
        super.tick();
        /// 齐射的坠星靠这个字段错开出生时刻：延迟期间不移动、不判定、也不生成粒子。
        if (spawnDelayTicks > 0) {
            spawnDelayTicks--;
            return;
        }
        /// 星怒外观由实体渲染器负责，这里不再叠一层粒子。
        ///
        /// **每个弹幕只登记一次发射器**（`emitter == null`）：旧实现在 `isRemoved()` 时重建，
        /// 迷魂孢雾这种短命/多发的弹幕会每刻往 `MolangParticleEngine` 的 `emitters` 里 put，
        /// 而引擎正用 fastutil 的复用迭代器遍历同一张表，于是被抛
        /// `NullPointerException: this.wrapped is null`（见 run/crash-reports/crash-2026-10-21_22.56.41-client.txt）。
        if (level().isClientSide && !isStarAppearance() && emitter == null && !NO_PARTICLE.equals(syncedParticle())) {
            this.emitter = new ParticleEmitter(level(), position(), syncedParticle());
            emitter.attachEntity(this);
            emitter.hideOutline = true;
            MolangParticleEngine.INSTANCE._addEmitter(emitter);
        }
        if (!level().isClientSide) {
            resolveWeaponTraits();
        }
        if (gravity > 0.0) {
            setDeltaMovement(getDeltaMovement().add(0.0, -gravity, 0.0));
        }
        if (homing && !level().isClientSide) {
            steerTowardsTarget(FULL_HOMING_STRENGTH, SoulSkillParameters.SOUL_LURE_BOUNCE_RADIUS, true);
        } else if (weakHoming && !level().isClientSide) {
            steerTowardsTarget(SoulSkillParameters.SOUL_LURE_WEAK_HOMING_STRENGTH,
                    SoulSkillParameters.SOUL_LURE_WEAK_HOMING_RADIUS, false);
        }
        Vec3 movement = getDeltaMovement();
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (!level().isClientSide && hit.getType() != HitResult.Type.MISS) {
            setPos(hit.getLocation());
            if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity victim
                    && hitEntities.add(LibEntityUtils.dedupeIdentity(victim).getUUID())) {
                victim.hurt(LibDamageTypes.soul(level(), this, getOwner()), damage);
                applySoulMark(victim);
                /// 引魂激荡：命中位置再来一发灵魂激荡，**不消耗灵魂**。
                spawnSurgeAt(victim);
                /// 颂魂指引：还有穿透次数就继续沿原方向飞。
                if (pierceRemaining > 0) {
                    pierceRemaining--;
                    advanceOneStep();
                    return;
                }
                /// 引魂弹幕：从被打中的敌怪朝下一个敌怪弹射。
                if (bouncesRemaining > 0) {
                    bouncesRemaining--;
                    spawnBounce(victim);
                    discard();
                    return;
                }
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

    /// 引魂激荡：在命中位置释放一次灵魂激荡。
    ///
    /// 这是**技能的衍生攻击**，和灵魂引发的连锁弹幕一样不消耗灵魂。
    private void spawnSurgeAt(Entity victim) {
        if (surgeOnHitRadius <= 0.0 || !(getOwner() instanceof LivingEntity owner)) {
            return;
        }
        SpiritSurgeEntity surge = new SpiritSurgeEntity(owner, damage, surgeOnHitRadius,
                SoulSkillParameters.SPIRIT_SURGE_VERTICAL_RANGE, SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS, 0, 0.0F);
        surge.setPos(victim.getBoundingBox().getCenter());
        level().addFreshEntity(surge);
    }

    /// 穿透时沿用这一 tick 的位移继续前进，射程与年龄照常结算。
    private void advanceOneStep() {
        Vec3 movement = getDeltaMovement();
        setPos(position().add(movement));
        travelledDistance += movement.length();
        if (travelledDistance >= maxDistance || tickCount >= maxAge) {
            applyAoeDamage();
            discard();
        }
    }

    /// 弹射：从被打中的敌怪位置朝最近的另一个敌怪射出一发；没有目标就朝随机偏移方向射出。
    private void spawnBounce(Entity victim) {
        if (!(getOwner() instanceof LivingEntity owner)) {
            return;
        }
        Vec3 from = victim.getBoundingBox().getCenter();
        LivingEntity next = findNextTarget(from, SoulSkillParameters.SOUL_LURE_BOUNCE_RADIUS, victim);
        Vec3 direction;
        if (next != null) {
            direction = LibEntityUtils.aimPoint(next, from).subtract(from);
        } else {
            RandomSource random = RandomSource.create();
            double yaw = random.nextDouble() * Math.PI * 2.0;
            double pitch = (random.nextDouble() - 0.5) * 0.6;
            direction = new Vec3(Math.cos(yaw), pitch, Math.sin(yaw));
        }
        double speed = Math.max(0.1, getDeltaMovement().length());
        SoulLureProjectile bounced = new SoulLureProjectile(owner, from, direction, damage, particle, (float) speed,
                gravity, maxDistance, maxAge, aoeRadius, aoeDamage);
        bounced.setStarAppearance(isStarAppearance());
        bounced.setSoulMarkOnHit(soulMarkOnHitTicks);
        bounced.setSurgeOnHit(surgeOnHitRadius);
        bounced.setBounces(bouncesRemaining);
        bounced.pierceRemaining = pierceRemaining;
        bounced.homing = homing;
        bounced.weakHoming = weakHoming;
        bounced.weaponTraitsResolved = true;
        bounced.hitEntities.addAll(hitEntities);
        level().addFreshEntity(bounced);
    }

    /// 追踪：每刻把速度方向朝最近的敌怪插值，速度大小保持不变。
    ///
    /// @param strength   每刻朝目标方向靠拢的比例：弱追踪 0.15，带路的老前辈 0.5
    /// @param radius     索敌半径
    /// @param chaseBack 是否追身后的目标（弱追踪不追，避免弹道被强行掰回去）
    private void steerTowardsTarget(double strength, double radius, boolean chaseBack) {
        LivingEntity target = findNextTarget(position(), radius, null);
        if (target == null) {
            return;
        }
        Vec3 movement = getDeltaMovement();
        double speed = movement.length();
        if (speed <= 1.0E-4) {
            return;
        }
        Vec3 toTarget = LibEntityUtils.aimPoint(target, position()).subtract(position());
        if (toTarget.lengthSqr() <= 1.0E-4) {
            return;
        }
        Vec3 desired = toTarget.normalize().scale(speed);
        if (!chaseBack && movement.dot(desired) <= 0.0) {
            return;
        }
        Vec3 steered = movement.scale(1.0 - strength).add(desired.scale(strength));
        if (steered.lengthSqr() <= 1.0E-8) {
            return;
        }
        setDeltaMovement(steered.normalize().scale(speed));
    }

    /// 索敌：半径内最近的、尚未被这一发弹幕打过的敌怪。
    private @Nullable LivingEntity findNextTarget(Vec3 from, double radius, @Nullable Entity exclude) {
        AABB box = new AABB(from, from).inflate(radius);
        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (Entity candidate : LibEntityUtils.queryHitCandidates(level(), this, box, this::canHitEntity)) {
            LivingEntity living = LibEntityUtils.logicalLivingTarget(candidate);
            if (living == null || living == exclude || !EnemyDamageRules.isEnemy(living)) continue;
            double distance = candidate.getBoundingBox().getCenter().distanceToSqr(from);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = living;
            }
        }
        return best;
    }

    /// 已经打过的敌怪不再命中，穿透弹幕因此不会对同一目标连续结算。
    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !hitEntities.contains(LibEntityUtils.dedupeIdentity(target).getUUID());
    }

    /// 命中或消散时对周围目标补一次范围伤害，半径为零时不做任何事。
    private void applyAoeDamage() {
        if (level().isClientSide || aoeRadius <= 0.0 || aoeDamage <= 0.0F) {
            return;
        }
        Vec3 center = position();
        for (Entity target : LibEntityUtils.areaTargets(level(), this, getBoundingBox().inflate(aoeRadius),
                candidate -> candidate != getOwner() && canHitEntity(candidate)
                        && LibEntityUtils.logicalLivingTarget(candidate) != null)) {
            if (target.getBoundingBox().getCenter().distanceToSqr(center) <= aoeRadius * aoeRadius)
                target.hurt(LibDamageTypes.soul(level(), this, getOwner()), aoeDamage);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat(DAMAGE_TAG);
        travelledDistance = tag.getDouble(TRAVELLED_TAG);
        if (tag.contains(PARTICLE_TAG)) {
            ResourceLocation parsed = ResourceLocation.tryParse(tag.getString(PARTICLE_TAG));
            if (parsed != null) {
                particle = parsed;
                entityData.set(DATA_PARTICLE, parsed.toString());
            }
        }
        gravity = tag.getDouble(GRAVITY_TAG);
        maxDistance = tag.getDouble(MAX_DISTANCE_TAG);
        maxAge = tag.getInt(MAX_AGE_TAG);
        aoeRadius = tag.getDouble(AOE_RADIUS_TAG);
        aoeDamage = tag.getFloat(AOE_DAMAGE_TAG);
        soulMarkOnHitTicks = tag.getInt(SOUL_MARK_ON_HIT_TAG);
        spawnDelayTicks = tag.getInt(SPAWN_DELAY_TAG);
        bouncesRemaining = tag.getInt(BOUNCES_TAG);
        pierceRemaining = tag.getInt(PIERCE_TAG);
        homing = tag.getBoolean(HOMING_TAG);
        weakHoming = tag.getBoolean(WEAK_HOMING_TAG);
        hitEntities.clear();
        for (Tag hit : tag.getList(HIT_ENTITIES_TAG, Tag.TAG_INT_ARRAY)) {
            hitEntities.add(NbtUtils.loadUUID(hit));
        }
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
        tag.putInt(SOUL_MARK_ON_HIT_TAG, soulMarkOnHitTicks);
        tag.putInt(SPAWN_DELAY_TAG, spawnDelayTicks);
        tag.putInt(BOUNCES_TAG, bouncesRemaining);
        tag.putInt(PIERCE_TAG, pierceRemaining);
        tag.putBoolean(HOMING_TAG, homing);
        tag.putBoolean(WEAK_HOMING_TAG, weakHoming);
        ListTag hits = new ListTag();
        for (UUID id : hitEntities) {
            hits.add(NbtUtils.createUUID(id));
        }
        tag.put(HIT_ENTITIES_TAG, hits);
        tag.putBoolean(STAR_APPEARANCE_TAG, isStarAppearance());
    }
}
