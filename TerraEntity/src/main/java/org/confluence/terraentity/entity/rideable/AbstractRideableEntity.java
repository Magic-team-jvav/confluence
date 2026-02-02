package org.confluence.terraentity.entity.rideable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import org.confluence.terraentity.api.entity.IFlyRideableMob;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class AbstractRideableEntity extends Mob implements OwnableEntity, IFlyRideableMob, GeoEntity {

    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(AbstractRideableEntity.class, EntityDataSerializers.BYTE);;
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(AbstractRideableEntity.class, EntityDataSerializers.OPTIONAL_UUID);;

    protected boolean isMoving;
    protected int movingCounter = 0;
    protected int stopCounter = 0;

    protected boolean isJumping;
    protected int jumpCount = 0;

    int jumpTick;

    @Nullable
    private UUID owner;

    public AbstractRideableEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_FLAGS, (byte)0);
        builder.define(DATA_OWNER, Optional.empty());
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(DATA_OWNER == key){
            this.owner = this.entityData.get(DATA_OWNER).orElse(null);
        }
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
    public boolean shouldBeSaved() {
         return false;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.owner;
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.owner = uuid;
        this.entityData.set(DATA_OWNER, Optional.ofNullable(uuid));
    }


    @Override
    public void onLocalStartInputJump() {
        setIsInputtingJumping(true);
    }


    @Override
    public void onLocalStopInputJump() {
        setIsInputtingJumping(false);
    }


    @Override
    public float calJumpingScale(float jumpTick, float orientation) {
        return orientation == 0? 0 : 1;
    }

    /**
     * 用于服务端检测是否按下
     */
    public boolean isInputtingJumping() {
        return this.getFlag(1);
    }

    /**
     * 用于服务端设置按下跳跃
     */
    public void setIsInputtingJumping(boolean jumping) {
        this.setFlag(1, jumping);
    }

    public boolean isMoving() {
        return this.isMoving;
    }

    public boolean isJumping() {
        return this.getFlag(2);
    }

    public void setJumping(boolean isJumping) {
        super.setJumping(isJumping);
        this.setFlag(2, isJumping);
    }


    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {

        this.doPlayerRide(player);
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if(getOwner() != null){
            getOwner().hurt(source, amount);
        }
        return source.is(DamageTypes.GENERIC_KILL) && super.hurt(source, amount);
    }

    public static AttributeSupplier.Builder createBaseHorseAttributes() {
        return Mob.createMobAttributes().add(Attributes.JUMP_STRENGTH, 0.7)
                .add(Attributes.MAX_HEALTH, 53.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22499999403953552)
                .add(Attributes.STEP_HEIGHT, 1.0)
                .add(Attributes.SAFE_FALL_DISTANCE, 6.0)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0.5);
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    public void doPlayerRide(Player player) {

        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }

    }

    @Override
    public boolean isImmobile() {
        return true;
    }

    @Nullable
    public LivingEntity getControllingPassenger() {

        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player)entity;
        }

        return super.getControllingPassenger();
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide){
            if(!this.hasControllingPassenger()){
                discard();
            }


        }
    }

    @Override
    protected void tickRidden(@NotNull Player player, @NotNull Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        Vec2 vec2 = this.getRiddenRotation(player);

        this.isMoving = player.xxa != 0 || player.zza != 0;

        if (isMoving && !isInputtingJumping()) {
            movingCounter++;
            stopCounter = 0;
        } else {
            movingCounter = 0;
            stopCounter++;
        }

        if (this.isJumping && !onGround()) {
            jumpCount++;
            setJumping(true);
        } else {
            jumpCount = 0;
            setJumping(false);
            isJumping = false;
        }

        if(onGround() ){
            if(!isInputtingJumping())
                this.setIsInputtingJumping(false);
        }

        this.setRot(vec2.y, vec2.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        if (this.isControlledByLocalInstance()) {

            tickRiddenLocal(player,travelVector);
        }

    }

    protected void tickRiddenLocal(Player player, Vec3 travelVector){
        if (this.onGround()) {
            this.setIsInputtingJumping(false);
//            this.setJumping(false);
            if (Minecraft.getInstance().player != null && !this.isJumping && Minecraft.getInstance().player.input.jumping) {
                isJumping = true;
                playLocalJumpSound();
                this.executeRidersJump(1, travelVector);
            }

        }
    }

    protected void executeRidersJump(float playerJumpPendingScale, Vec3 travelVector) {
        double d0 = this.getJumpPower(playerJumpPendingScale);
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, d0, vec3.z);
        this.setIsInputtingJumping(true);
        this.hasImpulse = true;
        CommonHooks.onLivingJump(this);
//        if (travelVector.z > 0.0) {
//            float f = Mth.sin(this.getYRot() * 0.017453292F);
//            float f1 = Mth.cos(this.getYRot() * 0.017453292F);
//            this.setDeltaMovement(this.getDeltaMovement().add(-0.4F * f * playerJumpPendingScale, 0.0, (double)(0.4F * f1 * playerJumpPendingScale)));
//        }
    }

    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {

    }

    protected Vec2 getRiddenRotation(LivingEntity entity) {
        return new Vec2(entity.getXRot() * 0.5F, entity.getYRot());
    }

    /**
     * 处理运动
     */
    @Override
    protected @NotNull Vec3 getRiddenInput(Player player, @NotNull Vec3 travelVector) {
        float f = player.xxa * 0.5F;
        float f1 = player.zza;

        if (this.onGround()) {
            return new Vec3(f*0.2f,0,f1*0.2f);
        } else {
            return new Vec3(f, 0.0, f1);
        }
    }

    @Override
    protected float getRiddenSpeed(@NotNull Player player) {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    public boolean canBeSeenAsEnemy() {
        return false;
    }

    public boolean canBeSeenByAnyone() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getOwnerUUID() != null) {
            compound.putUUID("Owner", this.getOwnerUUID());
        }

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        UUID uuid;
        if (compound.hasUUID("Owner")) {
            uuid = compound.getUUID("Owner");
        } else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            this.setOwnerUUID(uuid);
        }
    }

    @Override
    public void onPlayerJump(int jumpPower) {

    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public void handleStartJump(int jumpPower) {
        this.playJumpSound();
        isJumping = true;
        jumpTick = tickCount;
    }

    @Override
    public void handleStopJump() {
        setJumping(false);
        jumping = false;
    }

    protected void playJumpSound() {
//        this.playSound(SoundEvents.HORSE_JUMP, 0.4F, 1.0F);

    }

    @Override
    protected void positionRider(@NotNull Entity passenger, Entity.@NotNull MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof LivingEntity) {
            ((LivingEntity)passenger).yBodyRot = this.yBodyRot;
        }

    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected @NotNull Vec3 getPassengerAttachmentPoint(@NotNull Entity entity, @NotNull EntityDimensions dimensions, float partialTick) {
        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick).add(0,0.2F,0);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    public void onInit(Player player){
        this.playEnterSound();
    }
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        this.playExitSound();
    }
    protected void playEnterSound() {
        this.playSound(SoundEvents.AMBIENT_UNDERWATER_ENTER, 0.5F, 3.0F);

    }
    protected void playExitSound() {
        this.playSound(SoundEvents.AMBIENT_UNDERWATER_EXIT, 0.5F, 3.0F);

    }
    protected void playLocalJumpSound() {

    }

    public @NotNull SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }
}
