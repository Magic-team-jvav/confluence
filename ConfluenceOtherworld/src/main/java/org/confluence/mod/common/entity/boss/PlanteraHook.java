package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

/**
 * 世纪之花用于移动的三个临时抓钩之一。
 *
 * <p>抓钩锚定方块并为主体提供移动目标；它不独立持有 Boss 生命周期，
 * 主体消失、脱战或重新构建附属实体时必须一并清理。</p>
 */
public class PlanteraHook extends BaseBossPart<Plantera> implements GeoEntity {
    static final int STATE_IDLE = 0;
    static final int STATE_EXTENDING = 1;
    static final int STATE_GRABBED = 2;
    static final int STATE_RETRACTING = 3;

    private static final String HOOK_INDEX_TAG = "HookIndex";
    private static final String ANCHOR_TAG = "Anchor";
    private static final String STATE_TAG = "State";
    private static final double HOOK_SPEED = 2.0;

    private static final EntityDataAccessor<Integer> HOOK_INDEX =
            SynchedEntityData.defineId(PlanteraHook.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> ANCHOR =
            SynchedEntityData.defineId(PlanteraHook.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> STATE =
            SynchedEntityData.defineId(PlanteraHook.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PlanteraHook(EntityType<?> type, Level level) {
        super(type, level);
    }

    public void setMaster(Plantera master, int hookIndex, @Nullable BlockPos anchor) {
        entityData.set(HOOK_INDEX, Mth.clamp(hookIndex, 0, Plantera.HOOK_COUNT - 1));
        setAnchor(anchor);
        setState(anchor == null ? STATE_IDLE : STATE_EXTENDING);
        bindTo(master);
        master.bindHook(this);
    }

    public int getHookIndex() {
        return entityData.get(HOOK_INDEX);
    }

    public @Nullable BlockPos getAnchor() {
        return entityData.get(ANCHOR).orElse(null);
    }

    public void setAnchor(@Nullable BlockPos anchor) {
        entityData.set(ANCHOR, Optional.ofNullable(anchor == null ? null : anchor.immutable()));
    }

    public int getState() {
        return entityData.get(STATE);
    }

    public void setState(int state) {
        entityData.set(
                STATE,
                Mth.clamp(state, STATE_IDLE, STATE_RETRACTING));
    }

    public boolean hasReachedAnchor() {
        return getState() == STATE_GRABBED;
    }

    @Override
    protected void definePartSynchedData() {
        entityData.define(HOOK_INDEX, 0);
        entityData.define(ANCHOR, Optional.empty());
        entityData.define(STATE, STATE_IDLE);
    }

    @Override
    protected Class<Plantera> getOwnerType() {
        return Plantera.class;
    }

    @Override
    protected void tickPart(Plantera master) {
        if (level().isClientSide) return;

        int state = getState();
        BlockPos anchor = getAnchor();
        if ((state == STATE_EXTENDING || state == STATE_GRABBED)
                && (anchor == null || !master.isValidHookAnchor(anchor))) {
            setAnchor(null);
            setState(STATE_RETRACTING);
            state = STATE_RETRACTING;
        }

        Vec3 targetCenter = state == STATE_IDLE
                || state == STATE_RETRACTING
                ? master.getBoundingBox().getCenter()
                : Vec3.atCenterOf(anchor);
        Vec3 offset = targetCenter.subtract(
                getBoundingBox().getCenter());
        if (offset.lengthSqr() <= HOOK_SPEED * HOOK_SPEED) {
            if (state == STATE_RETRACTING) {
                setState(STATE_IDLE);
            } else if (state == STATE_EXTENDING) {
                setState(STATE_GRABBED);
            }
            Vec3 movement = offset.scale(0.9);
            setDeltaMovement(movement);
            move(MoverType.SELF, movement);
            updateRotation(movement);
            return;
        }
        if (offset.lengthSqr() < 1.0E-4) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        Vec3 movement = offset.normalize().scale(HOOK_SPEED);
        setDeltaMovement(movement);
        move(MoverType.SELF, movement);
        updateRotation(movement);
    }

    private void updateRotation(Vec3 movement) {
        if (movement.lengthSqr() < 1.0E-7) {
            return;
        }
        setYRot((float) (Mth.atan2(movement.z, movement.x) * Mth.RAD_TO_DEG) - 90.0F);
        setXRot((float) (-(Mth.atan2(movement.y, movement.horizontalDistance())) * Mth.RAD_TO_DEG));
    }

    @Override
    protected void readPartSaveData(CompoundTag tag) {
        entityData.set(HOOK_INDEX, Mth.clamp(tag.getInt(HOOK_INDEX_TAG), 0, Plantera.HOOK_COUNT - 1));
        setAnchor(tag.contains(ANCHOR_TAG) ? BlockPos.of(tag.getLong(ANCHOR_TAG)) : null);
        setState(tag.getInt(STATE_TAG));
    }

    @Override
    protected void addPartSaveData(CompoundTag tag) {
        tag.putInt(HOOK_INDEX_TAG, getHookIndex());
        tag.putInt(STATE_TAG, getState());
        BlockPos anchor = getAnchor();
        if (anchor != null) tag.putLong(ANCHOR_TAG, anchor.asLong());
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
