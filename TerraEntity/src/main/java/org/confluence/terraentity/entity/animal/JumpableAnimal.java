package org.confluence.terraentity.entity.animal;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;

public class JumpableAnimal extends SimpleAnimal {

    int jumpCount = 0;

    public JumpableAnimal(EntityType<? extends JumpableAnimal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new JumpMoveControl(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5){
            protected boolean findRandomPosition() {
                Vec3 vec3 = DefaultRandomPos.getPos(this.mob, 10, 4);
                if (vec3 == null) {
                    return false;
                } else {
                    this.posX = vec3.x;
                    this.posY = vec3.y;
                    this.posZ = vec3.z;
                    return true;
                }
            }
        });
//        this.goalSelector.addGoal(3, new JumpRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.1f));

        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));


    }


    static class JumpMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final JumpableAnimal slime;
        private boolean isAggressive;

        public JumpMoveControl(JumpableAnimal slime) {
            super(slime);
            this.slime = slime;
            this.yRot = 180.0F * slime.getYRot() / 3.1415927F;
        }

        public void setDirection(float yRot, boolean aggressive) {
            this.yRot = yRot;
            this.isAggressive = aggressive;
        }

        public void setWantedMovement(double speed) {
            this.speedModifier = speed;
            this.operation = Operation.MOVE_TO;
        }

        @Override
        public void tick() {
//            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            double deltaX = this.wantedX - this.mob.getX();
            double deltaY = this.wantedY - this.mob.getY();
            double deltaZ = this.wantedZ - this.mob.getZ();
            double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            if (distanceSquared < 2.500000277905201E-7) {
                this.mob.setXxa(0.0f);
                return;
            }
            float targetYaw = (float)(Mth.atan2(deltaZ, deltaX) * 180.0 / 3.1415927410125732) - 90.0F;
            if(this.slime.onGround()){
                this.slime.setJumping(false);
            }
            if(distanceSquared > 1f && this.slime.onGround()) {
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), targetYaw, 90.0F));
            }else{
                return;
            }

            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = Operation.WAIT;
                if (this.mob.onGround()) {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }
                        this.slime.getJumpControl().jump();
                        float f = (float) Math.min(distanceSquared * 0.4f, 0.5f);
                        f += this.slime.getRandom().nextFloat() * 0.5f;
                        this.slime.addDeltaMovement(this.slime.getForward().normalize().scale(f));
                        this.slime.setJumping(true);
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));

                }
            }
        }
    }

    protected int getJumpDelay() {
        return this.random.nextInt(10) + 5;
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        this.setZza(0);
    }

    @Override
    protected float getJumpPower() {
        return super.getJumpPower() * (1 + this.getRandom().nextFloat() * 0.5f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "Jump", 5, state->{
            if(this.jumpCount > 0) {
                return state.setAndContinue(DefaultAnimations.JUMP);
            }
            state.resetCurrentAnimation();
            return PlayState.STOP;
        }));
    }

    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(JumpableAnimal.class, EntityDataSerializers.BYTE);;

    public boolean isJumping() {
        return this.getFlag(2);
    }

    public void setJumping(boolean isJumping) {
        super.setJumping(isJumping);
        this.setFlag(2, isJumping);
    }

    protected boolean getFlag(int flagId) {
        return (this.entityData.get(DATA_ID_FLAGS) & flagId) != 0;
    }

    protected void setFlag(int flagId, boolean value) {
        byte b0 = this.entityData.get(DATA_ID_FLAGS);
        if (value) {
            this.entityData.set(DATA_ID_FLAGS, (byte)(b0 | flagId));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)(b0 & ~flagId));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_FLAGS, (byte)0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(DATA_ID_FLAGS == key && level().isClientSide && this.isJumping()){
            this.jumpCount = 8;
        }
    }

    @Override
    public void tick() {
        super.tick();
        --this.jumpCount;
    }
}
