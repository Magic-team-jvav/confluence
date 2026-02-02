package org.confluence.terraentity.entity.boss.plantera;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
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
import org.confluence.terraentity.config.ServerConfig;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * 世花触手
 */
public class PlanteraTentacle extends AbstractTerraBossBase implements Boss {
    private static final double DIST_FROM_OWNER = 6;
    private static final double TARGET_ATTRACTION_RADIUS = 24;
    private static final double REPULSION_FROM_TENTACLE = 2.5;
    private static final double ATTRACTION_FROM_TARGET = 0.25;
    public static final double MAX_HEALTH = 260;
    AbstractPlanteraTentacleSrc owner;

    public static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(PlanteraTentacle.class, EntityDataSerializers.OPTIONAL_UUID);

    public PlanteraTentacle(EntityType<? extends Monster> entityType, Level level) {
        this(entityType, level, null);
    }

    public PlanteraTentacle(EntityType<? extends Monster> entityType, Level level, @Nullable AbstractPlanteraTentacleSrc owner) {
        super(entityType, level);
        setDiscardFriction(true);
        if (ServerConfig.bossNoPhysics()) {
            noPhysics = true;
        }

        collisionProperties = new CollisionProperties(1,1,0.5f);

        setOwner(owner);

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
        if (pSource.is(DamageTypeTags.IS_DROWNING)) {
            return false;
        }
        return super.hurt(pSource, pAmount); // confluence mixin here
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TESounds.TR_ZOMBIE_DEATH.get();
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1,new MoveGoal());
    }

    public void setOwner(@Nullable AbstractPlanteraTentacleSrc newOwner) {
        if (newOwner == null) {
            if (getEntityData().get(DATA_OWNER).isPresent()) {
                newOwner = (AbstractPlanteraTentacleSrc) level().getEntities().get(getEntityData().get(DATA_OWNER).get());
            }
        } else {
            if (! level().isClientSide) {
                getEntityData().set(DATA_OWNER, Optional.of(newOwner.getUUID()));
            }
        }
        owner = newOwner;
        if (owner != null) {
            if (owner.tentacles == null) owner.tentacles = new ArrayList<>();
            // 注意：setOwner被多个位置调用，确保只保存一次
            boolean isRecorded = false;
            for (PlanteraTentacle test : owner.tentacles) {
                if (test == this) {
                    isRecorded = true;
                    break;
                }
            }
            if (! isRecorded) {
                owner.tentacles.add(this);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (owner != null) {
            compound.putUUID("Owner", owner.getUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (level() instanceof ServerLevel) {
            if (tag.contains("Owner")) {
                getEntityData().set(DATA_OWNER, Optional.of(tag.getUUID("Owner")));
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_OWNER) {
            setOwner(null);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER, Optional.empty());
    }

    @Override
    public float[] getBossEventProgress(){
        if (owner == null) return new float[]{1f, 1f};
        return owner.getBossEventProgress();
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        if (owner == null) return false;
        return owner.canAttack(entity);
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
            Vec3 offset = getEyePosition().subtract(owner.getEyePosition());
            // 伸长 / 缩短
            double distToOwner = offset.length();
            double distDelta = DIST_FROM_OWNER - distToOwner;
            if (Math.abs(distDelta) > 0.15) {
                distDelta = 0.15 * Math.signum(distDelta);
            }
            offset = offset.scale(Math.min(distToOwner + distDelta, DIST_FROM_OWNER) / distToOwner);
            // 各种推拉方向的信息
            ArrayList<Vec3> targetVecs = new ArrayList<>();
            ArrayList<Double> gatherStrengths = new ArrayList<>();
            // 向玩家聚拢
            if (owner.getTarget() != null) {
                Vec3 targetVec = owner.getTarget().getEyePosition().subtract(owner.getEyePosition());
                targetVecs.add(targetVec);
                gatherStrengths.add(Math.max(TARGET_ATTRACTION_RADIUS - targetVec.length(), 0) * ATTRACTION_FROM_TARGET);
            }
            // 触手互相推开
            if (owner.tentacles != null) {
                for (PlanteraTentacle tentacle : owner.tentacles) {
                    if (tentacle == PlanteraTentacle.this) continue;
                    if (! tentacle.isAlive()) continue;

                    Vec3 targetVec = tentacle.getEyePosition().subtract(owner.getEyePosition());
                    double distFromCurr = tentacle.getEyePosition().subtract(getEyePosition()).length();
                    targetVecs.add(targetVec);
                    gatherStrengths.add(-REPULSION_FROM_TENTACLE / Math.max(distFromCurr, 1));
                }
            }
            // 计算方向
            ArrayList<Quaterniond> quaternions = new ArrayList<>();
            for (int i = 0; i < targetVecs.size(); i ++) {
                Vec3 tgt = targetVecs.get(i);
                double interplStr = gatherStrengths.get(i);

                if (TEUtils.angleBetween(offset, tgt) < 1e-5) continue;
                if (Math.abs(interplStr) < 1e-9) continue;

                Vec3 rotAxis = offset.cross(tgt);
                Quaterniond rotator = new Quaterniond().fromAxisAngleDeg(rotAxis.x, rotAxis.y, rotAxis.z, interplStr);
                quaternions.add(rotator);
            }
            Vector3d offset3d = new Vector3d(offset.x, offset.y, offset.z);
            for (Quaterniond qd : quaternions) {
                qd.transform(offset3d);
            }
            setDeltaMovement(owner.getDeltaMovement().add(
                    owner.getEyePosition().add(offset3d.x(), offset3d.y(), offset3d.z()).subtract(getEyePosition())));

            // 视角 - 远离本体
            Vec3 lookDir = getEyePosition().subtract(owner.getEyePosition()).normalize();
            lookControl.setLookAt(getX() + lookDir.x, getY() + lookDir.y, getZ() + lookDir.z, 360, 360);
        }
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
}
