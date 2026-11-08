package org.confluence.mod.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;

/// 泰拉背包里的创造物品面板（类似泰拉旅途模式的复制菜单）。
/// 原版创造模式的同步包只认原版背包菜单，泰拉背包是另一个菜单，所以用自己的包；只有创造模式玩家有效。
public record CreativeItemPacketC2S(byte action, ItemStack stack) implements IPacketC2S {
    /// 放到鼠标上（替换手里的东西）
    public static final byte TO_CURSOR = 0;
    /// 整组放进背包（Shift+点击）
    public static final byte TO_INVENTORY = 1;
    /// 删掉鼠标上的物品（拿着东西点物品面板）
    public static final byte DELETE_CURSOR = 2;

    public static final Type<CreativeItemPacketC2S> TYPE = Confluence.createType("creative_item");
    public static final StreamCodec<RegistryFriendlyByteBuf, CreativeItemPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, CreativeItemPacketC2S::action,
            ItemStack.OPTIONAL_STREAM_CODEC, CreativeItemPacketC2S::stack,
            CreativeItemPacketC2S::new
    );

    @Override
    public Type<CreativeItemPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (!player.hasInfiniteMaterials()) return;
        var menu = player.containerMenu;
        switch (action) {
            case TO_CURSOR -> {
                if (stack.isEmpty()) return;
                ItemStack copy = stack.copyWithCount(Math.min(stack.getCount(), stack.getMaxStackSize()));
                menu.setCarried(copy);
            }
            case TO_INVENTORY -> {
                if (stack.isEmpty()) return;
                ItemStack copy = stack.copyWithCount(Math.min(stack.getCount(), stack.getMaxStackSize()));
                if (!player.getInventory().add(copy) && !copy.isEmpty()) return; // 背包满了就不给
            }
            case DELETE_CURSOR -> menu.setCarried(ItemStack.EMPTY);
            default -> {
                return;
            }
        }
        menu.broadcastChanges();
    }

    public static void sendToServer(byte action, ItemStack stack) {
        // 物品组（Confluence 物品栏里的折叠夹）带有不能联网编码的组件，发出去会断开连接
        if (stack.getItem() instanceof org.confluence.lib.common.item.GroupItem) return;
        PacketDistributor.sendToServer(new CreativeItemPacketC2S(action, stack));
    }
}
