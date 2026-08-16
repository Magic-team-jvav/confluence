package org.confluence.mod.common.entity.yoyo;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.lib.api.projectile.ProjectileAttributeResolver;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileDamageChannel;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/// 悠悠球共享实体。
///
/// <p>该类只负责生命周期、准星方向运动、方块反弹、接触伤害与收回。具体命中特效回调给
/// {@link YoyoItem}，因此公共运动实现不依赖任何具体悠悠球或衍生弹幕。</p>
public final class YoyoEntity extends DamageSettableProjectile
        implements GeoEntity {
    private static final EntityDataAccessor<Integer> OWNER_ID =
            SynchedEntityData.defineId(
                    YoyoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> WEAPON =
            SynchedEntityData.defineId(
                    YoyoEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> RETURNING =
            SynchedEntityData.defineId(
                    YoyoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> RANGE =
            SynchedEntityData.defineId(
                    YoyoEntity.class, EntityDataSerializers.FLOAT);
    private static final int HIT_INTERVAL_TICKS = 5;
    private static final double RETURN_DISTANCE_SQR = 0.25;
    private static final double OWNER_LIMIT_SQR = 64.0 * 64.0;

    private final Map<UUID, Integer> hitCooldowns = new HashMap<>();
    private final AnimatableInstanceCache animationCache =
            GeckoLibUtil.createInstanceCache(this);
    private int returnTicks;

    public YoyoEntity(EntityType<? extends YoyoEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    /// 创建实体并冻结发射瞬间的近战、暴击与穿甲上下文。
    public static @Nullable YoyoEntity spawn(
            ServerPlayer owner,
            ItemStack weapon
    ) {
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
        yoyo.setProjectileCombatSnapshot(
                ProjectileAttributeResolver.resolve(
                        owner,
                        weapon,
                        ProjectileDamageChannel.MELEE,
                        item.attackDamage(),
                        1.0F,
                        0.1F,
                        false));
        yoyo.setPos(owner.getX(), owner.getY(0.5F), owner.getZ());
        if (!owner.level().addFreshEntity(yoyo)) {
            yoyo.discard();
            return null;
        }
        return yoyo;
    }

    public static @Nullable YoyoEntity findOwned(ServerPlayer owner) {
        return owner.level().getEntitiesOfClass(
                        YoyoEntity.class,
                        owner.getBoundingBox().inflate(72.0),
                        yoyo -> yoyo.getOwner() == owner
                                && yoyo.isAlive())
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_ID, -1);
        entityData.define(WEAPON, ItemStack.EMPTY);
        entityData.define(RETURNING, false);
        entityData.define(RANGE, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldAbortSubclassTick() || level().isClientSide) {
            return;
        }
        if (!(getOwner() instanceof ServerPlayer owner)
                || !owner.isAlive()
                || owner.isSpectator()) {
            discard();
            return;
        }
        YoyoItem item = getYoyoItem();
        ProjectileCombatSnapshot snapshot = getProjectileCombatSnapshot();
        if (item == null || snapshot == null) {
            discard();
            return;
        }

        ItemStack liveWeapon = owner.getMainHandItem();
        if (liveWeapon.getItem() != item
                || tickCount >= item.lifetimeTicks()) {
            beginReturn();
        }
        if (distanceToSqr(owner) > OWNER_LIMIT_SQR) {
            discard();
            return;
        }

        reduceHitCooldowns();
        Vec3 destination = isReturning()
                ? owner.position().add(0.0, owner.getBbHeight() * 0.5F, 0.0)
                : resolveAim(owner);
        Vec3 difference = destination.subtract(position());
        if (isReturning()
                && difference.lengthSqr() <= RETURN_DISTANCE_SQR) {
            discard();
            return;
        }

        double response = isReturning()
                ? Math.min(1.0, 0.22 + returnTicks++ * 0.025)
                : 0.20;
        Vec3 motion = difference.scale(response);
        if (!isReturning()) {
            motion = bounceAgainstBlock(motion);
        }
        setDeltaMovement(motion);
        move(MoverType.SELF, motion);
        if (!isReturning()) {
            damageTouchingTargets(owner, item, liveWeapon, snapshot);
        }
    }

    /// 只吸附准星射线实际穿过的实体，不搜索视野外或附近目标。
    private Vec3 resolveAim(ServerPlayer owner) {
        float range = entityData.get(RANGE);
        Vec3 from = owner.getEyePosition();
        Vec3 view = owner.getViewVector(1.0F).normalize();
        Vec3 to = from.add(view.scale(range));
        AABB search = owner.getBoundingBox()
                .expandTowards(view.scale(range))
                .inflate(1.0);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                owner,
                from,
                to,
                search,
                entity -> ProjectileHitRules.canHit(owner, entity),
                range * range);
        if (hit == null) {
            return to;
        }
        return ProjectileHitRules.impactedEntity(hit.getEntity())
                .getBoundingBox()
                .getCenter();
    }

    private Vec3 bounceAgainstBlock(Vec3 motion) {
        HitResult hit = level().clip(new ClipContext(
                position(),
                position().add(motion),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this));
        if (!(hit instanceof BlockHitResult blockHit)) {
            return motion;
        }
        Vec3 normal = Vec3.atLowerCornerOf(
                blockHit.getDirection().getNormal());
        double dot = motion.dot(normal);
        if (dot >= 0.0) {
            return motion.scale(0.8);
        }
        setPos(blockHit.getLocation().add(normal.scale(0.05)));
        return motion.subtract(normal.scale(2.0 * dot)).scale(0.8);
    }

    private void damageTouchingTargets(
            ServerPlayer owner,
            YoyoItem item,
            ItemStack liveWeapon,
            ProjectileCombatSnapshot snapshot
    ) {
        for (LivingEntity target : level().getEntitiesOfClass(
                LivingEntity.class,
                getBoundingBox().inflate(0.75),
                entity -> entity != owner
                        && entity.isAlive()
                        && ProjectileHitRules.canHit(owner, entity))) {
            if (hitCooldowns.containsKey(target.getUUID())) {
                continue;
            }
            if (!target.hurt(
                    ModDamageTypes.of(
                            level(),
                            ModDamageTypes.SWORD_PROJECTILE,
                            this,
                            owner),
                    snapshot.baseDamage())) {
                continue;
            }
            hitCooldowns.put(target.getUUID(), HIT_INTERVAL_TICKS);
            ProjectileHitRules.applyResolvedKnockback(
                    this, target, snapshot.knockback(), 0.0);
            if (liveWeapon.getItem() == item) {
                liveWeapon.hurtAndBreak(1, owner, EquipmentSlot.MAINHAND);
            }
            item.applyHitEffect(this, owner, target);
        }
    }

    private void reduceHitCooldowns() {
        Iterator<Map.Entry<UUID, Integer>> iterator =
                hitCooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                iterator.remove();
            } else {
                entry.setValue(remaining);
            }
        }
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

    public void adjustRange(int amount) {
        YoyoItem item = getYoyoItem();
        if (item == null || amount == 0) {
            return;
        }
        entityData.set(
                RANGE,
                Mth.clamp(
                        entityData.get(RANGE) + amount,
                        1.0F,
                        item.maximumRange()));
    }

    public boolean isReturning() {
        return entityData.get(RETURNING);
    }

    public float getCurrentRange() {
        return entityData.get(RANGE);
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
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {
    }
}
