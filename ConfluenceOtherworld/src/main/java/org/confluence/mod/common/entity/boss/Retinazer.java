package org.confluence.mod.common.entity.boss;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.condition.HealthLowerThanCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.*;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;

/**
 * 激光眼——双子魔眼中发射激光的眼球。
 */
public class Retinazer extends BaseFlyingMonster {
    private TheTwins master;
    private boolean rageMode;

    public Retinazer(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    public void setMaster(TheTwins master) { this.master = master; }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                double s = rageMode ? 0.7 : 0.35;
                int w = rageMode ? 5 : 15;
                return SelectorNode.of(
                        SequenceNode.of(new HealthLowerThanCondition(Retinazer.this, 0.5f),
                                SelectorNode.of(
                                        SequenceNode.of(new HasTargetCondition(Retinazer.this),
                                                new ShootAction(Retinazer.this, 8.0f, 0.2f),
                                                new WaitAction(w)),
                                        SequenceNode.of(new HasTargetCondition(Retinazer.this),
                                                new CircleAroundTargetAction(Retinazer.this, s, 4), new WaitAction(10)),
                                        SequenceNode.of(new HasTargetCondition(Retinazer.this),
                                                new DashAction(Retinazer.this, 1.0, 15)),
                                        new FlyWanderAction(Retinazer.this, s * 0.4, 8)
                                )
                        ),
                        SelectorNode.of(
                                SequenceNode.of(new HasTargetCondition(Retinazer.this),
                                        new CircleAroundTargetAction(Retinazer.this, s, 5), new WaitAction(20)),
                                SequenceNode.of(new HasTargetCondition(Retinazer.this),
                                        new ShootAction(Retinazer.this, 6.0f, 0.4f), new WaitAction(30)),
                                new FlyWanderAction(Retinazer.this, s * 0.3, 8)
                        )
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

        // Rage mode when sibling dies
        if (master != null && !rageMode) {
            boolean siblingDead = !master.isSpazmatismAlive();
            if (siblingDead) {
                rageMode = true;
                getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.6);
                getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(20.0);
            }
        }

        if (!level().isClientSide && getTarget() == null && tickCount % 20 == 0) {
            Player nearest = level().getNearestPlayer(this, 64);
            if (nearest != null) setTarget(nearest);
        }
    }

    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }
}
