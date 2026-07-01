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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.AnglerData;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.FishingPoleItems;
import org.confluence.mod.common.init.item.MountItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.network.s2c.OpenAnglerDialogPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 渔夫 NPC —— 每日钓鱼任务。
 * 自然生成于海洋时处于躺下（睡眠）状态，玩家交互后唤醒。
 */
public class AnglerNPC extends BaseNPC {
    private static final EntityDataAccessor<Boolean> DATA_WAKE_UP =
            SynchedEntityData.defineId(AnglerNPC.class, EntityDataSerializers.BOOLEAN);
    private static final String WAKE_UP_KEY = "WakeUp";

    private Vec3 driftDir = Vec3.ZERO;
    private Vec3 driftSpeed = Vec3.ZERO;

    public AnglerNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_WAKE_UP, false);
    }

    public boolean isWakeUp() {
        return entityData.get(DATA_WAKE_UP);
    }

    public void setWakeUp(boolean wakeUp) {
        entityData.set(DATA_WAKE_UP, wakeUp);
        if (wakeUp) refreshDimensions();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_WAKE_UP) refreshDimensions();
    }

    @Override
    protected @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pose) {
        return isWakeUp()
                ? super.getDefaultDimensions(pose)
                : super.getDefaultDimensions(pose).scale(2F, 0.5F);
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
            driftSpeed = new Vec3(
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
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            if (!isWakeUp()) {
                setWakeUp(true);
                Confluence.NETWORK_HANDLER.sendToPlayer(sp,
                        new OpenAnglerDialogPacketS2C(getId(), OpenAnglerDialogPacketS2C.SHOW_HINT, ItemStack.EMPTY));
                return InteractionResult.sidedSuccess(level().isClientSide);
            }

            ServerLevel serverLevel = (ServerLevel) level();
            AnglerData.INSTANCE.refreshIfNeeded(serverLevel);
            PlayerSpecialData data = PlayerSpecialData.of(sp);

            if (data.hasCompletedAnglerQuestToday(serverLevel)) {
                Confluence.NETWORK_HANDLER.sendToPlayer(sp,
                        new OpenAnglerDialogPacketS2C(getId(), OpenAnglerDialogPacketS2C.COMPLETED, ItemStack.EMPTY));
            } else if (!AnglerData.INSTANCE.hasValidQuest()) {
                Confluence.NETWORK_HANDLER.sendToPlayer(sp,
                        new OpenAnglerDialogPacketS2C(getId(), OpenAnglerDialogPacketS2C.NO_QUEST, ItemStack.EMPTY));
            } else {
                ItemStack questFish = AnglerData.INSTANCE.getQuestFish();
                if (player.getInventory().contains(questFish)) {
                    submitQuest(sp, questFish, data);
                    return InteractionResult.sidedSuccess(level().isClientSide);
                } else {
                    Confluence.NETWORK_HANDLER.sendToPlayer(sp,
                            new OpenAnglerDialogPacketS2C(getId(), OpenAnglerDialogPacketS2C.SHOW_HINT, questFish));
                }
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    private void submitQuest(ServerPlayer player, ItemStack questFish, PlayerSpecialData data) {
        ServerLevel serverLevel = player.serverLevel();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(slot, questFish)) {
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

        List<ItemStack> rewards = new ArrayList<>();
        ItemStack milestone = getMilestoneReward(count);
        if (milestone != null) rewards.add(milestone);

        if (count >= 75) {
            rewards.addAll(rollLoot(serverLevel, ModLootTables.QUESTS_AFTER_75));
        } else if (count >= 10) {
            rewards.addAll(rollLoot(serverLevel, ModLootTables.QUESTS_AFTER_10));
        }
        rewards.addAll(rollLoot(serverLevel, ModLootTables.QUESTS_0));

        for (ItemStack reward : rewards) {
            if (!player.getInventory().add(reward)) {
                player.drop(reward, false);
            }
        }
    }

    private ItemStack getMilestoneReward(int count) {
        return switch (count) {
            case 5 -> MountItems.FUZZY_CARROT.toStack();
            case 10 -> ArmorItems.ANGLER_HAT.toStack();
            case 15 -> ArmorItems.ANGLER_VEST.toStack();
            case 20 -> ArmorItems.ANGLER_PANTS.toStack();
            case 25 -> ToolItems.BOTTOMLESS_WATER_BUCKET.toStack();
            case 30 -> FishingPoleItems.GOLDEN_FISHING_ROD.toStack();
            default -> null;
        };
    }

    private List<ItemStack> rollLoot(ServerLevel level, net.minecraft.resources.ResourceLocation tableKey) {
        LootTable table = level.getServer().getLootData().getLootTable(tableKey);
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        return table.getRandomItems(params);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(WAKE_UP_KEY)) {
            setWakeUp(tag.getBoolean(WAKE_UP_KEY));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(WAKE_UP_KEY, isWakeUp());
    }
}
