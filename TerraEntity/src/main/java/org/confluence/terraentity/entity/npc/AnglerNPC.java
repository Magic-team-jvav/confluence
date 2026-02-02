package org.confluence.terraentity.entity.npc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.network.s2c.SetAnglerDialogPacketS2C;
import org.confluence.terraentity.registries.npc_trade.variant.TradeTask;
import org.confluence.terraentity.registries.npc_trade_task.variant.DynamicAnglerTradeTask;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 渔夫：可以设置处理交易任务
 */
public class AnglerNPC extends AbstractTerraNPC {

    public static final String WAKE_UP_KEY = "WakeUp";
    boolean triggerNight = false;

    public AnglerNPC(EntityType<? extends AbstractTerraNPC> entityType, Level level) {
        super(entityType, level);

    }

    private static final EntityDataAccessor<Boolean> DATA_WAKE_UP = SynchedEntityData.defineId(AnglerNPC.class, EntityDataSerializers.BOOLEAN);

    /**
     * 每天12点重置交易任务
     */
    public void resetFishTask() {
//        this.entityData.set(DATA_TIME_TO_TRADE_FISH_DATA, true);
        int c = 0;
        if (trades() == null) {
            return;
        }
        for (ITrade trade : trades()) {
            if (trade instanceof TradeTask task) {
                if (task.task() instanceof DynamicAnglerTradeTask d) {
                    // 更新参数
                    if (!Objects.requireNonNull(getTradeParams()).isReady(c)) {
                        d.setNext(this, c);

                        getTradeParams().increaseLevel(c);
                        getTradeParams().setIsReady(c, true);
                    }
                }
            }
            c++;
        }
        syncTradeTasksParams();
        this.getTradeManager().syncDirtyTrade();
    }

    // 渔夫初始化时随机设置交易任务
    protected void onInitTrades() {
        int c = 0;
        for (ITrade trade : trades()) {
            if (trade instanceof TradeTask task) {
                if (task.task() instanceof DynamicAnglerTradeTask d) {
                    // 更新参数
                    d.setNext(this, c);
                    getTradeParams().increaseLevel(c);
                }
            }
            c++;
        }
        syncTradeTasksParams();
    }

    public @Nullable DynamicAnglerTradeTask getFirstTask() {
        for (ITrade trade : trades()) {
            if (trade instanceof TradeTask task) {
                if (task.task() instanceof DynamicAnglerTradeTask d) {
                    return d;
                }
            }
        }
        return null;
    }

    // 只要经历过晚上，天一亮就会刷新
    protected boolean timeToTradeFresh() {
        if (this.isNight()) {
            triggerNight = true;
        }
        if (this.triggerNight) {
            if (this.isDay()) {
                this.triggerNight = false;
                return true;
            }
        }
        return false;
//        return level().dayTime() % 24000 == 200;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isWakeUp();
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
        // confluence mixin here
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (!this.isWakeUp()) {
            this.brain = this.brain.copyWithoutBehaviors();
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.entityData.set(DATA_WAKE_UP, this.isWakeUp(), true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_WAKE_UP) {
            this.refreshDimensions();
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(WAKE_UP_KEY)) {
            setWakeUp(tag.getBoolean(WAKE_UP_KEY));
            this.entityData.set(DATA_WAKE_UP, isWakeUp(), true);
        } else {
            this.entityData.set(DATA_WAKE_UP, false, true);
        }
        if (tag.contains("TriggerNight")) {
            triggerNight = tag.getBoolean("TriggerNight");
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(WAKE_UP_KEY, isWakeUp());
        tag.putBoolean("TriggerNight", triggerNight);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_WAKE_UP, false);
    }

    public boolean isWakeUp() {
        return this.entityData.get(DATA_WAKE_UP);
    }

    public void setWakeUp(boolean wakeUp) {
        this.entityData.set(DATA_WAKE_UP, wakeUp, true);
    }

    public boolean isLieDown() {
        return !isWakeUp();
    }

    private boolean isNight() {
        return level().dayTime() % 24000 >= 12000 || level().dayTime() < 200;
    }

    private boolean isDay() {
        return !isNight();
    }

    Vec3 dir = Vec3.ZERO;
    Vec3 speed = Vec3.ZERO;

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {

            if (timeToTradeFresh()) {
                this.resetFishTask();
            }
            if (!this.isWakeUp()) {
                if (this.isInWater()) {
                    this.setDeltaMovement(0, 0.02f, 0);

                }
                if (this.isInWater() || level().getBlockState(this.blockPosition()).is(Blocks.WATER)) {
                    if (this.dir == null) {
                        this.dir = Vec3.ZERO;
                    }
                    if (this.speed == null) {
                        this.speed = Vec3.ZERO;
                    }
                    float f = 0.001f;
                    float maxSpeed = 0.008f;
                    speed = new Vec3(Math.random() * 2 * f - f, 0, Math.random() * 2 * f - f);
                    this.dir = this.dir.add(speed);
                    if (dir.length() > maxSpeed * 1.4F) {
                        dir = dir.scale(0.9F);
                    }

                    this.addDeltaMovement(dir);

                }
            }
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!isWakeUp() && player instanceof ServerPlayer serverPlayer) {
            setWakeUp(true);
            this.refreshBrain(serverPlayer.serverLevel());
            AdapterUtils.sendToPlayer(serverPlayer, new SetAnglerDialogPacketS2C(SetAnglerDialogPacketS2C.WAKEUP));
            return InteractionResult.CONSUME; // confluence mixin here
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pose) {
        if (!this.isWakeUp()) {
            return super.getDefaultDimensions(pose).scale(2F, 0.5f);
        }
        return super.getDefaultDimensions(pose);
    }
}
