package org.confluence.mod.common.entity.projectile.flail;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;

/**
 * 链锤附属弹幕共享的移动与碰撞骨架。
 *
 * <p>这里只处理所有花瓣、血肉弹和气泡都需要的所有者校验、射线碰撞、位移与寿命。
 * 伤害、重力、反弹和索敌仍由具体弹幕类实现，避免形成需要注册多份策略的第二套系统。</p>
 */
public abstract class FlailAuxiliaryProjectile extends Projectile {
    protected float damage;
    private int lifetime;
    private int maximumLifetime = 100;

    protected FlailAuxiliaryProjectile(
            EntityType<? extends FlailAuxiliaryProjectile> type,
            Level level
    ) {
        super(type, level);
    }

    /**
     * 由链锤实体在服务端创建弹幕后写入本次攻击数据。
     */
    public void initialize(
            BaseFlailEntity flail,
            Player owner,
            Vec3 velocity,
            float damage,
            int maximumLifetime
    ) {
        setOwner(owner);
        setPos(flail.position());
        setDeltaMovement(velocity);
        this.damage = damage;
        this.maximumLifetime = maximumLifetime;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        Entity owner = getOwner();
        if (!level().isClientSide()
                && (owner == null || owner.isRemoved())) {
            discard();
            return;
        }

        super.tick();

        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(
                this, this::canHitEntity);
        if (hit instanceof EntityHitResult entityHit
                && entityHit.getEntity() instanceof LivingEntity living) {
            onHitLiving(living);
        } else if (hit instanceof BlockHitResult blockHit
                && !onHitBlockAndContinue(blockHit)) {
            discard();
            return;
        }

        move(MoverType.SELF, getDeltaMovement());
        if (++lifetime >= maximumLifetime && !level().isClientSide()) {
            discard();
            return;
        }
        afterMove();
    }

    /**
     * 在本 tick 完成碰撞、位移和寿命检查后更新下一 tick 的速度。
     *
     * <p>该顺序与 1.21 的附属链锤弹幕一致。尤其是初速度为零的追踪弹幕，
     * 首次锁定只应改变下一 tick 的运动，不能先加速再被本 tick 的射线重复判碰撞。</p>
     */
    protected void afterMove() {
    }

    /**
     * 返回弹幕已经存活的服务端 tick 数。
     */
    protected final int getLifetime() {
        return lifetime;
    }

    /**
     * 返回本次弹幕的绝对寿命上限，供具体弹幕和行为测试查询。
     */
    public final int getMaximumLifetime() {
        return maximumLifetime;
    }

    /**
     * 调整本次弹幕的绝对寿命上限。
     */
    protected final void setMaximumLifetime(int maximumLifetime) {
        this.maximumLifetime = maximumLifetime;
    }

    /**
     * 返回 true 表示弹幕已经处理方块碰撞并继续存活。
     */
    protected boolean onHitBlockAndContinue(BlockHitResult hit) {
        return false;
    }

    protected abstract void onHitLiving(LivingEntity target);

    @Override
    protected boolean canHitEntity(Entity target) {
        return LibEntityUtils.canHitEntity(target, getOwner());
    }
}
