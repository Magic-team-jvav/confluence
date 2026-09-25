package org.confluence.mod.common.attachment;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;

/// 仅保存一次持键期间的服务端状态；死亡或重新登录后不会继承。
public final class LeftClickState {
    private ItemStack activeStack = ItemStack.EMPTY;

    public static LeftClickState of(Player player) {
        return ((IPortAttachmentHolder) player).getAttach(ModAttachmentTypes.LEFT_CLICK_STATE);
    }

    public boolean isPressed(ItemStack stack) {
        return activeStack == stack && !stack.isEmpty();
    }

    public ItemStack activeStack() {
        return activeStack;
    }

    public void press(ItemStack stack) {
        activeStack = stack;
    }

    public void release() {
        activeStack = ItemStack.EMPTY;
    }
}
