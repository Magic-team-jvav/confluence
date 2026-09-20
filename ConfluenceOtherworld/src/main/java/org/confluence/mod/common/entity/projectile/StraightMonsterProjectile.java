package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
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
import org.confluence.mod.common.data.map.CreatureDefinition.ProjectileOverrides;
import org.mesdag.portlib.event.entity.PortProjectileImpactEvent;
import org.mesdag.portlib.wrapper.common.extensions.IPortProjectileExtension;

/// 敌对生物直线弹幕的公共运行时。
///
/// 本类统一处理飞行、实体与方块碰撞、伤害来源、阵营过滤和寿命。
/// 具体弹幕只需要提供初始参数，并可通过 {@link #modifyVelocity(Vec3)}
/// 实现加速、减速等运动差异，避免每种远程生物重复一整套碰撞代码。
public abstract class StraightMonsterProjectile extends Projectile implements IPortProjectileExtension {
    private static final String DAMAGE_KEY = "Damage";
    private static final String MAXIMUM_LIFETIME_KEY = "MaximumLifetime";
    private static final String AGE_KEY = "ProjectileAge";
    private float damage;
    private float knockback;
    private int maximumLifetime = 100;

    protected StraightMonsterProjectile(EntityType<? extends StraightMonsterProjectile> type, Level level) {
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
            int maximumLifetime
    ) {
        Vec3 origin = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        configureAimed(
                owner,
                origin,
                target.getEyePosition().subtract(origin),
                damage,
                velocity,
                inaccuracy,
                maximumLifetime
        );
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
            int maximumLifetime
    ) {
        setOwner(owner);
        ProjectileOverrides parameters = ProjectileOverrides.get(owner, getType());
        this.damage = parameters.damageOr(damage);
        this.knockback = parameters.knockbackOr(0);
        this.maximumLifetime = parameters.lifetimeOr(maximumLifetime);
        setPos(origin);
        shoot(aim.x, aim.y, aim.z, parameters.speedOr(velocity), parameters.inaccuracyOr(inaccuracy));
    }

    /// 按明确的出生点和速度配置一次射击。
    ///
    /// 该入口供抛射物、延迟突进物等不直接瞄准目标的弹幕使用。
    /// 调用方仍然必须显式提供伤害快照和寿命，弹幕不会在后续 tick
    /// 重新读取发射者属性。
    public final void configure(Mob owner, Vec3 origin, Vec3 velocity, float damage, int maximumLifetime) {
        setOwner(owner);
        ProjectileOverrides parameters = ProjectileOverrides.get(owner, getType());
        this.damage = parameters.damageOr(damage);
        this.knockback = parameters.knockbackOr(0);
        this.maximumLifetime = parameters.lifetimeOr(maximumLifetime);
        setPos(origin);
        double length = velocity.length();
        setDeltaMovement(parameters.speed() >= 0 && length > 1.0E-8 ? velocity.scale(parameters.speedOr((float) length) / length) : velocity);
    }

    public final float getDamage() {
        return damage;
    }

    public void configure(Mob owner, LivingEntity target, float damage) {
        configure(owner, target, damage, 0.3F, 0.8F, 100);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > maximumLifetime) {
            discard();
            return;
        }

        Vec3 velocity = modifyVelocity(getDeltaMovement());
        setDeltaMovement(velocity);
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        boolean impacted = hitResult.getType() != HitResult.Type.MISS && !PortProjectileImpactEvent.onProjectileImpact(this, hitResult);
        if (impacted) {
            hitTargetOrDeflectSelf(hitResult);
        }
        if (isRemoved()) {
            return;
        }
        if (impacted && hitResult instanceof BlockHitResult blockHitResult && ownsBlockImpactMovement(blockHitResult)) {
            checkInsideBlocks();
            updateRotation();
            return;
        }
        if (impacted && hitResult instanceof EntityHitResult entityHitResult && ownsEntityImpactMovement(entityHitResult)) {
            checkInsideBlocks();
            updateRotation();
            return;
        }

        checkInsideBlocks();
        setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);
        updateRotation();
    }

    /// 允许子类在每个 tick 修改速度；默认保持匀速。
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return velocity;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide && result.getEntity() instanceof LivingEntity target
                && getOwner() instanceof Mob owner
                && owner.canAttack(target)) {
            if (target.hurt(damageSources().mobProjectile(this, owner), damage)) {
                if (knockback > 0) {
                    Vec3 direction = getDeltaMovement();
                    target.knockback(knockback, -direction.x, -direction.z);
                }
                onSuccessfulHit(owner, target);
            }
        }
        finishEntityHit(result);
    }

    /// 完成实体命中后的生命周期处理；普通直线弹幕在首次命中后消失。
    protected void finishEntityHit(EntityHitResult result) {
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

    /// 方块命中回调是否已经决定了本 tick 的最终位置与速度。
    ///
    /// 反弹弹体需要返回 {@code true}，防止公共直线移动在回调结束后再次使用命中前速度，
    /// 穿墙弹体则保持默认值并继续完成原位移。
    protected boolean ownsBlockImpactMovement(BlockHitResult result) {
        return false;
    }

    /// 实体命中回调是否已经决定了本 tick 的最终位置与速度。
    protected boolean ownsEntityImpactMovement(EntityHitResult result) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat(DAMAGE_KEY, damage);
        tag.putFloat("Knockback", knockback);
        tag.putInt(MAXIMUM_LIFETIME_KEY, maximumLifetime);
        tag.putInt(AGE_KEY, tickCount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat(DAMAGE_KEY);
        knockback = tag.getFloat("Knockback");
        maximumLifetime = tag.getInt(MAXIMUM_LIFETIME_KEY);
        tickCount = tag.getInt(AGE_KEY);
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return target instanceof LivingEntity living
                && getOwner() instanceof Mob owner
                && owner.canAttack(living)
                && super.canHitEntity(target);
  }
}
