package org.confluence.mod.common.entity.animal;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public final class TruffleWorm extends BaseCritter {
    private static final EntityDataAccessor<Boolean> BURROWING = SynchedEntityData.defineId(TruffleWorm.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");

    public TruffleWorm(EntityType<? extends TruffleWorm> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BURROWING, false);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(new BurrowAction(), withPassivePanic(createGroundCritterRoutine(0.5), 1.0));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Movement", 2, state -> state.isMoving() || entityData.get(BURROWING) ? state.setAndContinue(RUN) : PlayState.STOP));
    }

    private final class BurrowAction extends BTNode {
        private int ticks;

        private Player nearbyPlayer() {
            return level().getNearestPlayer(TruffleWorm.this, 10.0);
        }

        @Override
        public boolean canStart() {
            return entityData.get(BURROWING) || nearbyPlayer() != null;
        }

        @Override
        public BTStatus execute() {
            if (!entityData.get(BURROWING) && nearbyPlayer() == null) return BTStatus.FAILURE;
            if (++ticks >= 20) {
                entityData.set(BURROWING, true);
                getNavigation().stop();
                noPhysics = true;
                setDeltaMovement(0.0, -0.06, 0.0);
                if (ticks >= 30) discard();
            }
            return BTStatus.RUNNING;
        }

        @Override
        public void stop() {
            ticks = 0;
        }
    }
}
