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

/**
 * 昆虫及简单小动物——共用的标准 flee+wander BT。
 */
public class SimpleCritter extends BaseCritter {

    public SimpleCritter(EntityType<? extends SimpleCritter> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createCritterAttributes();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new PlayerCloseCondition(SimpleCritter.this, 3.0), new PanicFleeAction(SimpleCritter.this, 0.5)),
                        SequenceNode.of(new WaitAction(30 + random.nextInt(60)), new RandomStrollAction(SimpleCritter.this, 0.3, 4)));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
    }
}
