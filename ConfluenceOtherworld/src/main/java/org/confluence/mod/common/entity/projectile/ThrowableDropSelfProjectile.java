package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.mesdag.portlib.event.entity.PortProjectileImpactEvent;
import org.mesdag.portlib.wrapper.common.extensions.IPortProjectileExtension;

import javax.annotation.Nullable;

/**
 * 手里剑、飞刀和标枪等可回收投掷物的共享实体实现。
 *
 * <p>初始伤害、弹速、伤害通道和暴击由 MagicLib 发射事务冻结；本类只维护每次成功命中后
 * 递减的当前伤害、最多四次命中的穿透阶段、重力延迟和掉落物。共享战斗状态统一负责成功命中
 * UUID 去重，因而这些规则在区块卸载和服务器重启后仍保持一致。</p>
 */
public class ThrowableDropSelfProjectile extends DamageSettableProjectile implements IPortProjectileExtension {
    protected static final EntityDataAccessor<Integer> DATA_FLY_TICKS = SynchedEntityData.defineId(ThrowableDropSelfProjectile.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(ThrowableDropSelfProjectile.class, EntityDataSerializers.ITEM_STACK);
    protected int penetrate;
    protected float deltaDamage;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_FLY_TICKS, 5);
        entityData.define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    public ThrowableDropSelfProjectile(EntityType<? extends ThrowableDropSelfProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void setOwner(@Nullable Entity player) {
        super.setOwner(player);
        if (player != null) {
            setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        }
    }

    public void setItem(ItemStack drop) {
        entityData.set(DATA_ITEM_STACK, drop.copyWithCount(1));
    }

    public ItemStack getItem() {
        return entityData.get(DATA_ITEM_STACK).copy();
    }

    public void setFlyTicks(int ticks) {
        entityData.set(DATA_FLY_TICKS, ticks);
    }

    public int getFlyTicks() {
        return entityData.get(DATA_FLY_TICKS);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity.hurt(getDamageSource(), getCalculatedDamage())) {
            combatState().recordSuccessfulHit(ProjectileHitRules.impactedEntity(entity).getUUID());
            this.damage -= deltaDamage;
            LibEntityUtils.knockBackA2B(this, entity, 0.5, 0.2);
            if (penetrate >= 3) {
                if (random.nextBoolean()) {
                    LibEntityUtils.createItemEntity(getItem(), getX(), getY(), getZ(), level(), 0);
                }
                discard();
            } else {
                ++this.penetrate;
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (random.nextBoolean()) {
            LibEntityUtils.createItemEntity(getItem(), getX(), getY(), getZ(), level(), 0);
        }
        discard();
    }

    @Override
    public void setDamage(float damage) {
        this.damage = damage;
        this.deltaDamage = damage * 0.1F;
    }

    @Override
    public float getCalculatedDamage() {
        return damage;
    }

    /**
     * 每次安装冻结快照时同步建立不可变的百分之十递减步长。
     */
    @Override
    public void setProjectileCombatSnapshot(ProjectileCombatSnapshot snapshot) {
        super.setProjectileCombatSnapshot(snapshot);
        this.deltaDamage = snapshot.baseDamage() * 0.1F;
    }

    @Override
    public void applyGravity() {
        if (shouldApplyGravity()) {
            super.applyGravity();
        }
    }

    public boolean shouldApplyGravity() {
        return tickCount > getFlyTicks();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d0)) {
            d0 = 4.0;
        }

        d0 *= 64.0;
        return distance < d0 * d0;
    }

//    @Override
//    public boolean canUsePortal(boolean allowPassengers) {
//        return true;
//    }

    @Override
    public void tick() {
        super.tick();
        if (shouldAbortSubclassTick()) {
            return;
        }
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !PortProjectileImpactEvent.onProjectileImpact(this, hitresult)) {
            hitTargetOrDeflectSelf(hitresult);
        }

        checkInsideBlocks();
        Vec3 vec3 = getDeltaMovement();
        double d0 = getX() + vec3.x;
        double d1 = getY() + vec3.y;
        double d2 = getZ() + vec3.z;
        updateRotation();
        float f;
        if (isInWater()) {
            for (int i = 0; i < 4; i++) {
                level().addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25, d1 - vec3.y * 0.25, d2 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
            }

            f = 0.8F;
        } else {
            f = 0.99F;
        }

        setDeltaMovement(vec3.scale(f));
        applyGravity();
        setPos(d0, d1, d2);
    }

    @Override
    public double getDefaultGravity() {
        return 0.08;
    }

    /**
     * 普通投掷物已经成功命中三次后，第四次命中会立即结束实体。
     */
    protected int maximumPenetrationPhase() {
        return 3;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ProjectileCombatSnapshot snapshot = getProjectileCombatSnapshot();
        if (snapshot == null) {
            // 非武器入口误建的实体只写原版字段；再次读取时会因缺失当前战斗格式安全失效。
            return;
        }
        ThrowableProjectileRuntime.write(
                compound,
                !getItem().isEmpty(),
                getFlyTicks(),
                penetrate,
                damage,
                snapshot.baseDamage(),
                maximumPenetrationPhase());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setItem(ItemStack.EMPTY);
        setFlyTicks(0);
        penetrate = 0;
        damage = 0.0F;
        deltaDamage = 0.0F;
        if (combatState().isInvalid()) {
            return;
        }
        ProjectileCombatSnapshot snapshot = getProjectileCombatSnapshot();
        if (snapshot == null) {
            combatState().invalidate("Throwable projectile is missing its combat snapshot");
            return;
        }
        try {
            ThrowableProjectileRuntime.State state =
                    ThrowableProjectileRuntime.read(
                            compound, snapshot.baseDamage(), maximumPenetrationPhase());
            setItem(state.dropSelf()
                    ? snapshot.weapon().getItem().getDefaultInstance()
                    : ItemStack.EMPTY);
            setFlyTicks(state.flyTicks());
            penetrate = state.penetrationPhase();
            damage = state.currentDamage();
            deltaDamage = snapshot.baseDamage() * 0.1F;
        } catch (RuntimeException exception) {
            combatState().invalidate(englishReason(exception));
        }
    }

    private static String englishReason(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()
                || !message.chars().allMatch(character -> character < 128)) {
            return "Malformed throwable runtime state";
        }
        return message;
    }
}
