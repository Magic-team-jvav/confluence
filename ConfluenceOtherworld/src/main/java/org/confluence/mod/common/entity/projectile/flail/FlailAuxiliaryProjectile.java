package org.confluence.mod.common.entity.projectile.flail;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/// 链锤附属弹幕共享的移动与碰撞骨架。
///
/// 统一所有者校验、射线碰撞、部位伤害、穿透去重、位移与寿命。
/// 具体弹幕类决定命中后的存续、重力、反弹和索敌。
public abstract class FlailAuxiliaryProjectile extends Projectile {
    protected float damage;
    @Nullable
    private BaseFlailEntity parentFlail;
    private int lifetime;
    private int maximumLifetime = 100;
    private final Set<UUID> hitTargets = new HashSet<>();

    protected FlailAuxiliaryProjectile(EntityType<? extends FlailAuxiliaryProjectile> type, Level level) {
        super(type, level);
    }

    /// 由链锤实体在服务端创建弹幕后写入本次攻击数据。
    public void initialize(BaseFlailEntity flail, Player owner, Vec3 velocity, float damage, int maximumLifetime) {
        this.parentFlail = flail;
        setOwner(owner);
        setPos(flail.position());
        setDeltaMovement(velocity);
        this.damage = damage;
        this.maximumLifetime = maximumLifetime;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        Entity owner = getOwner();
        if (!level().isClientSide() && (owner == null || owner.isRemoved())) {
            discard();
            return;
        }

        super.tick();

        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() == HitResult.Type.ENTITY) {
            if (!level().isClientSide) {
                EntityHitResult entityHit = (EntityHitResult) hit;
                hitTargets.add(LibEntityUtils.dedupeIdentity(entityHit.getEntity()).getUUID());
                onHitEntity(entityHit);
            }
        } else if (hit.getType() == HitResult.Type.BLOCK) {
            if (!onHitBlockAndContinue((BlockHitResult) hit)) {
                discard();
                return;
            }
        }

        if (isRemoved()) return;
        move(MoverType.SELF, getDeltaMovement());
        if (++lifetime >= maximumLifetime && !level().isClientSide()) {
            discard();
            return;
        }
        afterMove();
    }

    /// 在本 tick 完成碰撞、位移和寿命检查后更新下一 tick 的速度。
    ///
    /// 该顺序与 1.21 的附属链锤弹幕一致。尤其是初速度为零的追踪弹幕，
    /// 首次锁定只应改变下一 tick 的运动，不能先加速再被本 tick 的射线重复判碰撞。
    protected void afterMove() {
    }

    /// 返回弹幕已经存活的服务端 tick 数。
    protected final int getLifetime() {
        return lifetime;
    }

    /// 返回本次弹幕的绝对寿命上限，供具体弹幕和行为测试查询。
    public final int getMaximumLifetime() {
        return maximumLifetime;
    }

    /// 调整本次弹幕的绝对寿命上限。
    protected final void setMaximumLifetime(int maximumLifetime) {
        this.maximumLifetime = maximumLifetime;
    }

    /// 返回 true 表示弹幕已经处理方块碰撞并继续存活。
    protected boolean onHitBlockAndContinue(BlockHitResult hit) {
        return false;
    }

    /// 伤害交给实际受击部位，击退作用于其所属主体。
    protected final void hurtTarget(Entity rawTarget, double knockback, double motionY) {
        if (level().isClientSide || !(getOwner() instanceof Player player)) return;
        if (rawTarget.hurt(LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, player), damage)) {
            LibEntityUtils.knockBackA2B(this, LibEntityUtils.encounterOwner(rawTarget), knockback, motionY);
        }
    }

    @Nullable
    protected final BaseFlailEntity getParentFlail() {
        return parentFlail;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return target != parentFlail && LibEntityUtils.logicalLivingTarget(target) != null
                && !hitTargets.contains(LibEntityUtils.dedupeIdentity(target).getUUID())
                && LibEntityUtils.canHit(getOwner(), target);
    }
}
