package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.JumpingMonsterCycleAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class Derpling extends BaseMonster {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlay("jump");

    public Derpling(EntityType<? extends BaseMonster> type, Level level) {
        super(type, level);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new JumpingMonsterCycleAction(Derpling.this, () -> triggerAnim("Controller", "jump"));
            }
        };
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, state -> state.setAndContinue(IDLE)).triggerableAnim("jump", JUMP));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.DERPLING_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.DERPLING_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.DERPLING_DEATH.get();
    }

}
