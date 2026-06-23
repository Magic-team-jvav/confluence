package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;

public class Ghost extends AbstractMonster {

    public Ghost(EntityType<? extends Monster> type, Level level, AttributeBuilder builder) {
        super(type, level, builder.setController((c, e)-> c.add(new AnimationController<GeoAnimatable>(e, "Walk", 0, state ->
                state.setAndContinue(DefaultAnimations.WALK)))));

        this.noPhysics = true;
    }

    @Override
    protected void registerTargetGoal(GoalSelector targetSelector){
        this.goalSelector.addGoal(0, new FloatAiGoal(this));
        targetSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 5));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SOUL_DEATH.get();
    }
}
