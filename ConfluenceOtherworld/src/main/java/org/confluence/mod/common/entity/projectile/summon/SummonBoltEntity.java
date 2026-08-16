package org.confluence.mod.common.entity.projectile.summon;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.api.summon.OwnedSummon;
import org.confluence.mod.api.whip.WhipTagTracker;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.summon.SummonInstance;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.UUID;

/// 黄蜂与小鬼共用的轻量召唤弹幕。
///
/// <p>弹幕保存基础伤害和玩家所有者 UUID，命中时读取当前召唤伤害并结算鞭痕。
/// 颜色只控制客户端可见轨迹，不参与行为判断。</p>
public class SummonBoltEntity extends DamageSettableProjectile implements OwnedSummon, Immunity {
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(SummonBoltEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HIT_EFFECT =
            SynchedEntityData.defineId(SummonBoltEntity.class, EntityDataSerializers.INT);
    private static final double MAX_LIFETIME = 80;
    private @Nullable UUID summonOwnerId;
    private @Nullable UUID intendedTargetId;

    public SummonBoltEntity(
            EntityType<? extends SummonBoltEntity> type,
            Level level
    ) {
        super(type, level);
        setNoGravity(true);
        noPhysics = false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(COLOR, 0xFFFFFF);
        entityData.define(HIT_EFFECT, HitEffect.NONE.id);
    }

    /// 由非实体召唤物运行实例生成弹幕；弹幕实体仅承担飞行、同步和碰撞。
    public void configure(SummonInstance source, LivingEntity target, int color, HitEffect hitEffect,
                          float velocity, float inaccuracy) {
        this.summonOwnerId = source.getSummonOwnerId();
        this.intendedTargetId = target.getUUID();
        this.entityData.set(COLOR, color & 0xFFFFFF);
        this.entityData.set(HIT_EFFECT, hitEffect.id);
        setOwner(source.owner());
        setPos(source.position());
        setDamage(source.stats().baseDamage());
        Vec3 aimPoint = source.actualTarget() != null && source.actualTarget() != source.target()
                ? source.targetPosition()
                : new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
        Vec3 direction = aimPoint.subtract(position()).normalize();
        shoot(direction.x, direction.y, direction.z, velocity, inaccuracy);
    }

    @Override
    public UUID getSummonOwnerId() {
        UUID resolved = resolveSummonOwnerId();
        if (resolved != null) {
            return resolved;
        }
        throw new IllegalStateException("Summon projectile owner has not been initialized");
    }

    /// 尝试解析玩家所有者，但允许客户端生成包刚到达、所有者尚未进入关卡的短暂窗口。
    /// 服务端在 {@link #configure} 中已经写入 UUID，因此权威伤害路径仍会立即失败，而不是静默降级。
    private @Nullable UUID resolveSummonOwnerId() {
        if (summonOwnerId != null) {
            return summonOwnerId;
        }
        Entity owner = getOwner();
        if (owner instanceof Player player) {
            summonOwnerId = player.getUUID();
        } else if (owner instanceof OwnedSummon summon) {
            summonOwnerId = summon.getSummonOwnerId();
        }
        return summonOwnerId;
    }

    @Override
    public boolean canHitEntity(Entity target) {
        if (!super.canHitEntity(target)) {
            return false;
        }
        UUID playerOwnerId = resolveSummonOwnerId();
        if (playerOwnerId == null) {
            // 客户端只负责预测可视碰撞；所有者实体尚未解析时等待下一 tick，不能让渲染线程崩溃。
            return false;
        }
        UUID targetOwnerId = resolveOwnedSummonOwner(target);
        if (playerOwnerId.equals(targetOwnerId)) {
            return false;
        }
        if (target.getUUID().equals(playerOwnerId)) {
            return false;
        }
        Entity impacted = ProjectileHitRules.impactedEntity(target);
        if (impacted instanceof Player
                && level() instanceof ServerLevel serverLevel) {
            var player = resolveSummonOwner(serverLevel);
            return player != null && ProjectileHitRules.canHit(player, target);
        }
        return true;
    }

    /// 读取另一召唤实体的玩家所有者时，同样容忍客户端生成顺序造成的短暂缺省。
    private static @Nullable UUID resolveOwnedSummonOwner(Entity entity) {
        if (entity instanceof SummonBoltEntity bolt) {
            return bolt.resolveSummonOwnerId();
        }
        if (entity instanceof OwnedSummon summon) {
            return summon.getSummonOwnerId();
        }
        return null;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity impacted = ProjectileHitRules.impactedEntity(result.getEntity());
        if (!level().isClientSide && impacted instanceof LivingEntity target) {
            DamageSource source = getDamageSource();
            if (!Immunity.isActive(this, target)) {
                int invulnerableTime = target.invulnerableTime;
                target.invulnerableTime = 0;
                try {
                    if (target.hurt(source, summonDamage(target))) {
                        HitEffect.byId(entityData.get(HIT_EFFECT)).apply(target);
                        Immunity.apply(this, source, target);
                    }
                } finally {
                    target.invulnerableTime = invulnerableTime;
                }
            }
            discard();
        }
    }

    private float summonDamage(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return getDamage();
        }
        Player owner = resolveSummonOwner(serverLevel);
        if (owner == null) {
            return getDamage();
        }
        float damage = getDamage() * (float) owner.getAttributeValue(LibAttributes.getSummonDamage());
        return WhipTagTracker.modifyDamage(owner, this, target, damage);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide && result.getType() == HitResult.Type.BLOCK) {
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldAbortSubclassTick()) {
            return;
        }
        if (tickCount > MAX_LIFETIME) {
            discard();
            return;
        }

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(
                this, this::canHitEntity);
        if (hitResult.getType() == HitResult.Type.MISS) {
            hitResult = findLivingEntityHit(getDeltaMovement());
        }
        if (hitResult.getType() != HitResult.Type.MISS) {
            hitTargetOrDeflectSelf(hitResult);
        }
        if (isRemoved()) {
            return;
        }

