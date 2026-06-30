package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.CircleAroundTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class Harpy extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");

    public Harpy(EntityType<? extends Harpy> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 10.0);
    }

    @Override
    protected BTRoot createBT() {
        BTNode combat = SelectorNode.of(
                SequenceNode.of(new CircleAroundTargetAction(this, 0.3, 4), new WaitAction(20)),
                SequenceNode.of(new ChargeAttackAction(this, 0.5)));
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(Harpy.this), combat),
                        new FlyWanderAction(Harpy.this, 0.15, 10));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(FLY)));
    }
}
