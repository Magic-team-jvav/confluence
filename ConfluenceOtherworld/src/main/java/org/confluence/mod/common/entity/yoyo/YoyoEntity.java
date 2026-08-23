package org.confluence.mod.common.entity.yoyo;

import PortLib.extensions.net.minecraft.world.item.enchantment.EnchantmentHelper.PortEnchantmentHelperExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import org.confluence.mod.common.item.yoyo.YoyoSession;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.event.entity.PortProjectileImpactEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/// 悠悠球共享实体。
///
/// <p>该类只负责生命周期、准星方向运动、方块反弹、接触伤害与收回。具体命中特效回调给
/// {@link YoyoItem}，因此公共运动实现不依赖任何具体悠悠球或衍生弹幕。</p>
public final class YoyoEntity extends Projectile implements GeoEntity {
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> WEAPON = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> RANGE = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.FLOAT);
    private static final int HIT_INTERVAL_TICKS = 5;
    private static final double RETURN_DISTANCE_SQR = 0.25;
    private static final double OWNER_LIMIT_SQR = 64.0 * 64.0;

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private int returnTicks;
    private int hitCooldownTicks = HIT_INTERVAL_TICKS;
    private float damage;
    private boolean trackingTarget;

    public YoyoEntity(EntityType<? extends YoyoEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    /// 创建实体并冻结发射瞬间的近战、暴击与穿甲上下文。
    public static @Nullable YoyoEntity spawn(ServerPlayer owner, ItemStack weapon) {
        if (!(weapon.getItem() instanceof YoyoItem item)) {
            return null;
        }
        YoyoEntity yoyo = ModEntities.YOYO.get().create(owner.level());
        if (yoyo == null) {
            return null;
        }
        yoyo.setOwner(owner);
        yoyo.entityData.set(WEAPON, weapon.copyWithCount(1));
        yoyo.entityData.set(RANGE, item.maximumRange());
        yoyo.setDamage(item.attackDamage());
        yoyo.setPos(owner.getX(), owner.getY(0.5F), owner.getZ());
        if (!owner.level().addFreshEntity(yoyo)) {
            yoyo.discard();
            return null;
        }
        return yoyo;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(OWNER_ID, -1);
        entityData.define(WEAPON, ItemStack.EMPTY);
        entityData.define(RETURNING, false);
        entityData.define(RANGE, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) return;
        Entity rawOwner = getOwner();
        YoyoItem item = getYoyoItem();
        if (level().isClientSide) {
            if (rawOwner instanceof LivingEntity owner && item != null)
                tickMovement(owner, null, item);
            return;
        }
        if (!(rawOwner instanceof ServerPlayer owner) || !owner.isAlive() || owner.isSpectator() || item == null || damage <= 0.0F) {
            discard();
            return;
        }
        if (!YoyoSession.of(owner).owns(this, owner) || tickCount > item.lifetimeTicks())
            beginReturn();
        if (distanceToSqr(owner) > OWNER_LIMIT_SQR) {
            discard();
            return;
        }
        if (hitCooldownTicks > 0) --hitCooldownTicks;
        tickMovement(owner, owner, item);
    }

    private void tickMovement(LivingEntity owner, @Nullable ServerPlayer serverOwner, YoyoItem item) {
        xOld = getX();
        yOld = getY();
        zOld = getZ();
        setXRot(0.0F);
        setYRot(0.0F);
        float speedModifier = 1.0F;
        if (isReturning()) {
            noPhysics = true;
        }
        Vec3 destination = isReturning()
                ? owner.position().add(0.0, owner.getBbHeight() * 0.5F, 0.0)
                : resolveAim(owner);
        Vec3 difference = destination.subtract(position());
        if (isReturning() && difference.lengthSqr() <= RETURN_DISTANCE_SQR) {
            if (serverOwner == null) setPos(destination);
            else discard();
            return;
        }
        if (trackingTarget && !isReturning()) speedModifier = 4.0F;
        setDeltaMovement(difference.scale(0.2F * speedModifier));
        if (isReturning()) {
            ++returnTicks;
            addDeltaMovement(difference.normalize().scale(returnTicks / 40.0F));
        }
        if (serverOwner != null)
            damageTouchingTargets(serverOwner, item, serverOwner.getMainHandItem());

        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS && !PortProjectileImpactEvent.onProjectileImpact(this, hit)) {
            hitTargetOrDeflectSelf(hit);
        }
        if (isRemoved()) return;

        checkInsideBlocks();
        Vec3 motion = getDeltaMovement();
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        float friction = 0.95F;
        if (isInWater()) {
            for (int i = 0; i < 4; ++i) {
                level().addParticle(ParticleTypes.BUBBLE, getX() - motion.x * 0.25, getY() - motion.y * 0.25, getZ() - motion.z * 0.25, motion.x, motion.y, motion.z);
            }
            friction = 0.8F;
        }
        setDeltaMovement(motion.add(motion.normalize().scale(0.1)).scale(friction));
        setPos(position().add(getDeltaMovement()));
    }

    /// 只吸附准星射线实际穿过的实体，不搜索视野外或附近目标。
    private Vec3 resolveAim(LivingEntity owner) {
        float range = entityData.get(RANGE);
        Vec3 from = owner.getEyePosition();
        Vec3 view = owner.getViewVector(1.0F).normalize();
        Vec3 limit = from.add(view.scale(range));
        BlockHitResult blockHit = level().clip(new ClipContext(from, limit, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));
        Vec3 to = blockHit.getType() == HitResult.Type.MISS ? limit : blockHit.getLocation();
        AABB search = owner.getBoundingBox().inflate(range);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(level(), owner, from, to, search, entity -> true, 0.1F);
        noPhysics = false;
        if (hit == null || !ProjectileHitRules.canHit(owner, hit.getEntity())) {
            trackingTarget = false;
            return to;
        }
        trackingTarget = true;
        Entity target = hit.getEntity();
        return target.position().add(0.0, target.getBbHeight() * 0.5F, 0.0);
    }

    private void damageTouchingTargets(ServerPlayer owner, YoyoItem item, ItemStack liveWeapon) {
        if (hitCooldownTicks > 0) return;
        List<Entity> nearby = level().getEntities(this, getBoundingBox().inflate(0.75), entity -> entity != this);
        if (nearby.isEmpty()) {
            hitCooldownTicks = HIT_INTERVAL_TICKS;
            return;
        }
        boolean attempted = false;
        DamageSource source = damageSources().mobAttack(owner);
        for (Entity candidate : nearby) {
            if (!ProjectileHitRules.canHit(owner, candidate)) continue;
            attempted = true;
            if (!candidate.hurt(source, getDamage())) continue;
            float knockback = (float) owner.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + 0.1F;
            if (knockback > 0.0F && candidate instanceof LivingEntity living) {
                living.knockback(knockback * 0.5F, Mth.sin(getYRot() * Mth.DEG_TO_RAD), -Mth.cos(getYRot() * Mth.DEG_TO_RAD));
                setDeltaMovement(getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }
            if (level() instanceof ServerLevel serverLevel) {
                PortEnchantmentHelperExtension.doPostAttackEffects(serverLevel, candidate, source);
            }
            owner.setLastHurtMob(candidate);
            Entity impacted = ProjectileHitRules.impactedEntity(candidate);
            if (!(impacted instanceof LivingEntity target)) continue;
            if (liveWeapon.getItem() == item)
                liveWeapon.hurtAndBreak(1, owner, EquipmentSlot.MAINHAND);
            item.applyHitEffect(this, owner, target);
        }
        if (attempted) hitCooldownTicks = HIT_INTERVAL_TICKS;
    }

    public void beginReturn() {
        if (!isReturning()) {
            entityData.set(RETURNING, true);
            returnTicks = 0;
            noPhysics = true;
        }
    }

    public void resumeExtension() {
        entityData.set(RETURNING, false);
        returnTicks = 0;
        noPhysics = false;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!isReturning()) {
            playSound(SoundEvents.WOOD_PLACE, 0.5F, 1.5F);
            Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());
            setDeltaMovement(getDeltaMovement().add(normal.multiply(getDeltaMovement().multiply(normal)).multiply(-1.0, -1.0, -1.0)));
        }
        super.onHitBlock(result);
        if (!level().isClientSide) return;
        BlockPos pos = result.getBlockPos();
        BlockState state = level().getBlockState(pos);
        Vec3 direction = getDeltaMovement().normalize().scale(2.0);
        Vec3 particlePos = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(result.getDirection().getNormal()));
        BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(pos);
        level().addParticle(particle, particlePos.x, particlePos.y, particlePos.z, -direction.x, -direction.y, -direction.z);
        level().addParticle(particle, particlePos.x, particlePos.y, particlePos.z, -direction.x, -direction.y, -direction.z);
    }

    public void adjustRange(int amount) {
        YoyoItem item = getYoyoItem();
        if (item == null || amount == 0) {
            return;
        }
        entityData.set(RANGE, Mth.clamp(entityData.get(RANGE) + amount, 1.0F, item.maximumRange()));
    }

    public boolean isReturning() {
        return entityData.get(RETURNING);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    public ItemStack getWeapon() {
        return entityData.get(WEAPON);
    }

    public @Nullable YoyoItem getYoyoItem() {
        return getWeapon().getItem() instanceof YoyoItem item
                ? item
                : null;
    }

    /// 服务端仍使用原版弹幕拥有者；客户端通过同步的实体 ID 解析玩家。
    ///
    /// <p>原版 {@link net.minecraft.world.entity.projectile.Projectile} 只保存拥有者 UUID，
    /// 自定义 Forge 生成包不会自动传递其客户端缓存。悠悠球渲染绳线又必须取得玩家，
    /// 因此仅为该实体同步网络实体 ID，避免修改全部弹幕的生成协议。</p>
    @Override
    public @Nullable Entity getOwner() {
        if (!level().isClientSide) {
            return super.getOwner();
        }
        int ownerId = entityData.get(OWNER_ID);
        return ownerId < 0 ? null : level().getEntity(ownerId);
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        if (!level().isClientSide) {
            entityData.set(OWNER_ID, owner == null ? -1 : owner.getId());
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }
}
