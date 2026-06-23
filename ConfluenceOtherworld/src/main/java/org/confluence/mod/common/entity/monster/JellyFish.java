package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.api.entity.ai.IFSMGeoMob;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JellyFish extends WaterAnimal implements GeoEntity, IFSMGeoMob, Enemy {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    ClientBoundAnimationMessage message = new ClientBoundAnimationMessage();
    FSMGoal<JellyFish> jellyFishFSMGoal;
    public Vec3 lastMovement = Vec3.ZERO;
    public Vec3 currentMovement = Vec3.ZERO;

    static EntityDataAccessor<Integer> skillIndexData = SynchedEntityData.defineId(JellyFish.class, EntityDataSerializers.INT);

    public JellyFish(EntityType<? extends JellyFish> type, Level level) {
        super(type, level);
//        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.moveControl = new JellyFishMoveControl(this);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        if(level.isClientSide) {
            this.jellyFishFSMGoal = new JellyFishFSMGoal(this, skillIndexData);
        }

    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
//        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2f,true));
        this.jellyFishFSMGoal = new JellyFishFSMGoal(this, skillIndexData);
        this.goalSelector.addGoal(1, this.jellyFishFSMGoal);
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0, 10));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, e->e.isInWater()));
    }

    static class JellyFishMoveControl extends MoveControl {

        int jumpCD = 0;
        final int _jumpCD = 20;
        public JellyFishMoveControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            if (this.mob.isInWater()) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.005, 0.0));
            }

            if(--this.jumpCD <= 0 && this.operation == MoveControl.Operation.MOVE_TO){
                this.operation = MoveControl.Operation.WAIT;
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedZ - this.mob.getZ();
                double d2 = this.wantedY - this.mob.getY();
                double d3 = d0 * d0 + d2 * d2 + d1 * d1;
                if (d3 < 2.500000277905201E-7) {
                    this.mob.setZza(0.0F);
                    return;
                }

                float rotY = (float)(Mth.atan2(d1, d0) * 180.0 / 3.1415927410125732) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), rotY, 90.0F));
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().normalize().scale(0.5f ));
                BlockPos blockpos = this.mob.blockPosition();
                BlockState blockstate = this.mob.level().getBlockState(blockpos);
                VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level(), blockpos);
                if (d2 > (double)this.mob.maxUpStep() && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY() && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
                    this.mob.getJumpControl().jump();
                }
                this.jumpCD = _jumpCD + this.mob.getRandom().nextInt(mob.getTarget() == null? _jumpCD * 3 : _jumpCD);
            }else {
                if(this.mob.getTarget() != null){
                    this.mob.lookAt(this.mob.getTarget(), 10.0F, 10.0F);
                    this.mob.getLookControl().setLookAt(this.mob.getTarget());
                }
            }
        }
    }

    static class JellyFishFSMGoal extends FSMGoal<JellyFish> {

        public JellyFishFSMGoal(JellyFish mob, EntityDataAccessor<Integer> skillIndexData) {
            super(mob, skillIndexData);
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.getTarget() != null;
        }
        @Override
        public void stop() {
            getSkills().forceStartIndex(0);
        }

        @Override
        public void start() {
            super.start();
            this.getSkills().syncForce(0);
        }

        @Override
        public void init(CircleMobSkills skills) {
            this.addSkill(new MobSkill<JellyFish>(DefaultAnimations.IDLE, 150, 0)
                    .onTick(e->{
                        if(e.getTarget() != null && e.getTarget().isInWater()){
                            e.navigation.moveTo(e.getTarget(), 1.0f);
                        }
                    })
            );
            this.addSkill(new MobSkill<JellyFish>(DefaultAnimations.ATTACK_STRIKE, 80, 0)
                    .onInit(e->{
                        e.navigation.stop();
                    })
                    .onOver(e->{
                        e.setTarget(null);
                    })
            );
        }
    }

    @Override
    public void tick() {
        if(level().isClientSide){
            this.getSkills().tick();
            if(this.getDeltaMovement().length() > 0.08f) {
                this.lastMovement = this.getDeltaMovement();
            }
        }
        super.tick();

        if(this.getDeltaMovement().length() > 0.08f) {
            this.currentMovement = this.getDeltaMovement();
        }

    }

    @Override
    public CircleMobSkills getSkills() {
        return this.jellyFishFSMGoal.getSkills();
    }

    @Override
    public ClientBoundAnimationMessage getAnimationMessage() {
        return message;
    }

    @Override
    public void addSkills() {
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(skillIndexData, 0);

    }
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key == skillIndexData) {
            syncSkills(key);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(this.fsmAnimationController());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSoundEvents.JELLYFISH_HURT.get();
    }

    @Override
    protected SoundEvent getAmbientSound(){
        return ModSoundEvents.JELLYFISH_FREE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.JELLYFISH_DEATH.get();
    }
}
