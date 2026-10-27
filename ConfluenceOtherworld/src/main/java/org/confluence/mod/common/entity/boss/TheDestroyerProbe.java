package org.confluence.mod.common.entity.boss;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.confluence.mod.common.entity.projectile.DestroyerLaserProjectile;
import org.jetbrains.annotations.Nullable;

/// 独立实现泰拉鸟妖式水平追踪、垂直悬停与碰壁反向；以更高速度执行探针激光攻击。
public final class TheDestroyerProbe extends BaseMonster {
    /// 探针最大水平/竖直速度，单位格/tick。
    private static final double HORIZONTAL_SPEED = 0.75, VERTICAL_SPEED = 0.45;
    /// 逐轴追踪加速度，单位格/tick²。
    private static final double ACCELERATION = 0.045;
    /// 探针优先在目标上方保持的高度，单位格。
    private static final double HOVER_HEIGHT = 6.0;
    /// 每轮三发激光的间隔、轮间冷却和最大射程，单位 tick/格。
    private static final int SHOT_INTERVAL = 12, VOLLEY_COOLDOWN = 60, VOLLEY_SIZE = 3;
    private static final double ATTACK_RANGE = 64.0;
    private static final float LASER_SPEED = 2.2F;
    private @Nullable TheDestroyer owner;
    private Vec3 flight = Vec3.ZERO;
    private int cooldown = VOLLEY_COOLDOWN, shots;

    public TheDestroyerProbe(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public void attach(TheDestroyer owner, Vec3 position) {
        this.owner = owner;
        setPos(position);
        setTarget(owner.getTarget());
    }

    @Override
    protected boolean usesSharedTargeting() {return false;}

    @Override
    protected boolean hasEntityContactAttack() {return true;}

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        tickFlight();
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    private void tickFlight() {
        if (owner == null || owner.isRemoved() || !owner.isAlive() || owner.level() != level()) {
            discard();
            return;
        }
        LivingEntity target = owner.getTarget();
        setTarget(target);
        if (target == null || !target.isAlive() || target.isRemoved() || target.level() != level() || !canAttack(target)) {
            flight = Vec3.ZERO;
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        Vec3 offset = target.position().add(0, HOVER_HEIGHT, 0).subtract(position());
        Vec3 horizontal = new Vec3(offset.x, 0, offset.z);
        Vec3 desired = horizontal.lengthSqr() < 1 ? Vec3.ZERO : horizontal.normalize().scale(HORIZONTAL_SPEED);
        double vertical = Mth.clamp(offset.y * 0.1, -VERTICAL_SPEED, VERTICAL_SPEED);
        if (horizontalCollision)
            flight = new Vec3(-flight.x * 0.5, Math.max(flight.y, 0.2), -flight.z * 0.5);
        if (verticalCollision) flight = new Vec3(flight.x, -flight.y * 0.5, flight.z);
        flight = new Vec3(approach(flight.x, desired.x), approach(flight.y, vertical), approach(flight.z, desired.z));
        setDeltaMovement(flight);
        faceCombatDirection(target.getEyePosition().subtract(getEyePosition()), 12, 12);
        if (--cooldown > 0 || distanceToSqr(target) > ATTACK_RANGE * ATTACK_RANGE || !getSensing().hasLineOfSight(target))
            return;
        DestroyerLaserProjectile.fire(this, getEyePosition(), target.getBoundingBox().getCenter(), LASER_SPEED);
        cooldown = ++shots >= VOLLEY_SIZE ? VOLLEY_COOLDOWN : SHOT_INTERVAL;
        if (shots >= VOLLEY_SIZE) shots = 0;
    }

    private static double approach(double value, double target) {
        return value + Mth.clamp(target - value, -ACCELERATION, ACCELERATION);
    }

    @Override
    public void travel(Vec3 input) {
        if (!level().isClientSide) move(MoverType.SELF, getDeltaMovement());
    }

    @Override
    public boolean removeWhenFarAway(double distance) {return false;}

    /// 探针属于本次战斗的临时子实体，由本体生命周期统一清理。
    @Override
    public boolean shouldBeSaved() {return false;}

    @Override
    public boolean canAttack(LivingEntity target) {
        return owner != null && owner.isAlive() && !owner.isRemoved() && owner.canAttack(target);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (owner != null) owner.removeSubEntity(this);
    }
}
