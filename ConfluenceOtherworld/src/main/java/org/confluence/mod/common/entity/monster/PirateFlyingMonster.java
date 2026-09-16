package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.DirectFloatingPursuitAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyingPursuitAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class PirateFlyingMonster extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
    private final boolean ghost;

    public PirateFlyingMonster(EntityType<? extends PirateFlyingMonster> type, Level level, boolean ghost) {
        super(type, level);
        this.ghost = ghost;
        noPhysics = ghost;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return ghost ? new DirectFloatingPursuitAction(PirateFlyingMonster.this)
                        : SelectorNode.of(new FlyingPursuitAction(PirateFlyingMonster.this, 1.2), new FlyWanderAction(PirateFlyingMonster.this, 0.3, 8));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        if (!ghost)
            controllers.add(new AnimationController<>(this, "movement", 3, state -> state.setAndContinue(FLY)));
    }
}
