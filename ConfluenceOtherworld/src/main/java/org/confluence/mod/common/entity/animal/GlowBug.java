package org.confluence.mod.common.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class GlowBug extends BaseFlyingCritter {
    private final RawAnimation animation;

    public GlowBug(EntityType<? extends GlowBug> type, Level level, String animation) {
        super(type, level);
        this.animation = RawAnimation.begin().thenLoop(animation);
    }

    @Override
    public boolean isFullBright() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Flight", 3, state -> state.setAndContinue(animation)));
    }
}
