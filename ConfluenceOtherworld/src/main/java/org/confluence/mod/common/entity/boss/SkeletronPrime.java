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

public class SkeletronPrime extends BaseBoss {
    private static final int ARM_COUNT = 4;
    private final SkeletronPrimeArm[] arms = new SkeletronPrimeArm[ARM_COUNT];
    private boolean armsSpawned = false;
    private boolean phase2 = false;

    public SkeletronPrime(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        this.xpReward = 2500;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 1200.0)
                .add(Attributes.ATTACK_DAMAGE, 25.0)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new PhaseCondition(),
                                SelectorNode.of(
                                        SequenceNode.of(new HasTargetCondition(SkeletronPrime.this),
                                                new DashAction(SkeletronPrime.this, 1.3, 20)),
                                        SequenceNode.of(new HasTargetCondition(SkeletronPrime.this),
                                                new ChargeAttackAction(SkeletronPrime.this, 1.0)),
                                        SequenceNode.of(new HasTargetCondition(SkeletronPrime.this),
                                                new CircleAroundTargetAction(SkeletronPrime.this, 0.7, 3), new WaitAction(8))
                                )
                        ),
                        SelectorNode.of(
                                SequenceNode.of(new HasTargetCondition(SkeletronPrime.this),
                                        new CircleAroundTargetAction(SkeletronPrime.this, 0.25, 6), new WaitAction(20)),
                                new FlyWanderAction(SkeletronPrime.this, 0.15, 8)
                        )
                );
            }
        };
    }

    private class PhaseCondition extends BTNode {
        @Override public BTStatus execute() { return phase2 ? BTStatus.SUCCESS : BTStatus.FAILURE; }
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
            if (!armsSpawned) { spawnArms(); armsSpawned = true; }
            if (!phase2 && allArmsDead()) phase2 = true;
            if (getTarget() == null && tickCount % 20 == 0) {
                Player nearest = level().getNearestPlayer(this, 64);
                if (nearest != null) setTarget(nearest);
            }
        }
    }

    private void spawnArms() {
        if (!(level() instanceof ServerLevel sl)) return;
        for (int i = 0; i < ARM_COUNT; i++) {
            SkeletronPrimeArm arm = BossEntities.SKELETRON_PRIME_ARM.get().create(level());
            if (arm != null) {
                arm.setPos(position());
                arm.setMaster(this, i);
                sl.addFreshEntity(arm);
                addSubEntity(arm);
                arms[i] = arm;
            }
        }
    }

    private boolean allArmsDead() {
        for (SkeletronPrimeArm a : arms) if (a != null && a.isAlive()) return false;
        return true;
    }

    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override protected boolean shouldDiscardWhenNoTarget() { return true; }
}
