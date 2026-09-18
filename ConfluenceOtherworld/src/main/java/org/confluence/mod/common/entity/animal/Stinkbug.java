package org.confluence.mod.common.entity.animal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class Stinkbug extends BaseCritter {
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(Stinkbug.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation REST = RawAnimation.begin().thenLoop("no_wing");
    private final GroundPathNavigation groundNavigation;
    private final FlyingPathNavigation flightNavigation;
    private final MoveControl groundControl;
    private final FlyingMoveControl flightControl;
    private int phaseTicks = 100;

    public Stinkbug(EntityType<? extends Stinkbug> type, Level level) {
        super(type, level);
        groundNavigation = (GroundPathNavigation) navigation;
        groundControl = moveControl;
        flightNavigation = new FlyingPathNavigation(this, level);
        flightNavigation.setCanFloat(true);
        flightControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(FLYING, false);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(new ConditionalSwitchNode(() -> entityData.get(FLYING), new VanillaGoalAction(new WaterAvoidingRandomFlyingGoal(Stinkbug.this, 0.8)), createGroundCritterRoutine(0.55)), 1.2);
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (entityData.get(FLYING)) {
            if (--phaseTicks <= 0) setFlying(false, 80 + random.nextInt(101));
        } else if (onGround() && --phaseTicks <= 0 || isInWater()) {
            setFlying(true, 40 + random.nextInt(41));
            setDeltaMovement(getDeltaMovement().add(0.0, 0.25, 0.0));
        }
    }

    private void setFlying(boolean flying, int ticks) {
        navigation.stop();
        entityData.set(FLYING, flying);
        setNoGravity(flying);
        navigation = flying ? flightNavigation : groundNavigation;
        moveControl = flying ? flightControl : groundControl;
        moveControl.setWantedPosition(getX(), getY(), getZ(), 0.0);
        phaseTicks = ticks;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Flying", entityData.get(FLYING));
        tag.putInt("PhaseTicks", phaseTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFlying(tag.getBoolean("Flying"), tag.contains("PhaseTicks") ? Math.max(1, tag.getInt("PhaseTicks")) : 100);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Wings", 2, state -> state.setAndContinue(entityData.get(FLYING) ? FLY : REST)));
    }
}
