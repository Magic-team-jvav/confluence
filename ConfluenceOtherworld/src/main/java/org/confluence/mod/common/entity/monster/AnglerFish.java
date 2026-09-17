package org.confluence.mod.common.entity.monster;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEffects;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class AnglerFish extends Piranha {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    public AnglerFish(EntityType<? extends AnglerFish> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && !level().isClientSide && target instanceof LivingEntity living && random.nextInt(8) == 0) {
            living.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 900), this);
        }
        return hit;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Body", 0, state -> state.setAndContinue(IDLE)));
    }
}
