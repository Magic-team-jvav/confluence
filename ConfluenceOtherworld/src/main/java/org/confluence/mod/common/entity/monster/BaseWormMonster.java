package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.entity.ai.WormChainTrail;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WormMovementAction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// 分段生物共用轨迹跟随与伤害逻辑，各本体明确提供自己的部件实体类型。
public abstract class BaseWormMonster extends BaseMonster implements WormSegment {
    // 命中后保留三刻冷却；未命中时逐刻检测，扫掠只覆盖本刻实际经过的路径。
    private static final int COLLISION_INTERVAL = 3;

    private final EntityType<BaseWormPart> segmentType;
    protected final List<BaseWormPart> segments = new ArrayList<>();
    private final java.util.Map<Integer, BaseWormPart> clientSegments = new java.util.HashMap<>();
    private final WormChainTrail segmentTrail = new WormChainTrail();
    private int collisionCooldown;
    private @Nullable Vec3 contactSweepStart;
    private @Nullable BaseWormPart attackingPart;
    private @Nullable BaseWormPart hurtPart;

    public BaseWormMonster(EntityType<? extends BaseWormMonster> type, Level level, EntityType<BaseWormPart> segmentType) {
        super(type, level);
        this.segmentType = segmentType;
        this.noPhysics = true;
        setNoGravity(true);
    }

    @Override
    public boolean canBeCollidedWith() {return false;}

    @Override
    public boolean isPushable() {return false;}

    protected abstract int getSegmentCount();

    protected float segmentSpacing() {
        return 1.6F;
    }

    public EntityDimensions segmentDimensions(boolean tail) {
        return segmentType.getDimensions();
    }

    protected double segmentDamageMultiplier(boolean tail) {
        return 1.0;
    }

    protected double segmentArmorMultiplier(boolean tail) {
        return 1.0;
    }

    public boolean attackFromSegment(BaseWormPart part, Entity target) {
        BaseWormPart previous = attackingPart;
        attackingPart = part;
        try {
            return doHurtTarget(target);
        } finally {
            attackingPart = previous;
        }
    }

    public boolean hurtSegment(BaseWormPart part, DamageSource source, float amount) {
        BaseWormPart previous = hurtPart;
        hurtPart = part;
        try {
            return hurt(source, amount);
        } finally {
            hurtPart = previous;
        }
    }

    @Override
    public double getAttributeValue(Attribute attribute) {
        double value = super.getAttributeValue(attribute);
        // 仅本次体节结算使用对应属性，保留本体的伤害事件、无敌帧和难度倍率。
        if (attribute == Attributes.ATTACK_DAMAGE && attackingPart != null)
            return value * segmentDamageMultiplier(attackingPart.isTail());
        if (attribute == Attributes.ARMOR && hurtPart != null)
            return value * segmentArmorMultiplier(hurtPart.isTail());
        return value;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return WormSegment.isWormDamage(source) || source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) initSegments();
    }

    public void initSegments() {
        if (!isAlive()) return;
        if (hasCompleteSegmentChain()) return;
        discardSegments();
        Vec3 previousPosition = position();
        Vec3 backward = getLookAngle().scale(-segmentSpacing());
        if (backward.lengthSqr() < 1.0E-7) backward = new Vec3(0.0, 0.0, -segmentSpacing());
        for (int index = 1; index <= getSegmentCount(); index++) {
            BaseWormPart part = segmentType.create(level());
            if (part == null) {
                discardSegments();
                return;
            }
            part.bindTo(this, index, index == getSegmentCount());
            previousPosition = previousPosition.add(backward);
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
        segmentTrail.invalidate();
    }

    private boolean hasCompleteSegmentChain() {
        if (segments.size() != getSegmentCount()) return false;
        for (int index = 1; index <= segments.size(); index++) {
            BaseWormPart part = segments.get(index - 1);
            if (part.isRemoved() || part.getOwner() != this || part.getSegmentIndex() != index)
                return false;
        }
        return true;
    }

    private void discardSegments() {
        for (BaseWormPart part : segments) {
            if (!part.isRemoved()) part.discard();
        }
        segments.clear();
        segmentTrail.invalidate();
    }

    @Nullable
    public WormSegment getSegment(int index) {
        if (index < 0) return null;
        if (index == 0) return this;
        if (level().isClientSide) {
            BaseWormPart part = clientSegments.get(index);
            if (part != null && part.isRemoved()) {
                clientSegments.remove(index);
                return null;
            }
            return part;
        }
        int segIdx = index - 1;
        return segIdx < segments.size() ? segments.get(segIdx) : null;
    }

    public List<BaseWormPart> getSegments() {
        return List.copyOf(segments);
    }

    /// 客户端由已同步的体节建立索引，不使用仅由服务端生成逻辑维护的 segments。
    public void trackClientSegment(BaseWormPart part) {
        if (level().isClientSide && part.getSegmentIndex() > 0 && !part.isRemoved())
            clientSegments.put(part.getSegmentIndex(), part);
    }

    @Override
    protected float tickHeadTurn(float bodyYaw, float animationSpeed) {
        yBodyRot = getYRot();
        yHeadRot = getYRot();
        return animationSpeed;
    }

    @Override
    public void tick() {
        if (isDeadOrDying()) setDeltaMovement(Vec3.ZERO);
        if (!level().isClientSide && contactSweepStart == null) contactSweepStart = position();
        super.tick();
        if (!isAlive()) return;
        if (!level().isClientSide) {
            initSegments();
            Vec3 leaderPosition = position();
            List<WormChainTrail.Sample> samples = segmentTrail.sample(leaderPosition, segments, segmentSpacing());
            for (int index = 0; index < segments.size(); index++) {
                WormChainTrail.Sample sample = samples.get(index);
                BaseWormPart segment = segments.get(index);
                segment.moveToChainPosition(sample.position());
                segment.orientAlongChain(sample.tangent());
            }
            Vec3 tangent = segmentTrail.headTangent();
            if (tangent.lengthSqr() > 1.0E-7D) {
                WormSegment.orientAlong(this, tangent);
                setYBodyRot(getYRot());
                setYHeadRot(getYRot());
            }
            tickCollision();
        }
    }

    private void tickCollision() {
        Vec3 sweepStart = contactSweepStart;
        contactSweepStart = position();
        if (collisionCooldown > 0) {
            collisionCooldown--;
            return;
        }
        boolean attacked = false;
        for (var target : SweptContactAttack.findTargets(this, sweepStart, 0.0, SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE, this::canContactAttack)) {
            if (getTarget() == null && target instanceof LivingEntity living) setTarget(living);
            attacked |= doHurtTarget(target);
        }
        if (attacked) collisionCooldown = COLLISION_INTERVAL;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        // 死亡结算完成后立即清理链条，不等待本体死亡动画结束。
        if (!isAlive() && !level().isClientSide) discardSegments();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (WormSegment.isWormDamage(source)) return false;
        boolean accepted = super.hurt(source, amount);
        if (accepted && !level().isClientSide) {
            for (BaseWormPart segment : segments) segment.indicateHurt();
        }
        return accepted;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof WormSegment) && super.canAttack(target);
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
    public @Nullable WormSegment getNext() {
        return getSegment(1);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WormMovementAction(BaseWormMonster.this, movementProfile());
            }
        };
    }

    /// 子类只声明所属蠕虫族的高度和速度边界，三维转向由公共节点完成。
    protected WormMovementAction.Profile movementProfile() {
        return WormMovementAction.Profile.underground();
    }
}
