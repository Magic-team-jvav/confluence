package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.MonsterLaser;
import org.confluence.mod.common.gameevent.MartianEventHelper;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/// 火星远程枪手，每七个游戏刻发射一次扰脑镭射。
public final class BrainScrambler extends MartianHumanoidMonster {
    public static final int SHOT_INTERVAL = 7;
    private static final EntityDataAccessor<Boolean> AIMING = SynchedEntityData.defineId(BrainScrambler.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation STAND = RawAnimation.begin().thenLoop("Stand Gun");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk Gun");
    private static final RawAnimation STAND_AIM = RawAnimation.begin().thenLoop("Stand Aim");
    private static final RawAnimation WALK_AIM = RawAnimation.begin().thenLoop("Walk Aim");
    private int shotCooldown;

    public BrainScrambler(EntityType<? extends BrainScrambler> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AIMING, false);
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    private boolean isUsableTarget(LivingEntity target) {
        return target != null && target.isAlive() && canAttack(target);
    }

    @Override
    protected BTNode createLandBehavior() {
        BTNode combatBehavior = new BTNode() {
            @Override
            public boolean canStart() {
                return isUsableTarget(getTarget());
            }

            @Override
            public BTStatus execute() {
                LivingEntity target = getTarget();
                if (!isUsableTarget(target)) {
                    return BTStatus.FAILURE;
                }

                getLookControl().setLookAt(target, 30, 30);
                double distance = distanceToSqr(target);
                if (distance < 36) {
                    getNavigation().moveTo(2 * getX() - target.getX(), getY(), 2 * getZ() - target.getZ(), 1);
                } else if (distance > 256 || !hasLineOfSight(target)) {
                    getNavigation().moveTo(target, 1);
                } else {
                    getNavigation().stop();
                }

                return BTStatus.RUNNING;
            }

            @Override
            public void stop() {
                getNavigation().stop();
            }

        };
        return SelectorNode.of(combatBehavior,
                new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(this, 0.8)),
                new VanillaGoalAction(new RandomLookAroundGoal(this)));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive()) {
            return;
        }

        if (shotCooldown > 0) {
            shotCooldown--;
        }

        LivingEntity target = getTarget();
        boolean aiming = isUsableTarget(target) && distanceToSqr(target) <= 1024 && hasLineOfSight(target);
        entityData.set(AIMING, aiming);
        if (!aiming || shotCooldown > 0) {
            return;
        }

        MonsterLaser laser = ModEntities.MONSTER_LASER.get().create(level());
        if (laser == null) {
            return;
        }

        laser.configureShot(this, target, getEyePosition().add(0, -0.1 * getScale(), 0), MonsterLaser.Variant.BRAIN_SCRAMBLER);
        MartianEventHelper.inheritEventMembership(this, laser);
        if (level().addFreshEntity(laser)) {
            shotCooldown = SHOT_INTERVAL;
            playSound(ModSoundEvents.ITEM_BRAIN_SCRAMBLER.get(), 0.8F, 1.0F);
        } else {
            laser.discard();
        }

    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.BRAIN_SCRAMBLER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.BRAIN_SCRAMBLER_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<BrainScrambler> controller = new AnimationController<>(this, "scrambler", 3, state -> {
            RawAnimation movement = state.isMoving() ? WALK : STAND;
            RawAnimation aiming = state.isMoving() ? WALK_AIM : STAND_AIM;
            return state.setAndContinue(entityData.get(AIMING) ? aiming : movement);
        });
        controllers.add(controller);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ShootingCooldown", shotCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        shotCooldown = Mth.clamp(tag.getInt("ShootingCooldown"), 0, SHOT_INTERVAL);
        entityData.set(AIMING, false);
    }
}
