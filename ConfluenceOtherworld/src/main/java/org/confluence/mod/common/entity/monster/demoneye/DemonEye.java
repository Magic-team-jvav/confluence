package org.confluence.mod.common.entity.monster.demoneye;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.api.entity.IMinion;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.mixin.world.entity.EntityAccessor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class DemonEye extends Monster implements Enemy, VariantHolder<DemonEyeVariant>, GeoEntity, IMinion {
    public static final String VARIANT_KEY = "Variant";
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(DemonEye.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
    public Vec3 moveTargetPoint;
    public DemonEyeSurroundTargetGoal surroundTargetGoal;
    private boolean dead = false;
    Mob owner;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH)
                .add(LibAttributes.getAttackDamage().get())
                .add(Attributes.ARMOR)
                .add(Attributes.MOVEMENT_SPEED);
    }

    public DemonEye(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveTargetPoint = Vec3.ZERO;
        this.xpReward = 5;

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT_ID, 0);
        this.entityData.define(DATA_OWNER_UUID, Optional.empty());
    }

    public @NotNull DemonEyeVariant getVariant() {
        return DemonEyeVariant.byId(entityData.get(DATA_VARIANT_ID));
    }

    public void setVariant(DemonEyeVariant pVariant) {
        entityData.set(DATA_VARIANT_ID, pVariant.id);
        AttributeMap attributeMap = getAttributes();
        attributeMap.getInstance(Attributes.MAX_HEALTH).setBaseValue(pVariant.health);
        attributeMap.getInstance(LibAttributes.getAttackDamage()).setBaseValue(pVariant.damage);
        attributeMap.getInstance(Attributes.ARMOR).setBaseValue(pVariant.armor);
        attributeMap.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(pVariant.big ? 0.1 : 0.2);
        setHealth(getMaxHealth());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt(VARIANT_KEY, this.getVariant().id);
        minion_saveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setVariant(DemonEyeVariant.byId(pCompound.getInt(VARIANT_KEY)));
        minion_readData(pCompound);
    }

    @Override
    protected void registerGoals() {
        surroundTargetGoal = new DemonEyeSurroundTargetGoal(this);
        goalSelector.addGoal(0, surroundTargetGoal);
        goalSelector.addGoal(1, new DemonEyeWanderGoal(this));
        goalSelector.addGoal(2, new DemonEyeLeaveGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, @NotNull BlockState pState, @NotNull BlockPos pPos) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(@NotNull Entity pEntity) {
    }

    public void move(@NotNull MoverType pType, @NotNull Vec3 motion) {
        if (dead) {
            super.move(pType, motion);
            return;
        }

        Vec3 collide = ((EntityAccessor) this).callCollide(motion);
        if (collide.x != motion.x) {
            motion = new Vec3(motion.x < 0 ? 0.22 : -0.22, motion.y, motion.z);
        }
        if (collide.y != motion.y) {
            boolean downward = motion.y < 0;
            motion = new Vec3(motion.x, downward ? Mth.clamp(-motion.y, 0.1, 0.22) : Mth.clamp(-motion.y, -0.22, -0.1), motion.z);
            if (surroundTargetGoal.targetPos != null && getTarget() != null) {
                surroundTargetGoal.targetPos = surroundTargetGoal.targetPos.with(Direction.Axis.Y, getTarget().position().y + (downward ? 2 : -1));
            }
        }
        if (collide.z != motion.z) {
            motion = new Vec3(motion.x, motion.y, motion.z < 0 ? 0.3 : -0.3);
        }

        setDeltaMovement(motion);
        super.move(pType, motion);
    }

    @Override
    public void tick() {
        // TODO: 仇恨值
        Vec3 pos = position();
        setTarget(level().getNearestPlayer(pos.x, pos.y, pos.z, 40, true));
        super.tick();
        // 在super.tick()结束后更新面向方向即可覆盖原版AI
        TEUtils.updateEntityRotation(this, this.getDeltaMovement().multiply(1, -1, 1));

        if (owner != null)
            setTarget(owner.getTarget());
    }

    @Override
    public void knockback(double pStrength, double pX, double pZ) {
        // TODO: 调数值
        super.knockback(pStrength * (getVariant().big ? 1.5 : 2), pX, pZ);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        setNoGravity(true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, state -> state.setAndContinue(RawAnimation.begin().thenLoop("fly"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }

    /* Minion API */

    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(DemonEye.class, EntityDataSerializers.OPTIONAL_UUID);
    ;


    @Override
    public EntityDataAccessor<Optional<UUID>> getDATA_OWNER_UUID() {
        return DATA_OWNER_UUID;
    }

    public void minion_setOwner(Entity owner) {
        if (owner instanceof EyeOfCthulhu eye) {
            minion_setOwnerUUID(owner.getUUID());
            this.owner = eye;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }
}
