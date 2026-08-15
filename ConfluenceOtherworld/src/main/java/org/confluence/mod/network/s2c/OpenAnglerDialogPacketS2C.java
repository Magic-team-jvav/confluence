package org.confluence.mod.network.s2c;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.screen.AnglerDialogScreen;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record OpenAnglerDialogPacketS2C(int entityId, byte state, ItemStack questFish) implements IPortPacket.S2C {
    public static final byte COMPLETED = 0;
    public static final byte NO_QUEST = 1;
    public static final byte SHOW_HINT = 2;
    public static final byte WAKE_UP = 3;
    // 可提交任务的状态由服务端钓鱼人直接处理，不进入客户端对话界面。

    public static final ResourceLocation ID = Confluence.asResource("open_angler_dialog");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, OpenAnglerDialogPacketS2C> STREAM_CODEC =
            PortStreamCodec.composite(
                    PortByteBufCodecs.VAR_INT, OpenAnglerDialogPacketS2C::entityId,
                    PortByteBufCodecs.BYTE, OpenAnglerDialogPacketS2C::state,
                    PortItemStackExtension.optionalStreamCodec(), OpenAnglerDialogPacketS2C::questFish,
                    OpenAnglerDialogPacketS2C::new
            );

    /**
     * 打开界面和读取客户端实体都必须在客户端主线程执行。
     */
    @Override
    public void handle(IPortPacket.Context context) {
        Player player = context.player();
        if (player != null) context.enqueueWork(() -> work(player));
    }

    @Override
    public void work(Player player) {
        AnglerDialogScreen.State s = switch (state) {
            case COMPLETED -> AnglerDialogScreen.State.COMPLETED;
            case NO_QUEST -> AnglerDialogScreen.State.NO_QUEST;
            case WAKE_UP -> AnglerDialogScreen.State.WAKE_UP;
            default -> AnglerDialogScreen.State.SHOW_HINT;
        };
        AnglerDialogScreen.open(entityId, s, questFish);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }
}
