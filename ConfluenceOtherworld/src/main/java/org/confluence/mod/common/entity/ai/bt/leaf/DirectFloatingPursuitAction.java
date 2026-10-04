package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;

/// 让悬浮敌怪直接追击目标。
///
/// 追击阶段不经过路径导航。每个游戏刻先叠加轻微的上下浮动；存在有效目标且自身未处于
/// 受伤硬直时，立即把速度改为指向目标的固定向量。固定速度来自移动速度属性的八成，
/// 因此数据包或难度系统修改属性后无需同步调整行为参数。
///
/// 可选的离地高度限制用于鬼魂和幻灵：它们能横向穿墙，并在方块内部向上穿行，但在开放
/// 空间不会脱离地形无限升高。陨石头等自由飞行敌怪使用默认构造器，不受此限制。
public final class DirectFloatingPursuitAction extends BTNode {
    private static final double SPEED_MULTIPLIER = 0.8;
    private static final double BOB_FREQUENCY = 0.2;
    private static final double BOB_STRENGTH = 0.008;

    private final BaseMonster mob;
    private final double maxHoverHeight;
    private final int duration;
    private int ticks;

    public DirectFloatingPursuitAction(BaseMonster mob) {
        this(mob, Double.POSITIVE_INFINITY, -1);
    }

    public DirectFloatingPursuitAction(BaseMonster mob, double maxHoverHeight) {
        this(mob, maxHoverHeight, -1);
    }

    /// 创建可作为战斗周期前置移动阶段的限时悬浮追击。
    public DirectFloatingPursuitAction(BaseMonster mob, double maxHoverHeight, int duration) {
        if (Double.isNaN(maxHoverHeight) || maxHoverHeight <= 0.0) {
            throw new IllegalArgumentException("Hover height must be positive");
        }
        if (duration == 0 || duration < -1) {
            throw new IllegalArgumentException("Duration must be positive or -1");
        }
        this.mob = mob;
        this.maxHoverHeight = maxHoverHeight;
        this.duration = duration;
    }

    @Override
    public void start() {
        ticks = 0;
    }

    @Override
    public BTStatus execute() {
        mob.addDeltaMovement(new Vec3(0.0, Math.sin(mob.tickCount * BOB_FREQUENCY) * BOB_STRENGTH, 0.0));
        mob.hasImpulse = true;

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            if (duration > 0) return BTStatus.FAILURE;
            Vec3 movement = mob.getDeltaMovement();
            mob.setDeltaMovement(movement.x * 0.8, movement.y, movement.z * 0.8);
            return BTStatus.RUNNING;
        }
        if (mob.hurtTime > 0) {
            return BTStatus.RUNNING;
        }

        Vec3 offset = target.position().subtract(mob.position());
        if (offset.y > -0.25 && !mob.horizontalCollision && !isInsideCollision() && !hasSupportBelow()) {
            offset = new Vec3(offset.x, -0.25, offset.z);
        }
        if (offset.lengthSqr() < 1.0E-7) return BTStatus.RUNNING;
        double speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * SPEED_MULTIPLIER;
        mob.faceCombatDirection(offset, 10.0F, 10.0F);
        mob.setDeltaMovement(offset.normalize().scale(speed));
        mob.hasImpulse = true;

        return duration > 0 && ++ticks >= duration ? BTStatus.SUCCESS : BTStatus.RUNNING;
    }

    private boolean isInsideCollision() {
        return !mob.level().noCollision(mob, mob.getBoundingBox().deflate(0.05));
    }

    private boolean hasSupportBelow() {
        BlockPos.MutableBlockPos cursor = mob.blockPosition().mutable();
        int distance = (int) Math.ceil(maxHoverHeight);
        for (int step = 1; step <= distance; step++) {
            cursor.set(mob.blockPosition()).move(Direction.DOWN, step);
            BlockState state = mob.level().getBlockState(cursor);
            if (!state.getFluidState().isEmpty() || state.isFaceSturdy(mob.level(), cursor, Direction.UP))
                return true;
        }
        return false;
    }
}
