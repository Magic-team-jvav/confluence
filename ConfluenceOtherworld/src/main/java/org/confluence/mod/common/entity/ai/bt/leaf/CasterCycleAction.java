package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/// 执行“预施法、延迟释放、循环传送”的完整法师战斗周期。
///
/// 阶段计数沿用 1.21 法师的倒计时语义，避免把挥手时刻、弹幕释放时刻和
/// 传送时刻拆成多个互相产生一 tick 偏差的顺序节点。节点只负责公共时序；
/// 弹幕种类、伤害和命中特效仍由具体实体提供的工厂决定。
public final class CasterCycleAction extends BTNode {
    private static final UUID BATTLE_RANGE_UUID = UUID.fromString("538db362-46e6-46b2-aa64-d674065dfc41");
    private static final int CYCLE_TICKS = 200;
    private static final int RELEASE_DELAY_TICKS = 8;
    private static final int[] CAST_PHASES = {180, 130, 80};

    private final PathfinderMob caster;
    private final Function<LivingEntity, @Nullable Projectile> projectileFactory;
    private final TeleportNearTargetAction teleportAction;
    private int phase = CYCLE_TICKS;
    private int releaseDelay = -1;
    private int lastCastPhase = CYCLE_TICKS;

    public CasterCycleAction(PathfinderMob caster, Function<LivingEntity, @Nullable Projectile> projectileFactory) {
        this.caster = Objects.requireNonNull(caster, "caster");
        this.projectileFactory = Objects.requireNonNull(projectileFactory, "projectileFactory");
        this.teleportAction = new TeleportNearTargetAction(caster, 20, 5, 4);
    }

    @Override
    public void start() {
        // 重新进入行为节点不能清除 1.21 实体字段中保留的施法延迟。
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = caster.getTarget();
        if (target == null || !target.isAlive()) {
            removeBattleRange();
            phase = CYCLE_TICKS;
            return BTStatus.FAILURE;
        }

        addBattleRange();
        caster.lookAt(target, 10.0F, 70.0F);
        caster.getMoveControl().strafe(0.01F, 0.01F);

        if (isCastPhase(phase)) {
            lastCastPhase = phase;
            releaseDelay = RELEASE_DELAY_TICKS;
            caster.swing(InteractionHand.MAIN_HAND, true);
        }
        if (--releaseDelay == 0) {
            Projectile projectile = projectileFactory.apply(target);
            if (projectile == null || !caster.level().addFreshEntity(projectile)) {
                return BTStatus.FAILURE;
            }
        }

        if (--phase <= 0) {
            phase = CYCLE_TICKS;
            teleportAction.start();
            teleportAction.execute();
        }
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        removeBattleRange();
    }

    /// 受击成功后跳过当前预施法，和 1.21 的法师打断语义保持一致。
    public void interruptAfterHurt() {
        phase = lastCastPhase - 1;
        releaseDelay = -1;
    }

    private static boolean isCastPhase(int value) {
        for (int castPhase : CAST_PHASES) {
            if (value == castPhase) {
                return true;
            }
        }
        return false;
    }

    private void addBattleRange() {
        AttributeInstance followRange = caster.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null && followRange.getModifier(BATTLE_RANGE_UUID) == null) {
            followRange.addTransientModifier(new AttributeModifier(BATTLE_RANGE_UUID, "Caster battle range", 1.0, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    private void removeBattleRange() {
        AttributeInstance followRange = caster.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null && followRange.getModifier(BATTLE_RANGE_UUID) != null) {
            followRange.removeModifier(BATTLE_RANGE_UUID);
        }
    }
}
