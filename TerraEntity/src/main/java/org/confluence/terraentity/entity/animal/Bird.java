package org.confluence.terraentity.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.init.TESounds;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * 套用的鹦鹉ai，确实好用
 */
public class Bird extends Animal implements GeoEntity, FlyingAnimal {


    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;
    private boolean partyBird;
    
    @Nullable
    private BlockPos jukebox;

    public Bird(EntityType<? extends Bird> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (spawnGroupData == null) {
            spawnGroupData = new AgeableMob.AgeableMobGroupData(false);
        }

        return super.finalizeSpawn(level, difficulty, spawnType, (SpawnGroupData)spawnGroupData);
    }

    public boolean isBaby() {
        return false;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25f));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new BirdWanderGoal(this, 1.0));
        this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0, 3.0F, 7.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.FLYING_SPEED, 0.4000000059604645).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.FALL_DAMAGE_MULTIPLIER, 0);
    }

    public static AttributeSupplier.Builder createInspectAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0).add(Attributes.FLYING_SPEED, 0.25).add(Attributes.MOVEMENT_SPEED, 0.18).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.FALL_DAMAGE_MULTIPLIER, 0);
    }

    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    public void aiStep() {
        if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 3.46) || !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
            this.partyBird = false;
            this.jukebox = null;
        }

        super.aiStep();
        this.calculateFlapping();
    }
    @Override
    public void setRecordPlayingNearby(@NotNull BlockPos pos, boolean isPartying) {
        this.jukebox = pos;
        this.partyBird = isPartying;
    }

    public boolean isPartyBird() {
        return this.partyBird;
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (float)(!this.onGround() && !this.isPassenger() ? 4 : -1) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < 0.0) {
            this.setDeltaMovement(vec3.multiply(1.0, 0.6, 1.0));
        }

        this.flap += this.flapping * 2.0F;
    }
    

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        return super.mobInteract(player, hand);
        
    }
    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return false;
    }

//    public static boolean checkBirdSpawnRules(EntityType<Bird> Bird, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
//        return level.getBlockState(pos.below()).is(BlockTags.Bird_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos);
//    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    public boolean canMate(@NotNull Animal otherAnimal) {
        return false;
    }

    @Nullable
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return null;
    }

    @Nullable
    public SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    }
    

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return TESounds.ROUTINE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return TESounds.ROUTINE_DEATH.get();
    }

    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState block) {
        this.playSound(SoundEvents.PARROT_STEP, 0.15F, 1.0F);
    }

    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    protected void onFlap() {
        this.playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    public float getVoicePitch() {
        return getPitch(this.random);
    }

    public static float getPitch(RandomSource random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
    }
    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }
    @Override
    public boolean isPushable() {
        return true;
    }
    
    @Override
    protected void doPush(@NotNull Entity entity) {
        if (!(entity instanceof Player)) {
            super.doPush(entity);
        }

    }
    
    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return super.hurt(source, amount);
    }



    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    public boolean isFlying() {
        return !this.onGround();
    }

    protected boolean canFlyToOwner() {
        return true;
    }
    @Override
    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0, (double)(0.5F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }

    
    public static class BirdWanderGoal extends WaterAvoidingRandomFlyingGoal {
        public BirdWanderGoal(PathfinderMob mob, double speedModifier) {
            super(mob, speedModifier);
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            Vec3 vec3 = null;
            if (this.mob.isInWater()) {
                vec3 = LandRandomPos.getPos(this.mob, 15, 15);
            }

            if (this.mob.getRandom().nextFloat() >= this.probability) {
                vec3 = this.getTreePos();
            }

            return vec3 == null ? super.getPosition() : vec3;
        }

        @Nullable
        private Vec3 getTreePos() {
            BlockPos blockpos = this.mob.blockPosition();
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();
            Iterator<BlockPos> var4 = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 3.0), Mth.floor(this.mob.getY() - 6.0), Mth.floor(this.mob.getZ() - 3.0), Mth.floor(this.mob.getX() + 3.0), Mth.floor(this.mob.getY() + 6.0), Mth.floor(this.mob.getZ() + 3.0)).iterator();

            BlockPos blockpos1;
            boolean flag;
            do {
                do {
                    if (!var4.hasNext()) {
                        return null;
                    }

                    blockpos1 = var4.next();
                } while(blockpos.equals(blockpos1));

                BlockState blockstate = this.mob.level().getBlockState(blockpos$mutableblockpos1.setWithOffset(blockpos1, Direction.DOWN));
                flag = blockstate.getBlock() instanceof LeavesBlock || blockstate.is(BlockTags.LOGS);
            } while(!flag || !this.mob.level().isEmptyBlock(blockpos1) || !this.mob.level().isEmptyBlock(blockpos$mutableblockpos.setWithOffset(blockpos1, Direction.UP)));

            return Vec3.atBottomCenterOf(blockpos1);
        }
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly/Idle", 5, state -> state.setAndContinue(state.isMoving() ? DefaultAnimations.FLY : DefaultAnimations.IDLE)));
    }

}
