package org.confluence.terraentity.entity.rideable;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.api.entity.IFlyRideableMob;
import org.confluence.terraentity.init.TEAttachments;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;

public class RideableBee extends AbstractRideableEntity implements IFlyRideableMob {

    int _flyTick = 200;

    public RideableBee(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.GRAVITY).setBaseValue(0.03f);
    }

    public void tick(){
        super.tick();

        if(!level().isClientSide){
            if(getOwner() != null && isInWater()) {
                this.getOwner().stopRiding();
                this.discard();
            }
            if(isInputtingJumping() && (tickCount & 1) == 0){
                playSound(SoundEvents.BEEHIVE_WORK, 0.5F, 2F);
            }
        }
    }

    @Override
    protected void tickRiddenLocal(Player player, Vec3 travelVector){
        if(getOwner() == null){
            return;
        }
        var data = getOwner().getData(TEAttachments.SUMMONER_STORAGE);
        Vec3 speed = getDeltaMovement();

        if(this.isInputtingJumping()){
            double vy;
            if(--data.beeFlyTick > 0){
                vy = Math.min(speed.y + 0.035f, 0.2f);
            }else{
                vy = Math.min(speed.y + 0.02f, 0.2f);
            }
            this.setDeltaMovement(speed.x, vy, speed.z);
        }
        if(onGround()){
            data.beeFlyTick = Math.min(_flyTick, data.beeFlyTick + 5);
        }
    }

    @Override
    protected @NotNull Vec3 getRiddenInput(Player player, @NotNull Vec3 travelVector) {
        float f = player.xxa * 0.5F;
        float f1 = Math.max(player.zza, -0.1f);

        if (this.onGround()) {
            return new Vec3(f*0.08f,0,f1*0.15f);
        } else {
            return new Vec3(f*0.5f, 0.0, f1);
        }
    }

    protected @NotNull Vec3 getPassengerAttachmentPoint(@NotNull Entity entity, @NotNull EntityDimensions dimensions, float partialTick) {

        float offsetY = this.isMoving && !isInputtingJumping()?
                Mth.lerp(Math.min((movingCounter + partialTick) / 12f, 1f), 0.4f, 0.1f)
                : Mth.lerp(Math.min((stopCounter + partialTick) / 7f, 1f), 0.1f, 0.4f);
        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick).add(0,offsetY,0);
    }

    @Override
    public void onPlayerJump(int jumpPower) {
    }

    @Override
    public float calJumpingScale(float jumpTick, float orientation) {
        if(getOwner() == null){
            return 0;
        }
        var data = getOwner().getData(TEAttachments.SUMMONER_STORAGE);
        return (float) (data.beeFlyTick) / _flyTick;
    }

    @Override
    public void handleStartJump(int jumpPower) {
        super.handleStartJump(jumpPower);
        this.onLocalStopInputJump();
    }

    /**
     * 用于服务端检测
     */
    public boolean isJumping() {
        return this.getFlag(1);
    }
    /**
     * 用于服务端设置
     */
    public void setIsInputtingJumping(boolean jumping) {
        super.setIsInputtingJumping(jumping);
        this.setFlag(1, jumping);
    }


    RawAnimation wing = RawAnimation.begin().thenLoop("wing");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "Wing", 10, state -> {
                if (isInputtingJumping()) {
                    return state.setAndContinue(wing);
                }
                state.resetCurrentAnimation();
                return PlayState.STOP;
            }),
            new AnimationController<>(this, "Fly/Idle/Move", 10, state -> {
                if (isMoving) {
                    if (isInputtingJumping()) {
                        return state.setAndContinue(DefaultAnimations.FLY);
                    }
                    return state.setAndContinue(DefaultAnimations.WALK);
                }
                return state.setAndContinue(DefaultAnimations.IDLE);
            })
        );
    }

    protected void playEnterSound() {
        this.playSound(SoundEvents.BEEHIVE_EXIT, 1.0F, 1.0F);
    }

    protected void playExitSound() {
        this.playSound(SoundEvents.BEEHIVE_ENTER, 1.0F, 1.0F);
    }

    @Override
    protected float getFlyingSpeed() {
        return this.getSpeed() * 0.2F;
    }
}
