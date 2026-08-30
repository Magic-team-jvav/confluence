package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.DirectFloatingPursuitAction;
import org.confluence.mod.common.entity.ai.bt.leaf.LookForwardWanderFlyAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 穿过地形并直接追逐玩家的陨石怪。
public class MeteorHead extends BaseFlyingMonster {
    private static final RawAnimation FLOAT = RawAnimation.begin().thenLoop("move.walk");

    public MeteorHead(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(MeteorHead.this), new DirectFloatingPursuitAction(MeteorHead.this)),
                        new LookForwardWanderFlyAction(MeteorHead.this, 0.18, 0.0F));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Float", 3, state -> state.setAndContinue(FLOAT)));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SOUL_DEATH.get();
    }

    @Override
    protected boolean hasPushableBody() {
        return true;
    }
}
