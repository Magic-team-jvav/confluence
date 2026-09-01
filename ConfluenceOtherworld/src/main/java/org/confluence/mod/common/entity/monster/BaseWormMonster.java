package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.WormChainTrail;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WormMovementAction;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// 蠕虫怪物基类——分段实体（头+体+尾），穿透方块移动。
/// 每 tick 头部移动，体节跟随前一个保持固定间距。
public abstract class BaseWormMonster extends BaseMonster implements WormSegment {
    // 头部每三 tick 扫描一次接触目标，连续位移仍由扫掠碰撞补足中间路径。
    private static final int COLLISION_INTERVAL = 3;

    protected final List<BaseWormPart> segments = new ArrayList<>();
    private final WormChainTrail segmentTrail = new WormChainTrail();
    private int collisionCooldown;

    public BaseWormMonster(EntityType<? extends BaseWormMonster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        setNoGravity(true);
    }

    protected abstract int getSegmentCount();

    protected float segmentSpacing() {
        return 1.6F;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
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
        Vec3 backward = getLookAngle().scale(-segmentSpacing());
        if (backward.lengthSqr() < 1.0E-7) backward = new Vec3(0.0, 0.0, -segmentSpacing());
        for (int index = 1; index <= getSegmentCount(); index++) {
            BaseWormPart part = MonsterEntities.WORM_SEGMENT.get().create(level());
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
        int segIdx = index - 1;
        return segIdx < segments.size() ? segments.get(segIdx) : null;
    }

    public List<BaseWormPart> getSegments() {
        return List.copyOf(segments);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            initSegments();
            List<WormChainTrail.Sample> chainPositions = segmentTrail.sample(position(), segments, segmentSpacing());
            for (int index = 0; index < segments.size(); index++) {
                WormChainTrail.Sample sample = chainPositions.get(index);
                segments.get(index).moveToChainPosition(sample.position());
                segments.get(index).orientAlongChain(sample.tangent());
            }
            tickCollision();
        }
    }

    private void tickCollision() {
        if (collisionCooldown > 0) { collisionCooldown--; return; }
        AABB box = getBoundingBox();
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (target == this) continue;
            if (target.getType() == getType()) continue;
            if (!canAttack(target)) continue;
            if (getTarget() == null) setTarget(target);
            doHurtTarget(target);
        }
        collisionCooldown = COLLISION_INTERVAL;
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
    public void updateSegmentRotation() {}

    public static AttributeSupplier.Builder createWormAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
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
