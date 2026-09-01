package org.confluence.mod.common.entity.boss;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.WormChainTrail;
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
    protected final List<BossWormPart> segments = new ArrayList<>();
    private final WormChainTrail segmentTrail = new WormChainTrail();

    public BaseWormBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    /// 蠕虫型 Boss 的升降完全由三维转向状态机控制。
    ///
    /// {@code noPhysics} 只允许实体穿过方块，不会关闭生物移动中的重力计算。如果这里仍
    /// 使用原版重力，世界吞噬怪和毁灭者会在每次状态机写入速度后额外下坠，空中阶段、入地
    /// 角度和整条体节链都会逐渐偏离状态机给出的轨迹。
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
    /// 普通蠕虫 Boss 使用紧凑间距；体型明显更大的子类可以覆盖该值。碰撞实体和
    /// 初始链必须读取同一个来源，不能让生成位置与后续跟随距离各自维护一份常量。
    protected float getSegmentSpacing() {
        return 1.6F;
    }

    /// 数据包体型倍率同时作用于模型、碰撞箱和体节弧长，避免放大后体节互相吞叠。
    public final float getEffectiveSegmentSpacing() {
        return getSegmentSpacing() * getScale();
    }

    /// 计算指定体节的初始位置。
    ///
    /// 默认沿头部朝向的反方向逐节排列，避免所有体节在生成首刻重叠。需要盘曲出生
    /// 形态的 Boss 可以覆盖此方法，但仍应保证相邻体节之间接近
    /// {@link #getSegmentSpacing()}。
    protected Vec3 getInitialSegmentPosition(int index, Vec3 previousPosition) {
        Vec3 forward = getLookAngle();
        if (forward.lengthSqr() <= 1.0E-7) {
            forward = new Vec3(0.0, 0.0, 1.0);
        }
        return previousPosition.subtract(forward.normalize().scale(getEffectiveSegmentSpacing()));
    }

    /// 以有限角速度朝三维目标修正，并写入原版同步速度。
    ///
    /// 蠕虫 Boss 都穿过方块移动，不能复用地面导航。该方法只负责连续转向和速度，
    /// 阶段选择、目标点和速度常量仍由具体 Boss 自己决定。
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

        Vec3 direction = turnDirectionToward(current, desired, maximumTurnDegrees);

        // 角速度已经由上面的方向插值限制；这里必须把最终方向一次性写入
        // 身体、头部、视线和俯仰，不能再让原版 LookControl 与蠕虫移动各写一套朝向。
        // 不钳制到 ±85°，否则垂直钻出/下潜时模型必然仍接近水平。
        faceCombatDirection(direction, 180.0F, 180.0F);
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
        int expectedCount = Math.max(0, getSegmentCount());
        if (expectedCount == 0) {
            discardSegments();
            return;
        }

        if (segments.isEmpty()) {
            spawnCompleteSegmentChain(expectedCount);
            segmentTrail.invalidate();
            return;
        }

        // 数量变化代表链条发生了结构性变化，必须由当前头部按新的索引完整重建。
        // 单个临时体节失效则只替换该槽位，避免一处加载或同步故障让整条虫瞬间重生。
        if (segments.size() != expectedCount) {
            discardSegments();
            spawnCompleteSegmentChain(expectedCount);
            segmentTrail.invalidate();
            return;
        }

        Vec3 previousPosition = position();
        for (int index = 1; index <= expectedCount; index++) {
            BossWormPart current = segments.get(index - 1);
            if (isValidSegment(current, index)) {
                previousPosition = current.position();
                continue;
            }

            Vec3 replacementPosition = finitePosition(current.position())
                    ? current.position()
                    : getInitialSegmentPosition(index, previousPosition);
            float yaw = current.getYRot();
            float pitch = current.getXRot();
            if (!current.isRemoved()) current.discard();

            BossWormPart replacement = createSegment(index, expectedCount, replacementPosition, yaw, pitch);
            if (replacement == null) {
                // 保留其余健康体节，下一 tick 只重试这个槽位。
                continue;
            }
            segments.set(index - 1, replacement);
            previousPosition = replacement.position();
        }
    }

    private void spawnCompleteSegmentChain(int expectedCount) {
        Vec3 previousPosition = position();
        for (int index = 1; index <= expectedCount; index++) {
            previousPosition = getInitialSegmentPosition(index, previousPosition);
            BossWormPart part = createSegment(index, expectedCount, previousPosition, getYRot(), getXRot());
            if (part == null) {
                discardSegments();
                return;
            }
            segments.add(part);
        }
    }

    private @Nullable BossWormPart createSegment(int index, int expectedCount, Vec3 segmentPosition, float yaw, float pitch) {
        BossWormPart part = BossEntities.WORM_SEGMENT.get().create(level());
        if (part == null) return null;

        part.bindTo(this, index, index == expectedCount);
        part.setPos(segmentPosition);
        part.setYRot(yaw);
        part.setXRot(pitch);
        part.yRotO = yaw;
        part.xRotO = pitch;
        if (!level().addFreshEntity(part)) {
            part.discard();
            return null;
        }
        return part;
    }

    private boolean isValidSegment(BossWormPart part, int expectedIndex) {
        return !part.isRemoved()
                && part.getOwner() == this
                && part.getSegmentIndex() == expectedIndex;
    }

    private static boolean finitePosition(Vec3 position) {
        return Double.isFinite(position.x) && Double.isFinite(position.y) && Double.isFinite(position.z);
    }

    protected final void discardSegments() {
        for (BossWormPart part : segments) {
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

    public List<BossWormPart> getSegments() {
        return List.copyOf(segments);
    }

    @Override
    protected double combatAnchorDistanceSqr(net.minecraft.world.entity.player.Player player) {
        double nearest = distanceToSqr(player);
        for (BossWormPart segment : segments) {
            if (segment.isAlive()) nearest = Math.min(nearest, segment.distanceToSqr(player));
        }
        return nearest;
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) return;
        if (!level().isClientSide) {
            initSegments();
            /// 头部完成本 tick 移动后立即按链表顺序刷新全部体节。不能依赖各体节
            /// 自己的实体 tick 顺序，否则当世界先 tick 身体、后 tick 头部时，
            /// 帧末相邻间距会额外叠加一次头部位移并产生明显拉伸。
            updateSegmentsAlongTrail();
        }
    }

    /// 子类若在 {@code super.tick()} 后直接提交头部位移，可再次调用以消费该段新轨迹。
    protected final void updateSegmentsAlongTrail() {
        List<WormChainTrail.Sample> chainPositions = segmentTrail.sample(position(), segments, getEffectiveSegmentSpacing());
        for (int index = 0; index < segments.size(); index++) {
            WormChainTrail.Sample sample = chainPositions.get(index);
            segments.get(index).moveToChainPosition(sample.position());
            segments.get(index).orientAlongChain(sample.tangent());
        }
    }

    @Override
    protected void onCreatureDefinitionReload() {
        super.onCreatureDefinitionReload();
        for (BossWormPart segment : segments) {
            if (!segment.isRemoved()) segment.refreshDimensions();
        }
        segmentTrail.invalidate();
    }

    @Override
    protected double contactAttackInflation() {
        return 1.0D;
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
    public void updateSegmentRotation() {}

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
