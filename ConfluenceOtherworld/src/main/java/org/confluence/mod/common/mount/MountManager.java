package org.confluence.mod.common.mount;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.advancement.AchievementAwardService;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.entity.mount.AbstractMountEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.mount.MountItem;

/// 本体坐骑的创建与清理入口。
///
/// <p>坐骑物品直接保存实体类型，管理器只负责从手中或坐骑槽找到物品、创建实体、
/// 切换骑乘状态，以及在槽位物品失效时清理实体。各坐骑的移动参数和特殊能力
/// 均留在自己的实体类中。</p>
public final class MountManager {
    private MountManager() {
    }

    /// 使用手中坐骑物品时召唤该物品绑定的坐骑；已经骑乘时不重复处理。
    public static void summonFromHand(ServerPlayer player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof MountItem<?> item) {
            summon(player, item, false);
        }
    }

    /// 按下快捷键时使用坐骑槽内的物品。
    public static void toggleFromSlot(ServerPlayer player) {
        if (player.getVehicle() instanceof AbstractMountEntity) {
            dismiss(player);
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        ItemStack stack = ExtraInventory.of(player).getMount(false);
        if (stack.getItem() instanceof MountItem<?> item) {
            summon(player, item, true);
        }
    }

    private static void summon(ServerPlayer player, MountItem<?> item, boolean slotBound) {
        if (player.getVehicle() != null || !player.isAlive() || player.isSpectator()) {
            return;
        }

        AbstractMountEntity mount = item.entityType().create(player.serverLevel());
        if (mount == null) {
            throw new IllegalStateException("Mount entity type failed to create an entity: " + item.entityType());
        }
        mount.initialize(player, slotBound);
        if (!player.serverLevel().addFreshEntity(mount)) {
            mount.discard();
            return;
        }
        if (!mount.mountPlayer(player)) {
            mount.discard();
            return;
        }
        player.level().playSound(null, player.blockPosition(), ModSoundEvents.USE_MOUNTS.get(), SoundSource.PLAYERS, 0.4F, 1.0F);
        AchievementAwardService.award(player, "the_cavalry");
    }

    /// 每个服务端 tick 校验当前临时坐骑。
    ///
    /// <p>手持物品召唤的坐骑无需一直持有原物品；坐骑槽召唤的坐骑则要求槽位
    /// 仍是同一实体类型的坐骑物品。</p>
    public static void validate(ServerPlayer player) {
        if (!(player.getVehicle() instanceof AbstractMountEntity mount)) {
            return;
        }
        if (!player.isAlive() || player.isSpectator() || mount.getOwnerUUID() == null || !mount.getOwnerUUID().equals(player.getUUID())) {
            dismiss(player);
            return;
        }
        if (!mount.isSlotBound()) {
            return;
        }

        ItemStack stack = ExtraInventory.of(player).getMount(false);
        if (!(stack.getItem() instanceof MountItem<?> item) || item.entityType() != mount.getType()) {
            dismiss(player);
        }
    }

    /// 清理玩家当前拥有并骑乘的临时坐骑。
    public static void dismiss(ServerPlayer player) {
        if (!(player.getVehicle() instanceof AbstractMountEntity mount) || mount.getOwnerUUID() == null || !mount.getOwnerUUID().equals(player.getUUID())) {
            return;
        }
        player.stopRiding();
        mount.discard();
    }
}
