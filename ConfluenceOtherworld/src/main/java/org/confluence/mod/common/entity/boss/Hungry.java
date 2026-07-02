package org.confluence.mod.common.entity.boss;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;

/**
 * 肉墙的饿鬼——拴在肉墙上空的吸血虫，周期性冲向玩家。
 */
public class Hungry extends BaseFlyingMonster {
    private BaseBoss master;
    private Vec3 leashPos;

    public Hungry(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    public void setMaster(BaseBoss master, Vec3 relativePos) {
        this.master = master;
        this.leashPos = relativePos;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(Hungry.this),
                                new ChargeAttackAction(Hungry.this, 0.6)),
                        SequenceNode.of(new WaitAction(10),
                                new FlyWanderAction(Hungry.this, 0.2, 3))
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

        // Leash to master's position
        if (master != null && master.isAlive()) {
            if (getTarget() == null) {
                Vec3 target = master.position().add(leashPos);
                Vec3 toTarget = target.subtract(position());
                if (toTarget.lengthSqr() > 4) {
                    setDeltaMovement(getDeltaMovement().add(toTarget.normalize().scale(0.1)));
                }
            }
        } else if (!level().isClientSide) {
            if (getTarget() == null && tickCount % 20 == 0) {
                Player nearest = level().getNearestPlayer(this, 16);
                if (nearest != null) setTarget(nearest);
            }
        }
    }
}
