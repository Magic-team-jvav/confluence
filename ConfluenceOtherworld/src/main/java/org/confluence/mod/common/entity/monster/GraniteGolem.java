package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public final class GraniteGolem extends BaseWarriorMonster {
    private static final EntityDataAccessor<Integer> DEFENSE_PHASE = SynchedEntityData.defineId(GraniteGolem.class, EntityDataSerializers.INT);
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation ENTER = RawAnimation.begin().thenPlayAndHold("defense.enter");
    private static final RawAnimation HOLD = RawAnimation.begin().thenLoop("defense.hold");
    private static final RawAnimation EXIT = RawAnimation.begin().thenPlayAndHold("defense.exit");

    private int defenseCooldown = 100;
    private int phaseTicks;

    public GraniteGolem(EntityType<? extends GraniteGolem> type, Level level) {
        super(type, level, 0.0, LandAnimationProfile.NONE, LandSoundProfile.ROUTINE, 1.0, true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DEFENSE_PHASE, 0);
    }

    @Override
    protected BTNode createLandBehavior() {
        return new ConditionalSwitchNode(() -> entityData.get(DEFENSE_PHASE) != 0, new BTNode() {
            @Override
            public BTStatus execute() {
                getNavigation().stop();
                getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0.0);
                setSpeed(0.0F);
                xxa = 0.0F;
                zza = 0.0F;
                setDeltaMovement(0.0, getDeltaMovement().y, 0.0);
                return BTStatus.RUNNING;
            }
        }, super.createLandBehavior());
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return entityData.get(DEFENSE_PHASE) != 0;
    }

    @Override
    public void tick() {
        if (!level().isClientSide && isAlive() && !isNoAi()) {
            if (defenseCooldown > 0) defenseCooldown--;
            int phase = entityData.get(DEFENSE_PHASE);
            if (phase == 0 && defenseCooldown == 0 && onGround()) {
                defenseCooldown = stateParameters(DefensePhase.ACTIVE).attackInterval();
                setDefensePhase(1, stateParameters(DefensePhase.ENTERING).duration());
            } else if (phase != 0 && --phaseTicks <= 0) {
                switch (phase) {
                    case 1 ->
                            setDefensePhase(2, stateParameters(DefensePhase.DEFENDING).duration());
                    case 2 -> setDefensePhase(3, stateParameters(DefensePhase.EXITING).duration());
                    case 3 -> setDefensePhase(0, 0);
                }
            }
        }
        super.tick();
    }

    private void setDefensePhase(int phase, int ticks) {
        int previous = entityData.get(DEFENSE_PHASE);
        if (previous != phase) setSpecialState(DefensePhase.values()[previous], false);
        entityData.set(DEFENSE_PHASE, phase);
        setSpecialState(DefensePhase.values()[phase], true);
        phaseTicks = ticks;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (entityData.get(DEFENSE_PHASE) == 2 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return false;
        boolean accepted = super.hurt(source, amount);
        if (accepted && !level().isClientSide) defenseCooldown = Math.max(0, defenseCooldown - 5);
        return accepted;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Movement", 3, state -> switch (entityData.get(DEFENSE_PHASE)) {
            case 1 -> state.setAndContinue(ENTER);
            case 2 -> state.setAndContinue(HOLD);
            case 3 -> state.setAndContinue(EXIT);
            default -> state.isMoving() ? state.setAndContinue(WALK) : PlayState.STOP;
        }));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("DefensePhase", entityData.get(DEFENSE_PHASE));
        tag.putInt("DefenseTicks", phaseTicks);
        tag.putInt("DefenseCooldown", defenseCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        int phase = Mth.clamp(tag.getInt("DefensePhase"), 0, 3);
        setDefensePhase(phase, Math.max(0, tag.getInt("DefenseTicks")));
        defenseCooldown = tag.contains("DefenseCooldown") ? Math.max(0, tag.getInt("DefenseCooldown"))
                : stateParameters(DefensePhase.ACTIVE).attackInterval();
    }

    public enum DefensePhase {ACTIVE, ENTERING, DEFENDING, EXITING}
}
