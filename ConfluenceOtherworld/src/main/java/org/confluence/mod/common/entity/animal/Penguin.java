package org.confluence.mod.common.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.ai.goal.AquaticRandomSwimmingGoal;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class Penguin extends SwimmingCritter {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");

    public Penguin(EntityType<? extends Penguin> type, Level level) {
        super(type, level);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(new VanillaGoalAction(new BreathAirGoal(Penguin.this)),
                        withPassivePanic(SelectorNode.of(new VanillaGoalAction(new AquaticRandomSwimmingGoal(Penguin.this, 1.0, 30)), createGroundCritterRoutine(0.7)), 1.2));
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && isAlive() && tickCount % 20 == 0 && BloodMoonGameEvent.INSTANCE.started()) {
            corrupt(CritterCorruption.selectsCrimson(this));
        }
    }

    public void corrupt(boolean crimson) {
        if (!level().isClientSide && isAlive()) {
            convertTo(crimson ? MonsterEntities.VICIOUS_PENGUIN.get() : MonsterEntities.CORRUPT_PENGUIN.get(), false);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Movement", 3, state -> state.setAndContinue(isInWater() ? SWIM : state.isMoving() ? WALK : IDLE)));
    }
}
