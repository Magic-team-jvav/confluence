package org.confluence.mod.common.entity.animal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.ai.goal.AquaticRandomSwimmingGoal;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class Goldfish extends SwimmingCritter {
    private static final EntityDataAccessor<Boolean> WALKING = SynchedEntityData.defineId(Goldfish.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    public Goldfish(EntityType<? extends Goldfish> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(WALKING, false);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new ConditionalSwitchNode(Goldfish.this::isInWater,
                        withPassivePanic(new VanillaGoalAction(new AquaticRandomSwimmingGoal(Goldfish.this, 1.0, 20)), 1.2),
                        new ConditionalSwitchNode(Goldfish.this::canWalk,
                                withPassivePanic(createGroundCritterRoutine(0.7), 1.2), new BTNode() {
                            @Override
                            public void start() {
                                navigation.stop();
                                moveControl.setWantedPosition(getX(), getY(), getZ(), 0.0);
                                setSpeed(0.0F);
                            }

                            @Override
                            public BTStatus execute() {
                                if (onGround() && tickCount % 25 == 0) {
                                    setDeltaMovement(0.0, 0.18, 0.0);
                                    hasImpulse = true;
                                }
                                return BTStatus.RUNNING;
                            }
                        }));
            }
        };
    }

    private boolean canWalk() {
        return entityData.get(WALKING);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            if (isInWater()) entityData.set(WALKING, false);
            else if (!canWalk() && level().isRainingAt(blockPosition()))
                entityData.set(WALKING, true);
        }
        super.tick();
        if (!level().isClientSide && isAlive() && tickCount % 20 == 0 && BloodMoonGameEvent.INSTANCE.started()) {
            corrupt(CritterCorruption.selectsCrimson(this));
        }
    }

    public void corrupt(boolean crimson) {
        if (!level().isClientSide && isAlive()) {
            var converted = convertTo(crimson ? MonsterEntities.VICIOUS_GOLDFISH.get() : MonsterEntities.CORRUPT_GOLDFISH.get(), false);
            if (converted != null) converted.suppressMoneyDrops();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Walking", canWalk());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(WALKING, tag.getBoolean("Walking"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Movement", 3, state -> state.setAndContinue(isInWater() ? SWIM : canWalk() && state.isMoving() ? WALK : IDLE)));
    }
}
