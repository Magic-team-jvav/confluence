package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.mesdag.portlib.event.entity.PortProjectileImpactEvent;
import org.mesdag.portlib.wrapper.common.extensions.IPortProjectileExtension;

/// 敌对生物直线弹幕的公共运行时。
///
/// 本类统一处理飞行、实体与方块碰撞、伤害来源、阵营过滤和寿命。
/// 具体弹幕只需要提供初始参数，并可通过 {@link #modifyVelocity(Vec3)}
/// 实现加速、减速等运动差异，避免每种远程生物重复一整套碰撞代码。
public abstract class StraightMonsterProjectile extends Projectile
        implements IPortProjectileExtension {
    private float damage;
    private int maximumLifetime = 100;

    protected StraightMonsterProjectile(
            EntityType<? extends StraightMonsterProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    /// 在弹幕入世前保存本次攻击参数。
    ///
    /// 伤害取自发射瞬间的生物属性，之后即使发射者属性发生变化，
    /// 已存在的弹幕也不会被追溯修改。
    public final void configure(
            Mob owner,
            LivingEntity target,
            float damage,
            float velocity,
            float inaccuracy,
            int maximumLifetime) {
        Vec3 origin = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        configureAimed(
                owner,
                origin,
                target.getEyePosition().subtract(origin),
                damage,
                velocity,
                inaccuracy,
                maximumLifetime);
    }

    /// 使用调用方给出的出生点和瞄准向量配置带散布的直线弹幕。
    /// 该入口供少数不以目标眼睛为瞄准点的弹幕使用。
    protected final void configureAimed(
            Mob owner,
            Vec3 origin,
            Vec3 aim,
            float damage,
            float velocity,
            float inaccuracy,
            int maximumLifetime) {
        setOwner(owner);
        this.damage = damage;
        this.maximumLifetime = maximumLifetime;
        setPos(origin);
        shoot(aim.x, aim.y, aim.z, velocity, inaccuracy);
    }

    /// 按明确的出生点和速度配置一次射击。
    ///
    /// 该入口供抛射物、延迟突进物等不直接瞄准目标的弹幕使用。
    /// 调用方仍然必须显式提供伤害快照和寿命，弹幕不会在后续 tick
    /// 重新读取发射者属性。
    public final void configure(
            Mob owner, Vec3 origin, Vec3 velocity, float damage, int maximumLifetime) {
        setOwner(owner);
        this.damage = damage;
        this.maximumLifetime = maximumLifetime;
        setPos(origin);
        setDeltaMovement(velocity);
    }

    public final float getDamage() {
        return damage;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();
        if (tickCount > maximumLifetime) {
            discard();
            return;
        }

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS
                && !PortProjectileImpactEvent.onProjectileImpact(this, hitResult)) {
            hitTargetOrDeflectSelf(hitResult);
        }
        if (isRemoved()) {
            return;
        }

        checkInsideBlocks();
        Vec3 velocity = modifyVelocity(getDeltaMovement());
        setDeltaMovement(velocity);
        setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);
        updateRotation();
    }

    /// 允许子类在每个 tick 修改速度；默认保持匀速。
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return velocity;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() instanceof LivingEntity target
                && getOwner() instanceof Mob owner
                && owner.canAttack(target)) {
            if (target.hurt(damageSources().mobProjectile(this, owner), damage)) {
                onSuccessfulHit(owner, target);
            }
        }
        discard();
    }

    /// 在服务端确认伤害结算成功后附加弹幕专属效果。
    ///
    /// 中毒、着火等效果应放在这里，而不是在通用行为树中判断弹幕种类。
    /// 这样免疫、无敌帧或阵营过滤阻止伤害时，也不会错误施加状态。
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {}

    @Override
    protected void onHitBlock(BlockHitResult result) {
        discard();
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return target instanceof LivingEntity living
                && getOwner() instanceof Mob owner
                && owner.canAttack(living)
                && super.canHitEntity(target);
  }
}
