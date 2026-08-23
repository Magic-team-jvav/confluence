package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.Nullable;

/// 保存单个玩家当前悠悠球的输入与实体所有权，不参与存档。
public final class YoyoSession {
    private @Nullable YoyoEntity entity;
    private ItemStack sourceStack = ItemStack.EMPTY;
    private int selectedSlot = -1;
    private boolean inputHeld;

    public static YoyoSession of(ServerPlayer player) {
        return player.getData(ModAttachmentTypes.YOYO_SESSION);
    }

    public boolean press(ServerPlayer player, ItemStack stack) {
        if (!(stack.getItem() instanceof YoyoItem) || !player.isAlive() || player.isSpectator())
            return false;
        boolean sameSource = selectedSlot == player.getInventory().selected && sourceStack == stack;
        inputHeld = true;
        selectedSlot = player.getInventory().selected;
        sourceStack = stack;
        if (entity == null || !entity.isAlive()) {
            return spawn(player);
        } else if (sameSource) {
            entity.resumeExtension();
        } else {
            entity.beginReturn();
        }
        return false;
    }

    public void release() {
        inputHeld = false;
        if (entity != null && entity.isAlive()) entity.beginReturn();
    }

    public void adjustRange(ServerPlayer player, int amount) {
        if (inputHeld && isSourceSelected(player) && entity != null && entity.isAlive())
            entity.adjustRange(amount);
    }

    public void tick(ServerPlayer player) {
        if (entity != null && !entity.isAlive()) {
            YoyoItem item = entity.getYoyoItem();
            if (item != null) player.getCooldowns().removeCooldown(item);
            entity = null;
        }
        if (!isSourceSelected(player)) {
            inputHeld = false;
            if (entity != null) entity.beginReturn();
            else clearSource();
            return;
        }
        if (inputHeld && entity == null) spawn(player);
        else if (!inputHeld && entity == null) clearSource();
    }

    public boolean owns(YoyoEntity candidate, ServerPlayer player) {
        return entity == candidate && isSourceSelected(player);
    }

    private boolean isSourceSelected(ServerPlayer player) {
        return selectedSlot == player.getInventory().selected && sourceStack == player.getMainHandItem() && sourceStack.getItem() instanceof YoyoItem;
    }

    private boolean spawn(ServerPlayer player) {
        entity = YoyoEntity.spawn(player, sourceStack);
        if (entity != null && sourceStack.getItem() instanceof YoyoItem item) {
            player.getCooldowns().addCooldown(item, item.lifetimeTicks());
        }
        return entity != null;
    }

    private void clearSource() {
        sourceStack = ItemStack.EMPTY;
        selectedSlot = -1;
    }
}
