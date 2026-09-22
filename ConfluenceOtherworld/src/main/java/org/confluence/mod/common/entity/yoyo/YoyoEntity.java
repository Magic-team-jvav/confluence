package org.confluence.mod.common.entity.yoyo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.yoyo.YoyoEquipment;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import org.confluence.mod.common.item.yoyo.YoyoSession;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.event.entity.PortProjectileImpactEvent;
import org.mesdag.portlib.wrapper.common.extensions.IPortEnchantmentHelperExtension;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/// 悠悠球共享实体。
///
/// 负责生命周期、准星方向运动、方块反弹、接触伤害与收回；具体命中特效回调给 {@link YoyoItem}。
public final class YoyoEntity extends Projectile implements GeoEntity, Immunity {
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> WEAPON = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> RANGE = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ROLE = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PRIMARY_ID = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DETACHED = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COUNTERWEIGHT = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STRING_COLOR = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ORBIT_DIRECTION = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.INT);
    /// 对齐 1.21 的碰撞攻击节奏，实体每 5 tick 才发起一轮接触攻击。
    private static final int HIT_INTERVAL_TICKS = 5;
    private static final double RETURN_DISTANCE_SQR = 0.25;
    private static final double OWNER_LIMIT_SQR = 64.0 * 64.0;

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private int returnTicks;
    private int hitCooldownTicks = HIT_INTERVAL_TICKS;
    private float damage;
    private float criticalChance;
    private float knockback;
    private boolean trackingTarget;
    private float maximumRange;
    private int detachedTicks;
    private int remainingBounces;
    private double orbitAngle;

    public enum Role {MAIN, DUPLICATE, COUNTERWEIGHT, SECOND_COUNTERWEIGHT}

    public YoyoEntity(EntityType<? extends YoyoEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    /// 创建实体并冻结发射瞬间的近战伤害、暴击率与击退。
    public static @Nullable YoyoEntity spawn(ServerPlayer owner, ItemStack weapon, YoyoEquipment equipment) {
        YoyoEntity yoyo = create(owner, weapon, equipment);
        if (yoyo == null) return null;
        if (!owner.level().addFreshEntity(yoyo)) {
            yoyo.discard();
            return null;
        }
        return yoyo;
    }

    /// 主球与伴随球先完整设置同步数据，再加入世界，避免生成首帧显示成主球。
    private static @Nullable YoyoEntity create(ServerPlayer owner, ItemStack weapon, YoyoEquipment equipment) {
        if (!(weapon.getItem() instanceof YoyoItem item)) {
            return null;
        }
        YoyoEntity yoyo = ModEntities.YOYO.get().create(owner.level());
        if (yoyo == null) {
            return null;
        }
        yoyo.setOwner(owner);
        yoyo.entityData.set(WEAPON, weapon.copyWithCount(1));
        float meleeSpeed = (float) owner.getAttributeValue(Attributes.ATTACK_SPEED) / 4;
        yoyo.maximumRange = equipment.range(item.maximumRange() * meleeSpeed);
        yoyo.entityData.set(RANGE, yoyo.maximumRange);
        YoyoEquipment.Appearance appearance = YoyoEquipment.appearance(owner,
                equipment.counterweight() == 7 ? 0xFFE2E48E : 0xFFFFFFFF, equipment.counterweight());
        yoyo.entityData.set(STRING_COLOR, appearance.stringColor());
        yoyo.entityData.set(COUNTERWEIGHT, appearance.counterweight());
        yoyo.setDamage(item.attackDamage() * (float) owner.getAttributeValue(LibAttributes.getAttackDamage()));
        yoyo.criticalChance = (float) owner.getAttributeValue(LibAttributes.getCriticalChance()) + item.bonusCriticalChance();
        yoyo.knockback = item.knockback() / 6 + (float) owner.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        yoyo.setPos(owner.getX(), owner.getY(0.5F), owner.getZ());
        return yoyo;
    }

    public static @Nullable YoyoEntity spawnCompanion(ServerPlayer owner, YoyoEntity primary, Role role, YoyoEquipment equipment) {
        YoyoEntity part = create(owner, primary.getWeapon(), equipment);
        if (part == null) return null;
        part.entityData.set(ROLE, role.ordinal());
        part.entityData.set(PRIMARY_ID, primary.getId());
        part.damage = primary.damage;
        part.criticalChance = primary.criticalChance;
        part.knockback = primary.knockback;
        part.setPos(primary.position());
        if (part.isCounterweight()) {
            part.knockback = (primary.knockback + 1) / 2;
            int color = primary.entityData.get(COUNTERWEIGHT);
            part.entityData.set(COUNTERWEIGHT, color == 8 ? 1 + owner.getRandom().nextInt(6) : color);
            part.maximumRange = equipment.string() ? 10.4F : 7.8F;
            part.entityData.set(RANGE, part.maximumRange);
        }
        if (!owner.level().addFreshEntity(part)) {
            part.discard();
            return null;
        }
        return part;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(OWNER_ID, -1);
        entityData.define(WEAPON, ItemStack.EMPTY);
        entityData.define(RETURNING, false);
        entityData.define(RANGE, 1.0F);
        entityData.define(ROLE, 0);
        entityData.define(PRIMARY_ID, -1);
        entityData.define(DETACHED, false);
        entityData.define(COUNTERWEIGHT, 0);
        entityData.define(STRING_COLOR, 0xFFFFFFFF);
        entityData.define(ORBIT_DIRECTION, 1);
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) return;
        Entity rawOwner = getOwner();
        YoyoItem item = getYoyoItem();
        if (level().isClientSide) {
            if (rawOwner instanceof LivingEntity owner && item != null) {
                if (isDetached()) tickDetached(owner, null, item);
                else tickMovement(owner, null, item);
                if (!isCounterweight()) item.tickVisual(this);
            }
            return;
        }
        if (!(rawOwner instanceof ServerPlayer owner) || !owner.isAlive() || owner.isSpectator() || item == null || damage <= 0.0F) {
            discard();
            return;
        }
        if (!isDetached() && !YoyoSession.of(owner).owns(this, owner))
            beginReturn();
        if (distanceToSqr(owner) > OWNER_LIMIT_SQR) {
            discard();
            return;
        }
        if (hitCooldownTicks > 0) --hitCooldownTicks;
        if (!isCounterweight() && !isReturning()) item.tickAttack(this);
        if (isDetached()) tickDetached(owner, owner, item);
        else tickMovement(owner, owner, item);
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
                : getRole() == Role.MAIN ? resolveAim(owner) : companionDestination(owner);
        Vec3 difference = destination.subtract(position());
        if (isReturning() && difference.lengthSqr() <= RETURN_DISTANCE_SQR) {
            if (serverOwner == null) setPos(destination);
            else discard();
            return;
        }
        if (trackingTarget && !isReturning()) speedModifier = 4.0F;
        if (!isReturning() && getRole() == Role.DUPLICATE)
            speedModifier *= item.duplicateSpeedMultiplier();
        setDeltaMovement(difference.scale(0.2F * speedModifier));
        if (isReturning()) {
            ++returnTicks;
            addDeltaMovement(difference.normalize().scale(returnTicks / 40.0F));
        }
        /// 实体命中由扫掠判定统一处理，不能让沿途实体遮住后面的方块碰撞。
        if (!isReturning()) {
            BlockHitResult hit = level().clip(new ClipContext(position(), position().add(getDeltaMovement()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() != HitResult.Type.MISS && !PortProjectileImpactEvent.onProjectileImpact(this, hit)) {
                hitTargetOrDeflectSelf(hit);
            }
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
        if (serverOwner != null)
            damageTouchingTargets(serverOwner, item, serverOwner.getMainHandItem(), position(), position().add(getDeltaMovement()));
        if (isRemoved()) return;
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
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(level(), owner, from, to, search,
                entity -> EnemyDamageRules.isEnemy(entity) && ProjectileHitRules.canHit(owner, entity), 0.1F);
        noPhysics = false;
        if (hit == null || !ProjectileHitRules.canHit(owner, hit.getEntity())) {
            trackingTarget = false;
            return to;
        }
        trackingTarget = true;
        Entity target = hit.getEntity();
        return target.position().add(0.0, target.getBbHeight() * 0.5F, 0.0);
    }

    private void damageTouchingTargets(ServerPlayer owner, YoyoItem item, ItemStack liveWeapon, Vec3 from, Vec3 to) {
        if (hitCooldownTicks > 0) return;
        /// 先重置再结算，命中特效和副球生成不能重入本轮攻击；空检测也使用同样间隔。
        hitCooldownTicks = HIT_INTERVAL_TICKS;
        double radius = isCounterweight() ? 0.3 : item.hitRadius();
        List<Entity> nearby = level().getEntities(this, new AABB(from, to).inflate(radius + getBbWidth() * 0.5), entity -> entity != this);
        if (nearby.isEmpty()) return;
        boolean obstructed = !isDetached() && !isCounterweight() && level().clip(new ClipContext(owner.getEyePosition(), position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
        Set<UUID> hitIdentities = new HashSet<>();
        for (Entity candidate : nearby) {
            if (!ProjectileHitRules.canHit(owner, candidate)) continue;
            AABB targetBox = candidate.getBoundingBox().inflate(radius + getBbWidth() * 0.5);
            if (!targetBox.contains(from) && targetBox.clip(from, to).isEmpty()) continue;
            Entity identity = ProjectileHitRules.dedupeIdentity(candidate);
            if (!hitIdentities.add(identity.getUUID())) continue;
            Entity damageRecipient = ProjectileHitRules.damageRecipient(candidate);
            LivingEntity logicalTarget = ProjectileHitRules.logicalLivingTarget(candidate);
            if (logicalTarget == null) continue;
            DamageSource source = LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, owner);
            float amount = getDamage() * (isCounterweight() ? 1 : item.hitMultiplier(owner));
            if (obstructed) amount *= 0.75F;
            float hitDamage = amount;
            Immunity immunity = isCounterweight() ? YoyoSession.of(owner) : item.hitImmunity(owner);
            if (Immunity.isActive(immunity, logicalTarget)) continue;
            /// 使用正常受伤流程，不为悠悠球清零目标的原版受击计时。
            if (!Immunity.withCause(immunity, () -> damageRecipient.hurt(source, hitDamage)))
                continue;
            if (knockback > 0.0F) {
                logicalTarget.knockback(knockback * (isDetached() ? 0.75F : 1), owner.getX() - logicalTarget.getX(), owner.getZ() - logicalTarget.getZ());
                setDeltaMovement(getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }
            if (level() instanceof ServerLevel serverLevel) {
                IPortEnchantmentHelperExtension.doPostAttackEffects(serverLevel, logicalTarget, source);
            }
            owner.setLastHurtMob(logicalTarget);
            if (liveWeapon.getItem() == item)
                liveWeapon.hurtAndBreak(1, owner, EquipmentSlot.MAINHAND);
            if (!isCounterweight()) {
                item.applyHitEffect(this, owner, logicalTarget);
                if (!isDetached() && !isReturning()) YoyoSession.of(owner).consumeSpin(obstructed);
                YoyoSession.of(owner).onHit(owner, this);
            } else entityData.set(ORBIT_DIRECTION, -entityData.get(ORBIT_DIRECTION));
            if (isDetached()) {
                if (isCounterweight() || --remainingBounces == 0) {
                    discard();
                    break;
                }
                setDeltaMovement(getDeltaMovement().x, 0.45, getDeltaMovement().z);
            }
        }
    }

    public void beginReturn() {
        if (isDetached()) return;
        if (!isReturning()) {
            entityData.set(RETURNING, true);
            returnTicks = 0;
            noPhysics = true;
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (isCounterweight() && !level().isClientSide)
            entityData.set(ORBIT_DIRECTION, -entityData.get(ORBIT_DIRECTION));
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
        entityData.set(RANGE, Mth.clamp(entityData.get(RANGE) + amount, 1.0F, maximumRange));
    }

    private Vec3 companionDestination(LivingEntity owner) {
        Entity primary = level().getEntity(entityData.get(PRIMARY_ID));
        if (!(primary instanceof YoyoEntity main) || main.isRemoved()) {
            beginReturn();
            return owner.position().add(0, owner.getBbHeight() * 0.5, 0);
        }
        orbitAngle += (isCounterweight() ? 0.15 : 0.6) * entityData.get(ORBIT_DIRECTION);
        double angle = orbitAngle + (getRole() == Role.SECOND_COUNTERWEIGHT ? Math.PI : 0);
        Vec3 sideways = owner.getLookAngle().cross(new Vec3(0, 1, 0)).normalize();
        if (sideways.lengthSqr() < 0.01) sideways = new Vec3(1, 0, 0);
        Vec3 center = isCounterweight() ? owner.position().add(0, owner.getBbHeight() * 0.5, 0) : main.position();
        double radius = isCounterweight() ? Math.min(entityData.get(RANGE), main.distanceTo(owner)) : 0.8;
        Vec3 destination = isCounterweight()
                ? center.add(Math.cos(angle) * radius, 0.35 * Math.sin(angle * 2), Math.sin(angle) * radius)
                : center.add(sideways.scale(Math.cos(angle) * radius)).add(0, Math.sin(angle) * radius, 0);
        Vec3 offset = destination.subtract(owner.getEyePosition());
        float range = entityData.get(RANGE);
        return offset.lengthSqr() > range * range ? owner.getEyePosition().add(offset.normalize().scale(range)) : destination;
    }

    /// 脱手后使用独立弹道；不会继续跟准星，也不会占用下一组悠悠球。
    public void detach() {
        if (isDetached() || isReturning()) return;
        entityData.set(DETACHED, true);
        noPhysics = false;
        damage *= 0.75F;
        remainingBounces = 3 + random.nextInt(4);
        Vec3 motion = getDeltaMovement();
        if (motion.lengthSqr() < 0.04 && getOwner() != null)
            motion = getOwner().getLookAngle().scale(0.7);
        setDeltaMovement(motion);
        hasImpulse = true;
    }

    private void tickDetached(LivingEntity owner, @Nullable ServerPlayer serverOwner, YoyoItem item) {
        if (++detachedTicks > 200) {
            if (serverOwner != null) discard();
            return;
        }
        Vec3 from = position();
        Vec3 motion = getDeltaMovement().add(0, -0.04, 0);
        BlockHitResult hit = level().clip(new ClipContext(position(), position().add(motion), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hit.getType() != HitResult.Type.MISS) {
            if (hit.getDirection().getAxis() != Direction.Axis.Y) {
                if (serverOwner != null) discard();
                return;
            }
            setPos(hit.getLocation().add(0, hit.getDirection() == Direction.UP ? 0.01 : -0.01, 0));
            motion = new Vec3(motion.x * 0.9, -motion.y * 0.65, motion.z * 0.9);
        } else setPos(position().add(motion));
        setDeltaMovement(motion.scale(0.99));
        if (serverOwner != null)
            damageTouchingTargets(serverOwner, item, ItemStack.EMPTY, from, position());
    }

    public Role getRole() {return Role.values()[entityData.get(ROLE)];}

    public boolean isCounterweight() {return getRole() == Role.COUNTERWEIGHT || getRole() == Role.SECOND_COUNTERWEIGHT;}

    public boolean isDetached() {return entityData.get(DETACHED);}

    public int counterweightColor() {return entityData.get(COUNTERWEIGHT);}

    public int stringColor() {
        return YoyoEquipment.animatedColor(entityData.get(STRING_COLOR), level().getGameTime() % 120);
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

    public float getCriticalChance() {return criticalChance;}

    public float getKnockback() {return knockback * (isDetached() ? 0.75F : 1);}

    public ItemStack getWeapon() {
        return entityData.get(WEAPON);
    }

    public boolean belongsTo(Player player) {
        Entity owner = getOwner();
        return owner != null && owner.getUUID().equals(player.getUUID());
    }

    public boolean represents(ItemStack stack) {
        return !stack.isEmpty() && getWeapon().getItem() == stack.getItem();
    }

    public @Nullable YoyoItem getYoyoItem() {
        return getWeapon().getItem() instanceof YoyoItem item ? item : null;
    }

    /// 服务端仍使用原版弹幕拥有者；客户端通过同步的实体 ID 解析玩家。
    ///
    /// 原版 {@link net.minecraft.world.entity.projectile.Projectile} 只保存拥有者 UUID，
    /// 自定义 Forge 生成包不会自动传递其客户端缓存。悠悠球渲染绳线又必须取得玩家，
    /// 因此仅为该实体同步网络实体 ID，避免修改全部弹幕的生成协议。
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

    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return HIT_INTERVAL_TICKS;
    }
}
