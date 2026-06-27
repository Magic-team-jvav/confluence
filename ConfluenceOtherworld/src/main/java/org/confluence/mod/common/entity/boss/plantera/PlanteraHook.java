package org.confluence.mod.common.entity.boss.plantera;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.terraentity.init.TESounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * 世花钩子
 */
public class PlanteraHook extends AbstractPlanteraTentacleSrc implements Boss {
    protected static final int STATE_IDLE = 0, STATE_EXTENDING = 1, STATE_GRABBED = 2, STATE_RETRACTING = 3;
    Plantera owner;
    BlockPos targetBlock;
    int hookNumber;
    int state;

    public static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(PlanteraHook.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<OptionalInt> DATA_HOOK_NUM = SynchedEntityData.defineId(PlanteraHook.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    public static final EntityDataAccessor<OptionalInt> DATA_STATE = SynchedEntityData.defineId(PlanteraHook.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    public static final EntityDataAccessor<Optional<BlockPos>> DATA_TARGET_BLOCK = SynchedEntityData.defineId(PlanteraHook.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    public PlanteraHook(EntityType<? extends Monster> entityType, Level level) {
        this(entityType, level, null, -1);
    }

    public PlanteraHook(EntityType<? extends Monster> entityType, Level level, @Nullable Plantera owner, int hookNum) {
        super(entityType, level);
        setDiscardFriction(true);

        collisionProperties = new CollisionProperties(1,1,0.5f);

        setOwner(owner);
        setHookNumber(hookNum);
        setTargetBlock(null);
        setState(STATE_IDLE);

        // 防止卡位置导致动不了
        this.noPhysics = true;
        // 防止超出包围盒不渲染
        this.noCulling = true;
        // 防止因为离得太远消失
        this.setPersistenceRequired();
    }

    @Override
    public void tick() {
        super.tick();

        // 服务端 - 对于加载区块中解冻部件的处理
        if (! level().isClientSide() && tickCount == 1) {
            setOwner(null);
            setHookNumber(-1);
            setState(-1);
            setTargetBlock(null);
        }

        if (owner != null && !owner.isAlive()) {
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public void addSkills() {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return TESounds.DRIPPLER_HURT.get();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TESounds.TR_ZOMBIE_DEATH.get();
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1,new MoveGoal());
    }

    public void setOwner(@Nullable Plantera newOwner) {
        if (newOwner == null) {
            if (getEntityData().get(DATA_OWNER).isPresent()) {
                newOwner = (Plantera) level().getEntities().get(getEntityData().get(DATA_OWNER).get());
            }
        } else {
            if (! level().isClientSide) {
                getEntityData().set(DATA_OWNER, Optional.of(newOwner.getUUID()));
            }
        }
        owner = newOwner;
        // 以防owner在hookNumber后初始化
        if (owner != null && hookNumber >= 0) {
            owner.hooks[hookNumber] = this;
        }
    }

    public void setHookNumber(int newHookNumber) {
        if (newHookNumber < 0) {
            if (getEntityData().get(DATA_HOOK_NUM).isPresent()) {
                newHookNumber = getEntityData().get(DATA_HOOK_NUM).getAsInt();
            }
        } else {
            if (! level().isClientSide) {
                getEntityData().set(DATA_HOOK_NUM, OptionalInt.of(newHookNumber));
            }
        }
        hookNumber = newHookNumber;
        // owner在hookNumber之前已经初始化，设置钩子
        if (owner != null && hookNumber >= 0) {
            owner.hooks[hookNumber] = this;
        }
    }

    public void setState(int newState) {
        if (newState < 0) {
            if (getEntityData().get(DATA_STATE).isPresent()) {
                newState = getEntityData().get(DATA_STATE).getAsInt();
            }
        } else {
            if (! level().isClientSide) {
                getEntityData().set(DATA_STATE, OptionalInt.of(newState));
            }
        }
        state = newState;
    }

    public void setTargetBlock(@Nullable BlockPos newTargetBlock) {
        if (newTargetBlock == null) {
            if (getEntityData().get(DATA_TARGET_BLOCK).isPresent()) {
                newTargetBlock = getEntityData().get(DATA_TARGET_BLOCK).get();
            }
        } else {
            if (! level().isClientSide) {
                getEntityData().set(DATA_TARGET_BLOCK, Optional.of(newTargetBlock));
            }
        }
        targetBlock = newTargetBlock;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (owner != null) {
            compound.putUUID("Owner", owner.getUUID());
        }
        compound.putInt("HookNum", hookNumber);
        compound.putInt("State", state);
        if (targetBlock != null) {
            compound.putIntArray("TargetBlock", List.of(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()));
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (level() instanceof ServerLevel) {
            if (tag.contains("Owner")) {
                getEntityData().set(DATA_OWNER, Optional.of(tag.getUUID("Owner")));
            }
            if (tag.contains("HookNum")) {
                getEntityData().set(DATA_HOOK_NUM, OptionalInt.of(tag.getInt("HookNum")));
            }
            if (tag.contains("State")) {
                getEntityData().set(DATA_STATE, OptionalInt.of(tag.getInt("State")));
            }
            if (tag.contains("TargetBlock")) {
                int[] stored = tag.getIntArray("TargetBlock");
                getEntityData().set(DATA_TARGET_BLOCK, Optional.of(new BlockPos(stored[0], stored[1], stored[2])));
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_OWNER) {
            setOwner(null);
        }
        else if (key == DATA_HOOK_NUM) {
            setHookNumber(-1);
        }
        else if (key == DATA_STATE) {
            setState(-1);
        }
        else if (key == DATA_TARGET_BLOCK) {
            setTargetBlock(null);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER, Optional.empty());
        builder.define(DATA_HOOK_NUM, OptionalInt.empty());
        builder.define(DATA_STATE, OptionalInt.empty());
        builder.define(DATA_TARGET_BLOCK, Optional.empty());
    }

    @Override
    public float[] getBossEventProgress(){
        if (owner == null) return new float[]{1f, 1f};
        return owner.getBossEventProgress();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean shouldDoCollision() {
        return super.shouldDoCollision();
    }

    @Override
    public void firstSpawn() {
    }

    @Override
    public boolean shouldShowBossBar() {
        return false;
    }

    @Override
    public boolean shouldEscape() {
        return false;
    }

    @Override
    public boolean isMainBody() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor(){
        return BossEvent.BossBarColor.GREEN;
    };

    @Override
    protected boolean shouldOverPlayer(){
        return false;
    }


    /*
     * GOALS
     */

    public class MoveGoal extends Goal {

        @Override
        public boolean canUse() {
            return owner != null;
        }

        @Override
        public void tick() {
            // 速度 - 将碰撞箱的中心对准目标位置（方块/本体）的中心
            Vec3 vel;
            switch (state) {
                case STATE_IDLE:
                case STATE_RETRACTING: {
                    Vec3 targetLoc = owner.position().add(0, owner.getBbHeight() / 2d, 0);
                    vel = targetLoc.subtract(position().add(0, getBbHeight() / 2d, 0));
                    break;
                }
                default: {
                    // 噶人们，防一手null pointer有没有懂得
                    if (targetBlock == null) {
                        vel = new Vec3(0, 0, 0);
                        setState(STATE_RETRACTING);
                    } else {
                        Vec3 targetLoc = targetBlock.getCenter();
                        vel = targetLoc.subtract(position().add(0, getBbHeight() / 2d, 0));
                    }
                    break;
                }
            }
            // 钩，移动
            double lenSqr = vel.lengthSqr();
            double hookSpeed = owner.hookSpeed;
            if (lenSqr > hookSpeed * hookSpeed) {
                vel = vel.normalize().scale(hookSpeed);
            }
            else {
                switch (state) {
                    case STATE_RETRACTING:
                        setState(STATE_IDLE);
                        break;
                    case STATE_EXTENDING:
                        setState(STATE_GRABBED);
                        break;
                }
                vel = vel.scale(0.9);
            }
            setDeltaMovement(vel);

            // 视角 - 远离本体
            Vec3 lookDir = getEyePosition().subtract(owner.getEyePosition()).normalize();
            lookControl.setLookAt(getX() + lookDir.x, getY() + lookDir.y, getZ() + lookDir.z, 360, 360);
        }
    }
}
