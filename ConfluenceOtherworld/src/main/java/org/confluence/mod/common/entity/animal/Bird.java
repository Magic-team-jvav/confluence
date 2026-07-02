package org.confluence.mod.common.entity.animal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
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

public class Bird extends BaseCritter {

    public Bird(EntityType<? extends Bird> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createCritterAttributes().add(Attributes.FLYING_SPEED, 0.4);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new PlayerCloseCondition(Bird.this, 5.0), new PanicFleeAction(Bird.this, 1.2)),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40)), new RandomStrollAction(Bird.this, 0.6, 8)));
            }
        };
    }

    @Override
    public ResourceLocation getModelPath() { return Confluence.asResource("animal/bird"); }
    @Override
    public ResourceLocation getTexturePath() { return Confluence.asResource("textures/entity/bird/blue.png"); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
    }
}
