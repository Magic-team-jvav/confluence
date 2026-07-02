package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MeleeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;

/**
 * 战士 AI 怪物——追击+近战+漫游。适用于僵尸、骷髅、木乃伊等大部分陆行怪。
 */
public class BaseWarriorMonster extends BaseMonster {
    private final double moveSpeed;
    private final double followRange;

    public BaseWarriorMonster(EntityType<? extends BaseWarriorMonster> type, Level level, double moveSpeed, double followRange) {
        super(type, level);
        this.moveSpeed = moveSpeed;
        this.followRange = followRange;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected BTRoot createBT() {
        BaseWarriorMonster self = this;
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(self),
                                new MoveToTargetAction(self, moveSpeed, 2.0),
                                new MeleeAttackAction(self, 1.0)),
                        SequenceNode.of(new WaitAction(20 + self.random.nextInt(40)),
                                new RandomStrollAction(self, 0.5, 8)));
            }
        };
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
        if (!level().isClientSide && getTarget() == null && tickCount % 20 == 0) {
            Player nearest = level().getNearestPlayer(this, followRange);
            if (nearest != null) setTarget(nearest);
        }
    }
}
