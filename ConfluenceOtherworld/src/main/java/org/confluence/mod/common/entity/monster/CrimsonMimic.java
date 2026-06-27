package org.confluence.mod.common.entity.monster;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.mod.util.entity.ai.goal.behavior.BTFactory;
import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;
import org.confluence.mod.util.entity.ai.goal.behavior.BTRoot;
import org.confluence.mod.util.entity.ai.goal.behavior.composite.ParallelNode;
import org.confluence.mod.util.entity.ai.goal.behavior.composite.SequenceNode;
import org.confluence.mod.util.entity.ai.goal.behavior.composite.WeightNode;
import org.confluence.mod.util.entity.ai.goal.behavior.condition.HealthLowerThanCondition;
import org.confluence.mod.util.entity.ai.goal.behavior.leaf.*;

import java.util.function.Function;

/// 肉后宝箱怪
public class CrimsonMimic extends WoodenMimic {
    public CrimsonMimic(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected BTRoot<WoodenMimic> createBehaviorTree() {
        return new CrimsonMimicBT(this);
    }

    private static class CrimsonMimicBT extends MimicBT {
        public CrimsonMimicBT(WoodenMimic mob) {
            super(mob);
        }

        @Override
        protected SequenceNode createActualAttackBehavior(Function<Integer, BTNode> waitActionFunction) {
            BTNode shortWait = waitActionFunction.apply(8);
            BTNode stage2Jump = BTFactory.sequence()
                    .addChild(BTFactory.repeater(2, BTFactory.sequence()
                            .addChild(shortWait)
                            .addChild(this.doJump(1f, 0f))
                    ))
                    .addChild(shortWait)
                    .addChild(this.doJump(1.5f, 0.2f));
            BTNode trackTarget = BTFactory.sequence()
                    .addChild(shortWait)
                    .addChild(new AnimCtrlAction<>(this.mob, "Controller", "jump", this.mob.jumpFlag, true))
                    .addChild(BTFactory.withTimer(50, new TrackTargetAction(this.mob, 5f)))
                    .addChild(new AnimCtrlAction<>(this.mob, "Controller", "jump", this.mob.jumpFlag, false))
                    .addChild(new AnimCtrlAction<>(this.mob, "Controller", "close", this.mob.closeFlag, true))
                    .addChild(shortWait);
            return super.createActualAttackBehavior(waitActionFunction)
                    .addChild(new WeightNode()
                            // 50%概率
                            .addChild(1, BTFactory.selector()
                                    // 50%血以下快速跳三次追踪三次
                                    .addWithCondition(new HealthLowerThanCondition(this.mob, 0.5f), BTFactory.sequence()
                                            .addChild(stage2Jump)
                                            .addChild(BTFactory.repeater(3, trackTarget))
                                    )
                                    // 快速跳三次追踪一次
                                    .addChild(BTFactory.sequence()
                                            .addChild(stage2Jump)
                                            .addChild(trackTarget)
                                    )
                            )
                            // 50%概率从头上砸下
                            .addChild(1, BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                                    .addChild(new LookAtTargetAction(this.mob))
                                    .addChild(BTFactory.sequence()
                                            .addChild(new SetAttributeAction(this.mob, PortAttributesExtension.gravity().value(), 0))
                                            .addChild(BTFactory.withTimer(50, new ParallelMoveAction(this.mob, 0, 3f, 5)))
                                            .addChild(new SetAttributeAction(this.mob, PortAttributesExtension.gravity().value(), 0.08))
                                    )
                            )
                    );
        }
    }
}
