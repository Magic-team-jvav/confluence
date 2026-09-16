package org.confluence.mod.common.entity.animal;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 沿地面短距离移动、停顿并在遇到障碍时改向的被动蠕虫类小动物。
public class SimpleCritter extends BaseCritter {
    private final RawAnimation restingAnimation;

    public SimpleCritter(EntityType<? extends SimpleCritter> type, Level level) {
        this(type, level, null);
    }

    public SimpleCritter(EntityType<? extends SimpleCritter> type, Level level, String restingAnimation) {
        super(type, level);
        this.restingAnimation = restingAnimation == null ? null : RawAnimation.begin().thenLoop(restingAnimation);
        getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.3);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(createGroundCritterRoutine(0.45D), 0.7D);
            }
        };
    }

    /// 体型很小的被动蠕虫不承受摔落伤害。
    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        if (restingAnimation == null) super.registerControllers(controllers);
        else
            controllers.add(new AnimationController<>(this, "Resting", 2, state -> state.setAndContinue(restingAnimation)));
    }
}
