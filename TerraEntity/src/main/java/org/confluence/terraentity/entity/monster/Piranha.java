package org.confluence.terraentity.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

/**
 * 食人鱼，改自海豚
 */
public class Piranha extends WaterAnimal implements Enemy, GeoEntity {


    public Piranha(EntityType<? extends Piranha> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setAirSupply(this.getMaxAirSupply());
        this.setXRot(0.0F);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void handleAirSupply(int airSupply) {
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2f,true));
        this.goalSelector.addGoal(2, new BreathAirGoal(this));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0, 10));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.ARMOR, 2).add(Attributes.MOVEMENT_SPEED, 1.2000000476837158).add(Attributes.ATTACK_DAMAGE, 13.0);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        return super.canAttack(target) && target.isInWater();
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    public void playAttackSound() {
        this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
    }

    @Override
    public int getMaxAirSupply() {
        return 4800;
    }

    @Override
    protected int increaseAirSupply(int currentAir) {
        return this.getMaxAirSupply();
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }


    @Override
    public void tick() {
        super.tick();
        this.updateSwingTime();

        if (this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
        } else {
            if (!this.isInWaterRainOrBubble()) {
                if (this.onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().add((double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5, (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
                    this.setYRot(this.random.nextFloat() * 360.0F);
                    this.setOnGround(false);
                    this.hasImpulse = true;
                }
            }
        }

    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 38) {
            this.addParticlesAroundSelf();
        } else {
            super.handleEntityEvent(id);
        }

    }

    private void addParticlesAroundSelf() {
        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.01;
            double d1 = this.random.nextGaussian() * 0.01;
            double d2 = this.random.nextGaussian() * 0.01;
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.2, this.getRandomZ(1.0), d0, d1, d2);
        }

    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return TESounds.ROUTINE_HURT.get();
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return TESounds.ROUTINE_DEATH.get();
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(travelVector);
        }

    }

    @Override
    public int getCurrentSwingDuration() {
        return 12;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Swim/Attack", 5, state -> {
            if(this.swinging){
                return state.setAndContinue(DefaultAnimations.ATTACK_STRIKE);
            }
            return state.setAndContinue(state.isMoving() ? DefaultAnimations.SWIM : DefaultAnimations.IDLE);
        }));
    }


}
