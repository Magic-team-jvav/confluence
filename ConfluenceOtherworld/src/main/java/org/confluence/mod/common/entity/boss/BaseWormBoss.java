package org.confluence.mod.common.entity.boss;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.WormSegment;
import org.confluence.mod.common.init.entity.BossEntities;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/// 蠕虫型 Boss 基类。穿透方块移动，体节跟随。
public abstract class BaseWormBoss extends BaseBoss implements WormSegment {
    private static final int COLLISION_COOLDOWN = 8;

    protected final List<BossWormPart> segments = new ArrayList<>();
    private int collisionCooldown;

    public BaseWormBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /// 蠕虫型 Boss 的升降完全由三维转向状态机控制。
    ///
    /// <p>{@code noPhysics} 只允许实体穿过方块，不会关闭生物移动中的重力计算。如果这里仍
    /// 使用原版重力，世界吞噬怪和毁灭者会在每次状态机写入速度后额外下坠，空中阶段、入地
    /// 角度和整条体节链都会逐渐偏离 1.21 行为。</p>
    @Override
    public boolean isNoGravity() {
        return true;
    }

    protected abstract int getSegmentCount();

    protected float getInitialSegmentHealth(int index) {
        return 0.0F;
    }

    /// 返回相邻体节中心之间的距离。
    ///
    /// <p>普通蠕虫 Boss 使用紧凑间距；体型明显更大的子类可以覆盖该值。碰撞实体和
    /// 初始链必须读取同一个来源，不能让生成位置与后续跟随距离各自维护一份常量。</p>
    protected float getSegmentSpacing() {
        return 1.6F;
    }

    /// 计算指定体节的初始位置。
    ///
    /// <p>默认沿头部朝向的反方向逐节排列，避免所有体节在生成首刻重叠。需要盘曲出生
    /// 形态的 Boss 可以覆盖此方法，但仍应保证相邻体节之间接近
    /// {@link #getSegmentSpacing()}。</p>
    protected Vec3 getInitialSegmentPosition(int index, Vec3 previousPosition) {
        Vec3 forward = getLookAngle();
        if (forward.lengthSqr() <= 1.0E-7) {
            forward = new Vec3(0.0, 0.0, 1.0);
        }
        return previousPosition.subtract(forward.normalize().scale(getSegmentSpacing()));
    }

    /// 以有限角速度朝三维目标修正，并写入原版同步速度。
    ///
    /// <p>蠕虫 Boss 都穿过方块移动，不能复用地面导航。该方法只负责连续转向和速度，
    /// 阶段选择、目标点和速度常量仍由具体 Boss 自己决定。</p>
    protected final void steerInThreeDimensions(Vec3 destination, double speed, float maximumTurnDegrees) {
        Vec3 desired = destination.subtract(position());
        if (desired.lengthSqr() <= 1.0E-7) {
            return;
        }
        desired = desired.normalize();
        Vec3 current = getLookAngle();
        if (current.lengthSqr() <= 1.0E-7) {
            current = desired;
        } else {
            current = current.normalize();
        }

        double angle = angleBetween(current, desired);
        Vec3 direction;
        if (angle <= Math.toRadians(maximumTurnDegrees)) {
            direction = desired;
        } else {
            double blend = Math.toRadians(maximumTurnDegrees) / angle;
            direction = current.scale(1.0 - blend).add(desired.scale(blend)).normalize();
        }

        float yaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) (-Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * Mth.RAD_TO_DEG);
        setYRot(yaw);
        setXRot(Mth.clamp(pitch, -85.0F, 85.0F));
        yBodyRot = yaw;
        yHeadRot = yaw;
        setDeltaMovement(direction.scale(speed));
    }

    protected static double angleBetween(Vec3 first, Vec3 second) {
        if (first.lengthSqr() <= 1.0E-7 || second.lengthSqr() <= 1.0E-7) {
            return Math.PI;
        }
        return Math.acos(Mth.clamp(first.normalize().dot(second.normalize()), -1.0, 1.0));
    }

    protected boolean hurtSegment(BossWormPart segment, DamageSource source, float amount) {
        return hurt(source, amount);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) initSegments();
    }

    public void initSegments() {
        if (hasCompleteSegmentChain()) return;
        discardSegments();
        Vec3 previousPosition = position();
        for (int index = 1; index <= getSegmentCount(); index++) {
            BossWormPart part = BossEntities.WORM_SEGMENT.get().create(level());
            if (part == null) {
                discardSegments();
                return;
            }
            part.bindTo(this, index, index == getSegmentCount());
            previousPosition = getInitialSegmentPosition(index, previousPosition);
            part.setPos(previousPosition);
            part.setYRot(getYRot());
            part.setXRot(getXRot());
            if (!level().addFreshEntity(part)) {
                part.discard();
                discardSegments();
                return;
            }
            segments.add(part);
        }
    }

    private boolean hasCompleteSegmentChain() {
        if (segments.size() != getSegmentCount()) return false;
        for (int index = 1; index <= segments.size(); index++) {
            BossWormPart part = segments.get(index - 1);
            if (part.isRemoved() || part.getOwner() != this || part.getSegmentIndex() != index)
                return false;
        }
        return true;
    }

    protected final void discardSegments() {
        for (BossWormPart part : segments) {
            if (!part.isRemoved()) part.discard();
        }
        segments.clear();
    }

    @Nullable
    public WormSegment getSegment(int index) {
        if (index < 0) return null;
        if (index == 0) return this;
        int segIdx = index - 1;
        return segIdx < segments.size() ? segments.get(segIdx) : null;
    }

    public List<BossWormPart> getSegments() {
        return List.copyOf(segments);
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) return;
        if (!level().isClientSide) {
            initSegments();
            /// 头部完成本 tick 移动后立即按链表顺序刷新全部体节。不能只依赖各体节
            /// 自己的实体 tick 顺序，否则当世界先 tick 身体、后 tick 头部时，
            /// 帧末相邻间距会额外叠加一次头部位移并产生明显拉伸。
            for (BossWormPart segment : segments) {
                segment.updateSegmentPosition();
            }
            tickCollision();
        }
    }

    private void tickCollision() {
        if (collisionCooldown > 0) { collisionCooldown--; return; }
        AABB box = getBoundingBox().inflate(1.0);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (target == this) continue;
            /// 碰撞伤害同样必须遵守实体自己的攻击规则。部分蠕虫 Boss 会明确排除
            /// 同类或同一场战斗中的其他头部；若这里只检查队伍关系，分裂后的头部
            /// 会互相造成伤害，并在连续晋升后把一条完整链错误拆成许多短链。
            if (!canAttack(target)) continue;
            target.hurt(damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
            collisionCooldown = COLLISION_COOLDOWN;
            if (getTarget() == null) setTarget(target);
            break;
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == damageSources().inWall() || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!level().isClientSide) discardSegments();
    }

    @Override
    public int getSegmentIndex() {return 0;}

    @Override
    public @Nullable WormSegment getPrev() {return null;}

    @Override
    public @Nullable WormSegment getNext() {return getSegment(1);}

    @Override
    public void updateSegmentPosition() {}

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(BaseWormBoss.this),
                                new MoveToTargetAction(BaseWormBoss.this, 0.5, 2.0)),
                        new WaitAction(20));
            }
        };
    }

    public static AttributeSupplier.Builder createWormBossAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }
}
