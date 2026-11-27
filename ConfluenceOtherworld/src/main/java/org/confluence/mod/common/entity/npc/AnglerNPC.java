package org.confluence.mod.common.entity.npc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.AnglerData;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.network.s2c.OpenAnglerDialogPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.mesdag.portlib.network.PortPacketDistributor;

import java.util.EnumSet;

/// 渔夫 NPC —— 每日钓鱼任务。
/// 自然生成于海洋时处于躺下（睡眠）状态，玩家交互后唤醒。
public class AnglerNPC extends BaseNPC {
    private static final EntityDataAccessor<Boolean> DATA_WAKE_UP = SynchedEntityData.defineId(AnglerNPC.class, EntityDataSerializers.BOOLEAN);
    public static final String WAKE_UP_KEY = "WakeUp";

    private Vec3 driftDir = Vec3.ZERO;

    public AnglerNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile combatProfile) {
        super(type, level, combatProfile);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(-1, new Goal() {
            {setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));}

            @Override
            public boolean canUse() {
                return !isWakeUp();
            }

            @Override
            public void start() {
                getNavigation().stop();
                setTarget(null);
                setSpeed(0.0F);
                setXxa(0.0F);
                setYya(0.0F);
                setZza(0.0F);
                setJumping(false);
            }
        });
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_WAKE_UP, false);
    }

    public boolean isWakeUp() {
        return entityData.get(DATA_WAKE_UP);
    }

    /// 睡姿属于渔夫自身的数据，由子类复制，通用微光流程不依赖具体 NPC 类型。
    @Override
    public void copyResidenceFrom(BaseNPC source) {
        if (source instanceof AnglerNPC angler) setWakeUp(angler.isWakeUp());
        super.copyResidenceFrom(source);
    }

    public void setWakeUp(boolean wakeUp) {
        entityData.set(DATA_WAKE_UP, wakeUp);
        if (wakeUp) {
            setShouldInteract(false);
            refreshDimensions();
        }
    }

    /// 渔夫的睡姿也属于待救援状态，与普通 NPC 的首次交互标记共用生命周期。
    @Override
    public boolean requiresRescue() {
        return !isWakeUp() || super.requiresRescue();
    }

    /// 完成救援时同步站姿尺寸，不再由对话入口单独解锁。
    @Override
    public void setRescued() {
        super.setRescued();
        setWakeUp(true);
    }

    @Override
    public boolean canDefendSelf() {
        return isWakeUp();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_WAKE_UP) refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return isWakeUp()
                ? super.getDimensions(pose)
                : super.getDimensions(pose).scale(2, 0.5F);
    }

    @Override
    protected void customServerAiStep() {
        if (!isWakeUp()) {
            tickSleeping();
            return;
        }
        super.customServerAiStep();
    }

    private void tickSleeping() {
        if (isInWater() || level().getBlockState(blockPosition()).is(Blocks.WATER)) {
            setDeltaMovement(0, 0.02, 0);
            float f = 0.001f;
            float maxSpeed = 0.008f;
            Vec3 driftSpeed = new Vec3(
                    (Math.random() * 2 * f - f),
                    0,
                    (Math.random() * 2 * f - f));
            driftDir = driftDir.add(driftSpeed);
            if (driftDir.length() > maxSpeed * 1.4F) {
                driftDir = driftDir.scale(0.9);
            }
            addDeltaMovement(driftDir);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isWakeUp();
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
        if (isRemoved() && !isWakeUp()) {
            NPCSpawner.INSTANCE.forgetNPC(this);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (hand == InteractionHand.OFF_HAND || !canStartInteraction(serverPlayer))
                return InteractionResult.SUCCESS;
            PlayerSpecialData data = PlayerSpecialData.of(serverPlayer);
            if (!isWakeUp()) {
                initName();
                recordInteraction(serverPlayer);
                PortPacketDistributor.sendToPlayer(serverPlayer, new OpenAnglerDialogPacketS2C(getId(), OpenAnglerDialogPacketS2C.WAKE_UP, Items.AIR, levelName(serverPlayer), data.getAnglerQuestCount()));
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            initName();
            recordInteraction(serverPlayer);

            ServerLevel serverLevel = (ServerLevel) level();
            AnglerData.INSTANCE.refreshIfNeeded(serverLevel);
            if (data.hasCompletedAnglerQuestToday(serverLevel)) {
                PortPacketDistributor.sendToPlayer(serverPlayer, new OpenAnglerDialogPacketS2C(getId(), OpenAnglerDialogPacketS2C.COMPLETED, Items.AIR, levelName(serverPlayer), data.getAnglerQuestCount()));
            } else if (!AnglerData.INSTANCE.hasValidQuest()) {
                PortPacketDistributor.sendToPlayer(serverPlayer, new OpenAnglerDialogPacketS2C(getId(), OpenAnglerDialogPacketS2C.NO_QUEST, Items.AIR, levelName(serverPlayer), data.getAnglerQuestCount()));
            } else {
                Item questFish = AnglerData.INSTANCE.getQuestFish();
                if (player.getInventory().countItem(questFish) > 0) {
                    submitQuest(serverPlayer, questFish, data);
                    return InteractionResult.sidedSuccess(level().isClientSide);
                } else {
                    PortPacketDistributor.sendToPlayer(serverPlayer, new OpenAnglerDialogPacketS2C(getId(), OpenAnglerDialogPacketS2C.SHOW_HINT, questFish, levelName(serverPlayer), data.getAnglerQuestCount()));
                }
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    private static String levelName(ServerPlayer player) {
        return player.server.getWorldData().getLevelName();
    }

    private void submitQuest(ServerPlayer player, Item questFish, PlayerSpecialData data) {
        ServerLevel serverLevel = player.serverLevel();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (slot.is(questFish)) {
                slot.shrink(1);
                break;
            }
        }

        data.markAnglerQuestCompleted(serverLevel);
        int count = data.getAnglerQuestCount();

        if (count >= 200) AchievementUtils.awardAchievement(player, "supreme_helper_minion");
        else if (count >= 50) AchievementUtils.awardAchievement(player, "fast_and_fishious");
        else if (count >= 25) AchievementUtils.awardAchievement(player, "trout_monkey");
        else if (count >= 10) AchievementUtils.awardAchievement(player, "good_little_slave");
        else AchievementUtils.awardAchievement(player, "servant_in_training");

        AnglerQuestRewards.grant(player, questFish, count, getMood().getBuyPriceMultiplier());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        /// 先恢复睡姿，父类才能按正确救援状态读取房屋与 HOME。
        if (tag.contains(WAKE_UP_KEY)) {
            setWakeUp(tag.getBoolean(WAKE_UP_KEY));
        }
        super.readAdditionalSaveData(tag);
        if (isWakeUp()) setShouldInteract(false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(WAKE_UP_KEY, isWakeUp());
    }
}
