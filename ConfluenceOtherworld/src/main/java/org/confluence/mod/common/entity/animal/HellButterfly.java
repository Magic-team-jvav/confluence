package org.confluence.mod.common.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.PlayerCloseCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.PanicFleeAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class HellButterfly extends BaseCritter {

    public HellButterfly(EntityType<? extends HellButterfly> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createCritterAttributes();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new PlayerCloseCondition(HellButterfly.this, 4.0), new PanicFleeAction(HellButterfly.this, 1.0)),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40)), new RandomStrollAction(HellButterfly.this, 0.5, 8)));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
    }
}
