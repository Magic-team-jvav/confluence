package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;

public class Derpling extends BaseMonster {

    public Derpling(EntityType<? extends BaseMonster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(Derpling.this),
                                new MoveToTargetAction(Derpling.this, 0.6, 2.0),
                                new JumpAttackAction(Derpling.this)),
                        new WaitAction(30 + random.nextInt(30))
                );
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getTarget() == null && tickCount % 20 == 0) {
            Player nearest = level().getNearestPlayer(this, 24);
            if (nearest != null) setTarget(nearest);
        }
    }

    private static class JumpAttackAction extends BTNode {
        private final Derpling mob;
        private int tick;

        JumpAttackAction(Derpling mob) { this.mob = mob; }

        @Override
        public void start() { tick = 0; }

        @Override
        public BTStatus execute() {
            tick++;
            if (tick <= 5) return BTStatus.RUNNING;
            if (tick == 6) {
                Vec3 dir = Vec3.ZERO;
                if (mob.getTarget() != null) {
                    Vec3 toTarget = mob.getTarget().position().subtract(mob.position());
                    double h = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
                    if (h > 0.01) dir = new Vec3(toTarget.x / h, 0, toTarget.z / h);
                }
                mob.setDeltaMovement(dir.x * 0.5, 0.6, dir.z * 0.5);
                mob.hasImpulse = true;
                return BTStatus.RUNNING;
            }
            if (mob.onGround() && tick > 7) return BTStatus.SUCCESS;
            if (tick > 30) return BTStatus.SUCCESS;
            return BTStatus.RUNNING;
        }
    }
}
