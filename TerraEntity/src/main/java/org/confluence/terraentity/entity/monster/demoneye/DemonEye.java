package org.confluence.terraentity.entity.monster.demoneye;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.util.DeathAnimOptions;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.mixin.accessor.EntityAccessor;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DemonEye extends Monster implements Enemy, VariantHolder<DemonEyeVariant>, GeoEntity, DeathAnimOptions {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(DemonEye.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
    public Vec3 moveTargetPoint;
    public DemonEyeSurroundTargetGoal surroundTargetGoal;
    private boolean dead = false;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH)
            .add(Attributes.ATTACK_DAMAGE)
            .add(Attributes.ARMOR)
            .add(Attributes.MOVEMENT_SPEED);
    }

    public DemonEye(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveTargetPoint = Vec3.ZERO;
        this.xpReward = 5;

    }

    public static boolean checkDemonEyeSpawn(EntityType<? extends Mob> type, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level level)) {
            return false;
        }
        if (checkMobSpawnRules(type, pLevel, pSpawnType, pPos, pRandom)) {
            // 新月100%，其他80%
            if (pPos.getY() < 260 && level.isNight()) {
                if (level.getMoonPhase() == 4) {
                    for (BlockPos.MutableBlockPos blockPos = pPos.mutable(); blockPos.getY() < level.getMaxBuildHeight(); blockPos.move(0, 1, 0)) {
                        if (level.getBlockState(blockPos).isCollisionShapeFullBlock(level, blockPos)) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return level.random.nextInt(99) < 80;
                }
            }
        }
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, 0);
    }

    public @NotNull DemonEyeVariant getVariant() {
        return DemonEyeVariant.byId(entityData.get(DATA_VARIANT_ID));
    }

    public void setVariant(DemonEyeVariant pVariant) {
        entityData.set(DATA_VARIANT_ID, pVariant.id);
        AttributeMap attributeMap = getAttributes();
        attributeMap.getInstance(Attributes.MAX_HEALTH).setBaseValue(pVariant.health);
        attributeMap.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(pVariant.damage);
        attributeMap.getInstance(Attributes.ARMOR).setBaseValue(pVariant.armor);
        attributeMap.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(pVariant.big ? 0.1 : 0.2);
        setHealth(getMaxHealth());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getVariant().id);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setVariant(DemonEyeVariant.byId(pCompound.getInt("Variant")));
    }

    @Override
    protected void registerGoals() {
        surroundTargetGoal = new DemonEyeSurroundTargetGoal(this);
        goalSelector.addGoal(0, surroundTargetGoal);
        goalSelector.addGoal(1, new DemonEyeWanderGoal(this));
        goalSelector.addGoal(2, new DemonEyeLeaveGoal(this));
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

    @Override
    protected void pushEntities() {
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
    }

    @Override
    public void knockback(double pStrength, double pX, double pZ) {
        // TODO: 调数值
        super.knockback(pStrength * 2, pX, pZ);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TESounds.ROUTINE_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return TESounds.ROUTINE_HURT.get();
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        setNoGravity(true);
    }

    @Override
    protected void tickDeath() {
        if (!dead) {
            // 我怕频繁set会影响性能，虽然会检查是否不同再真的set，但是它会从map里面get，
            // 每次get都有锁的操作，我怕锁太慢了，但是其实所有生物每刻都要检查一次生命值来决定是否要
            // 调这个方法，所以我也不确定会不会慢
            setNoGravity(false);
            dead = true;
        }
        super.tickDeath();
    }

    // 貌似不用：1.原版的setXRot并不吃性能 2.详见tick()部分的朝向更新
//    @Override
//    public void setXRot(float pXRot){
////        if(pXRot == 0 || pXRot == getXRot()){
////            return;
////        }
//        xRotO = getXRot();
//        super.setXRot(pXRot);
//    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state -> state.setAndContinue(RawAnimation.begin().thenLoop("fly"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }
}

