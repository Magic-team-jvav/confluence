package org.confluence.mod.common.entity.monster.skeleton;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.api.entity.animation.IUseItemAnimatable;
import org.confluence.mod.common.entity.ai.goal.TERangedAttackGoal;
import org.confluence.mod.common.entity.animation.BoneStateMachine;
import org.confluence.mod.common.entity.animation.BoneStates;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.entity.monster.prefab.IAttributeHolder;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

/// 远程单位
public class RangeSkeleton extends AbstractSkeleton implements GeoEntity, IUseItemAnimatable<BoneStates>, IAttributeHolder {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private BoneStateMachine<BoneStates> leftArmBoneStateMachine;
    private BoneStateMachine<BoneStates> rightArmBoneStateMachine;
    private boolean dirty = false;

    private final TERangedAttackGoal<RangeSkeleton> teBowGoal = new TERangedAttackGoal<>(this, 1.0, 20, 15.0F);

    AttributeBuilder builder;

    public RangeSkeleton(EntityType<? extends AbstractSkeleton> entityType, Level level, AttributeBuilder builder) {
        super(entityType, level);
        if (level.isClientSide) {
            leftArmBoneStateMachine = new BoneStateMachine<>(BoneStates.IDLE);
            rightArmBoneStateMachine = new BoneStateMachine<>(BoneStates.IDLE);
        }
        this.builder = builder;
//        builder.modify(this);
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!dirty && !level().isClientSide) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth());
            this.setHealth(getMaxHealth());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Health")) {
            dirty = true;
        }
    }

    @Override
    public void reassessWeaponGoal() {
        if (!this.level().isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.teBowGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem));
            if (itemstack.getItem() instanceof BowItem) {
                int i = this.getHardAttackInterval();
                if (this.level().getDifficulty() != Difficulty.HARD) {
                    i = this.getAttackInterval();
                }

                this.teBowGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(4, this.teBowGoal);
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }
        }
    }

    @Override
    public float getWalkTargetValue(BlockPos pos) {
        if (this.builder.spawnWithoutLight) {
            return 0;
        }
        return super.getWalkTargetValue(pos);
    }


    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk/Idle", 5, state ->
                state.setAndContinue(state.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE)
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isChargingCrossbow() {
        return false;
    }

    @Override
    public int getChargingTicks() {
        return 0;
    }

    @Override
    public BoneStateMachine<BoneStates> getLeftArmBoneStateMachine() {
        return leftArmBoneStateMachine;
    }

    @Override
    public BoneStateMachine<BoneStates> getRightArmBoneStateMachine() {
        return rightArmBoneStateMachine;
    }

    @Override
    public boolean isLieDown() {
        return false;
    }

    @Override
    public AttributeBuilder getAttributeBuilder() {
        return builder;
    }

    @Override
    public Vec3 getVehicleAttachmentPoint(Entity entity) {
        return super.getVehicleAttachmentPoint(entity).add(0, 0.65, 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        this.setLeftHanded(false);
        return spawnGroupData;
    }
}
