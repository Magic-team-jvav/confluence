package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.*;
import org.confluence.mod.common.init.entity.BossEntities;

public class Skeletron extends BaseBoss {
    private SkeletronHand leftHand;
    private SkeletronHand rightHand;
    private boolean handsSpawned = false;
    private boolean phase2 = false;

    public Skeletron(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        this.xpReward = 1500;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 800.0)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.WHITE;
    }

    // === BT ===

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        // Phase 2: no hands — aggressive spin/charge
                        SequenceNode.of(
                                new PhaseCondition(),
                                SelectorNode.of(
                                        SequenceNode.of(new HasTargetCondition(Skeletron.this),
                                                new DashAction(Skeletron.this, 1.2, 20)),
                                        SequenceNode.of(new HasTargetCondition(Skeletron.this),
                                                new ChargeAttackAction(Skeletron.this, 0.9)),
                                        SequenceNode.of(new HasTargetCondition(Skeletron.this),
                                                new CircleAroundTargetAction(Skeletron.this, 0.6, 3),
                                                new WaitAction(10))
                                )
                        ),
                        // Phase 1: hands protect, head floats above
                        SelectorNode.of(
                                SequenceNode.of(new HasTargetCondition(Skeletron.this),
                                        new CircleAroundTargetAction(Skeletron.this, 0.2, 6),
                                        new WaitAction(20)),
                                new FlyWanderAction(Skeletron.this, 0.15, 8)
                        )
                );
            }
        };
    }

    private class PhaseCondition extends BTNode {
        @Override
        public BTStatus execute() { return phase2 ? BTStatus.SUCCESS : BTStatus.FAILURE; }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (!handsSpawned) {
                spawnHands();
                handsSpawned = true;
            }

            // Check if hands are dead
            if (!phase2) {
                boolean leftDead = leftHand == null || !leftHand.isAlive();
                boolean rightDead = rightHand == null || !rightHand.isAlive();
                if (leftDead && rightDead) {
                    phase2 = true;
                }
            }

            if (getTarget() == null && tickCount % 20 == 0) {
                Player nearest = level().getNearestPlayer(this, 64);
                if (nearest != null) setTarget(nearest);
            }
        }
    }

    private void spawnHands() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        leftHand = BossEntities.SKELETRON_HAND.get().create(level());
        rightHand = BossEntities.SKELETRON_HAND.get().create(level());
        if (leftHand != null) {
            leftHand.setPos(position());
            leftHand.setMaster(this, 0);
            serverLevel.addFreshEntity(leftHand);
            addSubEntity(leftHand);
        }
        if (rightHand != null) {
            rightHand.setPos(position());
            rightHand.setMaster(this, 1);
            serverLevel.addFreshEntity(rightHand);
            addSubEntity(rightHand);
        }
    }

    @Override
    public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected boolean shouldDiscardWhenNoTarget() { return true; }
}