        checkInsideBlocks();
        Vec3 velocity = getDeltaMovement();
        setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);
        updateRotation();

        if (level().isClientSide) {
            int color = entityData.get(COLOR);
            Vector3f rgb = new Vector3f(
                    ((color >> 16) & 0xFF) / 255.0F,
                    ((color >> 8) & 0xFF) / 255.0F,
                    (color & 0xFF) / 255.0F
            );
            level().addParticle(
                    new DustParticleOptions(rgb, 0.8F),
                    getX(), getY(), getZ(),
                    0.0, 0.0, 0.0
            );
        }
    }

    /// 召唤弹幕只在当前战斗中短暂存在，不应随区块写入存档。
    ///
    /// <p>这与 1.21 侧基础弹幕的生命周期一致，也避免重载后把召唤物所有者误当成
    /// 玩家弹幕所有者进行恢复。</p>
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    /// 补充基础弹幕缺少的宽度扫掠检测。
    ///
    /// <p>召唤弹幕只有 0.2 格宽，而目标与弹丸都可能在同一 tick 内移动。原版移动向量检测
    /// 未命中时，再按弹丸实际半径扩展扫掠盒；仍然使用同一个敌我过滤器，并选择路径上最先
    /// 相交的目标，因此不会变成范围伤害或自动索敌。</p>
    private HitResult findLivingEntityHit(Vec3 velocity) {
        Vec3 start = position();
        Vec3 end = start.add(velocity);
        LivingEntity nearest = null;
        Vec3 nearestHit = null;
        double nearestDistance = Double.MAX_VALUE;

        /// 实体刚跨分区时，Level 的空间索引可能到下一 tick 才更新。原始瞄准目标通过 UUID
        /// 直接解析，但仍必须与当前直线段相交；这里只修复索引延迟，不改变弹道方向。
        if (intendedTargetId != null
                && level() instanceof ServerLevel serverLevel
                && serverLevel.getEntity(intendedTargetId) instanceof LivingEntity intended
                && canHitEntity(intended)) {
            Vec3 intersection = intersection(intended, start, end);
            if (intersection != null) {
                nearest = intended;
                nearestHit = intersection;
                nearestDistance = start.distanceToSqr(intersection);
            }
        }

        for (LivingEntity candidate : level().getEntitiesOfClass(
                LivingEntity.class,
                getBoundingBox().expandTowards(velocity).inflate(0.35),
                this::canHitEntity)) {
            Vec3 intersection = intersection(candidate, start, end);
            if (intersection == null) {
                continue;
            }
            double distance = start.distanceToSqr(intersection);
            if (distance < nearestDistance) {
                nearest = candidate;
                nearestHit = intersection;
                nearestDistance = distance;
            }
        }
        return nearest == null
                ? BlockHitResult.miss(end, getDirection(), blockPosition())
                : new EntityHitResult(nearest, nearestHit);
    }

    private static @Nullable Vec3 intersection(
            LivingEntity target,
            Vec3 start,
            Vec3 end
    ) {
        var hitBox = target.getBoundingBox().inflate(0.35);
        return hitBox.clip(start, end).orElse(hitBox.contains(start) ? start : null);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity owner = getOwner();
        int ownerId = owner == null ? 0 : owner.getId();
        return new ClientboundAddEntityPacket(
                getId(),
                getUUID(),
                getX(),
                getY(),
                getZ(),
                getXRot(),
                getYRot(),
                getType(),
                ownerId,
                getDeltaMovement(),
                0.0
        );
    }

    /// 普通召唤弹幕可携带的少量固有命中特效。
    ///
    /// <p>这里只保存弹幕飞行期间必须同步的数据；哪种召唤物选择哪种效果仍在召唤物实例
    /// 自身声明。幽灵飞灵使用专用声波实体逻辑，因此不会进入这个枚举。</p>
    @Override
    public Type confluence$getImmunityType() {return Type.LOCAL;}

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {return 1;}

    public enum HitEffect {
        NONE(0) {
            @Override
            void apply(LivingEntity target) {
            }
        },
        POISON(1) {
            @Override
            void apply(LivingEntity target) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
            }
        },
        IGNITE(2) {
            @Override
            void apply(LivingEntity target) {
                target.setSecondsOnFire(5);
            }
        };

        private final int id;

        HitEffect(int id) {
            this.id = id;
        }

        abstract void apply(LivingEntity target);

        private static HitEffect byId(int id) {
            return switch (id) {
                case 1 -> POISON;
                case 2 -> IGNITE;
                default -> NONE;
            };
        }
    }
}
